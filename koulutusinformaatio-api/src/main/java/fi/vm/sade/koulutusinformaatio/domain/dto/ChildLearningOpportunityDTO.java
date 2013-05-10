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

package fi.vm.sade.koulutusinformaatio.domain.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ChildLearningOpportunityDTO {

    private String losId;
    private String loiId;
    private String name;
    private String qualification;
    private String degreeTitle;
    private ApplicationOptionDTO applicationOption;
    private List<ChildLORefDTO> relatedChildLOs = new ArrayList<ChildLORefDTO>();

    private String translationLanguage;
    private List<String> availableTranslationLanguages = new ArrayList<String>();

    public String getLosId() {
        return losId;
    }

    public void setLosId(String losId) {
        this.losId = losId;
    }

    public String getLoiId() {
        return loiId;
    }

    public void setLoiId(String loiId) {
        this.loiId = loiId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ApplicationOptionDTO getApplicationOption() {
        return applicationOption;
    }

    public void setApplicationOption(ApplicationOptionDTO applicationOption) {
        this.applicationOption = applicationOption;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getDegreeTitle() {
        return degreeTitle;
    }

    public void setDegreeTitle(String degreeTitle) {
        this.degreeTitle = degreeTitle;
    }

    public String getTranslationLanguage() {
        return translationLanguage;
    }

    public void setTranslationLanguage(String translationLanguage) {
        this.translationLanguage = translationLanguage;
    }

    public List<String> getAvailableTranslationLanguages() {
        return availableTranslationLanguages;
    }

    public void setAvailableTranslationLanguages(List<String> availableTranslationLanguages) {
        this.availableTranslationLanguages = availableTranslationLanguages;
    }

    public List<ChildLORefDTO> getRelatedChildLOs() {
        return relatedChildLOs;
    }

    public void setRelatedChildLOs(List<ChildLORefDTO> relatedChildLOs) {
        this.relatedChildLOs = relatedChildLOs;
    }
}
