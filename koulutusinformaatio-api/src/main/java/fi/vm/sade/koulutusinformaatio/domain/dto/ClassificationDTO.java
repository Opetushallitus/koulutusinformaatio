package fi.vm.sade.koulutusinformaatio.domain.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ClassificationDTO {

    private String educationDomain;
    private String educationDegree;

    public ClassificationDTO() {}

    public ClassificationDTO(String educationDomain, String educationDegree) {
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
