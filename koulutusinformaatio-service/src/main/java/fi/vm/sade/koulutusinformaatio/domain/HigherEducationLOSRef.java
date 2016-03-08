package fi.vm.sade.koulutusinformaatio.domain;

import com.google.common.base.Objects;

import java.util.List;

/**
 * 
 * @author Markus
 */
public class HigherEducationLOSRef {
    
    private String id;
    private List<String> asIds;
    private I18nText name;
    private List<I18nText> qualifications;
    private Code prerequisite;
    private I18nText provider;
    private I18nText fieldOfExpertise;
    private I18nText educationKind;
    private boolean adultVocational = false;
     
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
    public List<I18nText> getQualifications() {
        return qualifications;
    }
    public void setQualifications(List<I18nText> qualifications) {
        this.qualifications = qualifications;
    }
    public Code getPrerequisite() {
        return prerequisite;
    }
    public void setPrerequisite(Code prerequisite) {
        this.prerequisite = prerequisite;
    }
    public I18nText getProvider() {
        return provider;
    }
    public void setProvider(I18nText provider) {
        this.provider = provider;
    }
    public void setFieldOfExpertise(I18nText name2) {
        this.fieldOfExpertise = name2;
        
    }
    public I18nText getFieldOfExpertise() {
        return fieldOfExpertise;
    }
    public void setEducationKind(I18nText educationKind) {
        this.educationKind = educationKind;
        
    }
    public I18nText getEducationKind() {
        return educationKind;
    }
    public void setAdultVocational(boolean b) {
        this.adultVocational = b;
        
    }
    public boolean isAdultVocational() {
        return adultVocational;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HigherEducationLOSRef that = (HigherEducationLOSRef) o;
        return Objects.equal(id, that.id);

    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
