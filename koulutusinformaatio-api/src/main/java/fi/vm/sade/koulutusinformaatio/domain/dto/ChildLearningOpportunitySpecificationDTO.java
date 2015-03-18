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

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @author Hannu Lyytikainen
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ChildLearningOpportunitySpecificationDTO {

    private String id;
    private String name;
    private String qualification;
    private List<String> qualifications;
    private String degreeTitle;
    private List<String> degreeTitles;
    private ParentLOSRefDTO parent;
    private String goals;
    private List<ChildLearningOpportunityInstanceDTO> lois;
    private String translationLanguage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public ParentLOSRefDTO getParent() {
        return parent;
    }

    public void setParent(ParentLOSRefDTO parent) {
        this.parent = parent;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public List<ChildLearningOpportunityInstanceDTO> getLois() {
        return lois;
    }

    public void setLois(List<ChildLearningOpportunityInstanceDTO> lois) {

        this.lois = lois;
    }

    public String getTranslationLanguage() {
        return translationLanguage;
    }

    public void setTranslationLanguage(String translationLanguage) {
        this.translationLanguage = translationLanguage;
    }

	public List<String> getDegreeTitles() {
		return degreeTitles;
	}

	public void setDegreeTitles(List<String> degreeTitles) {
		this.degreeTitles = degreeTitles;
	}

    public List<String> getQualifications() {
        return qualifications;
    }

    public void setQualifications(List<String> qualifications) {
        this.qualifications = qualifications;
    }

}
