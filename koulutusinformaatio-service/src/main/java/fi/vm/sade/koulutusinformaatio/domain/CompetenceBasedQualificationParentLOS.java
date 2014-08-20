
package fi.vm.sade.koulutusinformaatio.domain;

import java.util.List;

/**
 * 
 * @author Markus
 *
 */
public class CompetenceBasedQualificationParentLOS  extends LOS {
    
    private I18nText accessToFurtherStudies;
    private I18nText choosingCompetence;
    private I18nText degreeCompletion;
    
    private Provider provider;
    private List<ApplicationOption> applicationOptions;
    
    private List<AdultVocationalLOS> children;
    
   
    public List<AdultVocationalLOS> getChildren() {
        return children;
    }
    public void setChildren(List<AdultVocationalLOS> children) {
        this.children = children;
    }
    
    public I18nText getAccessToFurtherStudies() {
        return accessToFurtherStudies;
    }
    public void setAccessToFurtherStudies(I18nText accessToFurtherStudies) {
        this.accessToFurtherStudies = accessToFurtherStudies;
    }
    public I18nText getChoosingCompetence() {
        return choosingCompetence;
    }
    public void setChoosingCompetence(I18nText choosingCompetence) {
        this.choosingCompetence = choosingCompetence;
    }
    public I18nText getDegreeCompletion() {
        return degreeCompletion;
    }
    public void setDegreeCompletion(I18nText degreeCompletion) {
        this.degreeCompletion = degreeCompletion;
    }
    
    public Provider getProvider() {
        return provider;
    }
    public void setProvider(Provider provider) {
        this.provider = provider;
    }
    
    public List<ApplicationOption> getApplicationOptions() {
        return applicationOptions;
    }
    public void setApplicationOptions(List<ApplicationOption> applicationOptions) {
        this.applicationOptions = applicationOptions;
    }

}
