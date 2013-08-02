package fi.vm.sade.koulutusinformaatio.domain.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.*;

/**
 * @author Hannu Lyytikainen
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ChildLearningOpportunityInstanceDTO {

    private String id;
    private List<ApplicationSystemDTO> applicationSystems = new ArrayList<ApplicationSystemDTO>();
    private List<ChildLORefDTO> related = new ArrayList<ChildLORefDTO>();
    private Date startDate;
    private List<String> formOfEducation = new ArrayList<String>();
    private Map<String, String> webLinks;
    private List<String> formOfTeaching = new ArrayList<String>();
    private CodeDTO prerequisite;
    private String translationLanguage;
    private Set<String> availableTranslationLanguages = new HashSet<String>();
    private Set<String> teachingLanguages = new HashSet<String>();
    private List<String> professionalTitles;
    private String workingLifePlacement;
    private String internationalization;
    private String cooperation;
    private String content;

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

    public List<ChildLORefDTO> getRelated() {
        return related;
    }

    public void setRelated(List<ChildLORefDTO> related) {
        this.related = related;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
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

    public CodeDTO getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(CodeDTO prerequisite) {
        this.prerequisite = prerequisite;
    }

    public String getTranslationLanguage() {
        return translationLanguage;
    }

    public void setTranslationLanguage(String translationLanguage) {
        this.translationLanguage = translationLanguage;
    }

    public Set<String> getAvailableTranslationLanguages() {
        return availableTranslationLanguages;
    }

    public void setAvailableTranslationLanguages(Set<String> availableTranslationLanguages) {
        this.availableTranslationLanguages = availableTranslationLanguages;
    }

    public Set<String> getTeachingLanguages() {
        return teachingLanguages;
    }

    public void setTeachingLanguages(Set<String> teachingLanguages) {
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
}
