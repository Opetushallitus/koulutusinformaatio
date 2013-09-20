package fi.vm.sade.koulutusinformaatio.dao.entity;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Reference;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Hannu Lyytikainen
 */
@Embedded
public class ChildLearningOpportunityInstanceEntity {

    private String id;
    @Reference
    private List<ApplicationOptionEntity> applicationOptions;
    @Embedded
    private List<ChildLOIRefEntity> related;
    @Embedded
    private List<CodeEntity> teachingLanguages;
    private Date startDate;
    @Embedded
    private List<I18nTextEntity> formOfEducation;
    @Embedded
    private Map<String, String> webLinks;
    @Embedded
    private List<I18nTextEntity> formOfTeaching;
    @Embedded
    private CodeEntity prerequisite;
    @Embedded
    private List<I18nTextEntity> professionalTitles;
    @Embedded
    private I18nTextEntity workingLifePlacement;
    @Embedded
    private I18nTextEntity internationalization;
    @Embedded
    private I18nTextEntity cooperation;
    @Embedded
    private I18nTextEntity content;
    @Embedded
    private I18nText selectingDegreeProgram;
    @Embedded
    private List<ContactPersonEntity> contactPersons;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ApplicationOptionEntity> getApplicationOptions() {
        return applicationOptions;
    }

    public void setApplicationOptions(List<ApplicationOptionEntity> applicationOptions) {
        this.applicationOptions = applicationOptions;
    }

    public List<ChildLOIRefEntity> getRelated() {
        return related;
    }

    public void setRelated(List<ChildLOIRefEntity> related) {
        this.related = related;
    }

    public List<CodeEntity> getTeachingLanguages() {
        return teachingLanguages;
    }

    public void setTeachingLanguages(List<CodeEntity> teachingLanguages) {
        this.teachingLanguages = teachingLanguages;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public List<I18nTextEntity> getFormOfEducation() {
        return formOfEducation;
    }

    public void setFormOfEducation(List<I18nTextEntity> formOfEducation) {
        this.formOfEducation = formOfEducation;
    }

    public Map<String, String> getWebLinks() {
        return webLinks;
    }

    public void setWebLinks(Map<String, String> webLinks) {
        this.webLinks = webLinks;
    }

    public List<I18nTextEntity> getFormOfTeaching() {
        return formOfTeaching;
    }

    public void setFormOfTeaching(List<I18nTextEntity> formOfTeaching) {
        this.formOfTeaching = formOfTeaching;
    }

    public CodeEntity getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(CodeEntity prerequisite) {
        this.prerequisite = prerequisite;
    }

    public List<I18nTextEntity> getProfessionalTitles() {
        return professionalTitles;
    }

    public void setProfessionalTitles(List<I18nTextEntity> professionalTitles) {
        this.professionalTitles = professionalTitles;
    }

    public I18nTextEntity getWorkingLifePlacement() {
        return workingLifePlacement;
    }

    public void setWorkingLifePlacement(I18nTextEntity workingLifePlacement) {
        this.workingLifePlacement = workingLifePlacement;
    }

    public I18nTextEntity getInternationalization() {
        return internationalization;
    }

    public void setInternationalization(I18nTextEntity internationalization) {
        this.internationalization = internationalization;
    }

    public I18nTextEntity getCooperation() {
        return cooperation;
    }

    public void setCooperation(I18nTextEntity cooperation) {
        this.cooperation = cooperation;
    }

    public I18nTextEntity getContent() {
        return content;
    }

    public void setContent(I18nTextEntity content) {
        this.content = content;
    }

    public I18nText getSelectingDegreeProgram() {
        return selectingDegreeProgram;
    }

    public void setSelectingDegreeProgram(I18nText selectingDegreeProgram) {
        this.selectingDegreeProgram = selectingDegreeProgram;
    }

    public List<ContactPersonEntity> getContactPersons() {
        return contactPersons;
    }

    public void setContactPersons(List<ContactPersonEntity> contactPersons) {
        this.contactPersons = contactPersons;
    }
}
