package fi.vm.sade.koulutusinformaatio.domain.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;


@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class AdultUpperSecondaryLOSDTO extends StandaloneLOSDTO implements Articled {
    
  //Varmistetut
    private String id;
    private String content;
    private String goals;
    private String structure;
    private String internationalization;
    private String cooperation;
    private String accessToFurtherStudies;
    private List<ContactPersonDTO> contactPersons = new ArrayList<ContactPersonDTO>();
    private String educationDomain;
    private String name;
    private String koulutuskoodi;
    private String educationDegree;
    private String educationDegreeName;
    private String degreeTitle;
    private Date startDate;
    private String plannedDuration;
    private String plannedDurationUnit;
    private String pduCodeUri;
    private String creditValue;
    private String creditUnit;
    private String degree;
    
    
    private String educationCode;
    private List<String> teachingLanguages;

    private LearningOpportunityProviderDTO provider;
    private List<LearningOpportunityProviderDTO> additionalProviders = new ArrayList<LearningOpportunityProviderDTO>();
    private List<ApplicationSystemDTO> applicationSystems = new ArrayList<ApplicationSystemDTO>();

    
    private List<CodeDTO> prerequisites;
    private String translationLanguage;
    private List<CodeDTO> availableTranslationLanguages;
    
    //private List<Code> prerequisites;
    private List<String> formOfTeaching;
    private List<String> teachingTimes;
    private List<String> teachingPlaces;
    private List<String> qualifications;
    private List<String> degreeTitles;
    private String startSeason;
    private int startYear;
    
    private List<CodeDTO> topics;
    private List<CodeDTO> themes;

    //Status of the lo. For preview
    private String status;
    //private List<Code> availableTranslationLanguages;
    
    private String educationType;
    
    private String targetGroup;
    private String subjectsAndCourses;
    private List<LanguageSelectionDTO> languageSelection;
    private List<String> diplomas = new ArrayList<String>();
    private List<ArticleResultDTO> edCodeSuggestions;
    private List<ArticleResultDTO> edTypeSuggestions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
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

    public String getAccessToFurtherStudies() {
        return accessToFurtherStudies;
    }

    public void setAccessToFurtherStudies(String accessToFurtherStudies) {
        this.accessToFurtherStudies = accessToFurtherStudies;
    }

    public List<ContactPersonDTO> getContactPersons() {
        return contactPersons;
    }

    public void setContactPersons(List<ContactPersonDTO> contactPersons) {
        this.contactPersons = contactPersons;
    }

    public String getEducationDomain() {
        return educationDomain;
    }

    public void setEducationDomain(String educationDomain) {
        this.educationDomain = educationDomain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKoulutuskoodi() {
        return koulutuskoodi;
    }

    public void setKoulutuskoodi(String koulutuskoodi) {
        this.koulutuskoodi = koulutuskoodi;
    }

    public String getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(String educationDegree) {
        this.educationDegree = educationDegree;
    }

    public String getDegreeTitle() {
        return degreeTitle;
    }

    public void setDegreeTitle(String degreeTitle) {
        this.degreeTitle = degreeTitle;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
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

    public String getPduCodeUri() {
        return pduCodeUri;
    }

    public void setPduCodeUri(String pduCodeUri) {
        this.pduCodeUri = pduCodeUri;
    }

    public String getCreditValue() {
        return creditValue;
    }

    public void setCreditValue(String creditValue) {
        this.creditValue = creditValue;
    }

    public String getCreditUnit() {
        return creditUnit;
    }

    public void setCreditUnit(String creditUnit) {
        this.creditUnit = creditUnit;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getEducationCode() {
        return educationCode;
    }

    public void setEducationCode(String educationCode) {
        this.educationCode = educationCode;
    }

    public List<String> getTeachingLanguages() {
        return teachingLanguages;
    }

    public void setTeachingLanguages(List<String> teachingLanguages) {
        this.teachingLanguages = teachingLanguages;
    }

    public LearningOpportunityProviderDTO getProvider() {
        return provider;
    }

    public void setProvider(LearningOpportunityProviderDTO provider) {
        this.provider = provider;
    }

    public List<ApplicationSystemDTO> getApplicationSystems() {
        return applicationSystems;
    }

    public void setApplicationSystems(List<ApplicationSystemDTO> applicationSystems) {
        this.applicationSystems = applicationSystems;
    }

    public List<CodeDTO> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(List<CodeDTO> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public String getTranslationLanguage() {
        return translationLanguage;
    }

    public void setTranslationLanguage(String translationLanguage) {
        this.translationLanguage = translationLanguage;
    }

    public List<CodeDTO> getAvailableTranslationLanguages() {
        return availableTranslationLanguages;
    }

    public void setAvailableTranslationLanguages(
            List<CodeDTO> availableTranslationLanguages) {
        this.availableTranslationLanguages = availableTranslationLanguages;
    }

    public List<String> getFormOfTeaching() {
        return formOfTeaching;
    }

    public void setFormOfTeaching(List<String> formOfTeaching) {
        this.formOfTeaching = formOfTeaching;
    }

    public List<String> getTeachingTimes() {
        return teachingTimes;
    }

    public void setTeachingTimes(List<String> teachingTimes) {
        this.teachingTimes = teachingTimes;
    }

    public List<String> getTeachingPlaces() {
        return teachingPlaces;
    }

    public void setTeachingPlaces(List<String> teachingPlaces) {
        this.teachingPlaces = teachingPlaces;
    }

    public List<String> getQualifications() {
        return qualifications;
    }

    public void setQualifications(List<String> qualifications) {
        this.qualifications = qualifications;
    }

    public String getStartSeason() {
        return startSeason;
    }

    public void setStartSeason(String startSeason) {
        this.startSeason = startSeason;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEducationType() {
        return educationType;
    }

    public void setEducationType(String educationType) {
        this.educationType = educationType;
    }

    public List<CodeDTO> getTopics() {
        return topics;
    }

    public void setTopics(List<CodeDTO> topics) {
        this.topics = topics;
    }

    public List<CodeDTO> getThemes() {
        return themes;
    }

    public void setThemes(List<CodeDTO> themes) {
        this.themes = themes;
    }

    public String getTargetGroup() {
        return targetGroup;
    }

    public void setTargetGroup(String targetGroup) {
        this.targetGroup = targetGroup;
    }

    public String getSubjectsAndCourses() {
        return subjectsAndCourses;
    }

    public void setSubjectsAndCourses(String subjectsAndCourses) {
        this.subjectsAndCourses = subjectsAndCourses;
    }

    public List<LanguageSelectionDTO> getLanguageSelection() {
        return languageSelection;
    }

    public void setLanguageSelection(List<LanguageSelectionDTO> languageSelection) {
        this.languageSelection = languageSelection;
    }

    public List<String> getDiplomas() {
        return diplomas;
    }

    public void setDiplomas(List<String> diplomas) {
        this.diplomas = diplomas;
    }

    public String getEducationDegreeName() {
        return educationDegreeName;
    }

    public void setEducationDegreeName(String educationDegreeName) {
        this.educationDegreeName = educationDegreeName;
    }

    public List<LearningOpportunityProviderDTO> getAdditionalProviders() {
        return additionalProviders;
    }

    public void setAdditionalProviders(List<LearningOpportunityProviderDTO> additionalProviders) {
        this.additionalProviders = additionalProviders;
    }

    public void setEdCodeSuggestions(List<ArticleResultDTO> edCodeSuggestions) {
        this.edCodeSuggestions = edCodeSuggestions;
    }

    public List<ArticleResultDTO> getEdCodeSuggestions() {
        return edCodeSuggestions;
    }

    public void setEdTypeSuggestions(List<ArticleResultDTO> edTypeSuggestions) {
        this.edTypeSuggestions = edTypeSuggestions;
        
    }

    public List<ArticleResultDTO> getEdTypeSuggestions() {
        return edTypeSuggestions;
    }

	public List<String> getDegreeTitles() {
		return degreeTitles;
	}

	public void setDegreeTitles(List<String> degreeTitles) {
		this.degreeTitles = degreeTitles;
	}


}
