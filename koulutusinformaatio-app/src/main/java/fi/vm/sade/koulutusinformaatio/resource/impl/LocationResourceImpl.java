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

package fi.vm.sade.koulutusinformaatio.resource.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.domain.Location;
import fi.vm.sade.koulutusinformaatio.domain.dto.LocationDTO;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;
import fi.vm.sade.koulutusinformaatio.exception.KIExceptionHandler;
import fi.vm.sade.koulutusinformaatio.resource.LocationResource;
import fi.vm.sade.koulutusinformaatio.service.SearchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * @author Mikko Majapuro
 */
@Component
public class LocationResourceImpl implements LocationResource {

    private final SearchService searchService;

    @Autowired
    public LocationResourceImpl(SearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public List<LocationDTO> getLocations(List<String> code, String lang) {
        try {
            List<Location> locations = searchService.getLocations(code, lang);
            return Lists.transform(locations, getTransformFunction());
        } catch (SearchException e) {
            throw KIExceptionHandler.resolveException(e);
        }
    }

    @Override
    public List<LocationDTO> searchLocations(String term, String lang) {
        String key = null;
        try {
            key = URLDecoder.decode(term, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            key = term;
        }
        try { 
            List<Location> locations = searchService.searchLocations(key, lang);
            return Lists.transform(locations, getTransformFunction());
        } catch (SearchException e) {
            throw KIExceptionHandler.resolveException(e);
        } 
    }

    private Function<Location, LocationDTO> getTransformFunction() {
        return new Function<Location, LocationDTO>() {
            @Override
            public LocationDTO apply(Location location) {
                LocationDTO dto = new LocationDTO();
                dto.setName(location.getName());
                dto.setCode(location.getCode());
                return dto;
            }
        };
    }

    @Override
    public List<LocationDTO> getDistricts(String lang) {
        try {
            List<Location> locations = searchService.getDistricts(lang);
            return Lists.transform(locations, getTransformFunction());
        } catch (SearchException e) {
            throw KIExceptionHandler.resolveException(e);
        }
    }

    @Override
    public List<LocationDTO> getChildLocations(List<String> districts, String lang) {
        try {
            List<Location> locations = searchService.getChildLocations(districts, lang);
            return Lists.transform(locations, getTransformFunction());
        } catch (SearchException e) {
            throw KIExceptionHandler.resolveException(e);
        }
    }
}
