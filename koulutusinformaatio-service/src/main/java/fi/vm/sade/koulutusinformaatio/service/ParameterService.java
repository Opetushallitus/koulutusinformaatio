package fi.vm.sade.koulutusinformaatio.service;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystemParameters;

public interface ParameterService {
    
    
    /**
     * Fetching parameters for an application system from ohjausparametrit service.
     * @param oid The oid of the application system
     * @return the parameters
     */
    ApplicationSystemParameters getParametersForHaku(String oid);
    
    void clearCache();

}
