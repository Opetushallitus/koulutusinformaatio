package fi.vm.sade.koulutusinformaatio.dao.entity;

import java.util.List;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

@Entity("competenceBasedQualificationParentLOS")
public class CompetenceBasedQualificationParentLOSEntity {

    @Id
    private String id;
    @Embedded
    private I18nTextEntity name;
    @Embedded
    private I18nTextEntity goals;
    @Embedded
    private I18nTextEntity accessToFurtherStudies;
    @Embedded
    private I18nTextEntity choosingCompetence;
    @Embedded
    private I18nTextEntity degreeCompletion;
    @Embedded
    private I18nTextEntity educationKind;
    @Embedded
    private I18nTextEntity educationDomain;
    @Embedded
    private I18nTextEntity educationType;
    
    @Reference
    private LearningOpportunityProviderEntity provider;
    @Reference
    private List<ApplicationOptionEntity> applicationOptions;
    
    @Embedded
    private List<AdultVocationalLOSEntity> children;
    
    @Embedded
    private List<CodeEntity> availableTranslationLanguages;
    
    private boolean chargeable;
    private double charge;
    private boolean osaamisala;
    
    private String edtUri;
    private String determiner;
    
    @Embedded
    private List<CodeEntity> topics;
    @Embedded
    private List<CodeEntity> themes;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public I18nTextEntity getName() {
        return name;
    }
    public void setName(I18nTextEntity name) {
        this.name = name;
    }
    public I18nTextEntity getGoals() {
        return goals;
    }
    public void setGoals(I18nTextEntity goals) {
        this.goals = goals;
    }
    public I18nTextEntity getAccessToFurtherStudies() {
        return accessToFurtherStudies;
    }
    public void setAccessToFurtherStudies(I18nTextEntity accessToFurtherStudies) {
        this.accessToFurtherStudies = accessToFurtherStudies;
    }
    public I18nTextEntity getChoosingCompetence() {
        return choosingCompetence;
    }
    public void setChoosingCompetence(I18nTextEntity choosingCompetence) {
        this.choosingCompetence = choosingCompetence;
    }
    public I18nTextEntity getDegreeCompletion() {
        return degreeCompletion;
    }
    public void setDegreeCompletion(I18nTextEntity degreeCompletion) {
        this.degreeCompletion = degreeCompletion;
    }
    public I18nTextEntity getEducationKind() {
        return educationKind;
    }
    public void setEducationKind(I18nTextEntity educationKind) {
        this.educationKind = educationKind;
    }
    public I18nTextEntity getEducationDomain() {
        return educationDomain;
    }
    public void setEducationDomain(I18nTextEntity educationDomain) {
        this.educationDomain = educationDomain;
    }
    public I18nTextEntity getEducationType() {
        return educationType;
    }
    public void setEducationType(I18nTextEntity educationType) {
        this.educationType = educationType;
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
    public List<AdultVocationalLOSEntity> getChildren() {
        return children;
    }
    public void setChildren(List<AdultVocationalLOSEntity> children) {
        this.children = children;
    }
    public List<CodeEntity> getAvailableTranslationLanguages() {
        return availableTranslationLanguages;
    }
    public void setAvailableTranslationLanguages(
            List<CodeEntity> availableTranslationLanguages) {
        this.availableTranslationLanguages = availableTranslationLanguages;
    }
    public boolean isChargeable() {
        return chargeable;
    }
    public void setChargeable(boolean chargeable) {
        this.chargeable = chargeable;
    }
    public double getCharge() {
        return charge;
    }
    public void setCharge(double charge) {
        this.charge = charge;
    }
    public boolean isOsaamisala() {
        return osaamisala;
    }
    public void setOsaamisala(boolean osaamisala) {
        this.osaamisala = osaamisala;
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
    public String getEdtUri() {
        return edtUri;
    }
    public void setEdtUri(String edtUri) {
        this.edtUri = edtUri;
    }
    public String getDeterminer() {
        return determiner;
    }
    public void setDeterminer(String determiner) {
        this.determiner = determiner;
    }
    
}
