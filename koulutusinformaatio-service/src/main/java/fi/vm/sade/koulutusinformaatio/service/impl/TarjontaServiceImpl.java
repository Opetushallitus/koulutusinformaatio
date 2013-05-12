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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;
import fi.vm.sade.tarjonta.service.resources.HakukohdeResource;
import fi.vm.sade.tarjonta.service.resources.KomoResource;
import fi.vm.sade.tarjonta.service.resources.KomotoResource;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author Hannu Lyytikainen
 */
public class TarjontaServiceImpl implements TarjontaService {

    public static final String MODULE_TYPE_PARENT = "TUTKINTO";
    public static final String MODULE_TYPE_CHILD = "TUTKINTO_OHJELMA";

    private KomoResource komoResource;
    private KomotoResource komotoResource;
    private HakukohdeResource hakukohdeResource;
    private ConversionService conversionService;

    @Autowired
    private KoodistoService koodistoService;

    @Autowired
    public TarjontaServiceImpl(KomoResource komoResource, KomotoResource komotoResource, HakukohdeResource aoResource,
                               ConversionService conversionService) {
        this.komoResource = komoResource;
        this.komotoResource = komotoResource;
        this.hakukohdeResource = aoResource;
        this.conversionService = conversionService;
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
        if (komo.getTutkintonimikeUri() == null) {
            throw new TarjontaParseException("KomoDTO tutkinto nimike uri is null");
        }
        if (komo.getKoulutusOhjelmaKoodiUri() == null) {
            throw new TarjontaParseException("KomoDTO koulutusohjelma koodi uri is null");
        }
    }

    @Override
    public ParentLOS findParentLearningOpportunity(String oid) throws TarjontaParseException, KoodistoException {
        ParentLOS parentLOS = new ParentLOS();
        KomoDTO parentKomo = komoResource.getByOID(oid);
        validateParentKomo(parentKomo);

        parentLOS.setId(parentKomo.getOid());
        parentLOS.setName(new I18nText(parentKomo.getNimi()));
        parentLOS.setStructureDiagram(new I18nText(parentKomo.getKoulutuksenRakenne()));
        parentLOS.setAccessToFurtherStudies(new I18nText(parentKomo.getJatkoOpintoMahdollisuudet()));
        parentLOS.setGoals(new I18nText(parentKomo.getTavoitteet()));
        parentLOS.setEducationDomain(koodistoService.search(parentKomo.getKoulutusAlaUri()).get(0));
        parentLOS.setStydyDomain(koodistoService.search(parentKomo.getOpintoalaUri()).get(0));
        parentLOS.setEducationDegree(koodistoService.search(parentKomo.getKoulutusAsteUri()).get(0));

        //Sosiaali-, terveys- ja liikunta-ala
//                "koulutusAlaUri" : "koulutusalaoph2002_7#1",
        // Ammatillinen koulutus / 32
//                "koulutusAsteUri" : "koulutusasteoph2002_32#1",
        // opintoviikko
//                "laajuusYksikkoUri" : "opintojenlaajuusyksikko_1#1",
        // Hammaslääketiede ja muu hammashuolto
//                "opintoalaUri" : "opintoalaoph2002_704#1",

        List<String> childKomoOids = parentKomo.getAlaModuulit();
        List<ChildLOS> childLOSs = Lists.newArrayList();

        for (String childKomoOid : childKomoOids) {
            // los
            ChildLOS childLOS = new ChildLOS();
            KomoDTO childKomo = komoResource.getByOID(childKomoOid);
            validateChildKomo(childKomo);

            childLOS.setId(childKomo.getOid());
            childLOS.setName(new I18nText(childKomo.getNimi()));
            childLOS.setQualification(koodistoService.search(childKomo.getTutkintonimikeUri()).get(0));
            childLOS.setDegreeTitle(koodistoService.search(childKomo.getKoulutusOhjelmaKoodiUri()).get(0));

            // loi
            List<ChildLOI> childLOIs = Lists.newArrayList();
            List<String> childKomotoOids = komoResource.getKomotosByKomoOID(childKomoOid, 0, 0);
            for (String childKomotoOid : childKomotoOids) {
                ChildLOI childLOI = new ChildLOI();
                KomotoDTO komotoDTO = komotoResource.getByOID(childKomotoOid);
                childLOI.setId(komotoDTO.getOid());

                // how to get the name?
                //childLOI.setName(new I18nText(komotoDTO.getNimi()));
                childLOI.setName(childLOS.getName());

                String aoId = this.loiAoMap.get(komotoDTO.getOid());
                if (aoId == null) continue;
                HakukohdeDTO hakukohdeDTO = hakukohdeResource.getByOID(aoId);
                ApplicationOption ao = new ApplicationOption();
                ao.setId(hakukohdeDTO.getOid());
                ao.setName(koodistoService.search(hakukohdeDTO.getHakukohdeNimiUri()).get(0));
                HakuDTO hakuDTO = hakukohdeResource.getHakuByHakukohdeOID(aoId);
                childLOI.setApplicationSystemId(hakuDTO.getOid());

                ao.setApplicationSystemId(hakuDTO.getOid());
                childLOI.setApplicationOption(ao);

                childLOIs.add(childLOI);
            }
            childLOS.setChildLOIs(childLOIs);

            childLOSs.add(childLOS);
        }
        parentLOS.setChildren(childLOSs);

        return parentLOS;
    }

    @Override
    public List<String> listParentLearnignOpportunityOids() {
        List<String> oids = komoResource.search(null, 0, 0, null, null);
        List<String> returnVal = Lists.newArrayList();

        for (String oid : oids) {
            if (komoResource.getByOID(oid).getModuuliTyyppi().equals(MODULE_TYPE_PARENT)) {
                returnVal.add(oid);
            }
        }

        return returnVal;
    }

    @Override
    public List<String> listApplicationOptionOids() {
        return hakukohdeResource.search(null, 0, 0, null, null);
    }




    // temp data structures to simulate better tarjonta api
    private Map<String, String> loiAoMap;

    @Override
    public void updateTempData() {
        loiAoMap = Maps.newHashMap();
        List<String> aos = hakukohdeResource.search(null, 0, 0, null, null);

        for (String ao : aos) {
            List<String> komotosByHakukohdeOID = hakukohdeResource.getKomotosByHakukohdeOID(ao);
            for (String komotooid : komotosByHakukohdeOID) {
                loiAoMap.put(komotooid, ao);
            }
        }
    }


}
