package fi.vm.sade.koulutusinformaatio.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.code.morphia.annotations.Embedded;

import fi.vm.sade.koulutusinformaatio.dao.entity.I18nTextEntity;

/**
 * Child learning opportunity instance.
 *
 * @author Hannu Lyytikainen
 */
public class ChildLOI {

    private String id;
    private I18nText name;
    private ApplicationOption applicationOption;
    private String applicationSystemId;
    private Date startDate;
    // koulutuslaji -> nuorten koulutus
    private List<I18nText> formOfEducation;
    private Map<String, String> webLinks;
    private List<Code> teachingLanguages;
    // opetusmuoto -> lähiopetus
    private List<I18nText> formOfTeaching;
    private I18nText prerequisite;
    private List<I18nText> professionalTitles;
    private I18nText workingLifePlacement;
    private I18nText internationalization;
    private I18nText cooperation;

    ///// parent loi fix
    private String parentLOI;

    public String getParentLOI() {
        return parentLOI;
    }

    public void setParentLOI(String parentLOI) {
        this.parentLOI = parentLOI;
    }
    /////


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public I18nText getName() {
        return name;
    }

    public void setName(I18nText name) {
        this.name = name;
    }

    public ApplicationOption getApplicationOption() {
        return applicationOption;
    }

    public void setApplicationOption(ApplicationOption applicationOption) {
        this.applicationOption = applicationOption;
    }

    public String getApplicationSystemId() {
        return applicationSystemId;
    }

    public void setApplicationSystemId(String applicationSystemId) {
        this.applicationSystemId = applicationSystemId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public List<I18nText> getFormOfEducation() {
        return formOfEducation;
    }

    public void setFormOfEducation(List<I18nText> formOfEducation) {
        this.formOfEducation = formOfEducation;
    }

    public Map<String, String> getWebLinks() {
        return webLinks;
    }

    public void setWebLinks(Map<String, String> webLinks) {
        this.webLinks = webLinks;
    }

    public List<Code> getTeachingLanguages() {
        return teachingLanguages;
    }

    public void setTeachingLanguages(List<Code> teachingLanguages) {
        this.teachingLanguages = teachingLanguages;
    }

    public List<I18nText> getFormOfTeaching() {
        return formOfTeaching;
    }

    public void setFormOfTeaching(List<I18nText> formOfTeaching) {
        this.formOfTeaching = formOfTeaching;
    }

    public I18nText getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(I18nText prerequisite) {
        this.prerequisite = prerequisite;
    }

    public List<I18nText> getProfessionalTitles() {
        return professionalTitles;
    }

    public void setProfessionalTitles(List<I18nText> professionalTitles) {
        this.professionalTitles = professionalTitles;
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
}
