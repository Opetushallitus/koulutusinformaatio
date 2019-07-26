/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.koulutusinformaatio.service.impl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.*;
import fi.vm.sade.koulutusinformaatio.service.*;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.LOSObjectCreator;
import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * @author Hannu Lyytikainen
 */
@Service
@Profile("default")
public class TarjontaServiceImpl implements TarjontaService {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaServiceImpl.class);
    private static final int MAX_IMAGE_WIDTH = 800;

    private KoodistoService koodistoService;
    private ProviderService providerService;
    private TarjontaRawService tarjontaRawService;
    private LOSObjectCreator creator;

    private HashSet<String> processedOids = new HashSet<>();
    private HashMap<String, TutkintoLOS> processedTutkintos = new HashMap<>();

    private Set<String> overriddenASOids;

    @Autowired
    public TarjontaServiceImpl(KoodistoService koodistoService,
            ProviderService providerService, TarjontaRawService tarjontaRawService,
            OrganisaatioRawService organisaatioRawService, ParameterService parameterService,
            @Value("#{'${koulutusinformaatio.overridden.haku.uris}'.split(',')}") List<String> overriddenASOids) {
        this.koodistoService = koodistoService;
        this.providerService = providerService;
        this.tarjontaRawService = tarjontaRawService;
        ArrayList<String> filteredOverriddenASOids = Lists.newArrayList(Collections2
                .filter(overriddenASOids, new Predicate<String>() {
                    @Override
                    public boolean apply(String s) {
                        return StringUtils.isNotBlank(s);
                    }
                }));
        this.creator = new LOSObjectCreator(koodistoService, tarjontaRawService, providerService, organisaatioRawService, parameterService,
                filteredOverriddenASOids);
        this.overriddenASOids = Sets.newHashSet(filteredOverriddenASOids);
    }

    public TarjontaServiceImpl() {
    }

    @Override
    public List<HigherEducationLOS> findHigherEducations() throws KoodistoException, ResourceNotFoundException {

        // List of all properly published higher education learning objects, regardles of position in hierarchy
        List<HigherEducationLOS> koulutukset = new ArrayList<>();

        // A map containing komo oid as key and higher education lo as value. This is used in lo-hierarchy creation, because
        // hierarchy relationships are retrieved based on komos.
        Map<String, List<HigherEducationLOS>> komoToLOSMap = new HashMap<>();

        // Komo-oids of parent level learning opportunities.
        List<String> parentOids = new ArrayList<>();

        ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> rawRes = this.tarjontaRawService
                .listEducationsByToteutustyyppi(ToteutustyyppiEnum.KORKEAKOULUTUS.name());
        HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> results = rawRes.getResult();
        Map<String, Set<HigherEducationLOSRef>> aoToEducationsMap = new HashMap<>();
        for (TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> curRes : results.getTulokset()) {
            for (KoulutusHakutulosV1RDTO curKoulutus : curRes.getTulokset()) {
                ResultV1RDTO<KoulutusV1RDTO> koulutusRes = this.tarjontaRawService.getV1KoulutusLearningOpportunity(curKoulutus.getOid());
                KoulutusKorkeakouluV1RDTO koulutusDTO = (KoulutusKorkeakouluV1RDTO) koulutusRes.getResult();
                if (koulutusDTO == null) {
                    continue;
                }
                try {
                    LOG.debug("Indexing higher education: {}", koulutusDTO.getOid());
                    HigherEducationLOS los = creator.createHigherEducationLOS(koulutusDTO, true);
                    los.setStructureImage(retrieveStructureImage(curKoulutus.getOid()));
                    koulutukset.add(los);
                    List<HigherEducationLOS> loss = komoToLOSMap.get(koulutusDTO.getKomoOid());
                    if (loss == null) {
                        loss = new ArrayList<>();
                        loss.add(los);
                        komoToLOSMap.put(koulutusDTO.getKomoOid(), loss);
                    } else {
                        loss.add(los);
                    }
                    parentOids.add(los.getKomoOid());
                    updateAOLosReferences(los, aoToEducationsMap);

                } catch (TarjontaParseException | KoodistoException | OrganisaatioException ex) {
                    LOG.warn("Problem with higher education: " + koulutusDTO.getOid(), ex);
                } catch (NoValidApplicationOptionsException e) {
                    LOG.debug("Problem with higher education: {}, reason: {}", koulutusDTO.getOid(), e.getMessage());
                }

            }
        }

        return createChildHierarchy(koulutukset, komoToLOSMap, parentOids, aoToEducationsMap);
    }

    @Override
    public List<KoulutusHakutulosV1RDTO> findKorkeakouluOpinnot() {
        List<KoulutusHakutulosV1RDTO> result = Lists.newArrayList();
        ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> rawRes = this.tarjontaRawService
                .listEducationsByToteutustyyppi(ToteutustyyppiEnum.KORKEAKOULUOPINTO.name());
        HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> results = rawRes.getResult();
        for (TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> curRes : results.getTulokset()) {
            for (KoulutusHakutulosV1RDTO curKoulutus : curRes.getTulokset()) {
                result.add(curKoulutus);
            }
        }
        return result;
    }

    private I18nPicture retrieveStructureImage(String oid) throws KoodistoException {
        I18nPicture structureImage = null;
        ResultV1RDTO<List<KuvaV1RDTO>> result = this.tarjontaRawService.getStructureImages(oid);
        List<KuvaV1RDTO> imageDtos = result != null ? result.getResult() : null;

        if (imageDtos != null && !imageDtos.isEmpty()) {
            structureImage = new I18nPicture();
            for (KuvaV1RDTO curDto : imageDtos) {
                String kielikoodi = this.koodistoService.searchFirstCodeValue(curDto.getKieliUri());
                Picture pict = new Picture();
                pict.setId(String.format("%s_%s", oid, kielikoodi.toLowerCase()));
                pict.setPictureEncoded(resizePicture(curDto.getBase64data(), oid));
                structureImage.getPictureTranslations().put(kielikoodi.toLowerCase(), pict);
            }
        }
        return structureImage;
    }

    private BASE64Decoder decoder = new BASE64Decoder();
    private BASE64Encoder encoder = new BASE64Encoder();

    private String resizePicture(String kuvaEncoded, String koulutusOid) {
        LOG.debug("Resizing picture");
        if (kuvaEncoded == null) {
            return null;
        }
        try {
            byte[] imageByte = decoder.decodeBuffer(kuvaEncoded);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            BufferedImage image = ImageIO.read(bis);
            bis.close();

            if(null == image) {
                LOG.warn("Got null buffered image when trying to resize picture for koulutus {}. Skipping...", koulutusOid);
                return null;
            }

            if(image.getWidth() < MAX_IMAGE_WIDTH)
                return kuvaEncoded;

            double ratio = MAX_IMAGE_WIDTH / image.getWidth();
            int height = (int) (ratio * image.getHeight());
            BufferedImage scaledImage = Scalr.resize(image, MAX_IMAGE_WIDTH, height);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(scaledImage, "jpeg", bos);
            imageByte = bos.toByteArray();

            String resizedString = encoder.encode(imageByte);
            bos.close();
            return resizedString;
        } catch (Exception ex) {
            LOG.warn("problem resizing picture for koulutus " + koulutusOid, ex);
            return null;
        }
    }

    private void updateAOLosReferences(KoulutusLOS los,
            Map<String, Set<HigherEducationLOSRef>> aoToEducationsMap) {
        if (los.getApplicationOptions() != null) {
            for (ApplicationOption curAo : los.getApplicationOptions()) {
                LOG.debug("Updating ao los references for ao: {}", curAo.getId());
                Set<HigherEducationLOSRef> aoLoss = aoToEducationsMap.get(curAo.getId());
                if (aoLoss == null) {
                    aoLoss = new HashSet<>();
                    aoToEducationsMap.put(curAo.getId(), aoLoss);
                }

                HigherEducationLOSRef newRef = new HigherEducationLOSRef();
                newRef.setId(los.getId());
                newRef.setName(los.getName());
                newRef.setPrerequisite(curAo.getPrerequisite());
                newRef.setQualifications(los.getQualifications());
                newRef.setProvider(curAo.getProvider().getName());
                aoLoss.add(newRef);
                aoToEducationsMap.put(curAo.getId(), aoLoss);
            }
        }
        LOG.debug("ao los references now updated");

    }

    private void updateAOLosReferences(CompetenceBasedQualificationParentLOS los,
            Map<String, Set<HigherEducationLOSRef>> aoToEducationsMap) {
        if (los.getApplicationOptions() != null) {
            for (ApplicationOption curAo : los.getApplicationOptions()) {
                LOG.debug("Updating ao los references for ao: {}", curAo.getId());
                Set<HigherEducationLOSRef> aoLoss = aoToEducationsMap.get(curAo.getId());
                if (aoLoss == null) {
                    aoLoss = new HashSet<>();
                    aoToEducationsMap.put(curAo.getId(), aoLoss);
                }
                aoLoss.add(createAdultVocationalLosRef(los, curAo));
                aoToEducationsMap.put(curAo.getId(), aoLoss);
            }
        }
        LOG.debug("ao los references now updated");

    }

    public HigherEducationLOSRef createAdultVocationalLosRef(CompetenceBasedQualificationParentLOS los, ApplicationOption curAo) {

        HigherEducationLOSRef newRef = new HigherEducationLOSRef();
        newRef.setId(los.getId());
        newRef.setName(los.getName());
        newRef.setPrerequisite(curAo.getPrerequisite());
        newRef.setProvider(curAo.getProvider().getName());
        newRef.setFieldOfExpertise(los.getChildren().get(0).getName());
        newRef.setEducationKind(los.getEducationKind());
        newRef.setAdultVocational(true);
        return newRef;

    }

    /*
     * Creating the learning opportunity hierarchy for higher education
     */
    private List<HigherEducationLOS> createChildHierarchy(List<HigherEducationLOS> koulutukset,
            Map<String, List<HigherEducationLOS>> komoToLOSMap, List<String> parentOids, Map<String, Set<HigherEducationLOSRef>> aoToEducationsMap) {

        for (HigherEducationLOS curLos : koulutukset) {

            ResultV1RDTO<Set<String>> childKomoOids = this.tarjontaRawService.getChildrenOfParentHigherEducationLOS(curLos.getKomoOid());
            ResultV1RDTO<Set<String>> parentKomoOids = this.tarjontaRawService.getParentsOfHigherEducationLOS(curLos.getKomoOid());
            if (childKomoOids != null && childKomoOids.getResult() != null && !childKomoOids.getResult().isEmpty()) {
                for (String curChildKomoOid : childKomoOids.getResult()) {
                    List<HigherEducationLOS> loss = komoToLOSMap.get(curChildKomoOid);
                    if (loss != null && !loss.isEmpty()) {
                        curLos.getChildren().addAll(loss);
                    }

                    if (parentOids.contains(curChildKomoOid)) {
                        parentOids.remove(curChildKomoOid);
                    }
                }
            }
            if (parentKomoOids != null && parentKomoOids.getResult() != null) {
                for (String curParentKomoOid : parentKomoOids.getResult()) {
                    List<HigherEducationLOS> loss = komoToLOSMap.get(curParentKomoOid);
                    if (loss != null) {
                        curLos.getParents().addAll(loss);
                    }
                }
            }
            if (curLos.getApplicationOptions() != null) {
                for (ApplicationOption ao : curLos.getApplicationOptions()) {
                    ao.setHigherEdLOSRefs(aoToEducationsMap.get(ao.getId()));
                }
            }

        }

        List<HigherEducationLOS> parents = new ArrayList<>();
        for (String curParent : parentOids) {
            parents.addAll(komoToLOSMap.get(curParent));
        }
        return parents;
    }

    @Override
    public HigherEducationLOS findHigherEducationLearningOpportunity(String oid) throws TarjontaParseException, KoodistoException, ResourceNotFoundException, NoValidApplicationOptionsException, OrganisaatioException {
        if (this.providerService != null) {
            this.providerService.clearCache();
        }
        ResultV1RDTO<KoulutusV1RDTO> koulutusRes = this.tarjontaRawService.getV1KoulutusLearningOpportunity(oid);
        KoulutusKorkeakouluV1RDTO koulutusDTO = (KoulutusKorkeakouluV1RDTO) koulutusRes.getResult();
        if (koulutusDTO == null) {
            return null;
        }

        HigherEducationLOS los = creator.createHigherEducationLOS(koulutusDTO, false);
        los.setStatus(koulutusDTO.getTila().toString());

        if (los.getApplicationOptions() != null) {
            for (ApplicationOption curAo : los.getApplicationOptions()) {
                createEducationreReferencesForAo(curAo, false);
            }
        }

        ResultV1RDTO<Set<String>> childKomoOids = this.tarjontaRawService.getChildrenOfParentHigherEducationLOS(koulutusDTO.getKomoOid());
        ResultV1RDTO<Set<String>> parentKomoOids = this.tarjontaRawService.getParentsOfHigherEducationLOS(koulutusDTO.getKomoOid());
        los.setChildren(getHigherEducationRelatives(childKomoOids, creator, false));
        los.setParents(getHigherEducationRelatives(parentKomoOids, creator, false));

        los.setStructureImage(retrieveStructureImage(los.getId()));

        return los;
    }

    @Override
    public HigherEducationLOS createHigherEducationLearningOpportunityTree(String oid) throws TarjontaParseException, KoodistoException,
            ResourceNotFoundException, NoValidApplicationOptionsException, OrganisaatioException {
        ResultV1RDTO<KoulutusV1RDTO> koulutusRes = this.tarjontaRawService.getV1KoulutusLearningOpportunity(oid);
        KoulutusKorkeakouluV1RDTO koulutusDTO = (KoulutusKorkeakouluV1RDTO) koulutusRes.getResult();
        LOG.debug(" Koulutustila: {}", koulutusDTO.getTila().toString());
        if (koulutusDTO == null || !koulutusDTO.getTila().toString().equals(TarjontaTila.JULKAISTU.toString())) {
            LOG.info("Returning null HigherEducationLOS");
            return null;
        }

        LOG.debug("Now creating higherEducation learning opportunity tree");

        HigherEducationLOS los = creator.createHigherEducationLOS(koulutusDTO, true);
        los.setStatus(koulutusDTO.getTila().toString());

        if (los.getApplicationOptions() != null) {
            LOG.debug("now creating higher edu los refs for los: {}", los.getId());
            for (ApplicationOption curAo : los.getApplicationOptions()) {
                createEducationreReferencesForAo(curAo, true);
            }
        }

        ResultV1RDTO<Set<String>> childKomoOids = this.tarjontaRawService.getChildrenOfParentHigherEducationLOS(koulutusDTO.getKomoOid());

        List<HigherEducationLOS> children = new ArrayList<>();

        for (String curChildKomoOid : childKomoOids.getResult()) {
            List<String> koulutusOids = getKoulutusoidsForKomo(curChildKomoOid);
            for (String curKoulutusOid : koulutusOids) {
                try {

                    HigherEducationLOS curChild = createHigherEducationLearningOpportunityTree(curKoulutusOid);
                    if (curChild != null) {
                        curChild.getParents().add(los);
                        children.add(curChild);
                    }
                } catch (NoValidApplicationOptionsException e) {
                    LOG.warn("Child to add was not valid: {}, reason {}", curKoulutusOid, e.getMessage());
                }
            }
        }
        los.setChildren(children);

        los.setStructureImage(retrieveStructureImage(los.getId()));

        return los;
    }

    private List<String> getKoulutusoidsForKomo(String komoOid) {

        List<String> koulutusOids = new ArrayList<>();

        ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> rawRes = this.tarjontaRawService.getHigherEducationByKomo(komoOid);
        HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> results = rawRes.getResult();
        for (TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> curRes : results.getTulokset()) {
            for (KoulutusHakutulosV1RDTO curKoulutus : curRes.getTulokset()) {

                koulutusOids.add(curKoulutus.getOid());

            }
        }

        return koulutusOids;
    }

    private void createEducationreReferencesForAo(ApplicationOption curAo, boolean validating) throws TarjontaParseException, KoodistoException {

        ResultV1RDTO<HakukohdeV1RDTO> hakukohdeResDTO = this.tarjontaRawService.getV1Hakukohde(curAo.getId());
        HakukohdeV1RDTO hakukohdeDTO = hakukohdeResDTO.getResult();
        for (String curEduOid : hakukohdeDTO.getHakukohdeKoulutusOids()) {

            ResultV1RDTO<KoulutusV1RDTO> koulutusRes = this.tarjontaRawService.getV1KoulutusLearningOpportunity(curEduOid);
            KoulutusKorkeakouluV1RDTO koulutusDTO = (KoulutusKorkeakouluV1RDTO) koulutusRes.getResult();
            if (koulutusDTO == null) {
                continue;
            }

            LOG.debug("KOULUTUS TILA: {}", koulutusDTO.getTila().name());

            if (!validating || koulutusDTO.getTila().name().equals(TarjontaTila.JULKAISTU.name())) {

                LOG.debug("Creating higher education los ref for higher education!!!");

                HigherEducationLOSRef losRef = creator.createHigherEducationLOSRef(koulutusDTO, curAo);

                curAo.getHigherEdLOSRefs().add(losRef);

            }

        }

    }

    private List<HigherEducationLOS> getHigherEducationRelatives(
            ResultV1RDTO<Set<String>> komoOids, LOSObjectCreator creator, boolean fullRelatives) throws TarjontaParseException, KoodistoException,
            ResourceNotFoundException {
        List<HigherEducationLOS> relatives = new ArrayList<>();
        if (komoOids == null) {
            return relatives;
        }
        for (String curKomoOid : komoOids.getResult()) {
            ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> rawRes = this.tarjontaRawService.getHigherEducationByKomo(curKomoOid);
            HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> results = rawRes.getResult();
            for (TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> curRes : results.getTulokset()) {
                for (KoulutusHakutulosV1RDTO curKoulutus : curRes.getTulokset()) {
                    ResultV1RDTO<KoulutusV1RDTO> koulutusRes = this.tarjontaRawService.getV1KoulutusLearningOpportunity(curKoulutus.getOid());
                    KoulutusKorkeakouluV1RDTO koulutusDTO = (KoulutusKorkeakouluV1RDTO) koulutusRes.getResult();
                    if (koulutusDTO == null) {
                        continue;
                    }

                    if (fullRelatives && koulutusDTO.getTila().toString().equals(TarjontaTila.JULKAISTU.toString())) {

                        try {

                            HigherEducationLOS los = creator.createHigherEducationLOS(koulutusDTO, true);
                            relatives.add(los);

                        } catch (TarjontaParseException | OrganisaatioException | NoValidApplicationOptionsException ex) {
                            LOG.info("Skipping non published higher education {}, cause: {}", koulutusDTO.getOid(), ex.getMessage());
                        }
                    } else if (!fullRelatives) {
                        HigherEducationLOS los = creator.createHigherEducationLOSReference(koulutusDTO, false);
                        relatives.add(los);
                    }
                }
            }
        }
        return relatives;
    }

    public void setCreator(LOSObjectCreator creator) {
        this.creator = creator;
    }

    @Override
    public List<Code> getEdTypeCodes() throws KoodistoException {
        return koodistoService.searchByKoodisto(TarjontaConstants.KOULUTUSTYYPPIFASETTI_KOODISTO_URI, null);
    }

    @Override
    public List<Code> getEdBaseEducationCodes() throws KoodistoException {
        return koodistoService.searchByKoodisto(TarjontaConstants.POHJAKOULUTUSFASETTI_KOODISTO_URI, null);
    }

    @Override
    public List<KoulutusLOS> findAdultUpperSecondariesAndBaseEducation() throws KoodistoException, TarjontaParseException, OrganisaatioException {

        List<KoulutusLOS> koulutukset = new ArrayList<>();

        ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> rawRes = this.tarjontaRawService.listEducationsByToteutustyyppi(
                ToteutustyyppiEnum.LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA.name(), ToteutustyyppiEnum.AIKUISTEN_PERUSOPETUS.name());
        HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> results = rawRes.getResult();
        Map<String, Set<HigherEducationLOSRef>> aoToEducationsMap = new HashMap<>();
        for (TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> curRes : results.getTulokset()) {
            for (KoulutusHakutulosV1RDTO curKoulutus : curRes.getTulokset()) {

                if (!curKoulutus.getTila().toString().equals(TarjontaTila.JULKAISTU.toString())) {
                    continue;
                }

                Koulutus2AsteV1RDTO koulutusDTO;

                if (curKoulutus.getToteutustyyppiEnum().equals(ToteutustyyppiEnum.LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA)) {
                    koulutusDTO = (Koulutus2AsteV1RDTO) this.tarjontaRawService.getV1KoulutusLearningOpportunity(curKoulutus.getOid()).getResult();
                } else {
                    koulutusDTO = this.tarjontaRawService.getAdultBaseEducationLearningOpportunity(curKoulutus.getOid()).getResult();
                }

                if (koulutusDTO == null || koulutusDTO.getKoulutuslaji() == null
                        || koulutusDTO.getKoulutuslaji().getUri().contains(TarjontaConstants.NUORTEN_KOULUTUS)) {
                    continue;
                }

                try {
                    KoulutusLOS los;
                    if (curKoulutus.getToteutustyyppiEnum().equals(ToteutustyyppiEnum.LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA)) {
                        los = creator.createAdultUpperSeconcaryLOS((KoulutusLukioV1RDTO) koulutusDTO, true);
                    } else {
                        los = creator.createAdultBaseEducationLOS((KoulutusAikuistenPerusopetusV1RDTO) koulutusDTO, true);
                    }
                    LOG.debug("Created los: {}", los.getId());
                    koulutukset.add(los);
                    updateAOLosReferences(los, aoToEducationsMap);
                    LOG.debug("Updated aolos references for: {}", los.getId());

                } catch (NoValidApplicationOptionsException ex) {
                    //Ignore
                }

            }
        }

        for (KoulutusLOS curLos : koulutukset) {
            if (curLos.getApplicationOptions() != null) {
                for (ApplicationOption ao : curLos.getApplicationOptions()) {
                    ao.setHigherEdLOSRefs(aoToEducationsMap.get(ao.getId()));
                }
            }
        }

        return koulutukset;
    }

    @Override
    public List<KoulutusLOS> findValmistavaKoulutusEducations() {

        List<KoulutusLOS> losList = new ArrayList<>();

        ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> rawRes = this.tarjontaRawService.listEducationsByToteutustyyppi(
                ToteutustyyppiEnum.AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA.name(),
                ToteutustyyppiEnum.AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA_ER.name(),
                ToteutustyyppiEnum.VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS.name(),
                ToteutustyyppiEnum.PERUSOPETUKSEN_LISAOPETUS.name(),
                ToteutustyyppiEnum.MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS.name(),
                ToteutustyyppiEnum.VAPAAN_SIVISTYSTYON_KOULUTUS.name());
        HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> results = rawRes.getResult();

        Map<String, Set<HigherEducationLOSRef>> aoToEducationsMap = new HashMap<>();

        for (TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> curRes : results.getTulokset()) {
            LOG.debug("Cur Valmistava tarjoaja result: {}", curRes.getOid());

            for (KoulutusHakutulosV1RDTO curKoulutus : curRes.getTulokset()) {
                LOG.debug("cur Valmistava koulutus result: {}", curKoulutus.getOid());
                if (!curKoulutus.getTila().toString().equals(TarjontaTila.JULKAISTU.toString())) {
                    LOG.debug("koulutus not published, discarding");
                    continue;
                }

                ResultV1RDTO<KoulutusV1RDTO> koulutusRes = this.tarjontaRawService.getV1KoulutusLearningOpportunity(curKoulutus.getOid());
                ValmistavaKoulutusV1RDTO koulutusDTO = (ValmistavaKoulutusV1RDTO) koulutusRes.getResult();
                if (koulutusDTO == null) {
                    continue;
                }
                try {
                    LOG.debug("Indexing Valmistava education: {}", koulutusDTO.getOid());
                    KoulutusLOS los;
                    los = createKoulutusLOS(koulutusDTO, true);
                    if (los != null) {
                        losList.add(los);
                        updateAOLosReferences(los, aoToEducationsMap);
                    }

                } catch (TarjontaParseException | KoodistoException | ResourceNotFoundException | OrganisaatioException ex) {
                    LOG.warn("Problem with Valmistava education: " + koulutusDTO.getOid(), ex);
                } catch (NoValidApplicationOptionsException e) {
                    LOG.debug("No valid applications for valimastava koulutus: {}, reason: {}", koulutusDTO.getOid(), e.getMessage());
                }
            }
        }

        return losList;
    }

    public List<KoulutusLOS> findPelastusalanEducations() {

        List<KoulutusLOS> losList = new ArrayList<>();

        ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> rawRes = this.tarjontaRawService.listEducationsByToteutustyyppi(
                ToteutustyyppiEnum.PELASTUSALAN_KOULUTUS.name());
        HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> results = rawRes.getResult();

        Map<String, Set<HigherEducationLOSRef>> aoToEducationsMap = new HashMap<>();

        for (TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> curRes : results.getTulokset()) {
            LOG.debug("Cur Pelastusalan tarjoaja result: {}", curRes.getOid());

            for (KoulutusHakutulosV1RDTO curKoulutus : curRes.getTulokset()) {
                LOG.debug("cur Pelastusalan koulutus result: {}", curKoulutus.getOid());
                if (!curKoulutus.getTila().toString().equals(TarjontaTila.JULKAISTU.toString())) {
                    LOG.debug("koulutus not published, discarding");
                    continue;
                }

                ResultV1RDTO<KoulutusV1RDTO> koulutusRes = this.tarjontaRawService.getV1KoulutusLearningOpportunity(curKoulutus.getOid());
                PelastusalanKoulutusV1RDTO koulutusDTO = (PelastusalanKoulutusV1RDTO) koulutusRes.getResult();
                if (koulutusDTO == null) {
                    continue;
                }
                try {
                    LOG.debug("Indexing Pelastusalan education: {}", koulutusDTO.getOid());
                    KoulutusLOS los;
                    los = createKoulutusLOS(koulutusDTO, true);
                    if (los != null) {
                        losList.add(los);
                        updateAOLosReferences(los, aoToEducationsMap);
                    }

                } catch (TarjontaParseException | KoodistoException | ResourceNotFoundException | OrganisaatioException ex) {
                    LOG.warn("Problem with Pelastusalan education: " + koulutusDTO.getOid(), ex);
                } catch (NoValidApplicationOptionsException e) {
                    LOG.debug("No valid applications for Pelastusalan koulutus: {}, reason: {}", koulutusDTO.getOid(), e.getMessage());
                }
            }
        }

        return losList;
    }

    @Override
    public List<CompetenceBasedQualificationParentLOS> findAdultVocationals() throws KoodistoException {

        List<CompetenceBasedQualificationParentLOS> koulutukset = new ArrayList<>();

        ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> rawRes = this.tarjontaRawService.listEducationsByToteutustyyppi(
                ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA.name(),
                ToteutustyyppiEnum.AMMATTITUTKINTO.name(),
                ToteutustyyppiEnum.ERIKOISAMMATTITUTKINTO.name());
        HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> results = rawRes.getResult();
        Map<String, Set<HigherEducationLOSRef>> aoToEducationsMap = new HashMap<>();

        for (TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> curRes : results.getTulokset()) {
            LOG.debug("Cur Adult Vocationals tarjoaja result: {}", curRes.getOid());
            for (KoulutusHakutulosV1RDTO curKoulutus : curRes.getTulokset()) {

                LOG.debug("cur Adult Vocationals koulutus result: {}", curKoulutus.getOid());
                if (!curKoulutus.getTila().equals(TarjontaTila.JULKAISTU)) {
                    LOG.debug("koulutus not published, discarding");
                    continue;
                }

                try {
                    CompetenceBasedQualificationParentLOS newLos = this.createCBQPLOS(curKoulutus.getOid(), true);
                    koulutukset.add(newLos);

                    updateAOLosReferences(newLos, aoToEducationsMap);
                    LOG.debug("Updated aolos references for: {}", newLos.getId());

                } catch (TarjontaParseException | ResourceNotFoundException ex) {
                    ex.printStackTrace();
                }

            }
        }

        for (CompetenceBasedQualificationParentLOS curLos : koulutukset) {
            if (curLos.getApplicationOptions() != null) {
                for (ApplicationOption ao : curLos.getApplicationOptions()) {
                    ao.setHigherEdLOSRefs(aoToEducationsMap.get(ao.getId()));
                }
            }
        }

        return koulutukset;
    }

    @Override
    public CompetenceBasedQualificationParentLOS createCBQPLOS(String oid, boolean checkStatus)
            throws TarjontaParseException, KoodistoException,
            ResourceNotFoundException {

        ResultV1RDTO<AmmattitutkintoV1RDTO> res = this.tarjontaRawService.getAdultVocationalLearningOpportunity(oid);
        NayttotutkintoV1RDTO dto = res.getResult();

        return this.creator.createCBQPLOS(dto.getKomoOid(), Collections.singletonList(oid), checkStatus);

    }

    @Override
    public KoulutusLOS createKoulutusLOS(String oid, boolean checkStatus) throws KoodistoException, TarjontaParseException, ResourceNotFoundException, NoValidApplicationOptionsException, OrganisaatioException {
        KoulutusV1RDTO dto = this.tarjontaRawService.getV1KoulutusLearningOpportunity(oid).getResult();
        return createKoulutusLOS(dto, checkStatus);
    }

    private KoulutusLOS createKoulutusLOS(KoulutusV1RDTO koulutusDTO, boolean checkStatus) throws KoodistoException, ResourceNotFoundException, TarjontaParseException, NoValidApplicationOptionsException, OrganisaatioException {
        switch (koulutusDTO.getToteutustyyppi()) {
        case KORKEAKOULUTUS:
            return creator.createHigherEducationLOS((KoulutusKorkeakouluV1RDTO) koulutusDTO, checkStatus);
        case AMMATILLINEN_PERUSTUTKINTO:
        case AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA:
            return creator.createAmmatillinenLOS((KoulutusAmmatillinenPerustutkintoV1RDTO) koulutusDTO, checkStatus);
        case AMMATILLINEN_PERUSTUTKINTO_ALK_2018:
            return creator.createAmmatillinenLOS((KoulutusAmmatillinenPerustutkintoAlk2018V1RDTO) koulutusDTO, checkStatus);
        case AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA_ER:
            return creator.createValmaErLOS((ValmistavaKoulutusV1RDTO) koulutusDTO, checkStatus);
        case AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA:
            return creator.createValmaLOS((ValmistavaKoulutusV1RDTO) koulutusDTO, checkStatus);
        case VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS:
            if (koulutusDTO.getKoulutuskoodi().getVersio() == 1) {
                return creator.createValmentavaLOS((ValmistavaKoulutusV1RDTO) koulutusDTO, checkStatus);
            } else {
                return creator.createTelmaLOS((ValmistavaKoulutusV1RDTO) koulutusDTO, checkStatus);
            }
        case PERUSOPETUKSEN_LISAOPETUS:
            return creator.createKymppiluokkaLOS((ValmistavaKoulutusV1RDTO) koulutusDTO, checkStatus);
        case MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS:
            return creator.createMMLukioonValmistavaLOS((ValmistavaKoulutusV1RDTO) koulutusDTO, checkStatus);
        case VAPAAN_SIVISTYSTYON_KOULUTUS:
            return creator.createKansanopistoLOS((ValmistavaKoulutusV1RDTO) koulutusDTO, checkStatus);
        case LUKIOKOULUTUS:
            return creator.createLukioLOS((KoulutusLukioV1RDTO) koulutusDTO, checkStatus);
        case LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA:
            return creator.createAdultUpperSeconcaryLOS((KoulutusLukioV1RDTO) koulutusDTO, checkStatus);
        case EB_RP_ISH:
            return creator.createIbRfIshLOS((KoulutusLukioV1RDTO) koulutusDTO, checkStatus);
        case KORKEAKOULUOPINTO: // Opintokokonaisuus ja opintojakso
            List<KoulutusLOS> allLoses = creator.createKorkeakouluOpintos((KorkeakouluOpintoV1RDTO) koulutusDTO, checkStatus);
            for (KoulutusLOS temp : allLoses) {
                if (temp.getId().equals(koulutusDTO.getOid())) {
                    return temp;
                }
            }
        case PELASTUSALAN_KOULUTUS:
            return creator.createPelastusalanKoulutusLOS((PelastusalanKoulutusV1RDTO) koulutusDTO, checkStatus);
        default:
            throw new NotImplementedException("No creator mapping for koulutustyyppi: " + koulutusDTO.getToteutustyyppi());
        }
    }

    @Override
    public List<CalendarApplicationSystem> findApplicationSystemsForCalendar() throws KoodistoException {

        List<CalendarApplicationSystem> results = new ArrayList<>();
        ResultV1RDTO<List<String>> hakuRes = this.tarjontaRawService.searchHakus(TarjontaConstants.HAKUTAPA_YHTEISHAKUV1);

        List<String> hakuOids = hakuRes.getResult();

        LOG.debug("Fetching: {} application systems", hakuOids.size());

        if (hakuOids != null) {
            for (String curOid : hakuOids) {
                // Demoympäristössä halutaan näyttää vain ne haut, jotka on mainittu overriddenASOids listalla
                if (overriddenASOids != null && !overriddenASOids.isEmpty() && !overriddenASOids.contains(curOid)) {
                    continue;
                }

                LOG.debug("fetching application system: {}", curOid);

                ResultV1RDTO<HakuV1RDTO> curHakuResult = this.tarjontaRawService.getV1HakuByOid(curOid);
                HakuV1RDTO curHaku = curHakuResult.getResult();

                CalendarApplicationSystem applicationSystemForCalendar = this.creator.createApplicationSystemForCalendar(curHaku, isValidCalendarHaku(curHaku));
                if (applicationSystemForCalendar != null) {
                    results.add(applicationSystemForCalendar);
                    LOG.debug("Application system created");
                }
            }
        }

        LOG.debug("Returning {} results", results.size());

        return results;
    }

    private boolean isValidCalendarHaku(HakuV1RDTO curHaku) {
        return (curHaku.getTila().equals(TarjontaConstants.STATE_PUBLISHED) || curHaku.getTila().equals(TarjontaConstants.STATE_READY))
                && (curHaku.getHakutyyppiUri().startsWith(TarjontaConstants.HAKUTYYPPI_VARSINAINEN) || curHaku.getHakutyyppiUri().startsWith(
                        TarjontaConstants.HAKUTYYPPI_LISA))
                && (curHaku.getHakutapaUri().startsWith(TarjontaConstants.HAKUTAPA_YHTEISHAKU));
    }

    @Override
    public CalendarApplicationSystem createCalendarApplicationSystem(
            String hakuOid) throws KoodistoException {
        ResultV1RDTO<HakuV1RDTO> curHakuResult = this.tarjontaRawService.getV1HakuByOid(hakuOid);
        HakuV1RDTO curHaku = curHakuResult.getResult();
        if (curHaku != null) {
            return this.creator.createApplicationSystemForCalendar(curHaku, isValidCalendarHaku(curHaku));
        }
        return null;
    }

    @Override
    public List<KoulutusHakutulosV1RDTO> findAmmatillinenKoulutusDTOs() throws TarjontaParseException, KoodistoException,
            ResourceNotFoundException {
        List<KoulutusHakutulosV1RDTO> dtoList = new ArrayList<>();

        ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> rawRes = this.tarjontaRawService.listEducationsByToteutustyyppi(
                ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO.name(),
                ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_ALK_2018.name(),
                ToteutustyyppiEnum.AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA.name());
        HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> results = rawRes.getResult();
        for (TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> curRes : results.getTulokset()) {
            for (KoulutusHakutulosV1RDTO curKoulutus : curRes.getTulokset()) {
                LOG.debug("Added ammatillinen provider " + curRes.getOid() + " education " + curKoulutus.getOid());
                dtoList.add(curKoulutus);
            }
        }
        return dtoList;
    }

    @Override
    public List<KoulutusHakutulosV1RDTO> findKoulutus(String toteutusTyyppi, String providerOid, String koulutusKoodi)
            throws TarjontaParseException, KoodistoException, ResourceNotFoundException {
        List<KoulutusHakutulosV1RDTO> dtoList = new ArrayList<>();
        ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> rawRes = this.tarjontaRawService.listEducations(toteutusTyyppi, providerOid, koulutusKoodi);
        HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> results = rawRes.getResult();
        for (TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> curRes : results.getTulokset()) {
            if (providerOid.equals(curRes.getOid())) {
                for (KoulutusHakutulosV1RDTO curKoulutus : curRes.getTulokset()) {
                    LOG.debug("Added ammatillinen provider " + curRes.getOid() + " education " + curKoulutus.getOid());
                    dtoList.add(curKoulutus);
                }
            }
        }
        return dtoList;
    }

    @Override
    public List<KoulutusHakutulosV1RDTO> findLukioKoulutusDTOs() throws TarjontaParseException, KoodistoException,
            ResourceNotFoundException {
        List<KoulutusHakutulosV1RDTO> dtoList = new ArrayList<>();

        ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> rawRes = this.tarjontaRawService.listEducationsByToteutustyyppi(
                ToteutustyyppiEnum.LUKIOKOULUTUS.name(), ToteutustyyppiEnum.EB_RP_ISH.name());
        HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> results = rawRes.getResult();

        for (TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> curRes : results.getTulokset()) {

            for (KoulutusHakutulosV1RDTO curKoulutus : curRes.getTulokset()) {
                if (!curKoulutus.getTila().toString().equals(TarjontaTila.JULKAISTU.toString())) {
                    LOG.debug("Provider " + curRes.getOid() + " education " + curKoulutus.getOid() + " education not published, discarding");
                    continue;
                }
                LOG.debug("Added lukio provider " + curRes.getOid() + " education " + curKoulutus.getOid());
                dtoList.add(curKoulutus);
            }
        }

        return dtoList;
    }

    @Override
    public List<KoulutusLOS> createAmmatillinenKoulutusLOS(KoulutusHakutulosV1RDTO koulutusDTO) {
        try {

            ArrayList<KoulutusLOS> losses = new ArrayList<>();
            if (hasAlreadyProcessedOid(koulutusDTO.getOid())) {
                return losses;
            }
            String parentoid = koulutusDTO.getParentKomoOid() != null ? koulutusDTO.getParentKomoOid() : koulutusDTO.getKomoOid();
            String providerOid = koulutusDTO.getTarjoajat().iterator().next();
            KoulutusLOS koulutus = creator.createAmmatillinenLOS(koulutusDTO.getOid(), true);
            if (koulutus == null) {
                return losses;
            }
            String tutkintokey = Joiner.on("_").join(parentoid, providerOid, koulutus.getStartYear(), koulutus.getStartSeason().get("fi"));
            if(koulutus.getKoulutusPrerequisite() != null){
                tutkintokey = tutkintokey + "_" + koulutus.getKoulutusPrerequisite().getValue();
            } else { // Ammatillinen perustutkinto alk 2018
                tutkintokey = tutkintokey + "_UUSI";
            }
            TutkintoLOS tutkinto = getAlreadyProcessedTutkinto(tutkintokey);
            if (tutkinto == null) {
                tutkinto = creator.createTutkintoLOS(parentoid, providerOid, "" + koulutus.getStartYear(), koulutus.getStartSeason().get("fi"), koulutus.getKoulutusPrerequisite(), koulutus.getKoulutuskoodi());
            }
            if (koulutus.isOsaamisalaton()) {
                koulutus.setSiblings(new ArrayList<KoulutusLOS>());
                koulutus.setTutkinto(null);
                koulutus.setGoals(tutkinto.getGoals());
                return new ArrayList<>(Collections.singletonList(koulutus));
            }

            List<String> siblings = koulutusDTO.getSiblingKomotos();
            if (siblings != null) {
                for (String oid : siblings) {
                    try {
                        KoulutusLOS siblingLos = creator.createAmmatillinenLOS(oid, true);
                        if (siblingLos != null) {
                            losses.add(siblingLos);
                        } else {
                            addProcessedOid(oid);
                        }
                    // Invalid sibling should be skipped
                    } catch (KoodistoException | TarjontaParseException | OrganisaatioException e) {
                        addProcessedOid(oid);
                        LOG.warn("Vocational sibling " + oid + " was not valid: " + e.getMessage());
                    } catch (NoValidApplicationOptionsException e) {
                        addProcessedOid(oid);
                        LOG.debug("Vocational sibling " + oid + " was not valid: " + e.getMessage());
                    }
                }
            }
            losses.add(koulutus);

            for (KoulutusLOS los : losses) {
                tutkinto.getChildEducations().add(los);
                tutkinto.getTeachingLanguages().addAll(los.getTeachingLanguages());
                tutkinto.getApplicationOptions().addAll(los.getApplicationOptions());
            }

            for (KoulutusLOS los : losses) {
                los.setSiblings(losses);
                los.setTutkinto(tutkinto);
                addProcessedOid(los.getId());
            }

            for (ApplicationOption ao : tutkinto.getApplicationOptions()) {
                ao.setChildLOIRefs(new ArrayList<ChildLOIRef>());
                for (KoulutusLOS los : tutkinto.getChildEducations()) {
                    if (!ao.getKomotoOids().contains(los.getId())) {
                        continue;
                    }

                    ChildLOIRef childLoi = new ChildLOIRef();
                    childLoi.setId(los.getId());
                    childLoi.setPrerequisite(los.getKoulutusPrerequisite());
                    childLoi.setName(los.getName());
                    if (los.getQualifications() != null && !los.getQualifications().isEmpty()) {
                        childLoi.setQualification(los.getQualifications().iterator().next());
                    }
                    childLoi.setQualifications(los.getQualifications());

                    ao.getChildLOIRefs().add(childLoi);
                }
            }

            addProcessedTutkinto(tutkinto);
            return losses;
        } catch (KoodistoException | TarjontaParseException | OrganisaatioException e) {
            LOG.warn("Failed to create vocational education " + koulutusDTO.getOid(), e);
            addProcessedOid(koulutusDTO.getOid());
            return new ArrayList<>();
        } catch (NoValidApplicationOptionsException e) {
            LOG.debug("Failed to create vocational education " + koulutusDTO.getOid() + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public KoulutusLOS createLukioKoulutusLOS(KoulutusHakutulosV1RDTO koulutusDTO) {
        try {
            return creator.createLukioLOS(koulutusDTO.getOid(), true);
        } catch (KoodistoException | TarjontaParseException | OrganisaatioException e) {
            LOG.warn("Failed to create lukio education " + koulutusDTO.getOid(), e);
            return null;
        } catch (NoValidApplicationOptionsException e) {
            LOG.debug("Failed to create lukio education " + koulutusDTO.getOid() + ": " + e.getMessage());
            return null;
        }
    }

    @Override
    public void clearProcessedLists() {
        processedOids = new HashSet<>();
        processedTutkintos = new HashMap<>();
        if (creator != null) {
            creator.clearProcessedLists();
        }
    }

    @Override
    public boolean hasAlreadyProcessedOid(String oid) {
        return processedOids.contains(oid);
    }

    @Override
    public void addProcessedOid(String komoOid) {
        processedOids.add(komoOid);
    }

    public TutkintoLOS getAlreadyProcessedTutkinto(String oid) {
        return processedTutkintos.get(oid);
    }

    public void addProcessedTutkinto(TutkintoLOS tutkinto) {
        processedTutkintos.put(tutkinto.getId(), tutkinto);
    }

    @Override
    public Set<String> findKoulutusOidsByHaku(String asOid) {
        ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> rawRes = tarjontaRawService.getV1KoulutusByAsId(asOid);
        if (rawRes == null
                || rawRes.getResult() == null
                || rawRes.getResult().getTulokset() == null
                || rawRes.getResult().getTulokset().isEmpty()) {
            return Sets.newHashSet();
        }
        return FluentIterable.from(rawRes.getResult().getTulokset())
                .transformAndConcat(new Function<TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO>, Set<String>>() {
                    public Set<String> apply(TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> input) {
                        return FluentIterable.from(input.getTulokset())
                                .transform(new Function<KoulutusHakutulosV1RDTO, String>() {
                                    public String apply(KoulutusHakutulosV1RDTO input) {
                                        return input.getOid();
                                    }
                                }).toSet();
                    }
                }).toSet();
    }

    @Override
    public Set<String> findKoulutusOidsByAo(String aoOid) {
        ResultV1RDTO<List<NimiJaOidRDTO>> rawRes = tarjontaRawService.getV1KoulutusByAoId(aoOid);
        if (rawRes == null
                || rawRes.getResult() == null
                || rawRes.getResult().isEmpty()) {
            return Sets.newHashSet();
        }
        return FluentIterable.from(rawRes.getResult())
                .transform(new Function<NimiJaOidRDTO, String>() {
                    @Override
                    public String apply(NimiJaOidRDTO input) {
                        return input.getOid();
                    }
                }).toSet();
    }

    @Override
    public List<KoulutusLOS> createKorkeakouluopinto(KoulutusHakutulosV1RDTO dto) throws KoodistoException, TarjontaParseException, OrganisaatioException, NoValidApplicationOptionsException {
        ResultV1RDTO<KoulutusV1RDTO> koulutusRes = this.tarjontaRawService.getV1KoulutusLearningOpportunity(dto.getOid());
        KorkeakouluOpintoV1RDTO koulutusDTO = (KorkeakouluOpintoV1RDTO) koulutusRes.getResult();
        List<KoulutusLOS> result = creator.createKorkeakouluOpintos(koulutusDTO, true);
        for (KoulutusLOS los : result) {
            addProcessedOid(los.getId());
        }
        return result;
    }
}
