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
import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.*;
import fi.vm.sade.koulutusinformaatio.service.builder.LearningOpportunityBuilder;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.*;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.AmmattitutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusLukioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NayttotutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import javax.ws.rs.WebApplicationException;

import java.util.*;

/**
 * @author Hannu Lyytikainen
 */
@Service
@Profile("default")
public class TarjontaServiceImpl implements TarjontaService {

    public static final Logger LOG = LoggerFactory.getLogger(TarjontaServiceImpl.class);

    private ConversionService conversionService;
    private OrganisaatioRawService organisaatioRawService;
    private KoodistoService koodistoService;
    private ProviderService providerService;
    private LearningOpportunityDirector loDirector;
    private TarjontaRawService tarjontaRawService;
    private LOSObjectCreator creator;

    private static final String ED_TYPE_FACET_KOODISTO = "koulutustyyppifasetti";

    @Autowired
    public TarjontaServiceImpl(ConversionService conversionService, KoodistoService koodistoService,
            ProviderService providerService, LearningOpportunityDirector loDirector,
            TarjontaRawService tarjontaRawService, OrganisaatioRawService organisaatioRawService) {
        this.koodistoService = koodistoService;
        this.providerService = providerService;
        this.loDirector = loDirector;
        this.tarjontaRawService = tarjontaRawService;
        this.conversionService = conversionService;
        this.organisaatioRawService = organisaatioRawService;
    }

    public TarjontaServiceImpl() {
    }

    @Override
    public List<LOS> findParentLearningOpportunity(String oid) throws TarjontaParseException {
        try {
            KomoDTO komo = tarjontaRawService.getKomo(oid);
            LearningOpportunityBuilder builder = resolveBuilder(komo);
            return loDirector.constructLearningOpportunities(builder);

        } catch (KoodistoException e) {
            throw new TarjontaParseException("An error occurred while building parent LOS " + oid + " with koodisto: " + e.getMessage());
        }
        catch (WebApplicationException e) {
            throw new TarjontaParseException("An error occurred while building parent LOS " + oid
                    + " accessing remote resource: HTTP response code: "
                    + e.getResponse().getStatus() + ",  error message: " + e.getMessage());
        }
    }

    private LearningOpportunityBuilder resolveBuilder(KomoDTO komo) throws KoodistoException, TarjontaParseException {
        String educationType = komo.getKoulutusTyyppiUri();
        if (educationType.equals(TarjontaConstants.VOCATIONAL_EDUCATION_TYPE) &&
                komo.getModuuliTyyppi().equals(TarjontaConstants.MODULE_TYPE_PARENT)) {
            return new VocationalLearningOpportunityBuilder(
                    tarjontaRawService, providerService, koodistoService, komo, organisaatioRawService);
        }
        else if (educationType.equals(TarjontaConstants.UPPER_SECONDARY_EDUCATION_TYPE) &&
                komo.getModuuliTyyppi().equals(TarjontaConstants.MODULE_TYPE_CHILD)) {
            return new UpperSecondaryLearningOpportunityBuilder(
                    tarjontaRawService, providerService, koodistoService, komo, organisaatioRawService);
        }
        else if (educationType.equals(TarjontaConstants.REHABILITATING_EDUCATION_TYPE) &&
                komo.getModuuliTyyppi().equals(TarjontaConstants.MODULE_TYPE_CHILD)) {
            return new RehabilitatingLearningOpportunityBuilder(tarjontaRawService, providerService, koodistoService,
                    komo, organisaatioRawService);
        } 
        else if ((educationType.equals(TarjontaConstants.PREPARATORY_VOCATIONAL_EDUCATION_TYPE) 
                || educationType.equals(TarjontaConstants.TENTH_GRADE_EDUCATION_TYPE)
                || educationType.equals(TarjontaConstants.IMMIGRANT_PREPARATORY_VOCATIONAL)
                || educationType.equals(TarjontaConstants.IMMIGRANT_PREPARATORY_UPSEC)
                || educationType.endsWith(TarjontaConstants.KANSANOPISTO_TYPE))
                && komo.getModuuliTyyppi().equals(TarjontaConstants.MODULE_TYPE_CHILD)) {
            return new RehabilitatingLearningOpportunityBuilder(tarjontaRawService, providerService, koodistoService,
                    komo, organisaatioRawService);
        }
        else {
            throw new TarjontaParseException(String.format("Unknown education degree %s and module type %s incompatible",
                    educationType, komo.getModuuliTyyppi()));
        }
    }

