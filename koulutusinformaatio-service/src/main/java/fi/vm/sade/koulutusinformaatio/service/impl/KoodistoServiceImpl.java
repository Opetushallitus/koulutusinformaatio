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
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.googlecode.ehcache.annotations.Cacheable;
import fi.vm.sade.koodisto.service.GenericFault;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.CodeUriAndVersion;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.Collection;
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
    public List<I18nText> search(String koodiUri) throws KoodistoException {
        if (Strings.isNullOrEmpty(koodiUri)) {
            return null;
        } else {
            LOGGER.debug("search koodi: " + koodiUri);
            return convert(searchKoodiTypes(koodiUri), I18nText.class);
        }
    }

    @Override
    public List<I18nText> searchMultiple(List<String> koodiUris) throws KoodistoException {
        if (koodiUris == null) {
            return null;
        }
        else if (koodiUris.isEmpty()) {
            return Lists.newArrayList();
        }
        else {
            List<I18nText> results = Lists.newArrayList();
            for (String koodiUri : koodiUris) {
                results.addAll(search(koodiUri));
            }
            return results;
        }
    }

    @Override
    public I18nText searchFirst(String koodiUri) throws KoodistoException {
        if (Strings.isNullOrEmpty(koodiUri)) {
            return null;
        } else {
            LOGGER.debug("search first koodi: " + koodiUri);
            return search(koodiUri).get(0);
        }
    }

    @Override
    public List<Code> searchCodes(String koodiUri) throws KoodistoException {
        if (Strings.isNullOrEmpty(koodiUri)) {
            return null;
        } else {
            LOGGER.debug("search koodi: " + koodiUri);
            return convert(searchKoodiTypes(koodiUri), Code.class);
        }
    }

    @Override
    public List<Code> searchCodesMultiple(List<String> koodiUris) throws KoodistoException {
        if (koodiUris == null) {
            return null;
        }
        else if (koodiUris.isEmpty()) {
            return Lists.newArrayList();
        }
        else {
            List<Code> results = Lists.newArrayList();
            for (String koodiUri : koodiUris) {
                results.addAll(searchCodes(koodiUri));
            }
            return results;
        }
    }

    @Override
    public Code searchFirstCode(String koodiUri) throws KoodistoException {
        if (!Strings.isNullOrEmpty(koodiUri)) {
            LOGGER.debug("search first code: " + koodiUri);
            List<Code> codes = searchCodes(koodiUri);
            if (codes != null && !codes.isEmpty()) {
                return searchCodes(koodiUri).get(0);
            }
        }
        return null;
    }

    @Override
    public String searchFirstCodeValue(String koodiUri) throws KoodistoException {
        if (!Strings.isNullOrEmpty(koodiUri)) {
            Code code = searchFirstCode(koodiUri);
            if (code != null) {
                return code.getValue();
            }
        }
        return null;
    }

    @Override
    public List<String> searchCodeValuesMultiple(List<String> koodiUri) throws KoodistoException {
        List<Code> results = searchCodesMultiple(koodiUri);
        return Lists.transform(results, new Function<Code, String>() {
            @Override
            public String apply(fi.vm.sade.koulutusinformaatio.domain.Code code) {
                return code.getValue();
            }
        });
    }

    @Override
    public List<Code> searchSubCodes(String koodiURIAndVersion, String koodistoURI) throws KoodistoException {
        if (koodistoURI != null && !koodistoURI.isEmpty()) {
            return convert(searchSubKoodiTypes(koodiURIAndVersion, koodistoURI), Code.class);
        }
        else {
            return convert(searchSubKoodiTypes(koodiURIAndVersion), Code.class);
        }
    }

    @Cacheable(cacheName = "subKoodiCache")
    private List<KoodiType> searchSubKoodiTypes(final String koodiUriAndVersion, final String koodistoURI) throws KoodistoException {
        return Lists.newArrayList(Collections2.filter(searchSubKoodiTypes(koodiUriAndVersion), new Predicate<KoodiType>() {
            @Override
            public boolean apply(KoodiType koodiType) {
                return koodiType.getKoodisto().getKoodistoUri().equals(koodistoURI);
            }
        }));
    }

    private List<KoodiType> searchSubKoodiTypes(String koodiUriAndVersion) throws KoodistoException {
        CodeUriAndVersion codeUriAndVersion = resolveKoodiUriAndVersion(koodiUriAndVersion);
        KoodiUriAndVersioType koodiUriAndVersionType = conversionService.convert(codeUriAndVersion, KoodiUriAndVersioType.class);
        return koodiService.listKoodiByRelation(koodiUriAndVersionType, false, SuhteenTyyppiType.SISALTYY);
    }

    @Cacheable(cacheName = "koodiCache")
    private List<KoodiType> searchKoodiTypes(String koodiUri) throws KoodistoException {
        CodeUriAndVersion codeUriAndVersion = resolveKoodiUriAndVersion(koodiUri);
        return getKoodiTypes(codeUriAndVersion);
    }

    private CodeUriAndVersion resolveKoodiUriAndVersion(String koodiUri) throws KoodistoException {
        if (koodiUri != null && pattern.matcher(koodiUri).matches()) {
            String[] splitted = koodiUri.split("#");
            String uri = splitted[0];
            Integer version = Integer.parseInt(splitted[1]);
            return new CodeUriAndVersion(uri, version);
        }
        else if (koodiUri != null && !koodiUri.isEmpty()) {
            return new CodeUriAndVersion(koodiUri);
        }
        else {
            throw new KoodistoException("Illegal arguments: " + koodiUri);
        }
    }

    private List<KoodiType> getKoodiTypes(final CodeUriAndVersion codeUriAndVersion) throws KoodistoException {
        SearchKoodisCriteriaType criteria = null;
        if (codeUriAndVersion.getVersion() == null) {
            criteria = KoodiServiceSearchCriteriaBuilder.latestKoodisByUris(codeUriAndVersion.getUri());
        }
        else {
            criteria = KoodiServiceSearchCriteriaBuilder.koodiByUriAndVersion(codeUriAndVersion.getUri(),
                    codeUriAndVersion.getVersion());
        }
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

    private <T> List<T> convert(final List<KoodiType> codes, final Class<T> type) {
        return Lists.transform(codes, new Function<KoodiType, T>() {
            @Override
            public T apply(fi.vm.sade.koodisto.service.types.common.KoodiType koodiType) {
                return conversionService.convert(koodiType, type);
            }
        });
    }
}
