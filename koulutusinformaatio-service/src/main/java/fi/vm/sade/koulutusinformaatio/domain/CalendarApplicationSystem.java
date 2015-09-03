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
 */
public class CalendarApplicationSystem {
    
    private String id;
    private I18nText name;
    private List<ApplicationPeriod> applicationPeriods = new ArrayList<ApplicationPeriod>();
    private String targetGroupCode;
    private boolean shownInCalendar;
    private boolean VarsinainenHaku;
    
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
    public List<ApplicationPeriod> getApplicationPeriods() {
        return applicationPeriods;
    }
    public void setApplicationPeriods(List<ApplicationPeriod> applicationPeriods) {
        this.applicationPeriods = applicationPeriods;
    }
    public String getTargetGroupCode() {
        return targetGroupCode;
    }
    public void setTargetGroupCode(String targetGroupCode) {
        this.targetGroupCode = targetGroupCode;
    }
    public void setShownInCalendar(boolean shownInCalendar) {
        this.shownInCalendar = shownInCalendar;
        
    }
    public boolean isShownInCalendar() {
        return shownInCalendar;
    }
    public boolean isVarsinainenHaku() {
        return VarsinainenHaku;
    }
    public void setVarsinainenHaku(boolean varsinainenHaku) {
        VarsinainenHaku = varsinainenHaku;
    }

}
