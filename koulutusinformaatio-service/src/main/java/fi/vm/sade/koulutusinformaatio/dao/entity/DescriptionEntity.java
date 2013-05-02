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

/**
 * @author Mikko Majapuro
 */
@Embedded
public class DescriptionEntity {

    @Embedded
    private I18nTextEntity accessToFurtherStudies;
    @Embedded
    private I18nTextEntity educationAndProfessionalGoals;
    @Embedded
    private I18nTextEntity selectionOfDegreeProgram;
    @Embedded
    private I18nTextEntity structureDiagram;

    public DescriptionEntity() {}

    public I18nTextEntity getAccessToFurtherStudies() {
        return accessToFurtherStudies;
    }

    public void setAccessToFurtherStudies(I18nTextEntity accessToFurtherStudies) {
        this.accessToFurtherStudies = accessToFurtherStudies;
    }

    public I18nTextEntity getEducationAndProfessionalGoals() {
        return educationAndProfessionalGoals;
    }

    public void setEducationAndProfessionalGoals(I18nTextEntity educationAndProfessionalGoals) {
        this.educationAndProfessionalGoals = educationAndProfessionalGoals;
    }

    public I18nTextEntity getSelectionOfDegreeProgram() {
        return selectionOfDegreeProgram;
    }

    public void setSelectionOfDegreeProgram(I18nTextEntity selectionOfDegreeProgram) {
        this.selectionOfDegreeProgram = selectionOfDegreeProgram;
    }

    public I18nTextEntity getStructureDiagram() {
        return structureDiagram;
    }

    public void setStructureDiagram(I18nTextEntity structureDiagram) {
        this.structureDiagram = structureDiagram;
    }
}
