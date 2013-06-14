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

package fi.vm.sade.koulutusinformaatio.service.impl.builder;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.tarjonta.service.resources.HakukohdeResource;
import fi.vm.sade.tarjonta.service.resources.KomoResource;
import fi.vm.sade.tarjonta.service.resources.KomotoResource;
import fi.vm.sade.tarjonta.service.resources.dto.*;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.WebApplicationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Builds learning opportunity instances.
 *
 * @author Hannu Lyytikainen
 */
public class LOBuilder {

    public static final Logger LOG = LoggerFactory.getLogger(LOBuilder.class);

    public static final String MODULE_TYPE_PARENT = "TUTKINTO";
    public static final String MODULE_TYPE_CHILD = "TUTKINTO_OHJELMA";
    public static final String STATE_PUBLISHED = "JULKAISTU";
    public static final String STATE_READY = "VALMIS";

    private KomoResource komoResource;
    private KomotoResource komotoResource;
    private HakukohdeResource hakukohdeResource;
    private ProviderService providerService;
    //private ConversionService conversionService;

    @Autowired
    private KoodistoService koodistoService;

    public LOBuilder(KomoResource komoResource, KomotoResource komotoResource, HakukohdeResource hakukohdeResource,
                     ProviderService providerService) {
        this.komoResource = komoResource;
        this.komotoResource = komotoResource;
        this.hakukohdeResource = hakukohdeResource;
        this.providerService = providerService;
    }

