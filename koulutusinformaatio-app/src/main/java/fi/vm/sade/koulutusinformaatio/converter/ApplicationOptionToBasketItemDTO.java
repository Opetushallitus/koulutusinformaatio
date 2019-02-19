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

package fi.vm.sade.koulutusinformaatio.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOSRef;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.dto.BasketApplicationOptionDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.BasketItemDTO;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;

/**
 * @author Mikko Majapuro
 */
public final class ApplicationOptionToBasketItemDTO {

    private ApplicationOptionToBasketItemDTO() {
    }

    private final static String FALLBACK_LANG = "fi";
    private final static String HAKUTAPA_JATKUVA = "03";
    private final static String HAKU_GENERIC_ID = "erikseenHaettavatHakukohteet";

    public static List<BasketItemDTO> convert(final List<ApplicationOption> aos, String uiLang) {
        if (aos != null) {

            Map<String, BasketItemDTO> items = Maps.newHashMap();
            for (ApplicationOption ao : aos) {

                String lang = null;
                try {
                    if (ao.getTeachingLanguages().size() == 1) {
                        lang = ao.getTeachingLanguages().get(0).toLowerCase();
                    } else {
                        lang = uiLang;
                    }
                } catch (Exception e) {
                    lang = FALLBACK_LANG;
                }
                if (Strings.isNullOrEmpty(lang)) {
                    lang = FALLBACK_LANG;
                }

                BasketApplicationOptionDTO aoDTO = new BasketApplicationOptionDTO();
                aoDTO.setId(ao.getId());
                aoDTO.setType(ao.getType());
                aoDTO.setName(ConverterUtil.getTextByLanguageUseFallbackLang(ao.getName(), lang));
                aoDTO.setEducationDegree(ao.getEducationDegree());
                aoDTO.setSora(ao.isSora());
                aoDTO.setTeachingLanguages(ao.getTeachingLanguages());
                aoDTO.setParent(ParentLOSRefToDTO.convert(ao.getParent(), lang));
                aoDTO.setLosRefs(HigherEducationLOSRefToDTO.convert(ao.getHigherEdLOSRefs(), lang));
                aoDTO.setChildren(ChildLOIRefToDTO.convert(ao.getChildLOIRefs(), lang));
                aoDTO.setAttachmentDeliveryDeadline(ao.getAttachmentDeliveryDeadline());
                aoDTO.setAttachments(ApplicationOptionAttachmentToDTO.convertAll(ao.getAttachments(), lang));
                aoDTO.setExams(ExamToDTO.convertAll(ao.getExams(), lang));
                aoDTO.setAoIdentifier(ao.getAoIdentifier());
                aoDTO.setKaksoistutkinto(ao.isKaksoistutkinto());
                aoDTO.setVocational(ao.isVocational());
                aoDTO.setEducationCodeUri(ao.getEducationCodeUri());
                aoDTO.setEducationTypeUri(createEducationTypeUri(ao.getEducationTypeUri()));
                aoDTO.setPrerequisite(CodeToDTO.convert(ao.getPrerequisite(), lang));
                aoDTO.setKotitalous(ao.getEducationCodeUri() != null && ao.getEducationCodeUri().contains(TarjontaConstants.KOTITALOUSKOODI));
                aoDTO.setHakuaikaId(ao.getInternalASDateRef());
                aoDTO.setPseudo(ao.isPseudo());
                aoDTO.setPaid(ao.isPaid());
                aoDTO.setTunnistusKaytossa(ao.isTunnistusKaytossa());
                aoDTO.setKysytaanHarkinnanvaraiset(ao.isKysytaanHarkinnanvaraiset());
                aoDTO.setCanBeApplied(ConverterUtil.isOngoing(ao.getApplicationDates()));

                List<String> requiredBaseEducations = ao.getRequiredBaseEducations() == null ? Lists.<String>newArrayList() : ao.getRequiredBaseEducations();
                aoDTO.setBaseEducationRequirement(Joiner.on(",").join(requiredBaseEducations));

                ParentLOSRef los = ao.getParent();
                if (los != null) {
                    aoDTO.setHigherEducation(TarjontaConstants.TYPE_KK.equals(los.getLosType()));
                }
                Provider provider = ao.getProvider();
                if (provider != null) {
                    aoDTO.setProviderId(provider.getId());
                    aoDTO.setAthleteEducation(provider.isAthleteEducation() || ao.isAthleteEducation());
                    aoDTO.setProviderName(ConverterUtil.getTextByLanguageUseFallbackLang(provider.getName(), lang));
                    if (provider.getVisitingAddress() != null) {
                        aoDTO.setProviderLocation(ConverterUtil.getTextByLanguageUseFallbackLang(provider.getVisitingAddress().getPostOffice(), lang));
                    }
                }
                ApplicationSystem as = ao.getApplicationSystem();

                if(as.getAtaruFormKey() != null) {
                    aoDTO.setAtaruFormKey(as.getAtaruFormKey());
                    aoDTO.setApplicationDates(DateRangeToDTO.convert(ao.getApplicationDates()));
                }

                // add to generic application system pool (erikseen haettavat hakukohteet)
                if (as.getMaxApplications() <= 1 || isHakutapaJatkuva(as) || ao.isSpecificApplicationDates() || as.getApplicationFormLink() != null) {

                    aoDTO.setApplicationDates(DateRangeToDTO.convert(ao.getApplicationDates()));
                    aoDTO.setNextApplicationPeriodStarts(ConverterUtil.resolveNextDateRangeStart(ao.getApplicationDates()));

                    // set hakutapa for application option
                    aoDTO.setHakutapaUri(as.getHakutapaUri());

                    // set application form link from application system to application option
                    aoDTO.setApplicationFormLink(as.getApplicationFormLink());

                    // set flag telling if system application form is used
                    aoDTO.setUseSystemApplicationForm(as.isUseSystemApplicationForm());

                    // set application system id and name for application option (used for routing to correct application form)
                    aoDTO.setAsId(as.getId());
                    aoDTO.setAsName(ConverterUtil.getTextByLanguageUseFallbackLang(as.getName(), lang));

                    if (items.containsKey(HAKU_GENERIC_ID)) {
                        items.get(HAKU_GENERIC_ID).getApplicationOptions().add(aoDTO);
                    } else {
                        BasketItemDTO basketItem = new BasketItemDTO();
                        basketItem.setApplicationSystemId(HAKU_GENERIC_ID);
                        basketItem.setMaxApplicationOptions(1);
                        basketItem.getApplicationOptions().add(aoDTO);
                        items.put(HAKU_GENERIC_ID, basketItem);
                    }
                } else {
                    String asId = generateAsId(as, aoDTO);
                    if (as != null && items.containsKey(asId)) {
                        items.get(asId).getApplicationOptions().add(aoDTO);
                    } else if (as != null) {
                        BasketItemDTO basketItem = new BasketItemDTO();
                        basketItem.setApplicationFormLink(as.getApplicationFormLink());
                        basketItem.setUseSystemApplicationForm(as.isUseSystemApplicationForm());
                        basketItem.setMaxApplicationOptions(as.getMaxApplications());
                        basketItem.setApplicationSystemId(as.getId());
                        basketItem.getApplicationOptions().add(aoDTO);
                        basketItem.setApplicationSystemName(ConverterUtil.getTextByLanguageUseFallbackLang(as.getName(), uiLang));
                        basketItem.setApplicationDates(DateRangeToDTO.convert(ao.getApplicationDates()));
                        basketItem.setAsOngoing(ConverterUtil.isOngoing(ao.getApplicationDates()));
                        basketItem.setNextApplicationPeriodStarts(ConverterUtil.resolveNextDateRangeStart(ao.getApplicationDates()));

                        items.put(asId, basketItem);
                    }
                }
            }
            items = reorder(items);
            return Lists.newArrayList(items.values());
        } else {
            return null;
        }
    }