    @Override
    public List<String> listParentLearnignOpportunityOids() {
        return listParentLearnignOpportunityOids(Integer.MAX_VALUE, 0);
    }

    @Override
    public List<String> listParentLearnignOpportunityOids(int count, int startIndex) {
        List<OidRDTO> oids = tarjontaRawService.listParentLearnignOpportunityOids(count, startIndex);
        return Lists.transform(oids, new Function<OidRDTO, String>() {
            @Override
            public String apply(OidRDTO input) {
                return conversionService.convert(input, String.class);
            }
        });
    }

    @Override
    public List<HigherEducationLOS> findHigherEducations() throws KoodistoException, ResourceNotFoundException {

        if (creator == null) {
            creator = new LOSObjectCreator(koodistoService, tarjontaRawService, providerService, organisaatioRawService);
        }

        //List of all properly published higher education learning objects, regardles of position in hierarchy
        List<HigherEducationLOS> koulutukset = new ArrayList<HigherEducationLOS>();

        //A map containing komo oid as key and higher education lo as value. This is used in lo-hierarchy creation, because 
        //hierarchy relationships are retrieved based on komos.
        Map<String,List<HigherEducationLOS>> komoToLOSMap = new HashMap<String,List<HigherEducationLOS>>();

        //Komo-oids of parent level learning opportunities. 
        List<String> parentOids = new ArrayList<String>();

        ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> rawRes = this.tarjontaRawService.listEducations(TarjontaConstants.HIGHER_EDUCATION_TYPE);//listHigherEducation();
        HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> results = rawRes.getResult();
        Map<String,List<HigherEducationLOSRef>> aoToEducationsMap = new HashMap<String,List<HigherEducationLOSRef>>();
        for (TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> curRes : results.getTulokset()) {
            for (KoulutusHakutulosV1RDTO curKoulutus : curRes.getTulokset()) {
                if (!curKoulutus.getTila().toString().equals(TarjontaTila.JULKAISTU.toString())) {
                    continue;
                }
                ResultV1RDTO<KoulutusKorkeakouluV1RDTO> koulutusRes = this.tarjontaRawService.getHigherEducationLearningOpportunity(curKoulutus.getOid());
                KoulutusKorkeakouluV1RDTO koulutusDTO = koulutusRes.getResult();
                if (koulutusDTO == null) {
                    continue;
                }
                try {
                    HigherEducationLOS los = creator.createHigherEducationLOS(koulutusDTO, true);
                    los.setStructureImage(retrieveStructureImage(curKoulutus.getOid()));
                    koulutukset.add(los);
                    List<HigherEducationLOS> loss = komoToLOSMap.get(koulutusDTO.getKomoOid());
                    if (loss == null) {
                        loss = new ArrayList<HigherEducationLOS>();
                        loss.add(los);
                        komoToLOSMap.put(koulutusDTO.getKomoOid(), loss);
                    } else {
                        loss.add(los);
                    }
                    parentOids.add(los.getKomoOid());
                    updateAOLosReferences(los, aoToEducationsMap);

                } catch (TarjontaParseException ex) {
                    continue;
                }

            }
        }

        return createChildHierarchy(koulutukset, komoToLOSMap, parentOids, aoToEducationsMap);
    }

    private I18nPicture retrieveStructureImage(String oid) throws KoodistoException {
        I18nPicture structureImage = null;
        ResultV1RDTO<List<KuvaV1RDTO>> result = this.tarjontaRawService.getStructureImages(oid);
        List<KuvaV1RDTO> imageDtos = result != null ? result.getResult() : null;

        if (imageDtos != null && !imageDtos.isEmpty()) {
            structureImage = new I18nPicture();
            for (KuvaV1RDTO curDto : imageDtos) {
                String kielikoodi =  this.koodistoService.searchFirstCodeValue(curDto.getKieliUri());
                Picture pict = new Picture();
                pict.setId(String.format("%s_%s", oid, kielikoodi.toLowerCase()));
                pict.setPictureEncoded(curDto.getBase64data());
                structureImage.getPictureTranslations().put(kielikoodi.toLowerCase(), pict);
            }
        }
        return structureImage;
    }