    public ParentLOS buildParentLOS(String oid) throws TarjontaParseException, KoodistoException, WebApplicationException {
        ParentLOS parentLOS = new ParentLOS();
        KomoDTO parentKomo = komoResource.getByOID(oid);

        validateParentKomo(parentKomo);

        // parent info
        parentLOS.setId(parentKomo.getOid());
        parentLOS.setName(koodistoService.searchFirst(parentKomo.getKoulutusKoodiUri()));
        parentLOS.setStructureDiagram(getI18nText(parentKomo.getKoulutuksenRakenne()));
        parentLOS.setAccessToFurtherStudies(getI18nText(parentKomo.getJatkoOpintoMahdollisuudet()));
        parentLOS.setGoals(getI18nText(parentKomo.getTavoitteet()));
        parentLOS.setEducationDomain(koodistoService.searchFirst(parentKomo.getKoulutusAlaUri()));
        parentLOS.setStydyDomain(koodistoService.searchFirst(parentKomo.getOpintoalaUri()));
        parentLOS.setEducationDegree(koodistoService.searchFirstCodeValue(parentKomo.getKoulutusAsteUri()));

        List<OidRDTO> parentKomotoOids = komoResource.getKomotosByKomoOID(parentKomo.getOid(), Integer.MAX_VALUE, 0);
        if (parentKomotoOids == null || parentKomotoOids.size() == 0) {
            throw new TarjontaParseException("No instances found in parent LOS " + parentKomo.getOid());
        }

        // parent loi + provider
        List<ParentLOI> parentLOIs = Lists.newArrayList();
        for (OidRDTO parentKomotoOid : parentKomotoOids) {

            KomotoDTO parentKomoto = komotoResource.getByOID(parentKomotoOid.getOid());
            try {
                validateParentKomoto(parentKomoto);
            } catch (TarjontaParseException e) {
                continue;
            }

            ParentLOI parentLOI = new ParentLOI();
            Provider provider = providerService.getByOID(parentKomoto.getTarjoajaOid());

            if (provider != null) {
                parentLOI.setProvider(provider);
                parentLOI.setId(parentKomoto.getOid());
                parentLOI.setPrerequisite(koodistoService.searchFirst(parentKomoto.getPohjakoulutusVaatimusUri()));
                parentLOIs.add(parentLOI);
            }


        }
        parentLOS.setLois(parentLOIs);
//        if (parentLOS.getProvider() == null) {
//            throw new TarjontaParseException("No provider found for parent LOS " + parentKomo.getOid());
//        }

        /////////////////////
        // CHILD LOS
        ////////////////////
        List<String> childKomoOids = parentKomo.getAlaModuulit();
        List<ChildLOS> childLOSs = Lists.newArrayList();
        for (String childKomoOid : childKomoOids) {
            // los
            ChildLOS childLOS = new ChildLOS();
            KomoDTO childKomo = komoResource.getByOID(childKomoOid);

            try {
                validateChildKomo(childKomo);
            } catch (TarjontaParseException e) {
                continue;
            }

            childLOS.setId(childKomo.getOid());
            childLOS.setName(getI18nText(childKomo.getNimi()));
            childLOS.setQualification(koodistoService.searchFirst(childKomo.getTutkintonimikeUri()));
            childLOS.setDegreeTitle(koodistoService.searchFirst(childKomo.getKoulutusOhjelmaKoodiUri()));

            ParentLORef parentRef = new ParentLORef();
            parentRef.setId(parentLOS.getId());
            parentRef.setName(parentLOS.getName());

            // loi
            List<ChildLOI> childLOIs = Lists.newArrayList();
            List<OidRDTO> childKomotoOids = komoResource.getKomotosByKomoOID(childKomoOid, Integer.MAX_VALUE, 0);


            //////////////////
            // CHILD LOI
            /////////////////
            for (OidRDTO childKomotoOid : childKomotoOids) {

                KomotoDTO komotoDTO = komotoResource.getByOID(childKomotoOid.getOid());
                try {
                    validateChildKomoto(komotoDTO);
                } catch (TarjontaParseException e) {
                    continue;
                }

                List<OidRDTO> aoIds = komotoResource.getHakukohdesByKomotoOID(childKomotoOid.getOid());

                if (aoIds != null && aoIds.size() > 0) {
                    ChildLOI childLOI = new ChildLOI();

                    // application option
                    String aoId = aoIds.get(0).getOid();
                    HakukohdeDTO hakukohdeDTO = hakukohdeResource.getByOID(aoId);
                    ApplicationOption ao = new ApplicationOption();
                    ao.setParent(parentRef);
                    ao.setId(hakukohdeDTO.getOid());
                    ao.setName(koodistoService.searchFirst(hakukohdeDTO.getHakukohdeNimiUri()));
                    ao.setAoIdentifier(koodistoService.searchFirstCodeValue(hakukohdeDTO.getHakukohdeNimiUri()));
                    ao.setStartingQuota(hakukohdeDTO.getAloituspaikatLkm());
                    ao.setLowestAcceptedScore(hakukohdeDTO.getAlinValintaPistemaara());
                    ao.setLowestAcceptedAverage(hakukohdeDTO.getAlinHyvaksyttavaKeskiarvo());
                    ao.setAttachmentDeliveryDeadline(hakukohdeDTO.getLiitteidenToimitusPvm());
                    ao.setLastYearApplicantCount(hakukohdeDTO.getEdellisenVuodenHakijatLkm());
                    ao.setSelectionCriteria(getI18nText(hakukohdeDTO.getValintaperustekuvaus()));

                    HakuDTO hakuDTO = hakukohdeResource.getHakuByHakukohdeOID(aoId);
                    ApplicationSystem as = new ApplicationSystem();
                    as.setId(hakuDTO.getOid());
                    as.setName(getI18nText(hakuDTO.getNimi()));
                    if (hakuDTO.getHakuaikas() != null) {
                        for (HakuaikaRDTO ha : hakuDTO.getHakuaikas()) {
                            DateRange range = new DateRange();
                            range.setStartDate(ha.getAlkuPvm());
                            range.setEndDate(ha.getLoppuPvm());
                            as.getApplicationDates().add(range);
                        }
                    }
                    childLOI.setApplicationSystemId(hakuDTO.getOid());
                    ao.setApplicationSystem(as);


                    if (!Strings.isNullOrEmpty(hakukohdeDTO.getSoraKuvausKoodiUri())) {
                        ao.setSora(true);
                    }

                    // provider to ao
                    ao.setProvider(parentLOS.getProvider());

                    // asid to provider
                    //parentLOS.getProvider().getApplicationSystemIDs().add(hakuDTO.getOid());

                    // basic loi info
                    childLOI.setId(komotoDTO.getOid());
                    //education degree code value
                    ao.setEducationDegree(koodistoService.searchFirstCodeValue(komotoDTO.getKoulutusAsteUri()));
                    //set teaching language codes
                    ao.setTeachingLanguages(koodistoService.searchCodeValuesMultiple(komotoDTO.getOpetuskieletUris()));

                    // add to parent
                    parentLOS.getApplicationOptions().add(ao);

                    // how to get the name?
                    //childLOI.setName(new I18nText(komotoDTO.getNimi()));
                    childLOI.setName(childLOS.getName());
                    childLOI.setStartDate(komotoDTO.getKoulutuksenAlkamisDate());
                    childLOI.setFormOfEducation(koodistoService.searchMultiple(komotoDTO.getKoulutuslajiUris()));
                    childLOI.setWebLinks(komotoDTO.getWebLinkkis());
                    childLOI.setTeachingLanguages(koodistoService.searchCodesMultiple(komotoDTO.getOpetuskieletUris()));
                    childLOI.setFormOfTeaching(koodistoService.searchMultiple(komotoDTO.getOpetusmuodotUris()));
                    childLOI.setPrerequisite(koodistoService.searchFirst(komotoDTO.getPohjakoulutusVaatimusUri()));
                    childLOI.setProfessionalTitles(koodistoService.searchMultiple(komotoDTO.getAmmattinimikeUris()));
                    childLOI.setParentLOI(komotoDTO.getParentKomotoOid());
                    ao.setPrerequisite(childLOI.getPrerequisite());
                    childLOI.setApplicationOption(ao);

                    // set child loi names to application option
                    List<OidRDTO> komotosByHakukohdeOID = hakukohdeResource.getKomotosByHakukohdeOID(aoId);

                    for (OidRDTO s : komotosByHakukohdeOID) {
                        KomoDTO komoByKomotoOID = komotoResource.getKomoByKomotoOID(s.getOid());
                        ChildLORef cRef = new ChildLORef();
                        cRef.setLoiId(s.getOid());
                        cRef.setLosId(komoByKomotoOID.getOid());
                        cRef.setName(getI18nText(komoByKomotoOID.getNimi()));
                        cRef.setQualification(koodistoService.searchFirst(komoByKomotoOID.getTutkintonimikeUri()));
                        cRef.setPrerequisite(childLOI.getPrerequisite());
                        ao.getChildLORefs().add(cRef);
                    }
                    childLOIs.add(childLOI);
                }
            }
            childLOS.setChildLOIs(childLOIs);

            childLOSs.add(childLOS);
        }
        parentLOS.setChildren(childLOSs);

        return parentLOS;
    }

