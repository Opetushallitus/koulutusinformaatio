package fi.vm.sade.koulutusinformaatio.domain.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @author Hannu Lyytikainen
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ChildLearningOpportunityInstanceDTO {

    private String id;
    private List<ApplicationSystemDTO> applicationSystems = new ArrayList<ApplicationSystemDTO>();
    private List<ChildLOIRefDTO> related = new ArrayList<ChildLOIRefDTO>();
    private Date startDate;
    private List<Date> startDates;
    private List<String> formOfEducation = new ArrayList<String>();
    private Map<String, String> webLinks;
    private List<String> formOfTeaching = new ArrayList<String>();
    private List<String> placeOfTeaching = new ArrayList<String>();
    private List<String> timeOfTeaching = new ArrayList<String>();
    private CodeDTO prerequisite;
    private List<CodeDTO> availableTranslationLanguages;
    private List<String> teachingLanguages = new ArrayList<String>();
    private List<String> professionalTitles;
    private String workingLifePlacement;
    private String internationalization;
    private String targetGroup;
    private String cooperation;
    private String content;
    private String selectingDegreeProgram;
    private List<ContactPersonDTO> contactPersons = new ArrayList<ContactPersonDTO>();
    private String plannedDuration;
    private String plannedDurationUnit;
    private int startYear;
    private String startSeason;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ApplicationSystemDTO> getApplicationSystems() {
        return applicationSystems;
    }

    public void setApplicationSystems(List<ApplicationSystemDTO> applicationSystems) {
        this.applicationSystems = applicationSystems;
    }

    public List<ChildLOIRefDTO> getRelated() {
        return related;
    }

    public void setRelated(List<ChildLOIRefDTO> related) {
        this.related = related;
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

    public List<String> getFormOfEducation() {
        return formOfEducation;
    }

    public void setFormOfEducation(List<String> formOfEducation) {
        this.formOfEducation = formOfEducation;
    }

    public Map<String, String> getWebLinks() {
        return webLinks;
    }

    public void setWebLinks(Map<String, String> webLinks) {
        this.webLinks = webLinks;
    }

    public List<String> getFormOfTeaching() {
        return formOfTeaching;
    }

    public void setFormOfTeaching(List<String> formOfTeaching) {
        this.formOfTeaching = formOfTeaching;
    }
    public List<String> getTimeOfTeaching() {
        return timeOfTeaching;
    }

    public void setTimeOfTeaching(List<String> timeOfTeaching) {
        this.timeOfTeaching = timeOfTeaching;
    }

    public List<String> getPlaceOfTeaching() {
        return placeOfTeaching;
    }

    public void setPlaceOfTeaching(List<String> placeOfTeaching) {
        this.placeOfTeaching = placeOfTeaching;
    }
    
    public CodeDTO getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(CodeDTO prerequisite) {

        this.prerequisite = prerequisite;
    }

    public List<String> getTeachingLanguages() {
        return teachingLanguages;
    }

    public void setTeachingLanguages(List<String> teachingLanguages) {
        this.teachingLanguages = teachingLanguages;
    }

    public List<String> getProfessionalTitles() {
        return professionalTitles;
    }

    public void setProfessionalTitles(List<String> professionalTitles) {
        this.professionalTitles = professionalTitles;
    }

    public String getWorkingLifePlacement() {
        return workingLifePlacement;
    }

    public void setWorkingLifePlacement(String workingLifePlacement) {
        this.workingLifePlacement = workingLifePlacement;
    }

    public String getInternationalization() {
        return internationalization;
    }

    public void setInternationalization(String internationalization) {
        this.internationalization = internationalization;
    }

    public String getCooperation() {
        return cooperation;
    }

    public void setCooperation(String cooperation) {
        this.cooperation = cooperation;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSelectingDegreeProgram() {
        return selectingDegreeProgram;
    }

    public void setSelectingDegreeProgram(String selectingDegreeProgram) {
        this.selectingDegreeProgram = selectingDegreeProgram;
    }

    public List<ContactPersonDTO> getContactPersons() {
        return contactPersons;
    }

    public void setContactPersons(List<ContactPersonDTO> contactPersons) {
        this.contactPersons = contactPersons;
    }

    public String getPlannedDuration() {
        return plannedDuration;
    }

    public void setPlannedDuration(String plannedDuration) {
        this.plannedDuration = plannedDuration;
    }

    public String getPlannedDurationUnit() {
        return plannedDurationUnit;
    }

    public void setPlannedDurationUnit(String plannedDurationUnit) {
        this.plannedDurationUnit = plannedDurationUnit;
    }

    public List<CodeDTO> getAvailableTranslationLanguages() {
        return availableTranslationLanguages;
    }

    public void setAvailableTranslationLanguages(List<CodeDTO> availableTranslationLanguages) {
        this.availableTranslationLanguages = availableTranslationLanguages;
    }

    public String getTargetGroup() {
        return targetGroup;
    }

    public void setTargetGroup(String targetGroup) {
        this.targetGroup = targetGroup;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public String getStartSeason() {
        return startSeason;
    }

    public void setStartSeason(String startSeason) {
        this.startSeason = startSeason;
    }
}