    private void updateAOLosReferences(StandaloneLOS los,
            Map<String, List<HigherEducationLOSRef>> aoToEducationsMap) {
        if (los.getApplicationOptions() != null) {
            for (ApplicationOption curAo : los.getApplicationOptions()) {
                LOG.debug("Updating ao los references for ao: " + curAo.getId());
                List<HigherEducationLOSRef> aoLoss = aoToEducationsMap.get(curAo.getId());
                if (aoLoss == null) {
                    aoLoss = new ArrayList<HigherEducationLOSRef>();
                    aoToEducationsMap.put(curAo.getId(), aoLoss);
                }

                HigherEducationLOSRef newRef = new HigherEducationLOSRef();
                newRef.setId(los.getId());
                newRef.setName(los.getName());
                newRef.setPrerequisite(curAo.getPrerequisite());
                newRef.setQualifications(((StandaloneLOS)los).getQualifications());
                newRef.setProvider(curAo.getProvider().getName());
                aoLoss.add(newRef);
                aoToEducationsMap.put(curAo.getId(), aoLoss);
            }
        }
        LOG.debug("ao los references now updated");

    }

    /*
     * Creating the learning opportunity hierarchy for higher education
     */
    private List<HigherEducationLOS> createChildHierarchy(List<HigherEducationLOS> koulutukset,
            Map<String, List<HigherEducationLOS>> komoToLOSMap, List<String> parentOids, Map<String,List<HigherEducationLOSRef>> aoToEducationsMap) {

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

        List<HigherEducationLOS> parents = new ArrayList<HigherEducationLOS>();
        for (String curParent : parentOids) {
            parents.addAll(komoToLOSMap.get(curParent));
        }
        return parents;
    }

