/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
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

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parent level learning opportunity specification.
 *
 * @author Hannu Lyytikainen
 */
public class ParentLOS extends BasicLOS<ParentLOI> {

    private static final Logger LOG = LoggerFactory.getLogger(ParentLOS.class);
    
    private List<ParentLOI> lois;
    private List<ChildLOS> children;

    private I18nText educationDomain;           // koulutusala, Sosiaali-, terveys- ja liikunta-ala
    private I18nText stydyDomain;               // opintoala, Hammaslääketiede ja muu hammashuolto

    private List<Code> teachingLanguages;
    
    private boolean kotitalousopetus;

    public List<ParentLOI> getLois() {
        return lois;
    }

    public void setLois(List<ParentLOI> lois) {
        this.lois = lois;
    }

    public List<ChildLOS> getChildren() {
        return children;
    }

    public void setChildren(List<ChildLOS> children) {
        this.children = children;
    }

    public I18nText getEducationDomain() {
        return educationDomain;
    }

    public void setEducationDomain(I18nText educationDomain) {
        this.educationDomain = educationDomain;
    }

    public I18nText getStydyDomain() {
        return stydyDomain;
    }

    public void setStydyDomain(I18nText stydyDomain) {
        this.stydyDomain = stydyDomain;
    }

    public List<Code> getTeachingLanguages() {
        return teachingLanguages;
    }

    public void setTeachingLanguages(List<Code> teachingLanguages) {
        this.teachingLanguages = teachingLanguages;
    }

    public boolean isKotitalousopetus() {
        return kotitalousopetus;
    }

    public void setKotitalousopetus(boolean kotitalousopetus) {
        this.kotitalousopetus = kotitalousopetus;
    }

    public ChildLOI getLatestLoi() {
        ChildLOI latest = null;
        try {
            Date latestDate = null;
            for (ChildLOS c : getChildren()) {
                List<ChildLOI> lois = c.getLois();
                for (ChildLOI loi : lois) {
                    if (latestDate == null || getComparisonDate(loi).after(latestDate)) {
                        latestDate = getComparisonDate(loi);
                        latest = loi;
                    }
                }
            }
        } catch (Exception e) {
            LOG.debug("Calculating the latest child loi date failed. ", e);
        }
        return latest;
    }

    private static Date getComparisonDate(ChildLOI loi) {
        if (loi.getStartDate() != null) {
            return loi.getStartDate();
        }
        int month = 0;
        if (loi.getStartSeason().getTranslations().get("fi").equals("Syksy")) {
            month = 7;
        }
        GregorianCalendar cal = new GregorianCalendar(loi.getStartYear(), month, 0);
        return cal.getTime();
    } 

}
