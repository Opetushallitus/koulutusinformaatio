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

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

import java.util.List;
import java.util.Set;

/**
 * @author Mikko Majapuro
 */
@Entity("learningOpportunityProviders")
public class LearningOpportunityProviderEntity {

    @Id
    private String id;
    private String name;
    private Set<String> applicationSystemIds;

    public LearningOpportunityProviderEntity() {
    }

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

    public Set<String> getApplicationSystemIds() {
        return applicationSystemIds;
    }

    public void setApplicationSystemIds(Set<String> applicationSystemIds) {
        this.applicationSystemIds = applicationSystemIds;
    }
}
