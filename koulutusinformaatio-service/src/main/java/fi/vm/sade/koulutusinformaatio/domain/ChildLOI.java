package fi.vm.sade.koulutusinformaatio.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Child learning opportunity instance.
 *
 * @author Hannu Lyytikainen
 */
public class ChildLOI extends BasicLOI {

    private String losId;
    private String parentLOIId;
    private List<I18nText> professionalTitles;
    private List<Code> teachingLanguages;
    private Map<String, String> webLinks;
    private List<ChildLOIRef> related = new ArrayList<ChildLOIRef>();
    private I18nText workingLifePlacement;
    private I18nText selectingDegreeProgram;
    private I18nText targetGroup;
    private String creditValue;
    private I18nText creditUnit;
    
    public String getLosId() {
        return losId;
    }

    public void setLosId(String losId) {
        this.losId = losId;
    }

    public String getParentLOIId() {
        return parentLOIId;
    }

    public void setParentLOIId(String parentLOIId) {
        this.parentLOIId = parentLOIId;
    }

    public List<I18nText> getProfessionalTitles() {
        return professionalTitles;
    }

    public void setProfessionalTitles(List<I18nText> professionalTitles) {
        this.professionalTitles = professionalTitles;
    }

    public List<Code> getTeachingLanguages() {
        return teachingLanguages;
    }

    public void setTeachingLanguages(List<Code> teachingLanguages) {
        this.teachingLanguages = teachingLanguages;
    }

    public Map<String, String> getWebLinks() {
        return webLinks;
    }

    public void setWebLinks(Map<String, String> webLinks) {
        this.webLinks = webLinks;
    }

    public List<ChildLOIRef> getRelated() {
        return related;
    }

    public void setRelated(List<ChildLOIRef> related) {
        this.related = related;
    }

    public I18nText getWorkingLifePlacement() {
        return workingLifePlacement;
    }

    public void setWorkingLifePlacement(I18nText workingLifePlacement) {
        this.workingLifePlacement = workingLifePlacement;
    }

    public I18nText getSelectingDegreeProgram() {
        return selectingDegreeProgram;
    }

    public void setSelectingDegreeProgram(I18nText selectingDegreeProgram) {
        this.selectingDegreeProgram = selectingDegreeProgram;
    }

    public void setTargetGroup(I18nText i18nText) {
        this.targetGroup = i18nText;
        
    }

    public I18nText getTargetGroup() {
        return targetGroup;
    }
    
    public String getCreditValue() {
        return creditValue;
    }

    public void setCreditValue(String creditValue) {
        this.creditValue = creditValue;
    }

    public I18nText getCreditUnit() {
        return creditUnit;
    }

    public void setCreditUnit(I18nText i18nText) {
        this.creditUnit = i18nText;
    }
}

