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

import java.util.ArrayList;
import java.util.List;

/**
 * Instance of a parent level learning opportunity specification.
 *
 * @author Hannu Lyytikainen
 */
public class ParentLOI {

    private String id;
    private I18nText selectingEducation;
    private Code prerequisite;
    private List<ChildLearningOpportunity> children;
    private List<ChildLORef> childRefs = new ArrayList<ChildLORef>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Code getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(Code prerequisite) {
        this.prerequisite = prerequisite;
    }

    public I18nText getSelectingEducation() {
        return selectingEducation;
    }

    public void setSelectingEducation(I18nText selectingEducation) {
        this.selectingEducation = selectingEducation;
    }

    public List<ChildLearningOpportunity> getChildren() {
        return children;
    }

    public void setChildren(List<ChildLearningOpportunity> children) {
        this.children = children;
    }

    public List<ChildLORef> getChildRefs() {
        return childRefs;
    }

    public void setChildRefs(List<ChildLORef> childRefs) {
        this.childRefs = childRefs;
    }
}
