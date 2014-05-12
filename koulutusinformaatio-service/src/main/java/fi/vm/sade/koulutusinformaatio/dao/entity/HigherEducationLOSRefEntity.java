package fi.vm.sade.koulutusinformaatio.dao.entity;

import java.util.List;

import org.mongodb.morphia.annotations.Embedded;


/**
 * 
 * @author Markus
 */
@Embedded
public class HigherEducationLOSRefEntity {
    
    private String id;
    private List<String> asIds;
    @Embedded
    private I18nTextEntity name;
    @Embedded
    private List<I18nTextEntity> qualifications;
    @Embedded
    private CodeEntity prerequisite;
    @Embedded
    private I18nTextEntity provider;
    
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
    public I18nTextEntity getName() {
        return name;
    }
    public void setName(I18nTextEntity name) {
        this.name = name;
    }
    public List<I18nTextEntity> getQualifications() {
        return qualifications;
    }
    public void setQualifications(List<I18nTextEntity> qualifications) {
        this.qualifications = qualifications;
    }
    public CodeEntity getPrerequisite() {
        return prerequisite;
    }
    public void setPrerequisite(CodeEntity prerequisite) {
        this.prerequisite = prerequisite;
    }
    public I18nTextEntity getProvider() {
        return provider;
    }
    public void setProvider(I18nTextEntity provider) {
        this.provider = provider;
    }

}
