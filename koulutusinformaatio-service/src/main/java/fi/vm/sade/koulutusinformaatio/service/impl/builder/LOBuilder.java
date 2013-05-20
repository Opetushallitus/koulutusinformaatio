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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.tarjonta.service.resources.HakukohdeResource;
import fi.vm.sade.tarjonta.service.resources.KomoResource;
import fi.vm.sade.tarjonta.service.resources.KomotoResource;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Builds learning opportunity instances.
 *
 * @author Hannu Lyytikainen
 */
public class LOBuilder {

    public static final String MODULE_TYPE_PARENT = "TUTKINTO";
    public static final String MODULE_TYPE_CHILD = "TUTKINTO_OHJELMA";

    private KomoResource komoResource;
    private KomotoResource komotoResource;
    private HakukohdeResource hakukohdeResource;
    private ProviderService providerService;
    private ConversionService conversionService;

    @Autowired
    private KoodistoService koodistoService;

    public LOBuilder(KomoResource komoResource, KomotoResource komotoResource, HakukohdeResource hakukohdeResource,
                     ProviderService providerService, ConversionService conversionService) {
        this.komoResource = komoResource;
        this.komotoResource = komotoResource;
        this.hakukohdeResource = hakukohdeResource;
        this.providerService = providerService;
        this.conversionService = conversionService;
    }

    public ParentLOS buildParentLOS(String oid) throws TarjontaParseException, KoodistoException {
        ParentLOS parentLOS = new ParentLOS();
        KomoDTO parentKomo = komoResource.getByOID(oid);

        // tmp parent check
        if (!komoResource.getByOID(oid).getModuuliTyyppi().equals(MODULE_TYPE_PARENT)) {
            throw new TarjontaParseException("LOS not of type " + MODULE_TYPE_PARENT);
        }

        validateParentKomo(parentKomo);

        parentLOS.setId(parentKomo.getOid());
        parentLOS.setName(new I18nText(parentKomo.getNimi()));
        parentLOS.setStructureDiagram(new I18nText(parentKomo.getKoulutuksenRakenne()));
        parentLOS.setAccessToFurtherStudies(new I18nText(parentKomo.getJatkoOpintoMahdollisuudet()));
        parentLOS.setGoals(new I18nText(parentKomo.getTavoitteet()));
        parentLOS.setEducationDomain(koodistoService.searchFirst(parentKomo.getKoulutusAlaUri()));
        parentLOS.setStydyDomain(koodistoService.searchFirst(parentKomo.getOpintoalaUri()));
        parentLOS.setEducationDegree(koodistoService.searchFirst(parentKomo.getKoulutusAsteUri()));

        List<String> parentKomotoOids = komoResource.getKomotosByKomoOID(parentKomo.getOid(), 0, 0);

        // pick first parent loi and resolve provider
        if (parentKomotoOids != null && parentKomotoOids.size() > 0) {
            KomotoDTO parentKomoto = komotoResource.getByOID(parentKomotoOids.get(0));
            parentLOS.setProvider(providerService.getByOID(parentKomoto.getTarjoajaOid()));
        }

        if (parentLOS.getProvider() == null) {
            Map<String, String> langs = Maps.newHashMap();
            langs.put("fi", "dummy name");
            parentLOS.setProvider(new Provider("dummyid", new I18nText(langs)));
        }

        List<String> childKomoOids = parentKomo.getAlaModuulit();
        List<ChildLOS> childLOSs = Lists.newArrayList();

        Set<String> parentAoIds  = Sets.newHashSet();

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
            childLOS.setName(new I18nText(childKomo.getNimi()));
            childLOS.setQualification(koodistoService.searchFirst(childKomo.getTutkintonimikeUri()));
            childLOS.setDegreeTitle(koodistoService.searchFirst(childKomo.getKoulutusOhjelmaKoodiUri()));

            // loi
            List<ChildLOI> childLOIs = Lists.newArrayList();
            List<String> childKomotoOids = komoResource.getKomotosByKomoOID(childKomoOid, 0, 0);

            for (String childKomotoOid : childKomotoOids) {

                List<String> aoIds = komotoResource.getHakukohdesByKomotoOID(childKomotoOid);

                if (aoIds != null && aoIds.size() > 0) {
                    ChildLOI childLOI = new ChildLOI();

                    // application option
                    String aoId = aoIds.get(0);
                    HakukohdeDTO hakukohdeDTO = hakukohdeResource.getByOID(aoId);
                    ApplicationOption ao = new ApplicationOption();
                    ao.setId(hakukohdeDTO.getOid());
                    ao.setName(koodistoService.search(hakukohdeDTO.getHakukohdeNimiUri()).get(0));
                    ao.setStartingQuota(hakukohdeDTO.getAloituspaikatLkm());
                    ao.setLowestAcceptedScore(hakukohdeDTO.getAlinValintaPistemaara());
                    ao.setLowestAcceptedAverage(hakukohdeDTO.getAlinHyvaksyttavaKeskiarvo());
                    ao.setAttachmentDeliveryDeadline(hakukohdeDTO.getLiitteidenToimitusPvm());
                    ao.setLastYearApplicantCount(hakukohdeDTO.getEdellisenVuodenHakijatLkm());
                    HakuDTO hakuDTO = hakukohdeResource.getHakuByHakukohdeOID(aoId);
                    childLOI.setApplicationSystemId(hakuDTO.getOid());
                    ao.setApplicationSystemId(hakuDTO.getOid());
                    childLOI.setApplicationOption(ao);


                    KomotoDTO komotoDTO = komotoResource.getByOID(childKomotoOid);

                    // provider

                    // basic loi info
                    childLOI.setId(komotoDTO.getOid());
                    // how to get the name?
                    //childLOI.setName(new I18nText(komotoDTO.getNimi()));
                    childLOI.setName(childLOS.getName());
                    childLOI.setStartDate(komotoDTO.getKoulutuksenAlkamisDate());
                    childLOI.setFormOfEducation(koodistoService.searchMultiple(komotoDTO.getKoulutuslajiUris()));
                    childLOI.setWebLinks(komotoDTO.getWebLinkkis());
                    childLOI.setTeachingLanguages(koodistoService.searchCodesMultiple(komotoDTO.getOpetuskieletUris()));
                    childLOI.setFormOfTeaching(koodistoService.searchMultiple(komotoDTO.getOpetusmuodotUris()));
                    childLOI.setPrerequisite(koodistoService.searchFirst(komotoDTO.getPohjakoulutusVaatimusUri()));

                    childLOIs.add(childLOI);


                    parentAoIds.addAll(aoIds);
                }
            }
            childLOS.setChildLOIs(childLOIs);

            childLOSs.add(childLOS);
        }
        parentLOS.setChildren(childLOSs);

        for (String parentAoId : parentAoIds) {


            System.out.println();
        }

        return parentLOS;

    }

    private void validateParentKomo(KomoDTO komo) throws TarjontaParseException {
        if (komo.getNimi() == null) {
            //throw new TarjontaParseException("KomoDTO name is null");
            Map<String, String> name = Maps.newHashMap();
            name.put("fi", "fi dummy name");
            name.put("sv", "sv dummy name");
            komo.setNimi(name);
        }
    }

    private void validateChildKomo(KomoDTO komo) throws TarjontaParseException {
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


}
