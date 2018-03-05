package fi.vm.sade.koulutusinformaatio.dao.entity;

import java.util.*;

import org.mongodb.morphia.annotations.*;

import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.domain.LanguageSelection;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

/**
 *
 * @author Markus
 *
 */
@Entity("koulutusLOS")
public class KoulutusLOSEntity {

    @Id
    private String id;
    @Embedded
    private I18nTextEntity content;
    @Embedded
    private I18nTextEntity goals;
    @Embedded
    private I18nTextEntity structure;
    @Embedded
    private I18nTextEntity internationalization;
    @Embedded
    private I18nTextEntity cooperation;
    @Embedded
    private I18nTextEntity accessToFurtherStudies;
    @Embedded
    private I18nTextEntity selectingDegreeProgram;
    @Embedded
    private List<ContactPersonEntity> contactPersons = new ArrayList<ContactPersonEntity>();
    @Embedded
    private I18nTextEntity educationDomain;
    @Embedded
    private I18nTextEntity name;
    @Embedded
    private I18nTextEntity shortTitle;
    @Embedded
    private I18nTextEntity koulutuskoodi;
    private String educationDegree;
    @Embedded
    private I18nTextEntity educationDegreeLang;

    @Embedded
    private I18nTextEntity degreeTitle;
    @Embedded
    private List<I18nTextEntity> degreeTitles;
    @Embedded
    private List<Date> startDates;
    private Date startDate;
    private Date endDate;
    private String plannedDuration;

    @Embedded
    private I18nTextEntity plannedDurationUnit;
    private String pduCodeUri;
    private String creditValue;
    @Embedded
    private I18nTextEntity creditUnit;
    @Embedded
    private I18nTextEntity creditUnitShort;
    @Embedded
    private I18nTextEntity degree;
    @Embedded
    private List<I18nTextEntity> qualifications;
    @Embedded
    private CodeEntity educationCode;
    @Embedded
    private List<CodeEntity> teachingLanguages;
    @Reference
    private LearningOpportunityProviderEntity provider;
    @Reference
    private List<LearningOpportunityProviderEntity> additionalProviders = new ArrayList<LearningOpportunityProviderEntity>();
    @Reference
    private List<ApplicationOptionEntity> applicationOptions;
    @Reference
    private TutkintoLOSEntity tutkinto;

    private String komoOid;
    @Embedded
    private List<I18nTextEntity> formOfTeaching;

    @Embedded
    private List<CodeEntity> prerequisites;
    @Embedded
    private CodeEntity koulutusPrerequisite;
    @Embedded
    private List<I18nTextEntity> teachingTimes;
    @Embedded
    private List<I18nTextEntity> teachingPlaces;

    private String type;
    @Embedded
    private I18nTextEntity startSeason;
    private int startYear;
    @Embedded
    private List<CodeEntity> availableTranslationLanguages;

    @Embedded
    private List<CodeEntity> facetPrerequisites;

    @Embedded
    private List<CodeEntity> topics;
    @Embedded
    private List<CodeEntity> themes;

    private String educationType;

    @Embedded
    private List<CodeEntity> fotFacet = new ArrayList<CodeEntity>();

    @Embedded
    private List<CodeEntity> timeOfTeachingFacet = new ArrayList<CodeEntity>();
    @Embedded
    private List<CodeEntity> formOfStudyFacet = new ArrayList<CodeEntity>();

    @Embedded
    private CodeEntity koulutuslaji;
    //specials
    @Embedded
    private I18nTextEntity targetGroup;
    @Embedded
    private I18nTextEntity subjectsAndCourses;

    private List<LanguageSelection> languageSelection;
    @Embedded
    private List<I18nTextEntity> diplomas = new ArrayList<I18nTextEntity>();

    @Embedded
    private I18nTextEntity workingLifePlacement;
    private String linkToCurriculum;

    @Reference
    private List<KoulutusLOSEntity> siblings = new ArrayList<KoulutusLOSEntity>();

    private boolean osaamisalaton;

    private ToteutustyyppiEnum toteutustyyppi;

    @Reference
    private Set<KoulutusLOSEntity> opintokokonaisuudet;
    @Reference
    private List<KoulutusLOSEntity> opintojaksos = Lists.newArrayList();

    private String opettaja;
    private String opinnonTyyppiUri;
    private String hinta;

    @Embedded
    private I18nTextEntity vastaavaKorkeakoulu;
    @Embedded
    private I18nTextEntity maksullisuus;
    @Embedded
    private I18nTextEntity edeltavatOpinnot;
    @Embedded
    private I18nTextEntity arviointi;
    @Embedded
    private I18nTextEntity opetuksenAikaJaPaikka;
    @Embedded
    private I18nTextEntity lisatietoja;

