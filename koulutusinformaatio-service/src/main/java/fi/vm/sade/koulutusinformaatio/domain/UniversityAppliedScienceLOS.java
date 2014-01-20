package fi.vm.sade.koulutusinformaatio.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UniversityAppliedScienceLOS extends LOS {
	
    
    
    //private List<ChildLOI> lois;
    
    //private I18nText creditUnit;


	//Varmistetut
    private String id;
	private I18nText infoAboutTeachingLangs;
	private I18nText content;
	private I18nText goals;
	private I18nText majorSelection;
	private I18nText structure;
	private I18nText finalExam;
	private I18nText careerOpportunities;
	private I18nText internationalization;
	private I18nText cooperation;
	private I18nText competence;
	private I18nText researchFocus;
	private I18nText accessToFurtherStudies;
	private List<ContactPerson> contactPersons = new ArrayList<ContactPerson>();
	private I18nText educationDomain;
	private I18nText name;
	private I18nText koulutuskoodi;
	private I18nText educationDegree;
    private I18nText degreeTitle;
	private Date startDate;
	private String plannedDuration;
	private I18nText plannedDurationUnit;
	private String pduCodeUri;
	private String creditValue;
	private I18nText degree;
	private I18nText qualification;
	private Boolean chargeable;
	private String educationCode;
	private List<Code> teachingLanguages;
	
	private Provider provider;
	//private List<ApplicationOption
	private List<ApplicationOption> applicationOptions;
	
	private String komoOid;
	private List<String> childKomoOids = new ArrayList<String>();
	
	private List<UniversityAppliedScienceLOS> children = new ArrayList<UniversityAppliedScienceLOS>();
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public void setName(I18nText name) {
		this.name = name;
	}
	
	public I18nText getName() {
		return this.name;
	}
	
	public void setEducationDegree(I18nText degree) {
		this.educationDegree = degree;
	}
	
	public I18nText getEducationDegree() {
		return this.educationDegree;
	}
	
	public void setDegreeTitle(I18nText degreeTitle) {
		this.degreeTitle = degreeTitle;
	}
	
	public I18nText getDegreeTitle() {
		return degreeTitle;
	}
	
	public void setQualification(I18nText qualification) {
		this.qualification = qualification;
	}
	
	public I18nText getQualification() {
		return qualification;
	}
	
	public void setGoals(I18nText goals) {
		this.goals = goals;
	}
	
	public I18nText getGoals() {
		return goals;
	}
	
	public void setProvider(Provider provider) {
		this.provider = provider;
	}
	
	public Provider getProvider() {
		return this.provider;
	}
	
	public void setStructure(I18nText structure) {
		this.structure = structure;
	}
	
	public I18nText getStructure() {
		return structure;
	}
	
	public void setAccessToFurtherStudies(I18nText accessToFurtherStudies) {
		this.accessToFurtherStudies = accessToFurtherStudies;
	}
	
	public I18nText getAccessToFurtherStudies() {
		return accessToFurtherStudies;
	}
	
	public void setCreditValue(String creditValue) {
		this.creditValue = creditValue;
	}
	
	public String getCreditValue() {
		return creditValue;
	}
	/*
	public void setCreditUnit(I18nText creditUnit) {
		this.creditUnit = creditUnit;
	}
	
	public I18nText getCreditUnit() {
		return creditUnit;
	}*/
	
	public void setEducationDomain(I18nText educationDomain) {
		this.educationDomain = educationDomain;
	}
	
	public I18nText getEducationDomain() {
		return educationDomain;
	}

	public void setInfoAboutTeachingLangs(I18nText i18nText) {
		this.infoAboutTeachingLangs = i18nText;	
	}
	
	public I18nText getInfoAboutTeachingLangs() {
		return infoAboutTeachingLangs;
	}

	public void setContent(I18nText i18nTextEnriched) {
		this.content = i18nTextEnriched;
	}
	
	public I18nText getContent() {
		return content;
	}

	public void setMajorSelection(I18nText i18nTextEnriched) {
		this.majorSelection = i18nTextEnriched;	
	}
	
	public I18nText getMajorSelection() {
		return majorSelection;
	}

	public void setFinalExam(I18nText i18nTextEnriched) {
		this.finalExam = i18nTextEnriched;
	}

	public I18nText getFinalExam() {
		return finalExam;
	}

	public void setCareerOpportunities(I18nText i18nTextEnriched) {
		this.careerOpportunities = i18nTextEnriched;	
	}
	
	public I18nText getCareerOpportunities() {
		return careerOpportunities;
	}

	public void setInternationalization(I18nText i18nTextEnriched) {
		this.internationalization = i18nTextEnriched;
	}

	public I18nText getInternationalization() {
		return internationalization;
	}

	public void setCooperation(I18nText i18nTextEnriched) {
		this.cooperation  = i18nTextEnriched;
	}

	public I18nText getCooperation() {
		return cooperation;
	}

	public void setCompetence(I18nText i18nTextEnriched) {
		this.competence = i18nTextEnriched;
	}

	public I18nText getCompetence() {
		return competence;
	}

	public void setResearchFocus(I18nText i18nTextEnriched) {
		this.researchFocus = i18nTextEnriched;
	}

	public I18nText getResearchFocus() {
		return researchFocus;
	}
	
    public List<ContactPerson> getContactPersons() {
        return contactPersons;
    }

    public void setContactPersons(List<ContactPerson> contactPersons) {
        this.contactPersons = contactPersons;
    }

	public I18nText getKoulutuskoodi() {
		return koulutuskoodi;
	}

	public void setKoulutuskoodi(I18nText koulutuskoodi) {
		this.koulutuskoodi = koulutuskoodi;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}	

	public Date getStartDate() {
		return startDate;
	}

	public void setPlannedDuration(String suunniteltuKestoArvo) {
		this.plannedDuration = suunniteltuKestoArvo;
	}

	public String getPlannedDuration() {
		return plannedDuration;
	}

	public void setPlannedDurationUnit(I18nText i18nTextEnriched) {
		this.plannedDurationUnit = i18nTextEnriched;
	}

	public I18nText getPlannedDurationUnit() {
		return plannedDurationUnit;
	}

	public void setPduCodeUri(String uri) {
		this.pduCodeUri = uri;
	}
	
	public String getPduCodeUri() {
		return pduCodeUri;
	}

	public void setDegree(I18nText i18nText) {
		this.degree = i18nText;
	}
	
	public I18nText getDegree() {
		return degree;
	}

	public void setChargeable(Boolean opintojenMaksullisuus) {
		this.chargeable = opintojenMaksullisuus;	
	}

	public Boolean getChargeable() {
		return chargeable;
	}

	public void setEducationCode(String uri) {
		educationCode = uri;
	}

	public String getEducationCode() {
		return educationCode;
	}

	public void setTeachingLanguages(List<Code> createCodes) {
		this.teachingLanguages = createCodes;
	}

	public List<Code> getTeachingLanguages() {
		return teachingLanguages;
	}

	public List<ApplicationOption> getApplicationOptions() {
		return applicationOptions;
	}

	public void setApplicationOptions(List<ApplicationOption> applicationOptions) {
		this.applicationOptions = applicationOptions;
	}    
	
    public String getKomoOid() {
		return komoOid;
	}

	public void setKomoOid(String komoOid) {
		this.komoOid = komoOid;
	}

	public List<String> getChildKomoOids() {
		return childKomoOids;
	}

	public void setChildKomoOids(List<String> childKomoOids) {
		this.childKomoOids = childKomoOids;
	}

	public List<UniversityAppliedScienceLOS> getChildren() {
		return children;
	}

	public void setChildren(List<UniversityAppliedScienceLOS> children) {
		this.children = children;
	}

}
