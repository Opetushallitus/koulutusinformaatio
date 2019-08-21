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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import fi.vm.sade.koulutusinformaatio.service.impl.metrics.RollingAverageLogger;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fi.vm.sade.koodisto.service.GenericFault;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.koodisto.util.KoodistoClient;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.CodeUriAndVersion;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;

/**
 * @author Mikko Majapuro
 */
@Service
public class KoodistoServiceImpl implements KoodistoService {

    private static Map<String, List<KoodiType>> koodisByKoodistoMap = new HashMap<String, List<KoodiType>>();
    private static Map<String, List<KoodiType>> koodiTypeMap = new HashMap<String, List<KoodiType>>();
    private static Map<String, List<KoodiType>> subkoodisMap = new HashMap<String, List<KoodiType>>();
    private static Map<String, List<KoodiType>> superkoodisMap = new HashMap<String, List<KoodiType>>();
    
    
    private final KoodistoClient koodiService;
    private final Pattern pattern;
    private static final String KOODI_URI_WITH_VERSION_PATTERN = "^[^#]+#\\d+$";
    private static final Logger LOGGER = LoggerFactory.getLogger(KoodistoServiceImpl.class);
    private final RollingAverageLogger rollingAverageLogger;

    @Autowired
    public KoodistoServiceImpl(final KoodistoClient koodiService, RollingAverageLogger rollingAverageLogger) {
        this.koodiService = koodiService;
        this.pattern = Pattern.compile(KOODI_URI_WITH_VERSION_PATTERN);
        this.rollingAverageLogger = rollingAverageLogger;
    }

    @Override
    public List<Code> search(String koodiUri) throws KoodistoException {
        if (Strings.isNullOrEmpty(koodiUri)) {
            return null;
        } else {
            LOGGER.debug("search koodi: {}", koodiUri);
            return convertAllToCode(searchKoodiTypes(koodiUri));
        }
    }

    @Override
    public List<Code> searchMultiple(List<String> koodiUris) throws KoodistoException {
        if (koodiUris == null) {
            return null;
        } else if (koodiUris.isEmpty()) {
            return Lists.newArrayList();
        }  else {
            List<Code> results = Lists.newArrayList();
            for (String koodiUri : koodiUris) {
                results.addAll(search(koodiUri));
            }
            return results;
        }
    }

    @Override
    public Code searchFirst(String koodiUri) throws KoodistoException {
        if (Strings.isNullOrEmpty(koodiUri)) {
            return null;
        } else {
            LOGGER.debug("search first koodi: {}", koodiUri);
            List<Code> koodis = search(koodiUri);
            if (koodis.size() < 1) {
                LOGGER.warn("No koodis found with uri: " + koodiUri);
                return null;
            }
            return koodis.get(0);
        }
    }

    @Override
    public List<Code> searchByKoodisto(String koodistoUri, Integer version) throws KoodistoException {
        try {
            String key = String.format("%s#%s", koodistoUri, version);
            List<KoodiType> codes = null;
            if (koodisByKoodistoMap.containsKey(key)) {
                codes = koodisByKoodistoMap.get(key);
            } else {
               rollingAverageLogger.start("getKoodisForKoodisto");
               codes = koodiService.getKoodisForKoodisto(koodistoUri, version, true);
               rollingAverageLogger.stop("getKoodisForKoodisto");
               koodisByKoodistoMap.put(key, codes);
            }
            if (codes == null || codes.isEmpty()) {
                LOGGER.warn(String.format("No koodis found with koodistoUri %s, version %d", koodistoUri, version));
            }
            return convertAllToCode(codes);
        } catch (GenericFault e) {
            throw new KoodistoException(e);
        }
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

    @Override
    public List<I18nText> searchNames(String koodiUri) throws KoodistoException {
        if (Strings.isNullOrEmpty(koodiUri)) {
            return null;
        } else {
            LOGGER.debug("search koodi: {}", koodiUri);
            return convertAllToName(searchKoodiTypes(koodiUri));
        }
    }

    @Override
    public I18nText searchFirstName(String koodiUri) throws KoodistoException {
        if (!Strings.isNullOrEmpty(koodiUri)) {
            LOGGER.debug("search first code: {}", koodiUri);
            List<Code> codes = search(koodiUri);
            if (codes != null && !codes.isEmpty()) {
                return searchNames(koodiUri).get(0);
            }
        }
        return null;
    }

    @Override
    public String searchFirstCodeValue(String koodiUri) throws KoodistoException {
        if (!Strings.isNullOrEmpty(koodiUri)) {
            Code code = searchFirst(koodiUri);
            if (code != null) {
                return code.getValue();
            }
        }
        return null;
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
        
        if (subkoodisMap.containsKey(koodiUriAndVersion)) {
            return subkoodisMap.get(koodiUriAndVersion);
        }
        if(StringUtils.isBlank(koodiUriAndVersion)){
            return Lists.newArrayList();
        }
        CodeUriAndVersion codeUriAndVersion = resolveKoodiUriAndVersion(koodiUriAndVersion);
        rollingAverageLogger.start("getAlakoodis");
        List<KoodiType> alakoodis = koodiService.getAlakoodis(codeUriAndVersion.getUri());
        rollingAverageLogger.stop("getAlakoodis");
        if (alakoodis == null || alakoodis.isEmpty()) {
            LOGGER.warn(String.format("No sub koodis found with koodi uri and version %s", koodiUriAndVersion));
        }
        subkoodisMap.put(koodiUriAndVersion, alakoodis);
        return alakoodis;
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
        if (superkoodisMap.containsKey(koodiUriAndVersion)) {
            return superkoodisMap.get(koodiUriAndVersion);
        }

        if(StringUtils.isBlank(koodiUriAndVersion)){
            return Lists.newArrayList();
        }
        CodeUriAndVersion codeUriAndVersion = resolveKoodiUriAndVersion(koodiUriAndVersion);
        rollingAverageLogger.start("getYlakoodis");
        List<KoodiType> ylakoodis = koodiService.getYlakoodis(codeUriAndVersion.getUri());
        rollingAverageLogger.stop("getYlakoodis");

        if (ylakoodis == null || ylakoodis.isEmpty()) {
            LOGGER.warn(String.format("No super koodis found with koodi uri and version %s", koodiUriAndVersion));
        }
        
        superkoodisMap.put(koodiUriAndVersion, ylakoodis);
        return ylakoodis;
    }

    private List<KoodiType> searchKoodiTypes(String koodiUri) throws KoodistoException {
        
        if (koodiTypeMap.containsKey(koodiUri)) {
            return koodiTypeMap.get(koodiUri);
        }

        CodeUriAndVersion codeUriAndVersion = resolveKoodiUriAndVersion(koodiUri);
        koodiTypeMap.put(koodiUri, getKoodiTypes(codeUriAndVersion));
        return koodiTypeMap.get(koodiUri);
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
            rollingAverageLogger.start("searchKoodis");
            List<KoodiType> codes = koodiService.searchKoodis(criteria);
            rollingAverageLogger.stop("searchKoodis");
            if (codes == null || codes.isEmpty()) {
                LOGGER.warn(String.format("No koodis found with search criteria: %s", searchCriteriaToString(criteria)));
            }
            return codes;
        } catch (Exception e) {
            throw new KoodistoException("Koodin haku termillä epäonnistui termillä: " + criteria + " : " + searchCriteriaToString(criteria), e);
        }
    }