    @Embedded
    private I18nTextEntity competence;
    @Embedded
    private String hakijalleNaytettavaTunniste;
    @Embedded
    private Map<String, List<String>> subjects;
    @Embedded
    private Map<String, List<CodeEntity>> aoToRequiredBaseEdCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public I18nTextEntity getContent() {
        return content;
    }

    public void setContent(I18nTextEntity content) {
        this.content = content;
    }

    public I18nTextEntity getGoals() {
        return goals;
    }

    public void setGoals(I18nTextEntity goals) {
        this.goals = goals;
    }

    public I18nTextEntity getStructure() {
        return structure;
    }

    public void setStructure(I18nTextEntity structure) {
        this.structure = structure;
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

    public I18nTextEntity getAccessToFurtherStudies() {
        return accessToFurtherStudies;
    }

    public void setAccessToFurtherStudies(I18nTextEntity accessToFurtherStudies) {
        this.accessToFurtherStudies = accessToFurtherStudies;
    }

    public List<ContactPersonEntity> getContactPersons() {
        return contactPersons;
    }

    public void setContactPersons(List<ContactPersonEntity> contactPersons) {
        this.contactPersons = contactPersons;
    }

    public I18nTextEntity getEducationDomain() {
        return educationDomain;
    }

    public void setEducationDomain(I18nTextEntity educationDomain) {
        this.educationDomain = educationDomain;
    }

    public I18nTextEntity getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(I18nTextEntity shortTitle) {
        this.shortTitle = shortTitle;
    }

    public I18nTextEntity getName() {
        return name;
    }

    public void setName(I18nTextEntity name) {
        this.name = name;
    }

    public I18nTextEntity getKoulutuskoodi() {
        return koulutuskoodi;
    }

    public void setKoulutuskoodi(I18nTextEntity koulutuskoodi) {
        this.koulutuskoodi = koulutuskoodi;
    }

    public String getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(String educationDegree) {
        this.educationDegree = educationDegree;
    }

    public I18nTextEntity getDegreeTitle() {
        return degreeTitle;
    }

    public void setDegreeTitle(I18nTextEntity degreeTitle) {
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

    public I18nTextEntity getPlannedDurationUnit() {
        return plannedDurationUnit;
    }

    public void setPlannedDurationUnit(I18nTextEntity plannedDurationUnit) {
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

    public I18nTextEntity getDegree() {
        return degree;
    }

    public void setDegree(I18nTextEntity degree) {
        this.degree = degree;
    }

    public List<I18nTextEntity> getQualifications() {
        return qualifications;
    }

    public void setQualifications(List<I18nTextEntity> qualifications) {
        this.qualifications = qualifications;
    }

    public CodeEntity getEducationCode() {
        return educationCode;
    }

    public void setEducationCode(CodeEntity educationCode) {
        this.educationCode = educationCode;
    }

    public List<CodeEntity> getTeachingLanguages() {
        return teachingLanguages;
    }

    public void setTeachingLanguages(List<CodeEntity> teachingLanguages) {
        this.teachingLanguages = teachingLanguages;
    }

    public LearningOpportunityProviderEntity getProvider() {
        return provider;
    }

    public void setProvider(LearningOpportunityProviderEntity provider) {
        this.provider = provider;
    }

    public List<ApplicationOptionEntity> getApplicationOptions() {
        return applicationOptions;
    }

    public void setApplicationOptions(
            List<ApplicationOptionEntity> applicationOptions) {
        this.applicationOptions = applicationOptions;
    }

    public String getKomoOid() {
        return komoOid;
    }

    public void setKomoOid(String komoOid) {
        this.komoOid = komoOid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<I18nTextEntity> getFormOfTeaching() {
        return formOfTeaching;
    }

    public void setFormOfTeaching(List<I18nTextEntity> formOfTeaching) {
        this.formOfTeaching = formOfTeaching;
    }

    public List<CodeEntity> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(List<CodeEntity> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public I18nTextEntity getCreditUnit() {
        return creditUnit;
    }

    public void setCreditUnit(I18nTextEntity creditUnit) {
        this.creditUnit = creditUnit;
    }

    public I18nTextEntity getCreditUnitShort() {
        return creditUnitShort;
    }

    public void setCreditUnitShort(I18nTextEntity creditUnitShort) {
        this.creditUnitShort = creditUnitShort;
    }

    public I18nTextEntity getEducationDegreeLang() {
        return educationDegreeLang;
    }

    public void setEducationDegreeLang(I18nTextEntity educationDegreeLang) {
        this.educationDegreeLang = educationDegreeLang;
    }

    public List<I18nTextEntity> getTeachingTimes() {
        return teachingTimes;
    }

    public void setTeachingTimes(List<I18nTextEntity> teachingTimes) {
        this.teachingTimes = teachingTimes;
    }

    public List<I18nTextEntity> getTeachingPlaces() {
        return teachingPlaces;
    }

    public void setTeachingPlaces(List<I18nTextEntity> teachingPlaces) {
        this.teachingPlaces = teachingPlaces;
    }

    public I18nTextEntity getStartSeason() {
        return startSeason;
    }

    public void setStartSeason(I18nTextEntity startSeason) {
        this.startSeason = startSeason;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public List<CodeEntity> getAvailableTranslationLanguages() {
        return availableTranslationLanguages;
    }

    public void setAvailableTranslationLanguages(List<CodeEntity> availableTranslationLanguages) {
        this.availableTranslationLanguages = availableTranslationLanguages;
    }

    public List<CodeEntity> getFacetPrerequisites() {
        return facetPrerequisites;
    }

    public void setFacetPrerequisites(List<CodeEntity> facetPrerequisites) {
        this.facetPrerequisites = facetPrerequisites;
    }

    public List<CodeEntity> getTopics() {
        return topics;
    }

    public void setTopics(List<CodeEntity> topics) {
        this.topics = topics;
    }

    public List<CodeEntity> getThemes() {
        return themes;
    }

    public void setThemes(List<CodeEntity> themes) {
        this.themes = themes;
    }

    public String getEducationType() {
        return educationType;
    }

    public void setEducationType(String educationType) {
        this.educationType = educationType;
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

    public I18nTextEntity getSubjectsAndCourses() {
        return subjectsAndCourses;
    }

    public void setSubjectsAndCourses(I18nTextEntity subjectsAndCourses) {
        this.subjectsAndCourses = subjectsAndCourses;
    }

    public List<LanguageSelection> getLanguageSelection() {
        return languageSelection;
    }

    public void setLanguageSelection(List<LanguageSelection> languageSelection) {
        this.languageSelection = languageSelection;
    }

    public List<I18nTextEntity> getDiplomas() {
        return diplomas;
    }

    public void setDiplomas(List<I18nTextEntity> diplomas) {
        this.diplomas = diplomas;
    }

    public List<LearningOpportunityProviderEntity> getAdditionalProviders() {
        return additionalProviders;
    }

    public void setAdditionalProviders(
            List<LearningOpportunityProviderEntity> additionalProviders) {
        this.additionalProviders = additionalProviders;
    }

    public List<I18nTextEntity> getDegreeTitles() {
        return degreeTitles;
    }

    public void setDegreeTitles(List<I18nTextEntity> degreeTitles) {
        this.degreeTitles = degreeTitles;
    }

    public List<Date> getStartDates() {
        return startDates;
    }

    public void setStartDates(List<Date> startDates) {
        this.startDates = startDates;
    }

    public I18nTextEntity getWorkingLifePlacement() {
        return workingLifePlacement;
    }

    public void setWorkingLifePlacement(I18nTextEntity workingLifePlacement) {
        this.workingLifePlacement = workingLifePlacement;
    }

    public String getLinkToCurriculum() {
        return linkToCurriculum;
    }

    public void setLinkToCurriculum(String linkToCurriculum) {
        this.linkToCurriculum = linkToCurriculum;
    }

    public List<KoulutusLOSEntity> getSiblings() {
        return siblings;
    }

    public void setSiblings(List<KoulutusLOSEntity> siblings) {
        this.siblings = siblings;
    }

    public TutkintoLOSEntity getTutkinto() {
        return tutkinto;
    }

    public void setTutkinto(TutkintoLOSEntity tutkinto) {
        this.tutkinto = tutkinto;
    }

    public CodeEntity getKoulutusPrerequisite() {
        return koulutusPrerequisite;
    }

    public void setKoulutusPrerequisite(CodeEntity koulutusPrerequisite) {
        this.koulutusPrerequisite = koulutusPrerequisite;
    }

    public I18nTextEntity getSelectingDegreeProgram() {
        return selectingDegreeProgram;
    }

    public void setSelectingDegreeProgram(I18nTextEntity selectingDegreeProgram) {
        this.selectingDegreeProgram = selectingDegreeProgram;
    }

    public boolean isOsaamisalaton() {
        return osaamisalaton;
    }

    public void setOsaamisalaton(boolean osaamisalaton) {
        this.osaamisalaton = osaamisalaton;
    }

    public ToteutustyyppiEnum getToteutustyyppi() {
        return toteutustyyppi;
    }

    public void setToteutustyyppi(ToteutustyyppiEnum toteutustyyppi) {
        this.toteutustyyppi = toteutustyyppi;
    }

    public Set<KoulutusLOSEntity> getOpintokokonaisuudet() {
        return opintokokonaisuudet;
    }

    public void setOpintokokonaisuudet(Set<KoulutusLOSEntity> opintokokonaisuudet) {
        this.opintokokonaisuudet = opintokokonaisuudet;
    }

    public List<KoulutusLOSEntity> getOpintojaksos() {
        return opintojaksos;
    }

    public void setOpintojaksos(List<KoulutusLOSEntity> opintojaksos) {
        this.opintojaksos = opintojaksos;
    }

    public String getOpettaja() {
        return opettaja;
    }

    public void setOpettaja(String opettaja) {
        this.opettaja = opettaja;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getOpinnonTyyppiUri() {
        return opinnonTyyppiUri;
    }

    public void setOpinnonTyyppiUri(String opinnonTyyppiUri) {
        this.opinnonTyyppiUri = opinnonTyyppiUri;
    }

    public I18nTextEntity getMaksullisuus() {
        return maksullisuus;
    }

    public void setMaksullisuus(I18nTextEntity maksullisuus) {
        this.maksullisuus = maksullisuus;
    }

    public I18nTextEntity getEdeltavatOpinnot() {
        return edeltavatOpinnot;
    }

    public void setEdeltavatOpinnot(I18nTextEntity edeltavatOpinnot) {
        this.edeltavatOpinnot = edeltavatOpinnot;
    }

    public I18nTextEntity getArviointi() {
        return arviointi;
    }

    public void setArviointi(I18nTextEntity arviointi) {
        this.arviointi = arviointi;
    }

    public I18nTextEntity getOpetuksenAikaJaPaikka() {
        return opetuksenAikaJaPaikka;
    }

    public void setOpetuksenAikaJaPaikka(I18nTextEntity opetuksenAikaJaPaikka) {
        this.opetuksenAikaJaPaikka = opetuksenAikaJaPaikka;
    }

    public I18nTextEntity getLisatietoja() {
        return lisatietoja;
    }

    public void setLisatietoja(I18nTextEntity lisatietoja) {
        this.lisatietoja = lisatietoja;
    }

    public I18nTextEntity getCompetence() {
        return competence;
    }

    public void setCompetence(I18nTextEntity competence) {
        this.competence = competence;
    }

    public String getHinta() {
        return hinta;
    }

    public void setHinta(String hinta) {
        this.hinta = hinta;
    }

    public I18nTextEntity getVastaavaKorkeakoulu() {
        return vastaavaKorkeakoulu;
    }

    public void setVastaavaKorkeakoulu(I18nTextEntity vastaavaKorkeakoulu) {
        this.vastaavaKorkeakoulu = vastaavaKorkeakoulu;
    }

    public String getHakijalleNaytettavaTunniste() {
        return hakijalleNaytettavaTunniste;
    }

    public void setHakijalleNaytettavaTunniste(String hakijalleNaytettavaTunniste) {
        this.hakijalleNaytettavaTunniste = hakijalleNaytettavaTunniste;
    }

    public Map<String, List<String>> getSubjects() {
        return subjects;
    }

    public void setSubjects(Map<String, List<String>> subjects) {
        this.subjects = subjects;
    }

    public Map<String, List<CodeEntity>> getAoToRequiredBaseEdCode() {
        return aoToRequiredBaseEdCode;
    }

    public void setAoToRequiredBaseEdCode(Map<String, List<CodeEntity>> aoToRequiredBaseEdCode) {
        this.aoToRequiredBaseEdCode = aoToRequiredBaseEdCode;
    }


    @PrePersist
    void prePersist(){
        Map<String, List<CodeEntity>> aoToRequiredBaseEdCode = this.getAoToRequiredBaseEdCode();
        if(aoToRequiredBaseEdCode == null) return;
        Map<String, List<CodeEntity>> converted = new HashMap<>();
        for (Map.Entry<String, List<CodeEntity>> e : aoToRequiredBaseEdCode.entrySet()) {
            if(e == null) continue;
            String newkey = e.getKey().replace('.', '_');
            converted.put(newkey, e.getValue());
        }
        this.aoToRequiredBaseEdCode = converted;
    }

    @PostLoad
    void postLoad(){
        Map<String, List<CodeEntity>> aoToRequiredBaseEdCode = this.getAoToRequiredBaseEdCode();
        if(aoToRequiredBaseEdCode == null) return;
        Map<String, List<CodeEntity>> converted = new HashMap<>();
        for (Map.Entry<String, List<CodeEntity>> e : aoToRequiredBaseEdCode.entrySet()) {
            if(e == null) continue;
            String newkey = e.getKey().replace('_', '.'); //opposite of pre-persist
            converted.put(newkey, e.getValue());
        }
        this.aoToRequiredBaseEdCode = converted;
    }
}
