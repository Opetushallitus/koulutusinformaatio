package fi.vm.sade.koulutusinformaatio.service;

public interface IncrementalUpdateService {
    

    public void updateChangedEducationData() throws Exception;

    boolean isRunning();

    long getRunningSince();

}
