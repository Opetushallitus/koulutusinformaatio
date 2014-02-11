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

package fi.vm.sade.koulutusinformaatio.domain;

import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Instance of a parent level learning opportunity specification.
 *
 * @author Hannu Lyytikainen
 */
public class ParentLOI {

    private String id;
    private Set<ApplicationOption> applicationOptions = Sets.newHashSet();
    private I18nText selectingDegreeProgram;
    private Code prerequisite;
    private List<ChildLOIRef> childRefs = new ArrayList<ChildLOIRef>();
    private List<String> availableTranslationLanguages;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<ApplicationOption> getApplicationOptions() {
        return applicationOptions;
    }

    public void setApplicationOptions(Set<ApplicationOption> applicationOptions) {
        this.applicationOptions = applicationOptions;
    }

    public Code getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(Code prerequisite) {
        this.prerequisite = prerequisite;
    }

    public I18nText getSelectingDegreeProgram() {
        return selectingDegreeProgram;
    }

    public void setSelectingDegreeProgram(I18nText selectingDegreeProgram) {
        this.selectingDegreeProgram = selectingDegreeProgram;
    }

    public List<ChildLOIRef> getChildRefs() {
        return childRefs;
    }

    public void setChildRefs(List<ChildLOIRef> childRefs) {
        this.childRefs = childRefs;
    }

    public List<String> getAvailableTranslationLanguages() {
        return availableTranslationLanguages;
    }

    public void setAvailableTranslationLanguages(List<String> availableTranslationLanguages) {
        this.availableTranslationLanguages = availableTranslationLanguages;
    }
}