    private I18nText getI18nText(final Map<String, String> texts) throws KoodistoException {
        if (texts != null && !texts.isEmpty()) {
            Map<String, String> translations = new HashMap<String, String>();
            Iterator<Map.Entry<String, String>> i = texts.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<String, String> entry = i.next();
                if (!Strings.isNullOrEmpty(entry.getKey()) && !Strings.isNullOrEmpty(entry.getValue())) {
                    String key = koodistoService.searchFirstCodeValue(entry.getKey());
                    translations.put(key.toLowerCase(), entry.getValue());
                }
            }
            I18nText i18nText = new I18nText();
            i18nText.setTranslations(translations);
            return i18nText;
        }
        return null;
    }

    private void validateParentKomo(KomoDTO komo) throws TarjontaParseException {

        // tmp parent check
        if (!komo.getModuuliTyyppi().equals(MODULE_TYPE_PARENT)) {
            throw new TarjontaParseException("LOS not of type " + MODULE_TYPE_PARENT);
        }

        // published
        if (!komo.getTila().equals(STATE_PUBLISHED)) {
            throw new TarjontaParseException("LOS state not " + STATE_PUBLISHED);
        }

        if (komo.getNimi() == null) {
            //throw new TarjontaParseException("KomoDTO name is null");
            Map<String, String> name = Maps.newHashMap();
            name.put("fi", "fi dummy name");
            name.put("sv", "sv dummy name");
            komo.setNimi(name);
        }
    }

    private void validateParentKomoto(KomotoDTO komoto) throws TarjontaParseException {
//        if (!komoto.getTila().equals(STATE_PUBLISHED) && !komoto.getTila().equals(STATE_READY)) {
//            throw new TarjontaParseException("LOI " + komoto.getOid() + " not of type " + MODULE_TYPE_PARENT);
//        }

    }

    private void validateChildKomo(KomoDTO komo) throws TarjontaParseException {
        if (!komo.getTila().equals(STATE_PUBLISHED)) {
            throw new TarjontaParseException("LOS " + komo.getOid() + " not of type " + MODULE_TYPE_PARENT);
        }
        if (komo.getNimi() == null) {
            throw new TarjontaParseException("Child KomoDTO nimi is null");
        }
        if (komo.getTutkintonimikeUri() == null) {
            throw new TarjontaParseException("Child KomoDTO tutkinto nimike uri is null");
        }
        if (komo.getKoulutusOhjelmaKoodiUri() == null) {
            throw new TarjontaParseException("Child KomoDTO koulutusohjelma koodi uri is null");
        }
    }

    private void validateChildKomoto(KomotoDTO komoto) throws TarjontaParseException {
        if (!komoto.getTila().equals(STATE_PUBLISHED)) {
            throw new TarjontaParseException("LOI " + komoto.getOid() + " not of type " + MODULE_TYPE_PARENT);
        }

    }

    ////////////////////////////////////////////////
    // parent loi fix

    public List<ParentLOS> postProcess(ParentLOS p) {

        List<ParentLOS> nParents = Lists.newArrayList();

        // parent loi id -> childlos
        Map<String, List<ChildLOS>> childMap = Maps.newHashMap();

        for (ChildLOS clos : p.getChildren()) {
            for (ChildLOI cloi : clos.getChildLOIs()) {

                ChildLOS nclos = new ChildLOS();
                nclos.setId(clos.getId() + "_" + cloi.getId());
                nclos.setName(clos.getName());
                nclos.setDegreeTitle(clos.getDegreeTitle());
                nclos.setQualification(clos.getQualification());

                nclos.setChildLOIs(Lists.newArrayList(cloi));

                List<ChildLOS> l = childMap.get(cloi.getParentLOI());
                if (l == null) {
                    l = Lists.newArrayList(nclos);
                } else {
                    l.add(nclos);
                }

                childMap.put(cloi.getParentLOI(), l);

            }
        }


        for (ParentLOI loi : p.getLois()) {
            ParentLOS np = new ParentLOS();
            np.setId(loi.getId());
            np.setName(p.getName());
            np.setStructureDiagram(p.getStructureDiagram());
            np.setAccessToFurtherStudies(p.getAccessToFurtherStudies());
            np.setGoals(p.getGoals());
            np.setEducationDomain(p.getEducationDomain());
            np.setStydyDomain(p.getStydyDomain());
            np.setEducationDegree(p.getEducationDegree());

            Provider provider = loi.getProvider();
            List<ChildLOS> children = childMap.get(loi.getId());

            if (children != null) {
                np.setChildren(children);

                // asid to provider
                // ao to parent
                for (ChildLOS childLOS : children) {
                    for (ChildLOI childLOI : childLOS.getChildLOIs()) {
                        provider.getApplicationSystemIDs().add(childLOI.getApplicationSystemId());


                        np.getApplicationOptions().add(childLOI.getApplicationOption());

                        for (ApplicationOption ao : np.getApplicationOptions()) {
                            ao.setProvider(provider);
                        }


                    }
                }
            }

            np.setProvider(provider);
            np.setLois(Lists.newArrayList(loi));

            nParents.add(np);
        }

        return nParents;
    }


}
