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

import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.Location;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Mikko Majapuro
 */
@Service
public class LocationServiceImpl implements LocationService {

    public static final String CODE_MUNICIPALITY = "kunta";
    private KoodistoService koodistoService;

    @Autowired
    public LocationServiceImpl(KoodistoService koodistoService) {
        this.koodistoService = koodistoService;
    }

    @Override
    public List<Location> getMunicipalities() throws KoodistoException {
        List<Code> codes = koodistoService.searchCodesByKoodisto(CODE_MUNICIPALITY, null);
        List<Location> municipalities = Lists.newArrayList();
        if (codes != null) {
            for (Code code : codes) {
                I18nText name = code.getName();

                Iterator entries = name.getTranslations().entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry<String, String> entry = (Map.Entry) entries.next();
                    Location municipality = new Location(entry.getKey() + code.getValue(), entry.getValue(),
                            code.getValue(), entry.getKey());
                    municipalities.add(municipality);
                }
            }
        }
        return municipalities;
    }
}
