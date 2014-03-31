package fi.vm.sade.koulutusinformaatio.service;

import java.util.Date;

import fi.vm.sade.koulutusinformaatio.domain.exception.KIException;

public interface TextVersionService {
    
    public void update() throws KIException;
    
    public boolean isRunning();
    
    public String getLastTextVersionUpdateFinished();

}
