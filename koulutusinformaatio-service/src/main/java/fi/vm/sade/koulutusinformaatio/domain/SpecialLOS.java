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

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Special education learning opportunity specification.
 *
 * @author Hannu Lyytikainen
 */
public class SpecialLOS extends BasicLOS<ChildLOI> {

    private static final Logger LOG = LoggerFactory.getLogger(SpecialLOS.class);

    private List<ChildLOI> lois;

    private I18nText qualification;
    private I18nText educationDomain;
    private ParentLOSRef parent;
    private String educationTypeUri;
    private I18nText degreeTitle; // tutkintonimike
    private List<I18nText> degreeTitles; // tutkintonimikkeet

    private List<String> aoIds = new ArrayList<String>();

    private I18nText subName;

    public List<ChildLOI> getLois() {
        return lois;
    }

    public void setLois(List<ChildLOI> lois) {
        this.lois = lois;
    }

    public I18nText getEducationDomain() {
        return educationDomain;
    }

    public void setEducationDomain(I18nText educationDomain) {
        this.educationDomain = educationDomain;
    }

    public ParentLOSRef getParent() {
        return parent;
    }

    public void setParent(ParentLOSRef parent) {
        this.parent = parent;
    }

    public I18nText getQualification() {
        return qualification;
    }

    public void setQualification(I18nText qualification) {
        this.qualification = qualification;
    }

    public String getEducationTypeUri() {
        return educationTypeUri;
    }

    public void setEducationTypeUri(String educationTypeUri) {
        this.educationTypeUri = educationTypeUri;
    }

    public List<String> getAoIds() {
        return aoIds;
    }

    public void setAoIds(List<String> aoIds) {
        this.aoIds = aoIds;
    }

    public void setSubName(I18nText name) {
        this.subName = name;

    }

    public I18nText getSubName() {
        return subName;
    }

    public I18nText getDegreeTitle() {
        return degreeTitle;
    }

    public void setDegreeTitle(I18nText degreeTitle) {
        this.degreeTitle = degreeTitle;
    }

    public List<I18nText> getDegreeTitles() {
        return degreeTitles;
    }

    public void setDegreeTitles(List<I18nText> degreeTitles) {
        this.degreeTitles = degreeTitles;
    }

    public ChildLOI getLatestLoi() {
        ChildLOI latest = null;
        try {
            Date latestDate = null;
            for (ChildLOI loi : getLois()) {
                if (latestDate == null || getComparisonDate(loi).after(latestDate)) {
                    latestDate = getComparisonDate(loi);
                    latest = loi;
                }
            }
        } catch (Exception e) {
            LOG.debug("Calculating the latest child loi date failed.");
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
