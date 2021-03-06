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

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.KoulutusLOS;
import fi.vm.sade.koulutusinformaatio.domain.TutkintoLOS;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationSystemDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ChildLOIRefDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.TutkintoLOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.exception.KIConversionException;

/**
 * @author Mikko Majapuro
 */
public final class TutkintoLOSToDTO {

    public static TutkintoLOSDTO convert(final TutkintoLOS tutkintoLOS,
                                                                    final String lang,
                                                                    final String uiLang,
                                                                    final String defaultLang,
                                                                    final String prerequisite) {

        TutkintoLOSDTO parent = new TutkintoLOSDTO();
        parent.setId(tutkintoLOS.getId());
        parent.setName(ConverterUtil.getTextByLanguage(tutkintoLOS.getName(), lang));
        parent.setEducationDegree(tutkintoLOS.getEducationDegree());
        parent.setProvider(ProviderToDTO.convert(tutkintoLOS.getProvider(), lang, defaultLang, uiLang));
        parent.setStructure(ConverterUtil.getTextByLanguage(tutkintoLOS.getStructure(), lang));
        parent.setAccessToFurtherStudies(ConverterUtil.getTextByLanguage(tutkintoLOS.getAccessToFurtherStudies(), lang));
        parent.setGoals(ConverterUtil.getTextByLanguage(tutkintoLOS.getGoals(), lang));
        parent.setEducationDomain(ConverterUtil.getTextByLanguage(tutkintoLOS.getEducationDomain(), uiLang));
        parent.setStydyDomain(ConverterUtil.getTextByLanguage(tutkintoLOS.getStydyDomain(), uiLang));
        parent.setTranslationLanguage(lang);
        ArrayList<Code> languages = new ArrayList<Code>();
        languages.addAll(tutkintoLOS.getTeachingLanguages());
        parent.setTeachingLanguages(CodeToDTO.convertAll(languages, lang));
        if (tutkintoLOS.getEducationCode() != null) {
            parent.setKoulutuskoodi(tutkintoLOS.getEducationCode().getUri());
        }

        try {
            KoulutusLOS latestLoi = tutkintoLOS.getLatestLoi();
            parent.setCreditValue(latestLoi.getCreditValue());
            parent.setCreditUnit(ConverterUtil.getTextByLanguage(latestLoi.getCreditUnit(), uiLang));
        } catch (Exception e) {
            parent.setCreditValue(tutkintoLOS.getCreditValue());
            parent.setCreditUnit(ConverterUtil.getTextByLanguage(tutkintoLOS.getCreditUnit(), uiLang));
        }

        if (tutkintoLOS.getThemes() != null) {
            parent.setThemes(CodeToDTO.convertCodesDistinct(tutkintoLOS.getThemes(), uiLang));
        }
        if (tutkintoLOS.getTopics() != null) {
            parent.setTopics(CodeToDTO.convertAll(tutkintoLOS.getTopics(), uiLang));
        }

        String fetchWithPrerequisite = prerequisite;
        if(prerequisite != null && prerequisite.equals("UUSI")){
            fetchWithPrerequisite = null;
        }

        if (!tutkintoLOS.getChildEducations().isEmpty()) {

            SetMultimap<ApplicationSystem, ApplicationOption> aoByAs = HashMultimap.create();

            // Koulutusohjelman valinta on aina sama tutkinnon kaikille komotoille, joilla on sama PK-vaatimus
            KoulutusLOS firstChild = getFirstLosWithMatchingPrerequisite(tutkintoLOS.getChildEducations(), fetchWithPrerequisite);
            if (firstChild == null) {
                throw new KIConversionException("No matching child educations!");
            }
            parent.setSelectingDegreeProgram(ConverterUtil.getTextByLanguage(firstChild.getSelectingDegreeProgram(), lang));

            for (KoulutusLOS child : tutkintoLOS.getChildEducations()) {

                if (!isSamePrerequisite(fetchWithPrerequisite, child)) {
                    continue;
                }

                ChildLOIRefDTO childDto = new ChildLOIRefDTO();
                childDto.setId(child.getId());
                childDto.setName(ConverterUtil.getTextByLanguageUseFallbackLang(child.getName(), lang));
                childDto.setPrerequisite(CodeToDTO.convert(child.getKoulutusPrerequisite(), lang));
                parent.getChildren().add(childDto);

                if (child.getApplicationOptions() != null) {
                    for (ApplicationOption ao : child.getApplicationOptions()) {
                        aoByAs.put(ao.getApplicationSystem(), ao);
                    }
                }
            }

            for (ApplicationSystem as : aoByAs.keySet()) {
                ApplicationSystemDTO asDTO = ApplicationSystemToDTO.convert(as, uiLang);
                asDTO.setStatus(as.getStatus());
                for (ApplicationOption ao : aoByAs.get(as)) {
                    asDTO.getApplicationOptions().add(ApplicationOptionToDTO.convertHigherEducation(ao, lang, uiLang, lang));
                }
                parent.getApplicationSystems().add(asDTO);
            }
        }

        if (parent.getChildren().isEmpty())
            throw new KIConversionException("Tutkinnolla " + parent.getId() + " ei ole koulutuksia pohjakoulutuksella " + prerequisite);

        return parent;
    }

    public static KoulutusLOS getFirstLosWithMatchingPrerequisite(List<KoulutusLOS> loses, String prerequisite) {
        for (KoulutusLOS los : loses) {
            if (isSamePrerequisite(prerequisite, los)) {
                return los;
            }
        }
        return null;
    }

    public static boolean isSamePrerequisite(String prerequisite, KoulutusLOS child) {
        if (prerequisite == null) {
            return true;
        }
        if (child.getKoulutusPrerequisite() == null
                || !prerequisite.equals(child.getKoulutusPrerequisite().getValue())) {
            return false;
        }
        return true;
    }
}
