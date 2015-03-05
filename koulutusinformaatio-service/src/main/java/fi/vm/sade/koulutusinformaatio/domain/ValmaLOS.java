
package fi.vm.sade.koulutusinformaatio.domain;

import java.util.ArrayList;
import java.util.List;

public class ValmaLOS extends StandaloneLOS {  
    
    private I18nText infoAboutTeachingLangs;
    private I18nText majorSelection;
    private I18nText finalExam;
    private I18nText competence;
    private I18nText researchFocus;
    
    
    private List<I18nText> professionalTitles;
    
    private Boolean chargeable;
    
    private List<HigherEducationLOS> children = new ArrayList<HigherEducationLOS>();
    private List<HigherEducationLOS> parents = new ArrayList<HigherEducationLOS>();
    
    private I18nPicture structureImage;
    

    public void setInfoAboutTeachingLangs(I18nText i18nText) {
        this.infoAboutTeachingLangs = i18nText;
    }

    public I18nText getInfoAboutTeachingLangs() {
        return infoAboutTeachingLangs;
    }

    public void setMajorSelection(I18nText i18nTextEnriched) {
        this.majorSelection = i18nTextEnriched; 
    }

    public I18nText getMajorSelection() {
        return majorSelection;
    }

    public void setFinalExam(I18nText i18nTextEnriched) {
        this.finalExam = i18nTextEnriched;
    }

    public I18nText getFinalExam() {
        return finalExam;
    }

    public void setCompetence(I18nText i18nTextEnriched) {
        this.competence = i18nTextEnriched;
    }

    public I18nText getCompetence() {
        return competence;
    }

    public void setResearchFocus(I18nText i18nTextEnriched) {
        this.researchFocus = i18nTextEnriched;
    }

    public I18nText getResearchFocus() {
        return researchFocus;
    }
    
    public void setChargeable(Boolean opintojenMaksullisuus) {
        this.chargeable = opintojenMaksullisuus;    
    }

    public Boolean getChargeable() {
        return chargeable;
    }

    public List<HigherEducationLOS> getChildren() {
        return children;
    }
    public void setChildren(List<HigherEducationLOS> children) {
        this.children = children;
    }
    public List<HigherEducationLOS> getParents() {
        return parents;
    }
    public void setParents(List<HigherEducationLOS> parents) {
        this.parents = parents;
    }

    public List<I18nText> getProfessionalTitles() {
        return professionalTitles;
    }

    public void setProfessionalTitles(List<I18nText> professionalTitles) {
        this.professionalTitles = professionalTitles;
    }

    public I18nPicture getStructureImage() {
        return structureImage;
    }

    public void setStructureImage(I18nPicture structureImage) {
        this.structureImage = structureImage;
    }

    
    
}
