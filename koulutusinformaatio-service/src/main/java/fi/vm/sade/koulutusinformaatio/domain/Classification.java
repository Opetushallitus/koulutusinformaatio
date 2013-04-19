package fi.vm.sade.koulutusinformaatio.domain;

public class Classification {
    
    private String educationDomain;
    private String educationDegree;
    
    public Classification() {}

    public Classification(String educationDomain, String educationDegree) {
        this.educationDomain = educationDomain;
        this.educationDegree = educationDegree;
    }

    public String getEducationDomain() {
        return educationDomain;
    }

    public void setEducationDomain(String educationDomain) {
        this.educationDomain = educationDomain;
    }

    public String getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(String educationDegree) {
        this.educationDegree = educationDegree;
    }

}
