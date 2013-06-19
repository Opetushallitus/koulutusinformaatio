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

import java.util.List;

/**
 * Instance of a parent level learning opportunity specification.
 *
 * @author Hannu Lyytikainen
 */
public class ParentLOI {

    private String id;
    private I18nText prerequisite;
    private List<ChildLearningOpportunity> children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public I18nText getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(I18nText prerequisite) {
        this.prerequisite = prerequisite;
    }

    public List<ChildLearningOpportunity> getChildren() {
        return children;
    }

    public void setChildren(List<ChildLearningOpportunity> children) {
        this.children = children;
    }
}
