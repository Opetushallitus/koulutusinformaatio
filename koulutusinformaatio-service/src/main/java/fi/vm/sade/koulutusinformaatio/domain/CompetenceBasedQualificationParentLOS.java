
package fi.vm.sade.koulutusinformaatio.domain;

import java.util.List;

/**
 * 
 * @author Markus
 *
 */
public class CompetenceBasedQualificationParentLOS extends LOS {
    
    private I18nText accessToFurtherStudies;
    private I18nText choosingCompetence;
    private I18nText degreeCompletion;
    private I18nText educationKind;
    
    private I18nText educationDomain;
    private I18nText educationType;
    
    private Provider provider;
    private List<ApplicationOption> applicationOptions;
    
    private List<AdultVocationalLOS> children;
    
    private List<Code> availableTranslationLanguages;
    
    private boolean chargeable;
    private String charge;
    private boolean osaamisala;
    private String edtUri;
    private String determiner;
    
   
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
    public List<Code> getAvailableTranslationLanguages() {
        return availableTranslationLanguages;
    }
    public void setAvailableTranslationLanguages(
            List<Code> availableTranslationLanguages) {
        this.availableTranslationLanguages = availableTranslationLanguages;
    }
    public I18nText getEducationDomain() {
        return educationDomain;
    }
    public void setEducationDomain(I18nText educationDomain) {
        this.educationDomain = educationDomain;
    }
    public I18nText getEducationKind() {
        return educationKind;
    }
    public void setEducationKind(I18nText educationKind) {
        this.educationKind = educationKind;
    }
    public I18nText getEducationType() {
        return educationType;
    }
    public void setEducationType(I18nText educationType) {
        this.educationType = educationType;
    }
    public boolean isChargeable() {
        return chargeable;
    }
    public void setChargeable(boolean chargeable) {
        this.chargeable = chargeable;
    }
    public String getCharge() {
        return charge;
    }
    public void setCharge(String string) {
        this.charge = string;
    }
    public void setOsaamisala(boolean isOsaamisala) {
        this.osaamisala = isOsaamisala;
        
    }
    public boolean isOsaamisala() {
        return osaamisala;
    }
    public void setEdtUri(String uri) {
        this.edtUri = uri;
        
    }
    public String getEdtUri() {
        return edtUri;
    }
    public void setDeterminer(String tarkenne) {
        this.determiner = tarkenne;
    }
    public String getDeterminer() {
        return determiner;
    }


}
