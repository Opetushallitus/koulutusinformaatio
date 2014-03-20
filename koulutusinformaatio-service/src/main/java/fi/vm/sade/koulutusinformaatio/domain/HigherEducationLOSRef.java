package fi.vm.sade.koulutusinformaatio.domain;

import java.util.List;

/**
 * 
 * @author Markus
 */
public class HigherEducationLOSRef {
    
    private String id;
    private List<String> asIds;
    private I18nText name;
    private I18nText qualification;
    private Code prerequisite;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public List<String> getAsIds() {
        return asIds;
    }
    public void setAsIds(List<String> asIds) {
        this.asIds = asIds;
    }
    public I18nText getName() {
        return name;
    }
    public void setName(I18nText name) {
        this.name = name;
    }
    public I18nText getQualification() {
        return qualification;
    }
    public void setQualification(I18nText qualification) {
        this.qualification = qualification;
    }
    public Code getPrerequisite() {
        return prerequisite;
    }
    public void setPrerequisite(Code prerequisite) {
        this.prerequisite = prerequisite;
    }

}
