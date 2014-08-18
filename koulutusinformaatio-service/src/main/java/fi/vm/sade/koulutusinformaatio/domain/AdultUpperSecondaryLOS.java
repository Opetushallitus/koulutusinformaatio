package fi.vm.sade.koulutusinformaatio.domain;

import java.util.ArrayList;
import java.util.List;

public class AdultUpperSecondaryLOS extends StandaloneLOS {

    private I18nText targetGroup;
    private I18nText subjectsAndCourses;
    private List<LanguageSelection> languageSelection;
    private List<I18nText> diplomas = new ArrayList<I18nText>();   

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
    
    
}
