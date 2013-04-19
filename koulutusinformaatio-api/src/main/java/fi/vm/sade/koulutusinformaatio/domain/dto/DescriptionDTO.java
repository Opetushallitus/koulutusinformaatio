package fi.vm.sade.koulutusinformaatio.domain.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class DescriptionDTO {
    
    private String accessToFurtherStudies;
    private String educationAndProfessionalGoals;
    private String selectionOfDegreeProgram;
    private String structureDiagram;
    
    public DescriptionDTO() {}

    public String getAccessToFurtherStudies() {
        return accessToFurtherStudies;
    }

    public void setAccessToFurtherStudies(String accessToFurtherStudies) {
        this.accessToFurtherStudies = accessToFurtherStudies;
    }

    public String getEducationAndProfessionalGoals() {
        return educationAndProfessionalGoals;
    }

    public void setEducationAndProfessionalGoals(
            String educationAndProfessionalGoals) {
        this.educationAndProfessionalGoals = educationAndProfessionalGoals;
    }

    public String getSelectionOfDegreeProgram() {
        return selectionOfDegreeProgram;
    }

    public void setSelectionOfDegreeProgram(String selectionOfDegreeProgram) {
        this.selectionOfDegreeProgram = selectionOfDegreeProgram;
    }

    public String getStructureDiagram() {
        return structureDiagram;
    }

    public void setStructureDiagram(String structureDiagram) {
        this.structureDiagram = structureDiagram;
    }

}
