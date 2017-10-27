package fi.vm.sade.koulutusinformaatio.domain;

import java.util.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class KoulutusLOS extends LOS {

    // Varmistetut
    private I18nText content;
    private I18nText structure;
    private I18nText internationalization;
    private I18nText cooperation;
    private I18nText accessToFurtherStudies;
    private List<ContactPerson> contactPersons = new ArrayList<ContactPerson>();
    private I18nText educationDomain;
    private I18nText koulutuskoodi;
    private String educationDegree;
    private I18nText educationDegreeLang;
    private I18nText degreeTitle;
    private Date startDate;
    private Date endDate;
    private List<Date> startDates;
    private String plannedDuration;
    private I18nText plannedDurationUnit;
    private String pduCodeUri;
    private String creditValue;
    private I18nText creditUnit;
    private I18nText creditUnitShort;
    private I18nText degree;
    private I18nText infoAboutCharge;
    private I18nText careerOpportunities;
    private I18nText targetGroup;
    private I18nText workingLifePlacement;
    private I18nText selectingDegreeProgram;
    private String linkToCurriculum;

    private Code educationCode;
    private List<Code> teachingLanguages;

    private List<Provider> additionalProviders = Lists.newArrayList();

    private String komoOid;

    private List<Code> prerequisites = new ArrayList<>();
    private Code koulutusPrerequisite;
    private List<I18nText> formOfTeaching;
    private List<I18nText> teachingTimes;
    private List<I18nText> teachingPlaces;
    private List<I18nText> qualifications = new ArrayList<>();
    private List<I18nText> degreeTitles;
    private I18nText startSeason;
    private int startYear;

    // Status of the lo. For preview
    private String status;
    private List<Code> availableTranslationLanguages;

    private List<Code> facetPrerequisites = new ArrayList<Code>();
    private String educationType;

    private Code additionalEducationType;

    private List<Code> fotFacet = new ArrayList<Code>();

    private List<Code> timeOfTeachingFacet = new ArrayList<Code>();

    private List<Code> formOfStudyFacet = new ArrayList<Code>();

    private Code koulutuslaji;

    private Map<String, List<String>> subjects;

    // AdultUpperSecondaryLOSin kentät
    private I18nText subjectsAndCourses;
    private List<LanguageSelection> languageSelection;
    private List<I18nText> diplomas = new ArrayList<I18nText>();
    private TutkintoLOS tutkinto;

    // AmmatillinenKoulutusLOSin kenttä
    private List<KoulutusLOS> siblings = new ArrayList<KoulutusLOS>();

    private boolean osaamisalaton;

    //Tutkintoon johtamattoman LOSin kentät

    private Set<KoulutusLOS> opintokokonaisuudet = Sets.newHashSet();
    private List<KoulutusLOS> opintojaksos = Lists.newArrayList();

    //Does not persist to database, used for creating korkeakoulututkinto LOS.
    private Set<KoulutusLOS> cousins = Sets.newHashSet();

    private String opettaja;
    private String opinnonTyyppiUri;
    private String hinta;
    
    private I18nText vastaavaKorkeakoulu;
    private I18nText maksullisuus;
    private I18nText edeltavatOpinnot;
    private I18nText arviointi;
    private I18nText opetuksenAikaJaPaikka;
    private I18nText lisatietoja;

    private I18nText competence;
    private String hakijalleNaytettavaTunniste;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEducationDegree(String degree) {
        this.educationDegree = degree;
    }

    public String getEducationDegree() {
        return this.educationDegree;
    }

    public void setDegreeTitle(I18nText degreeTitle) {
        this.degreeTitle = degreeTitle;
    }

    public I18nText getDegreeTitle() {
        return degreeTitle;
    }

    public void setGoals(I18nText goals) {
        this.goals = goals;
    }

    public I18nText getGoals() {
        return goals;
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

    public void setEducationDomain(I18nText educationDomain) {
        this.educationDomain = educationDomain;
    }

    public I18nText getEducationDomain() {
        return educationDomain;
    }

    public void setContent(I18nText i18nTextEnriched) {
        this.content = i18nTextEnriched;
    }

    public I18nText getContent() {
        return content;
    }

    public void setInternationalization(I18nText i18nTextEnriched) {
        this.internationalization = i18nTextEnriched;
    }

    public I18nText getInternationalization() {
        return internationalization;
    }

    public void setCooperation(I18nText i18nTextEnriched) {
        this.cooperation = i18nTextEnriched;
    }

    public I18nText getCooperation() {
        return cooperation;
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

    public void setEducationCode(Code edCode) {
        educationCode = edCode;
    }

    public Code getEducationCode() {
        return educationCode;
    }

    public void setTeachingLanguages(List<Code> createCodes) {
        this.teachingLanguages = createCodes;
    }

    public List<Code> getTeachingLanguages() {
        return teachingLanguages;
    }

    public String getKomoOid() {
        return komoOid;
    }

    public void setKomoOid(String komoOid) {
        this.komoOid = komoOid;
    }

    public List<Code> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(List<Code> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public void setFormOfTeaching(List<I18nText> opetusmuodos) {
        this.formOfTeaching = opetusmuodos;
    }

    public List<I18nText> getFormOfTeaching() {
        return formOfTeaching;
    }

    public I18nText getCreditUnit() {
        return creditUnit;
    }

    public void setCreditUnit(I18nText creditUnit) {
        this.creditUnit = creditUnit;
    }

    public I18nText getCreditUnitShort() {
        return creditUnitShort;
    }

    public void setCreditUnitShort(I18nText creditUnitShort) {
        this.creditUnitShort = creditUnitShort;
    }

    public I18nText getEducationDegreeLang() {
        return educationDegreeLang;
    }

    public void setEducationDegreeLang(I18nText educationDegreeLang) {
        this.educationDegreeLang = educationDegreeLang;
    }

    public void setTeachingTimes(List<I18nText> teachingTimes) {
        this.teachingTimes = teachingTimes;

    }

    public List<I18nText> getTeachingTimes() {
        return teachingTimes;
    }

    public void setTeachingPlaces(List<I18nText> teachingPlaces) {
        this.teachingPlaces = teachingPlaces;

    }

    public List<I18nText> getTeachingPlaces() {
        return teachingPlaces;
    }

    public I18nText getStartSeason() {
        return startSeason;
    }

    public void setStartSeason(I18nText startSeason) {
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

    public List<Code> getAvailableTranslationLanguages() {
        return availableTranslationLanguages;
    }

    public void setAvailableTranslationLanguages(List<Code> availableTranslationLanguages) {
        this.availableTranslationLanguages = availableTranslationLanguages;
    }

    public List<Code> getFacetPrerequisites() {
        return facetPrerequisites;
    }

    public void setFacetPrerequisites(List<Code> facetPrerequisites) {
        this.facetPrerequisites = facetPrerequisites;
    }

    public void setEducationType(String educationType) {
        this.educationType = educationType;
    }

    public String getEducationType() {
        return educationType;
    }

    public Code getAdditionalEducationType() {
        return additionalEducationType;
    }

    public void setAdditionalEducationType(Code additionalEducationType) {
        this.additionalEducationType = additionalEducationType;
    }

    public List<Code> getFotFacet() {
        return fotFacet;
    }

    public void setFotFacet(List<Code> formOfTeachingFacet) {
        this.fotFacet = formOfTeachingFacet;
    }

    public List<Code> getTimeOfTeachingFacet() {
        return timeOfTeachingFacet;
    }

    public void setTimeOfTeachingFacet(List<Code> timeOfTeachingFacet) {
        this.timeOfTeachingFacet = timeOfTeachingFacet;
    }

    public List<Code> getFormOfStudyFacet() {
        return formOfStudyFacet;
    }

    public void setFormOfStudyFacet(List<Code> formOfStudyFacet) {
        this.formOfStudyFacet = formOfStudyFacet;
    }

    public Code getKoulutuslaji() {
        return koulutuslaji;
    }

    public void setKoulutuslaji(Code koulutuslaji) {
        this.koulutuslaji = koulutuslaji;
    }

    public List<I18nText> getQualifications() {
        return qualifications;
    }

    public void setQualifications(List<I18nText> qualifications) {
        this.qualifications = qualifications;
    }

    public void setInfoAboutCharge(I18nText i18nTextEnriched) {
        this.infoAboutCharge = i18nTextEnriched;

    }

    public I18nText getInfoAboutCharge() {
        return infoAboutCharge;
    }

    public void setCareerOpportunities(I18nText i18nTextEnriched) {
        this.careerOpportunities = i18nTextEnriched;
    }

    public I18nText getCareerOpportunities() {
        return careerOpportunities;
    }

    public I18nText getTargetGroup() {
        return targetGroup;
    }

    public void setTargetGroup(I18nText targetGroup) {
        this.targetGroup = targetGroup;
    }

    public List<Provider> getAdditionalProviders() {
        return additionalProviders;
    }

    public void setAdditionalProviders(List<Provider> additionalProviders) {
        this.additionalProviders = additionalProviders;
    }

    public List<I18nText> getDegreeTitles() {
        return degreeTitles;
    }

    public void setDegreeTitles(List<I18nText> degreeTitles) {
        this.degreeTitles = degreeTitles;
    }

    public List<Date> getStartDates() {
        return startDates;
    }

    public void setStartDates(List<Date> startDates) {
        this.startDates = startDates;
    }

    public Map<String, List<String>> getSubjects() {
        return subjects;
    }

    public void setSubjects(Map<String, List<String>> subjects) {
        this.subjects = subjects;
    }

    public I18nText getSubjectsAndCourses() {
        return subjectsAndCourses;
    }

    public void setSubjectsAndCourses(I18nText subjectsAndCourses) {
        this.subjectsAndCourses = subjectsAndCourses;
    }

    public void setLanguageSelection(List<LanguageSelection> languageSelection) {
        this.languageSelection = languageSelection;
    }

    public List<LanguageSelection> getLanguageSelection() {
        return languageSelection;
    }

    public List<I18nText> getDiplomas() {
        return diplomas;
    }

    public void setDiplomas(List<I18nText> diplomas) {
        this.diplomas = diplomas;
    }

    public String getLinkToCurriculum() {
        return linkToCurriculum;
    }

    public void setLinkToCurriculum(String linkToCurriculum) {
        this.linkToCurriculum = linkToCurriculum;
    }

    public I18nText getWorkingLifePlacement() {
        return workingLifePlacement;
    }

    public void setWorkingLifePlacement(I18nText i18nText) {
        this.workingLifePlacement = i18nText;
    }

    public TutkintoLOS getTutkinto() {
        return this.tutkinto;
    }

    public void setTutkinto(TutkintoLOS tutkinto) {
        this.tutkinto = tutkinto;
    }

    public List<KoulutusLOS> getSiblings() {
        return siblings;
    }

    public void setSiblings(List<KoulutusLOS> siblings) {
        this.siblings = siblings;
    }

    public Code getKoulutusPrerequisite() {
        return koulutusPrerequisite;
    }

    public void setKoulutusPrerequisite(Code koulutusPrerequisite) {
        this.koulutusPrerequisite = koulutusPrerequisite;
    }

    public I18nText getSelectingDegreeProgram() {
        return selectingDegreeProgram;
    }

    public void setSelectingDegreeProgram(I18nText selectingDegreeProgram) {
        this.selectingDegreeProgram = selectingDegreeProgram;
    }

    public boolean isOsaamisalaton() {
        return osaamisalaton;
    }

    public void setOsaamisalaton(boolean osaamisalaton) {
        this.osaamisalaton = osaamisalaton;
    }

    public Set<KoulutusLOS> getOpintokokonaisuudet() {
        return opintokokonaisuudet;
    }

    public void setOpintokokonaisuudet(Set<KoulutusLOS> opintokokonaisuudet) {
        this.opintokokonaisuudet = opintokokonaisuudet;
    }

    public void appendOpintokokonaisuus(KoulutusLOS opintokokonaisuus) {
        this.opintokokonaisuudet.add(opintokokonaisuus);
    }

    public List<KoulutusLOS> getOpintojaksos() {
        return opintojaksos;
    }

    public void setOpintojaksos(List<KoulutusLOS> opintojaksos) {
        this.opintojaksos = opintojaksos;
    }

    public Set<KoulutusLOS> getCousins() { return cousins; }

    public void setCousins(Set<KoulutusLOS> cousins) { this.cousins = cousins; }

    public String getOpettaja() {
        return opettaja;
    }

    public void setOpettaja(String opettaja) {
        this.opettaja = opettaja;
    }

    public String getOpinnonTyyppiUri() {
        return opinnonTyyppiUri;
    }

    public void setOpinnonTyyppiUri(String opinnonTyyppiUri) {
        this.opinnonTyyppiUri = opinnonTyyppiUri;
    }

    public I18nText getVastaavaKorkeakoulu() {
        return vastaavaKorkeakoulu;
    }

    public void setVastaavaKorkeakoulu(I18nText vastaavaKorkeakoulu) {
        this.vastaavaKorkeakoulu = vastaavaKorkeakoulu;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public I18nText getMaksullisuus() {
        return maksullisuus;
    }

    public void setMaksullisuus(I18nText maksullisuus) {
        this.maksullisuus = maksullisuus;
    }

    public I18nText getEdeltavatOpinnot() {
        return edeltavatOpinnot;
    }

    public void setEdeltavatOpinnot(I18nText edeltavatOpinnot) {
        this.edeltavatOpinnot = edeltavatOpinnot;
    }

    public I18nText getArviointi() {
        return arviointi;
    }

    public void setArviointi(I18nText arviointi) {
        this.arviointi = arviointi;
    }

    public I18nText getOpetuksenAikaJaPaikka() {
        return opetuksenAikaJaPaikka;
    }

    public void setOpetuksenAikaJaPaikka(I18nText opetuksenAikaJaPaikka) {
        this.opetuksenAikaJaPaikka = opetuksenAikaJaPaikka;
    }

    public I18nText getLisatietoja() {
        return lisatietoja;
    }

    public void setLisatietoja(I18nText lisatietoja) {
        this.lisatietoja = lisatietoja;
    }

    public I18nText getCompetence() {
        return competence;
    }

    public void setCompetence(I18nText competence) {
        this.competence = competence;
    }

    public String getHinta() {
        return hinta;
    }

    public void setHinta(String hinta) {
        this.hinta = hinta;
    }

    public String getHakijalleNaytettavaTunniste() {
        return hakijalleNaytettavaTunniste;
    }

    public void setHakijalleNaytettavaTunniste(String hakijalleNaytettavaTunniste) {
        this.hakijalleNaytettavaTunniste = hakijalleNaytettavaTunniste;
    }
}
