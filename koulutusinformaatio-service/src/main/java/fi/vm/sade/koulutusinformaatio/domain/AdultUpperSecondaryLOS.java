/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.koulutusinformaatio.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Markus
 *
 */
public class AdultUpperSecondaryLOS extends StandaloneLOS {

    private I18nText subjectsAndCourses;
    private List<LanguageSelection> languageSelection;
    private List<I18nText> diplomas = new ArrayList<I18nText>();   


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
