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

package fi.vm.sade.koulutusinformaatio.domain.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Mikko Majapuro
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ParentLearningOpportunityInstanceDTO {

    private String id;
    private String selectingDegreeProgram;
    private CodeDTO prerequisite;
    private Set<ApplicationSystemDTO> applicationSystems = new HashSet<ApplicationSystemDTO>();


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CodeDTO getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(CodeDTO prerequisite) {
        this.prerequisite = prerequisite;
    }

    public String getSelectingDegreeProgram() {
        return selectingDegreeProgram;
    }

    public void setSelectingDegreeProgram(String selectingDegreeProgram) {
        this.selectingDegreeProgram = selectingDegreeProgram;
    }

    public Set<ApplicationSystemDTO> getApplicationSystems() {
        return applicationSystems;
    }

    public void setApplicationSystems(Set<ApplicationSystemDTO> applicationSystems) {
        this.applicationSystems = applicationSystems;
    }
}
