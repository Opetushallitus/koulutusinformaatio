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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.googlecode.ehcache.annotations.Cacheable;
import fi.vm.sade.koodisto.service.GenericFault;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Mikko Majapuro
 */
@Service
public class KoodistoServiceImpl implements KoodistoService {

    private final KoodiService koodiService;
    private final ConversionService conversionService;
    private final Pattern pattern;
    private static final String KOODI_URI_WITH_VERSION_PATTERN = "^[^#]+#\\d+$";
    public static final Logger LOGGER = LoggerFactory.getLogger(KoodistoServiceImpl.class);

    @Autowired
    public KoodistoServiceImpl(final KoodiService koodiService, final ConversionService conversionService) {
        this.koodiService = koodiService;
        this.conversionService = conversionService;
        this.pattern = Pattern.compile(KOODI_URI_WITH_VERSION_PATTERN);
    }

    @Override
    @Cacheable(cacheName = "koodiCache")
    public List<I18nText> search(String koodiUri) throws KoodistoException {
        LOGGER.debug("search koodi: " + koodiUri);
        if (koodiUri != null && pattern.matcher(koodiUri).matches()) {
            String[] splitted = koodiUri.split("#");
            String uri = splitted[0];
            Integer version = Integer.parseInt(splitted[1]);
            return convert(getKoodiTypes(uri, version));
        } else if (koodiUri != null && !koodiUri.isEmpty()) {
             return convert(getKoodiTypes(koodiUri));
        } else {
            throw new KoodistoException("Illegal arguments: " + koodiUri);
        }
    }

    @Override
    public I18nText searchFirst(String koodiUri) throws KoodistoException {
        LOGGER.debug("search first koodi: " + koodiUri);
        return search(koodiUri).get(0);
    }

    private List<KoodiType> getKoodiTypes(final String koodiUri, int version) throws KoodistoException {
        SearchKoodisCriteriaType criteria = KoodiServiceSearchCriteriaBuilder.koodiByUriAndVersion(koodiUri, version);
        return searchKoodis(criteria);
    }

    private List<KoodiType> getKoodiTypes(final String koodiUri) throws KoodistoException {
        SearchKoodisCriteriaType criteria = KoodiServiceSearchCriteriaBuilder.latestKoodisByUris(koodiUri);
        return searchKoodis(criteria);
    }

    private List<KoodiType> searchKoodis(final SearchKoodisCriteriaType criteria) throws KoodistoException {
        try {
            List<KoodiType> codes = koodiService.searchKoodis(criteria);
            return codes;
        } catch (GenericFault e) {
            throw new KoodistoException(e);
        }
    }

    private List<I18nText> convert(final List<KoodiType> codes) {
        return Lists.transform(codes, new Function<KoodiType, I18nText>() {
            @Override
            public I18nText apply(fi.vm.sade.koodisto.service.types.common.KoodiType koodiType) {
                return conversionService.convert(koodiType, I18nText.class);
            }
        });
    }
}
