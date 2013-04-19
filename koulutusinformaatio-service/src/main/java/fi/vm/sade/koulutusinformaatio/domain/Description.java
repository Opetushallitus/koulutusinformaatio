package fi.vm.sade.koulutusinformaatio.domain;

public class Description {
    
    private String accessToFurtherStudies;
    private String educationAndProfessionalGoals;
    private String selectionOfDegreeProgram;
    private String structureDiagram;
    
    public Description() {}
    
    public Description(String accessToFurtherStudies, String educationAndProfessionalGoals, String selectionOfDegreeProgram, String structureDiagram) {
        this.accessToFurtherStudies = accessToFurtherStudies;
        this.educationAndProfessionalGoals = educationAndProfessionalGoals;
        this.selectionOfDegreeProgram = selectionOfDegreeProgram;
        this.structureDiagram = structureDiagram;
    }

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
