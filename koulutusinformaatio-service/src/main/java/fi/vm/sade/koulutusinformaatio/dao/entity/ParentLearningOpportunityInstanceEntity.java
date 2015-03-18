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

package fi.vm.sade.koulutusinformaatio.dao.entity;

import java.util.List;
import java.util.Set;

import org.mongodb.morphia.annotations.Embedded;

import com.google.common.collect.Sets;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;

/**
 * @author Mikko Majapuro
 */
@Embedded
public class ParentLearningOpportunityInstanceEntity {

    private String id;
    @Embedded
    private List<ChildLOIRefEntity> childRefs;
    @Embedded
    private CodeEntity prerequisite;
    @Embedded
    private I18nTextEntity selectingDegreeProgram;
    @Embedded
    private Set<ApplicationOption> applicationOptions = Sets.newHashSet();
    @Embedded
    private List<CodeEntity> availableTranslationLanguages;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CodeEntity getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(CodeEntity prerequisite) {
        this.prerequisite = prerequisite;
    }

    public List<ChildLOIRefEntity> getChildRefs() {
        return childRefs;
    }

    public void setChildRefs(List<ChildLOIRefEntity> childRefs) {
        this.childRefs = childRefs;
    }

    public I18nTextEntity getSelectingDegreeProgram() {
        return selectingDegreeProgram;
    }

    public void setSelectingDegreeProgram(I18nTextEntity selectingDegreeProgram) {
        this.selectingDegreeProgram = selectingDegreeProgram;
    }

    public Set<ApplicationOption> getApplicationOptions() {
        return applicationOptions;
    }

    public void setApplicationOptions(Set<ApplicationOption> applicationOptions) {
        this.applicationOptions = applicationOptions;
    }

    public List<CodeEntity> getAvailableTranslationLanguages() {
        return availableTranslationLanguages;
    }

    public void setAvailableTranslationLanguages(List<CodeEntity> availableTranslationLanguages) {
        this.availableTranslationLanguages = availableTranslationLanguages;
    }
}
