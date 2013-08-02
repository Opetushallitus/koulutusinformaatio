package fi.vm.sade.koulutusinformaatio.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Child learning opportunity instance.
 *
 * @author Hannu Lyytikainen
 */
public class ChildLOI {

    private String id;
    private String losId;
    private I18nText name;
    private String parentLOIId;
    private List<I18nText> professionalTitles;
    private Code prerequisite;
    // opetusmuoto -> lähiopetus
    private List<I18nText> formOfTeaching;
    private List<Code> teachingLanguages;
    private Map<String, String> webLinks;
    // koulutuslaji -> nuorten koulutus
    private List<I18nText> formOfEducation;
    private Date startDate;
    private List<String> applicationSystemIds;
    private List<ChildLOIRef> related = new ArrayList<ChildLOIRef>();
    private List<ApplicationOption> applicationOptions;
    private I18nText workingLifePlacement;
    private I18nText internationalization;
    private I18nText cooperation;
    private I18nText content;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLosId() {
        return losId;
    }

    public void setLosId(String losId) {
        this.losId = losId;
    }

    public I18nText getName() {
        return name;
    }

    public void setName(I18nText name) {
        this.name = name;
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

    public Code getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(Code prerequisite) {
        this.prerequisite = prerequisite;
    }

    public List<I18nText> getFormOfTeaching() {
        return formOfTeaching;
    }

    public void setFormOfTeaching(List<I18nText> formOfTeaching) {
        this.formOfTeaching = formOfTeaching;
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

    public List<I18nText> getFormOfEducation() {
        return formOfEducation;
    }

    public void setFormOfEducation(List<I18nText> formOfEducation) {
        this.formOfEducation = formOfEducation;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public List<String> getApplicationSystemIds() {
        return applicationSystemIds;
    }

    public void setApplicationSystemIds(List<String> applicationSystemIds) {
        this.applicationSystemIds = applicationSystemIds;
    }

    public List<ChildLOIRef> getRelated() {
        return related;
    }

    public void setRelated(List<ChildLOIRef> related) {
        this.related = related;
    }

    public List<ApplicationOption> getApplicationOptions() {
        return applicationOptions;
    }

    public void setApplicationOptions(List<ApplicationOption> applicationOptions) {
        this.applicationOptions = applicationOptions;
    }

    public I18nText getWorkingLifePlacement() {
        return workingLifePlacement;
    }

    public void setWorkingLifePlacement(I18nText workingLifePlacement) {
        this.workingLifePlacement = workingLifePlacement;
    }

    public I18nText getInternationalization() {
        return internationalization;
    }

    public void setInternationalization(I18nText internationalization) {
        this.internationalization = internationalization;
    }

    public I18nText getCooperation() {
        return cooperation;
    }

    public void setCooperation(I18nText cooperation) {
        this.cooperation = cooperation;
    }

    public I18nText getContent() {
        return content;
    }

    public void setContent(I18nText content) {
        this.content = content;
    }
}
