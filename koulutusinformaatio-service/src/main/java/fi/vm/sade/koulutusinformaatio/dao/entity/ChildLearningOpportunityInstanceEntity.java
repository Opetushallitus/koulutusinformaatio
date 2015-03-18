package fi.vm.sade.koulutusinformaatio.dao.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Reference;

import fi.vm.sade.koulutusinformaatio.domain.I18nText;

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
    private List<Date> startDates;
    @Embedded
    private List<I18nTextEntity> formOfEducation;
    @Embedded
    private List<I18nTextEntity> timeOfEducation;
    @Embedded
    private List<I18nTextEntity> placeOfEducation;
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
    private I18nTextEntity selectingDegreeProgram;
    @Embedded
    private List<ContactPersonEntity> contactPersons;
    private String plannedDuration;
    @Embedded
    private I18nTextEntity plannedDurationUnit;
    @Embedded
    private List<CodeEntity> availableTranslationLanguages;
    @Embedded
    private List<CodeEntity> fotFacet = new ArrayList<CodeEntity>();
    @Embedded
    private List<CodeEntity> timeOfTeachingFacet = new ArrayList<CodeEntity>();
    @Embedded
    private List<CodeEntity> formOfStudyFacet = new ArrayList<CodeEntity>();
    @Embedded
    private CodeEntity koulutuslaji;
    @Embedded
    private I18nTextEntity targetGroup;
    
    private int startYear;
    @Embedded
    private I18nTextEntity startSeason;
    @Embedded
    private String creditValue;
    @Embedded
    private I18nText creditUnit;
    
    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public I18nTextEntity getStartSeason() {
        return startSeason;
    }

    public void setStartSeason(I18nTextEntity startSeason) {
        this.startSeason = startSeason;
    }
    

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
    
    public List<Date> getStartDates() {
        return startDates;
    }

    public void setStartDates(List<Date> startDates) {
        this.startDates = startDates;
    }

    public List<I18nTextEntity> getFormOfEducation() {
        return formOfEducation;
    }

    public void setFormOfEducation(List<I18nTextEntity> formOfEducation) {
        this.formOfEducation = formOfEducation;
    }
    
    public List<I18nTextEntity> getTimeOfTeaching() {
        return timeOfEducation;
    }

    public void setTimeOfTeaching(List<I18nTextEntity> timeOfEducation) {
        this.timeOfEducation = timeOfEducation;
    }
    
    public List<I18nTextEntity> getPlaceOfTeaching() {
        return placeOfEducation;
    }

    public void setPlaceOfTeaching(List<I18nTextEntity> placeOfEducation) {
        this.placeOfEducation = placeOfEducation;
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

    public I18nTextEntity getSelectingDegreeProgram() {
        return selectingDegreeProgram;
    }

    public void setSelectingDegreeProgram(I18nTextEntity selectingDegreeProgram) {
        this.selectingDegreeProgram = selectingDegreeProgram;
    }

    public List<ContactPersonEntity> getContactPersons() {
        return contactPersons;
    }

    public void setContactPersons(List<ContactPersonEntity> contactPersons) {
        this.contactPersons = contactPersons;
    }
    
    public String getPlannedDuration() {
        return plannedDuration;
    }

    public void setPlannedDuration(String plannedDuration) {
        this.plannedDuration = plannedDuration;
    }

    public I18nTextEntity getPlannedDurationUnit() {
        return plannedDurationUnit;
    }

    public void setPlannedDurationUnit(I18nTextEntity plannedDurationUnit) {
        this.plannedDurationUnit = plannedDurationUnit;
    }

    public List<CodeEntity> getAvailableTranslationLanguages() {
        return availableTranslationLanguages;
    }

    public void setAvailableTranslationLanguages(List<CodeEntity> availableTranslationLanguages) {
        this.availableTranslationLanguages = availableTranslationLanguages;
    }

    public List<CodeEntity> getFotFacet() {
        return fotFacet;
    }

    public void setFotFacet(List<CodeEntity> fotFacet) {
        this.fotFacet = fotFacet;
    }

    public List<CodeEntity> getTimeOfTeachingFacet() {
        return timeOfTeachingFacet;
    }

    public void setTimeOfTeachingFacet(List<CodeEntity> timeOfTeachingFacet) {
        this.timeOfTeachingFacet = timeOfTeachingFacet;
    }

    public List<CodeEntity> getFormOfStudyFacet() {
        return formOfStudyFacet;
    }

    public void setFormOfStudyFacet(List<CodeEntity> formOfStudyFacet) {
        this.formOfStudyFacet = formOfStudyFacet;
    }

    public CodeEntity getKoulutuslaji() {
        return koulutuslaji;
    }

    public void setKoulutuslaji(CodeEntity koulutuslaji) {
        this.koulutuslaji = koulutuslaji;
    }

    public I18nTextEntity getTargetGroup() {
        return targetGroup;
    }

    public void setTargetGroup(I18nTextEntity targetGroup) {
        this.targetGroup = targetGroup;
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

    public void setCreditUnit(I18nText creditUnit) {
        this.creditUnit = creditUnit;
    }

    
}
