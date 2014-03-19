package fi.vm.sade.koulutusinformaatio.dao.entity;

import java.util.List;

import org.mongodb.morphia.annotations.Embedded;

import fi.vm.sade.koulutusinformaatio.domain.I18nText;

/**
 * 
 * @author Markus
 */
@Embedded
public class HigherEducationLOSRefEntity {
    
    private String id;
    private List<String> asIds;
    @Embedded
    private I18nText name;
    @Embedded
    private I18nText qualification;
    @Embedded
    private CodeEntity prerequisite;
    
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
    public CodeEntity getPrerequisite() {
        return prerequisite;
    }
    public void setPrerequisite(CodeEntity prerequisite) {
        this.prerequisite = prerequisite;
    }

}
