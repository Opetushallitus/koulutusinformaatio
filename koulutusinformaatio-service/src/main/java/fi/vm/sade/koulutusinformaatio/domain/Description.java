package fi.vm.sade.koulutusinformaatio.domain;

public class Description {
    
    private I18nText accessToFurtherStudies;
    private I18nText educationAndProfessionalGoals;
    private I18nText selectionOfDegreeProgram;
    private I18nText structureDiagram;
    
    public Description() {}
    
    public Description(I18nText accessToFurtherStudies, I18nText educationAndProfessionalGoals, I18nText selectionOfDegreeProgram, I18nText structureDiagram) {
        this.accessToFurtherStudies = accessToFurtherStudies;
        this.educationAndProfessionalGoals = educationAndProfessionalGoals;
        this.selectionOfDegreeProgram = selectionOfDegreeProgram;
        this.structureDiagram = structureDiagram;
    }

    public I18nText getAccessToFurtherStudies() {
        return accessToFurtherStudies;
    }

    public void setAccessToFurtherStudies(I18nText accessToFurtherStudies) {
        this.accessToFurtherStudies = accessToFurtherStudies;
    }

    public I18nText getEducationAndProfessionalGoals() {
        return educationAndProfessionalGoals;
    }

    public void setEducationAndProfessionalGoals(I18nText educationAndProfessionalGoals) {
        this.educationAndProfessionalGoals = educationAndProfessionalGoals;
    }

    public I18nText getSelectionOfDegreeProgram() {
        return selectionOfDegreeProgram;
    }

    public void setSelectionOfDegreeProgram(I18nText selectionOfDegreeProgram) {
        this.selectionOfDegreeProgram = selectionOfDegreeProgram;
    }

    public I18nText getStructureDiagram() {
        return structureDiagram;
    }

    public void setStructureDiagram(I18nText structureDiagram) {
        this.structureDiagram = structureDiagram;
    }
}
