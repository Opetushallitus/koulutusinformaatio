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

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Reference;

import java.util.List;

/**
 * @author Mikko Majapuro
 */
@Embedded
public class ParentLearningOpportunityInstanceEntity {

    private String id;
    @Reference
    private List<ChildLearningOpportunityEntity> children;
    @Embedded
    private I18nTextEntity prerequisite;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ChildLearningOpportunityEntity> getChildren() {
        return children;
    }

    public void setChildren(List<ChildLearningOpportunityEntity> children) {
        this.children = children;
    }

    public I18nTextEntity getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(I18nTextEntity prerequisite) {
        this.prerequisite = prerequisite;
    }
}
