package fi.vm.sade.koulutusinformaatio.domain;

public class AdultUpperSecondaryLOS extends StandaloneLOS {

    private I18nText targetGroup;
    private I18nText subjectsAndCourses;
    

    public I18nText getTargetGroup() {
        return targetGroup;
    }

    public void setTargetGroup(I18nText targetGroup) {
        this.targetGroup = targetGroup;
    }

    public I18nText getSubjectsAndCourses() {
        return subjectsAndCourses;
    }

    public void setSubjectsAndCourses(I18nText subjectsAndCourses) {
        this.subjectsAndCourses = subjectsAndCourses;
    }
    
    
}
