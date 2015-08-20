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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;

import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;

/**
 * @author Hannu Lyytikainen
 */
public abstract class ObjectCreator {

    protected static final String AIHEET_KOODISTO_URI = "aiheet";
    protected static final String TEEMAT_KOODISTO_URI = "teemat";
    protected static final String POHJAKOULUTUSFASETTI_KOODISTO_URI = "pohjakoulutusfasetti";



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
            return new I18nText(translations);
        }
        return null;
    }

    protected I18nText mergeI18nTexts(final I18nText t1, final I18nText t2) throws KoodistoException {
        if (t1 == null && t2 == null) {
            return null;
        }
        Map<String, String> translations = new HashMap<String, String>();
        if (t1 != null && t1.getTranslations() != null) {
            translations.putAll(t1.getTranslations());
        }
        if (t2 != null && t2.getTranslations() != null) {
            translations.putAll(t2.getTranslations());
        }
        return new I18nText(translations);
    }

    protected I18nText getI18nTextEnriched(NimiV1RDTO rawMaterial) throws KoodistoException {
        final Map<String, String> texts = (rawMaterial != null) ? rawMaterial.getTekstis() : null;
        if (texts != null && !texts.isEmpty()) {
            Map<String, String> translations = new HashMap<String, String>();
            Iterator<Map.Entry<String, String>> i = texts.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<String, String> entry = i.next();
                if (!Strings.isNullOrEmpty(entry.getKey()) && !Strings.isNullOrEmpty(entry.getValue())) {
                    try {
                        String key = rawMaterial.getMeta().get(entry.getKey()).getArvo();//koodistoService.searchFirstCodeValue(entry.getKey());
                        key = (key == null) ? rawMaterial.getMeta().get(entry.getKey()).getKieliArvo() : key;
                        String val = entry.getValue() != null ? entry.getValue() : "";
                        translations.put(key.toLowerCase(), val);
                    } catch (Exception ex) {
                        throw new KoodistoException(ex.getMessage());
                    }

                }
            }
            I18nText i18nText = new I18nText();
            i18nText.setTranslations(translations);
            return i18nText;
        }
        return null;
    }
    
    protected List<String> getTranslationUris(NimiV1RDTO rawMaterial) {
        List<String> translUris = new ArrayList<String>();
        final Map<String, String> texts = (rawMaterial != null) ? rawMaterial.getTekstis() : null;
        if (texts != null && !texts.isEmpty()) {
            Iterator<Map.Entry<String, String>> i = texts.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<String, String> entry = i.next();
                if (!Strings.isNullOrEmpty(entry.getKey()) && !Strings.isNullOrEmpty(entry.getValue())) {
                    translUris.add(entry.getKey());
                }
            }
        }
        
        return translUris;
    }

    protected I18nText getI18nTextEnriched(Map<String, KoodiV1RDTO> meta) throws KoodistoException {
        if (meta != null && !meta.isEmpty()) {
            Map<String, String> translations = new HashMap<String, String>();
            Iterator<Map.Entry<String, KoodiV1RDTO>> i = meta.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<String, KoodiV1RDTO> entry = i.next();
                if (!Strings.isNullOrEmpty(entry.getKey()) && (entry.getValue() != null)) {
                    try {
                        String key = entry.getValue().getKieliArvo();
                        String kielikaannos = entry.getValue().getNimi() != null ? entry.getValue().getNimi() : "";
                        translations.put(key.toLowerCase(), kielikaannos);
                    } catch (Exception ex) {
                        throw new KoodistoException(ex.getMessage());
                    }

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

    protected List<Code> getThemes(LOS los) throws KoodistoException {
        Set<Code> set = new HashSet<Code>();
        List<Code> updatedTopics = new ArrayList<Code>();
        for (Code curTopic : los.getTopics()) {
            List<Code> superCodes = koodistoService.searchSuperCodes(curTopic.getUri(), TEEMAT_KOODISTO_URI);
            set.addAll(superCodes);
            if (!superCodes.isEmpty()) {
                curTopic.setParent(superCodes.get(0));
                curTopic.setUri(String.format("%s.%s", curTopic.getParent().getUri(), curTopic.getUri()));
            } 
            updatedTopics.add(curTopic);
        }

        los.setTopics(updatedTopics);

        return new ArrayList<Code>(set);
    }

    protected List<I18nText> getI18nTextMultiple(KoodiUrisV1RDTO opetusmuodos) throws KoodistoException {
        List<I18nText> opetusmuodot = new ArrayList<I18nText>();
        if (opetusmuodos != null && opetusmuodos.getMeta() != null && !opetusmuodos.getMeta().isEmpty()) {

            Iterator<Map.Entry<String, KoodiV1RDTO>> i = opetusmuodos.getMeta().entrySet().iterator();

            while (i.hasNext()) {
                Map.Entry<String, KoodiV1RDTO> entry = i.next();
                if (entry.getValue() != null && entry.getValue().getMeta() != null) {
                    I18nText text = this.getI18nTextEnriched(entry.getValue().getMeta());
                    if (text != null) {
                        opetusmuodot.add(text);
                    }
                }
            }


        }
        return opetusmuodot;
    }

    protected List<Code> createCodes(KoodiUrisV1RDTO opetuskielis) throws KoodistoException {
        List<Code> codes = new ArrayList<Code>();
        if (opetuskielis != null && opetuskielis.getMeta() != null) {
            for (KoodiV1RDTO curKoodi : opetuskielis.getMeta().values()) {
                codes.addAll(koodistoService.search(curKoodi.getUri()));
            }
        }
        return codes;
    }

    protected Code createCode(KoodiV1RDTO koodi) throws KoodistoException {
        if (koodi == null) {
            return null;
        }
        return koodistoService.searchFirst(koodi.getUri());
    }

    protected I18nText getI18nText(String text, String kieliUri) {
        if (StringUtils.isEmpty(text) || StringUtils.isEmpty(kieliUri))
            return null;
        I18nText type = new I18nText();
        Map<String, String> translations = new HashMap<String,String>();
        String lang = kieliUri.substring(kieliUri.length() - 2);
        translations.put(lang, text);
        type.setTranslations(translations);
        return type;
    }
    
    protected List<Code> getFacetPrequisites(List<Code> rawPrereqs) throws KoodistoException {
        
        List<Code> facetPrereqs = new ArrayList<Code>();
        if (rawPrereqs != null) {
            for (Code curRawPrereq : rawPrereqs) {
                if (curRawPrereq != null && curRawPrereq.getUri() != null) {
                    facetPrereqs.addAll(koodistoService.searchSuperCodes(curRawPrereq.getUri(), POHJAKOULUTUSFASETTI_KOODISTO_URI));
                }
            }
        }
        return facetPrereqs;
    }

}
