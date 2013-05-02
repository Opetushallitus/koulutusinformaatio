package fi.vm.sade.koulutusinformaatio.domain;

public class Classification {
    
    private I18nText educationDomain;
    private I18nText educationDegree;
    
    public Classification() {}

    public Classification(I18nText educationDomain, I18nText educationDegree) {
        this.educationDomain = educationDomain;
        this.educationDegree = educationDegree;
    }

    public I18nText getEducationDomain() {
        return educationDomain;
    }

    public void setEducationDomain(I18nText educationDomain) {
        this.educationDomain = educationDomain;
    }

    public I18nText getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(I18nText educationDegree) {
        this.educationDegree = educationDegree;
    }
}