    @Override
    public HigherEducationLOS findHigherEducationLearningOpportunity(String oid) throws TarjontaParseException, KoodistoException, ResourceNotFoundException {
        if (this.providerService != null) {
            this.providerService.clearCache();
        }
        if (creator == null) {
            creator = new LOSObjectCreator(koodistoService, tarjontaRawService, providerService, organisaatioRawService);
        }
        ResultV1RDTO<KoulutusKorkeakouluV1RDTO> koulutusRes = this.tarjontaRawService.getHigherEducationLearningOpportunity(oid);
        KoulutusKorkeakouluV1RDTO koulutusDTO = koulutusRes.getResult();
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
    public HigherEducationLOS createHigherEducationLearningOpportunityTree(String oid) throws TarjontaParseException, KoodistoException, ResourceNotFoundException {
        if (this.providerService != null) {
            this.providerService.clearCache();
        }
        if (creator == null) {
            creator = new LOSObjectCreator(koodistoService, tarjontaRawService, providerService, organisaatioRawService);
        }
        ResultV1RDTO<KoulutusKorkeakouluV1RDTO> koulutusRes = this.tarjontaRawService.getHigherEducationLearningOpportunity(oid);
        KoulutusKorkeakouluV1RDTO koulutusDTO = koulutusRes.getResult();
        LOG.debug(" Koulutustila: " + koulutusDTO.getTila().toString());
        if (koulutusDTO == null || !koulutusDTO.getTila().toString().equals(TarjontaTila.JULKAISTU.toString())) {
            LOG.debug("Returning null ");
            return null;
        }

        LOG.debug("Now creating higherEducation learning opportunity tree");
        HigherEducationLOS los = creator.createHigherEducationLOS(koulutusDTO, true);
        los.setStatus(koulutusDTO.getTila().toString());


        if (los.getApplicationOptions() != null) {
            LOG.debug("now creating higher edu los refs for los: " + los.getId());
            for (ApplicationOption curAo : los.getApplicationOptions()) {
                createEducationreReferencesForAo(curAo, true);
            }
        }

        ResultV1RDTO<Set<String>> childKomoOids = this.tarjontaRawService.getChildrenOfParentHigherEducationLOS(koulutusDTO.getKomoOid());

        List<HigherEducationLOS> children = new ArrayList<HigherEducationLOS>();

        for (String curChildKomoOid : childKomoOids.getResult()) {
            List<String> koulutusOids = getKoulutusoidsForKomo(curChildKomoOid);
            for (String curKoulutusOid : koulutusOids) {
                try {
                    HigherEducationLOS curChild = createHigherEducationLearningOpportunityTree(curKoulutusOid);
                    if (curChild != null) {
                        curChild.getParents().add(los);
                        children.add(curChild);
                    }
                } catch (TarjontaParseException tpe) {
                    LOG.warn("Child to add was not valid: " + curKoulutusOid);
                }
            }
        }
        los.setChildren(children);

        los.setStructureImage(retrieveStructureImage(los.getId()));

        return los;
    }

    private List<String> getKoulutusoidsForKomo(String komoOid) {

        List<String> koulutusOids = new ArrayList<String>();

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

        ResultV1RDTO<HakukohdeV1RDTO> hakukohdeResDTO =  this.tarjontaRawService.getV1EducationHakukohode(curAo.getId());
        HakukohdeV1RDTO hakukohdeDTO = hakukohdeResDTO.getResult();
        for (String curEduOid : hakukohdeDTO.getHakukohdeKoulutusOids()) {

            ResultV1RDTO<KoulutusKorkeakouluV1RDTO> koulutusRes = this.tarjontaRawService.getHigherEducationLearningOpportunity(curEduOid);
            KoulutusKorkeakouluV1RDTO koulutusDTO = koulutusRes.getResult();
            if (koulutusDTO == null) {
                continue;
            }

            LOG.debug("KOULUTUS TILA: " + koulutusDTO.getTila().name());

            if ((validating && koulutusDTO.getTila().name().equals(TarjontaTila.JULKAISTU.name())) || !validating) {

                LOG.debug("Creating higher education los ref for higher education!!!");

                HigherEducationLOSRef losRef = creator.createHigherEducationLOSRef(koulutusDTO, validating, curAo);

                curAo.getHigherEdLOSRefs().add(losRef);

            } 

        }

    }

    private List<HigherEducationLOS> getHigherEducationRelatives(
            ResultV1RDTO<Set<String>> komoOids, LOSObjectCreator creator, boolean fullRelatives) throws TarjontaParseException, KoodistoException, ResourceNotFoundException {
        List<HigherEducationLOS> relatives = new ArrayList<HigherEducationLOS>();
        if (komoOids == null) {
            return relatives;
        }
        for (String curKomoOid : komoOids.getResult()) {
            ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> rawRes = this.tarjontaRawService.getHigherEducationByKomo(curKomoOid);
            HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> results = rawRes.getResult();
            for (TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> curRes : results.getTulokset()) {
                for (KoulutusHakutulosV1RDTO curKoulutus : curRes.getTulokset()) {
                    ResultV1RDTO<KoulutusKorkeakouluV1RDTO> koulutusRes = this.tarjontaRawService.getHigherEducationLearningOpportunity(curKoulutus.getOid());
                    KoulutusKorkeakouluV1RDTO koulutusDTO = koulutusRes.getResult();
                    if (koulutusDTO == null) {
                        continue;
                    }

                    if (fullRelatives && koulutusDTO.getTila().equals(TarjontaTila.JULKAISTU)) {

                        try {

                            HigherEducationLOS los = creator.createHigherEducationLOS(koulutusDTO, true);
                            relatives.add(los);

                        } catch (TarjontaParseException ex) {
                            LOG.warn("Skipping non published higher education");
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


    public LOSObjectCreator getCreator() {
        return creator;
    }

    public void setCreator(LOSObjectCreator creator) {
        this.creator = creator;
    }

    @Override
    public List<Code> getEdTypeCodes() throws KoodistoException {
        return koodistoService.searchByKoodisto(ED_TYPE_FACET_KOODISTO, null);
    }

    @Override
    public List<AdultUpperSecondaryLOS> findAdultUpperSecondaries() throws KoodistoException {

        if (creator == null) {
            creator = new LOSObjectCreator(koodistoService, tarjontaRawService, providerService, organisaatioRawService);
        }

        List<AdultUpperSecondaryLOS> koulutukset = new ArrayList<AdultUpperSecondaryLOS>();

        ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> rawRes =  this.tarjontaRawService.listEducationsByToteutustyyppi(ToteutustyyppiEnum.LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA.name());//listEducations(TarjontaConstants.UPPER_SECONDARY_EDUCATION_TYPE);
        HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> results = rawRes.getResult();
        Map<String,List<HigherEducationLOSRef>> aoToEducationsMap = new HashMap<String,List<HigherEducationLOSRef>>();
        for (TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> curRes : results.getTulokset()) {
            for (KoulutusHakutulosV1RDTO curKoulutus : curRes.getTulokset()) {

                if (!curKoulutus.getTila().toString().equals(TarjontaTila.JULKAISTU.toString())) {
                    continue;
                }

                ResultV1RDTO<KoulutusLukioV1RDTO> koulutusRes = this.tarjontaRawService.getUpperSecondaryLearningOpportunity(curKoulutus.getOid());
                KoulutusLukioV1RDTO koulutusDTO = koulutusRes.getResult();

                LOG.debug("cur upsec adult education dto: " + koulutusDTO.getOid());
                if (koulutusDTO == null || koulutusDTO.getKoulutuslaji() == null || koulutusDTO.getKoulutuslaji().getUri().contains(TarjontaConstants.NUORTEN_KOULUTUS)) {
                    continue;
                }

                try {
                    AdultUpperSecondaryLOS los = creator.createAdultUpperSeconcaryLOS(koulutusDTO, true);//createHigherEducationLOS(koulutusDTO, true);
                    LOG.debug("Created los: " + los.getId());
                    koulutukset.add(los);
                    updateAOLosReferences(los, aoToEducationsMap);
                    LOG.debug("Updated aolos references for: " + los.getId());

                } catch (TarjontaParseException ex) {
                    continue;
                }

            }
        }

        for (AdultUpperSecondaryLOS curLos : koulutukset) {
            if (curLos.getApplicationOptions() != null) {
                for (ApplicationOption ao : curLos.getApplicationOptions()) {
                    ao.setHigherEdLOSRefs(aoToEducationsMap.get(ao.getId()));
                }
            }
        }

        return koulutukset;
    }


    @Override
    public List<CompetenceBasedQualificationParentLOS> findAdultVocationals()
            throws KoodistoException {

        if (creator == null) {
            creator = new LOSObjectCreator(koodistoService, tarjontaRawService, providerService, organisaatioRawService);
        }

        List<CompetenceBasedQualificationParentLOS> koulutukset = new ArrayList<CompetenceBasedQualificationParentLOS>();

        ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> rawRes =  this.tarjontaRawService.listEducationsByToteutustyyppi(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA.name());// //AMMATTITUTKINTO.name());//listEducations(TarjontaConstants.UPPER_SECONDARY_EDUCATION_TYPE);
        HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> results = rawRes.getResult();
        /*Map<String,List<HigherEducationLOSRef>> aoToEducationsMap = new HashMap<String,List<HigherEducationLOSRef>>();

        Map<String,List<String>> parentChildKomos = new HashMap<String,List<String>>();
        Map<String,List<String>> komoToKomotoMap = new HashMap<String,List<String>>();*/ 

        List<String> createdOids = new ArrayList<String>();

        for (TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> curRes : results.getTulokset()) {
            LOG.debug("Cur Adult Vocationals tarjoaja result: " + curRes.getOid());
            for (KoulutusHakutulosV1RDTO curKoulutus : curRes.getTulokset()) {

                LOG.debug("cur Adult Vocationals koulutus result: " + curKoulutus.getOid());
                if (!curKoulutus.getTila().toString().equals(TarjontaTila.JULKAISTU.toString())) {
                    LOG.debug("koulutus not published, discarding");
                    continue;
                }

                if (!createdOids.contains(curKoulutus.getOid())) {
                    try {
                        CompetenceBasedQualificationParentLOS newLos = this.createCBQPLOS(curKoulutus.getOid(), createdOids, true);
                        koulutukset.add(newLos);
                    } catch (TarjontaParseException ex) {
                        ex.printStackTrace();
                    } catch (ResourceNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    if (!createdOids.contains(curKoulutus.getOid())) {
                        createdOids.add(curKoulutus.getOid());
                    }
                }

            }
        }

        return koulutukset;
    }

    @Override
    public AdultUpperSecondaryLOS createAdultUpperSecondaryLOS(String oid, boolean checkStatus)
            throws TarjontaParseException, KoodistoException,
            ResourceNotFoundException {

        if (creator == null) {
            creator = new LOSObjectCreator(koodistoService, tarjontaRawService, providerService, organisaatioRawService);
        }

        ResultV1RDTO<KoulutusLukioV1RDTO> koulutusRes = this.tarjontaRawService.getUpperSecondaryLearningOpportunity(oid);
        KoulutusLukioV1RDTO koulutusDTO = koulutusRes.getResult();

        LOG.debug("cur upsec adult education dto: " + koulutusDTO.getOid());
        if (koulutusDTO == null || koulutusDTO.getKoulutuslaji() == null || koulutusDTO.getKoulutuslaji().getUri().contains(TarjontaConstants.NUORTEN_KOULUTUS)) {
            LOG.debug("Koulutus is not adult upper secondary");
            throw new TarjontaParseException("Koulutus is not adult upper secondary");
        }
        if (checkStatus && !(TarjontaTila.JULKAISTU.toString().equals(koulutusDTO.getTila().toString()))) {
            throw new TarjontaParseException("Koulutus: "  +  oid + " is not published");
        }

        try {
            AdultUpperSecondaryLOS los = creator.createAdultUpperSeconcaryLOS(koulutusDTO, checkStatus);
            los.setStatus(koulutusDTO.getTila().toString());
            LOG.debug("Created los: " + los.getId());
            LOG.debug("Updated aolos references for: " + los.getId());
            return los;

        } catch (TarjontaParseException ex) {
            LOG.debug(ex.getMessage());
            throw ex;
        }

    }

    @Override
    public CompetenceBasedQualificationParentLOS createCBQPLOS(String oid, List<String> createdOids, boolean checkStatus)
            throws TarjontaParseException, KoodistoException,
            ResourceNotFoundException {

        if (creator == null) {
            creator = new LOSObjectCreator(koodistoService, tarjontaRawService, providerService, organisaatioRawService);
        }
        //String providerOid = null;
        //String parentKomoOid = null;
        //List<String> komoOids = new ArrayList<String>();
        /*int splitIndex = oid.indexOf('_');
        if (splitIndex > -1) {
            parentKomoOid = oid.substring(0, splitIndex);
            providerOid = oid.substring(splitIndex + 1);

            ResultV1RDTO<Set<String>> childRes = this.tarjontaRawService.getChildrenOfParentHigherEducationLOS(parentKomoOid);
            if (childRes != null && childRes.getResult() != null) {
                komoOids.addAll(new ArrayList<String>(childRes.getResult()));
            }
            komoOids.add(parentKomoOid);

        } else {*/

            ResultV1RDTO<AmmattitutkintoV1RDTO> res = this.tarjontaRawService.getAdultVocationalLearningOpportunity(oid);
            NayttotutkintoV1RDTO dto = res.getResult();




            //parentKomoOid = dto.getKomoOid();
            //providerOid = dto.getOrganisaatio().getOid();
            //komoOids.add(parentKomoOid);

            /*if (dto.getKoulutusmoduuliTyyppi().name().equals(KoulutusmoduuliTyyppi.TUTKINTO.name())) {
                if (!createdOids.contains(oid)) {
                    createdOids.add(oid);
                }*/
                
                return this.creator.createCBQPLOS(dto.getKomoOid(), Arrays.asList(oid), checkStatus);
                
            /*} else {


                ResultV1RDTO<Set<String>> parentsRes = this.tarjontaRawService.getParentsOfHigherEducationLOS(dto.getKomoOid());

                if (parentsRes != null && parentsRes.getResult() != null && !parentsRes.getResult().isEmpty()) {


                    for (String curKomoOid : parentsRes.getResult()) {


                        parentKomoOid = curKomoOid;
                        ResultV1RDTO<Set<String>> childRes = this.tarjontaRawService.getChildrenOfParentHigherEducationLOS(curKomoOid);
                        if (childRes != null && childRes.getResult() != null) {
                            komoOids.addAll(new ArrayList<String>(childRes.getResult()));
                        }
                    }
                }
            }
        }


        List<String> komotoOids  = new ArrayList<String>();

        for (String curKomoOid : komoOids) {
            LOG.debug("CurKomoOid: " + curKomoOid + "\n");
            ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> curRes = this.tarjontaRawService.getAdultEducationByKomo(curKomoOid);
            if (curRes != null 
                    && curRes.getResult() != null 
                    && curRes.getResult().getTulokset() != null 
                    && !curRes.getResult().getTulokset().isEmpty()) {
                LOG.debug("There is some komotoresult");

                for (TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> curTarjRes : curRes.getResult().getTulokset()) {
                    
                    if (curTarjRes.getOid().equals(providerOid)
                            && curTarjRes.getTulokset() != null
                            && !curTarjRes.getTulokset().isEmpty()) {

                        for ( KoulutusHakutulosV1RDTO curKoul : curTarjRes.getTulokset()) {


                            LOG.debug("There is a koulutus result");
                            
                            if (!komotoOids.contains(curKoul.getOid())) {
                                komotoOids.add(curKoul.getOid());
                            }
                            LOG.debug(curKoul.getOid());
                            if (!createdOids.contains(curKoul.getOid())) {
                                createdOids.add(curKoul.getOid());
                            }
                        }
                    }
                }
            }
        }
        LOG.debug("data gathewred, now creating Adult vocational stuff");

        LOG.debug("komotoOids: " + komotoOids.size());

        return this.creator.createCBQPLOS(parentKomoOid, komotoOids, checkStatus);*/
    }
}
