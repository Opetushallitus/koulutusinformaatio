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

package fi.vm.sade.koulutusinformaatio.service.builder.impl;

import com.google.common.base.Strings;

import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Hannu Lyytikainen
 */
public abstract class ObjectCreator {

    protected static final String ATHLETE_EDUCATION_KOODISTO_URI = "urheilijankoulutus_1#1";
    protected static final String APPLICATION_OPTIONS_KOODISTO_URI = "hakukohteet";
    protected static final String AIHEET_KOODISTO_URI = "aiheet";
    protected static final String TEEMAT_KOODISTO_URI = "teemat";
    
    

    KoodistoService koodistoService;

    protected ObjectCreator(KoodistoService koodistoService) {
        this.koodistoService = koodistoService;
    }

    protected I18nText getI18nText(final Map<String, String> texts) throws KoodistoException {
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
    
    protected List<Code> getTopics(String opintoalaKoodiUri) throws KoodistoException {
        return koodistoService.searchSuperCodes(opintoalaKoodiUri, AIHEET_KOODISTO_URI);
    }
    
    protected List<Code> getThemes(List<Code> topics) throws KoodistoException {
        Set<Code> set = new HashSet<Code>();
        for (Code curTopic : topics) {
            List<Code> superCodes = koodistoService.searchSuperCodes(curTopic.getValue(), TEEMAT_KOODISTO_URI);
            set.addAll(superCodes);
            if (!superCodes.isEmpty()) {
                curTopic.setParent(superCodes.get(0));
            }
        }
        return new ArrayList<Code>(set);
    }
    
    
    
    
    



}
