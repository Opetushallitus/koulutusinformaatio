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
 * @author Hannu Lyytikainen
 */
public class ApplicationOption {

    private String id;
    private I18nText name;
    private String applicationSystemId;
    private String educationDegree;
    private List<I18nText> childLOINames = new ArrayList<I18nText>();
    private LearningOpportunityProvider provider;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public I18nText getName() {
        return name;
    }

    public void setName(I18nText name) {
        this.name = name;
    }

    public String getApplicationSystemId() {
        return applicationSystemId;
    }

    public void setApplicationSystemId(String applicationSystemId) {
        this.applicationSystemId = applicationSystemId;
    }

    public String getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(String educationDegree) {
        this.educationDegree = educationDegree;
    }

    public List<I18nText> getChildLOINames() {
        return childLOINames;
    }

    public void setChildLOINames(List<I18nText> childLOINames) {
        this.childLOINames = childLOINames;
    }

    public LearningOpportunityProvider getProvider() {
        return provider;
    }

    public void setProvider(LearningOpportunityProvider provider) {
        this.provider = provider;
    }
}
