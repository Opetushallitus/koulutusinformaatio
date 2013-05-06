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
 * @author Hannu Lyytikainen
 */
public class LearningOpportunityData {

    private List<ParentLOS> parentLearningOpportinities;
    private List<ApplicationOption> applicationOptions;
    private List<LearningOpportunityProvider> providers;

    public List<ApplicationOption> getApplicationOptions() {
        return applicationOptions;
    }

    public void setApplicationOptions(List<ApplicationOption> applicationOptions) {
        this.applicationOptions = applicationOptions;
    }

    public List<ParentLOS> getParentLearningOpportinities() {
        return parentLearningOpportinities;
    }

    public void setParentLearningOpportinities(List<ParentLOS> parentLearningOpportinities) {
        this.parentLearningOpportinities = parentLearningOpportinities;
    }

    public List<LearningOpportunityProvider> getProviders() {
        return providers;
    }

    public void setProviders(List<LearningOpportunityProvider> providers) {
        this.providers = providers;
    }
}
