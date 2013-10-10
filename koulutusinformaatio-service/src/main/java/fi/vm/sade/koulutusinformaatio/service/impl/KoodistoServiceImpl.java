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
import com.google.common.collect.Maps;
import fi.vm.sade.koodisto.service.GenericFault;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.CachingKoodistoClient;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.koodisto.util.KoodistoClient;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.CodeUriAndVersion;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Mikko Majapuro
 */
@Service
public class KoodistoServiceImpl implements KoodistoService {

    private final KoodistoClient koodiService;
    private final Pattern pattern;
    private static final String KOODI_URI_WITH_VERSION_PATTERN = "^[^#]+#\\d+$";
    public static final Logger LOGGER = LoggerFactory.getLogger(KoodistoServiceImpl.class);

    @Autowired
    public KoodistoServiceImpl(final KoodistoClient koodiService) {
        this.koodiService = koodiService;
        this.pattern = Pattern.compile(KOODI_URI_WITH_VERSION_PATTERN);
    }

    @Override
    public List<I18nText> search(String koodiUri) throws KoodistoException {
        if (Strings.isNullOrEmpty(koodiUri)) {
            return null;
        } else {
            LOGGER.debug("search koodi: " + koodiUri);
            return convertAllToI18nText(searchKoodiTypes(koodiUri));
        }
    }

    @Override
    public List<I18nText> searchMultiple(List<String> koodiUris) throws KoodistoException {
        if (koodiUris == null) {
            return null;
        } else if (koodiUris.isEmpty()) {
            return Lists.newArrayList();
        } else {
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
            List<I18nText> koodis = search(koodiUri);
            if (koodis.size() < 1) {
                LOGGER.warn("No koodis found with uri: " + koodiUri);
                return null;
            }
            return koodis.get(0);
        }
    }

    @Override
    public List<Code> searchCodes(String koodiUri) throws KoodistoException {
        if (Strings.isNullOrEmpty(koodiUri)) {
            return null;
        } else {
            LOGGER.debug("search koodi: " + koodiUri);
            return convertAllToCode(searchKoodiTypes(koodiUri));
        }
    }

    @Override
    public List<Code> searchCodesMultiple(List<String> koodiUris) throws KoodistoException {
        if (koodiUris == null) {
            return null;
        } else if (koodiUris.isEmpty()) {
            return Lists.newArrayList();
        } else {
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
            return convertAllToCode(searchSubKoodiTypes(koodiURIAndVersion, koodistoURI));
        } else {
            return convertAllToCode(searchSubKoodiTypes(koodiURIAndVersion));
        }
    }

    @Override
    public List<Code> searchSuperCodes(String koodiURIAndVersion, String koodistoURI) throws KoodistoException {
        if (koodistoURI != null && !koodistoURI.isEmpty()) {
            return convertAllToCode(searchSuperKoodiTypes(koodiURIAndVersion, koodistoURI));
        } else {
            return convertAllToCode(searchSuperKoodiTypes(koodiURIAndVersion));
        }
    }

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
        return koodiService.getAlakoodis(codeUriAndVersion.getUri());
    }

    private List<KoodiType> searchSuperKoodiTypes(final String koodiUriAndVersion, final String koodistoURI) throws KoodistoException {
        return Lists.newArrayList(Collections2.filter(searchSuperKoodiTypes(koodiUriAndVersion), new Predicate<KoodiType>() {
            @Override
            public boolean apply(KoodiType koodiType) {
                return koodiType.getKoodisto().getKoodistoUri().equals(koodistoURI);
            }
        }));
    }

    private List<KoodiType> searchSuperKoodiTypes(String koodiUriAndVersion) throws KoodistoException {
        CodeUriAndVersion codeUriAndVersion = resolveKoodiUriAndVersion(koodiUriAndVersion);
        return koodiService.getYlakoodis(codeUriAndVersion.getUri());
    }

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
        } else if (koodiUri != null && !koodiUri.isEmpty()) {
            return new CodeUriAndVersion(koodiUri);
        } else {
            throw new KoodistoException("Illegal arguments: " + koodiUri);
        }
    }

    private List<KoodiType> getKoodiTypes(final CodeUriAndVersion codeUriAndVersion) throws KoodistoException {
        SearchKoodisCriteriaType criteria = null;
        if (codeUriAndVersion.getVersion() == null) {
            criteria = KoodiServiceSearchCriteriaBuilder.latestKoodisByUris(codeUriAndVersion.getUri());
        } else {
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

    private List<I18nText> convertAllToI18nText(final List<KoodiType> codes) {
        return Lists.transform(codes, new Function<KoodiType, I18nText>() {
            @Override
            public I18nText apply(fi.vm.sade.koodisto.service.types.common.KoodiType koodiType) {
                return convertToI18nText(koodiType);
            }
        });
    }


    public I18nText convertToI18nText(KoodiType koodiType) {
        List<KoodiMetadataType> metadata = koodiType.getMetadata();
        Map<String, String> translations = new HashMap<String, String>();
        Map<String, String> translationsShortName = new HashMap<String, String>();
        for (KoodiMetadataType koodiMetadataType : metadata) {
            translations.put(koodiMetadataType.getKieli().value().toLowerCase(), koodiMetadataType.getNimi());
            translationsShortName.put(koodiMetadataType.getKieli().value().toLowerCase(), koodiMetadataType.getLyhytNimi());
        }
        return new I18nText(translations, translationsShortName);
    }


    private List<Code> convertAllToCode(final List<KoodiType> codes) {
        return Lists.transform(codes, new Function<KoodiType, Code>() {
            @Override
            public Code apply(fi.vm.sade.koodisto.service.types.common.KoodiType koodiType) {
                return convertToCode(koodiType);
            }
        });
    }

    private Code convertToCode(KoodiType koodiType) {
        List<KoodiMetadataType> metadata = koodiType.getMetadata();
        Map<String, String> name = Maps.newHashMap();
        Map<String, String> shortName = Maps.newHashMap();
        Map<String, String> description = Maps.newHashMap();
        for (KoodiMetadataType koodiMetadataType : metadata) {
            String lang = koodiMetadataType.getKieli().value().toLowerCase();
            name.put(lang, koodiMetadataType.getNimi());
            shortName.put(lang, koodiMetadataType.getLyhytNimi());
            description.put(lang, koodiMetadataType.getKuvaus());
        }
        return new Code(koodiType.getKoodiArvo(), new I18nText(name, shortName), new I18nText(description));
    }




}
