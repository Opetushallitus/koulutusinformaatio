/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.Location;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.LocationService;

/**
 * @author Mikko Majapuro
 */
@Service
public class LocationServiceImpl implements LocationService {

    public static final String CODE_MUNICIPALITY = "kunta";
    public static final String CODE_DISTRICT = "maakunta";
    private KoodistoService koodistoService;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationServiceImpl.class);

    @Autowired
    public LocationServiceImpl(KoodistoService koodistoService) {
        this.koodistoService = koodistoService;
    }

    @Override
    public List<Location> getMunicipalities() throws KoodistoException {
        List<Code> codes = koodistoService.searchByKoodisto(CODE_MUNICIPALITY, null);
        List<Location> municipalities = Lists.newArrayList();
        if (codes != null) {
            for (Code code : codes) {
                List<Code> maakuntaCodes = koodistoService.searchSubCodes(String.format("%s_%s#1", CODE_MUNICIPALITY, code.getValue()), CODE_DISTRICT);
                
                Code parent =  (maakuntaCodes != null &&!maakuntaCodes.isEmpty()) ? maakuntaCodes.get(0) : null;
                String parentVal = (parent != null) ? parent.getValue() : null;
                
                if (parent != null) {
                    addDistrict(parent, municipalities);
                }
                
                
                I18nText name = code.getName();

                Iterator entries = name.getTranslations().entrySet().iterator();
                Location finnish = null;
                Location english = null;
                while (entries.hasNext()) {
                    Map.Entry<String, String> entry = (Map.Entry) entries.next();

                    Location municipality = new Location(entry.getKey() + code.getValue(), entry.getValue(),
                            code.getValue(), entry.getKey(), CODE_MUNICIPALITY, parentVal);
                    if (entry.getKey().equalsIgnoreCase("fi")) {
                        finnish = municipality;
                    } else  if (entry.getKey().equalsIgnoreCase("en")) {
                        english = municipality;
                    }
                    municipalities.add(municipality);
                }
                
                if (english == null && finnish != null) {
                    english = new Location("en" + finnish.getCode(), finnish.getName(), finnish.getCode(), "en", CODE_MUNICIPALITY, finnish.getParent());
                    municipalities.add(english);
                }
                
                LOGGER.debug(String.format("Added location: %s", code.getUri()));
            }
        }
        return municipalities;
    }

    private void addDistrict(Code parent, List<Location> municipalities) {
        
        I18nText name = parent.getName();

        Iterator entries = name.getTranslations().entrySet().iterator();
        Location finnish = null;
        Location english = null;
        while (entries.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry) entries.next();
            Location district = new Location(entry.getKey() + parent.getValue(), entry.getValue(),
                    parent.getValue(), entry.getKey(), CODE_DISTRICT, null);
            municipalities.add(district);
            if (entry.getKey().equalsIgnoreCase("fi")) {
                finnish = district;
            } else  if (entry.getKey().equalsIgnoreCase("en")) {
                english = district;
            }
        }
        
        if (english == null && finnish != null) {
            english = new Location("en" + finnish.getCode(), finnish.getName(), finnish.getCode(), "en", CODE_DISTRICT, null);
            municipalities.add(english);
        }
        
        
    }
}