    private String searchCriteriaToString(SearchKoodisCriteriaType sc) {
        return  String.format("URIs: %s Version: %d",
                Joiner.on(" ").join(sc.getKoodiUris()), sc.getKoodiVersio());
    }

    private List<I18nText> convertAllToName(final List<KoodiType> codes) {
        return Lists.transform(codes, new Function<KoodiType, I18nText>() {
            @Override
            public I18nText apply(fi.vm.sade.koodisto.service.types.common.KoodiType koodiType) {
                return convertToName(koodiType);
            }
        });
    }

    private List<I18nText> convertAllToShortName(final List<KoodiType> codes) {
        return Lists.transform(codes, new Function<KoodiType, I18nText>() {
            @Override
            public I18nText apply(fi.vm.sade.koodisto.service.types.common.KoodiType koodiType) {
                return convertToShortName(koodiType);
            }
        });
    }

    private I18nText convertToName(KoodiType koodiType) {
        List<KoodiMetadataType> metadata = koodiType.getMetadata();
        Map<String, String> translations = new HashMap<String, String>();
        for (KoodiMetadataType koodiMetadataType : metadata) {
            translations.put(koodiMetadataType.getKieli().value().toLowerCase(), koodiMetadataType.getNimi());
        }
        return new I18nText(translations);
    }

    private I18nText convertToShortName(KoodiType koodiType) {
        List<KoodiMetadataType> metadata = koodiType.getMetadata();
        Map<String, String> translations = new HashMap<String, String>();
        for (KoodiMetadataType koodiMetadataType : metadata) {
            if (koodiMetadataType.getLyhytNimi() == null) {
                translations.put(koodiMetadataType.getKieli().value().toLowerCase(), koodiMetadataType.getNimi());
            } else {
                translations.put(koodiMetadataType.getKieli().value().toLowerCase(), koodiMetadataType.getLyhytNimi());
            }
        }
        return new I18nText(translations);
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
            String nameStr = koodiMetadataType.getNimi() != null ? koodiMetadataType.getNimi() : "";
            name.put(lang, nameStr);
            String shortNameStr = koodiMetadataType.getLyhytNimi() != null ? koodiMetadataType.getLyhytNimi() : "";
            shortName.put(lang, shortNameStr);
            String descrStr = koodiMetadataType.getKuvaus() != null ? koodiMetadataType.getKuvaus() : "";
            description.put(lang, descrStr);
        }
        return new Code(koodiType.getKoodiArvo(), new I18nText(name), new I18nText(shortName), new I18nText(description), koodiType.getKoodiUri());
    }

    @Override
    public void clearCache() {
        koodisByKoodistoMap = new HashMap<String,List<KoodiType>>();
        koodiTypeMap = new HashMap<String,List<KoodiType>>();
        subkoodisMap = new HashMap<String,List<KoodiType>>();
        superkoodisMap = new HashMap<String,List<KoodiType>>();
    }
    
}