    // FIXME: Purkkakorjaus kunnes V1 on toteutettu loppuun
    private static Map<String, BasketItemDTO> reorder(Map<String, BasketItemDTO> items) {
        Map<String, BasketItemDTO> reordered = new HashMap<String, BasketItemDTO>();
        for (String s : items.keySet()) {
            BasketItemDTO item = items.get(s);
            if (s.endsWith("_null")) { // V0 application option without hakuaikaID should be merged with V1 ao if one exists
                String v1Key = searchPartialKey(items, s.split("_")[0]);
                if (v1Key != null) { // Existing key found
                    appendAOs(reordered, v1Key, item);
                } else {
                    appendAOs(reordered, s, item); // There was no V1 haku, put the item back
                }
            } else {
                appendAOs(reordered, s, item);
            }
        }
        return reordered;
    }

    private static void appendAOs(Map<String, BasketItemDTO> reordered, String keyToBeAppended, BasketItemDTO source) {
        if (reordered.containsKey(keyToBeAppended)) {
            reordered.get(keyToBeAppended).getApplicationOptions().addAll(source.getApplicationOptions());
        } else {
            reordered.put(keyToBeAppended, source);
        }
    }

    private static String searchPartialKey(Map<String, BasketItemDTO> items, String s) {
        for (String key : items.keySet()) {
            if (key.contains(s) && !key.contains("_null"))
                return key;
        }

        return null;
    }

    private static String createEducationTypeUri(String educationTypeUri) {

        if (educationTypeUri != null) {
            return educationTypeUri.replace(".", "");
        }

        return null;
    }

    private static boolean isHakutapaJatkuva(ApplicationSystem as) {
        if (as.getHakutapaUri() != null && as.getHakutapaUri().equals(HAKUTAPA_JATKUVA)) {
            return true;
        }

        return false;
    }

    private static String generateAsId(ApplicationSystem as, BasketApplicationOptionDTO aoDTO) {
        return as.getId() + "_" + aoDTO.getHakuaikaId();
    }

}
