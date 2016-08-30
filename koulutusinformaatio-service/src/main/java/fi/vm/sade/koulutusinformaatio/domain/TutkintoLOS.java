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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TutkintoLOS extends LOS {

    private static final Logger LOG = LoggerFactory.getLogger(TutkintoLOS.class);

    // InstantiatedLOS
    private String id;
    private I18nText name;
    private I18nText shortTitle;
    private I18nText goals;
    private String type;
    private List<Code> topics;
    private List<Code> themes;

    // BasicLOS
    private List<Provider> additionalProviders = new ArrayList<Provider>(); // muut tarjoajat
    private I18nText structure; // rakenne
    private I18nText accessToFurtherStudies; // jatko-opintomahdollisuudet
    private String educationDegree; // koulutusaste, 32
    private String creditValue; // laajuus arvo, 120
    private I18nText creditUnit; // laajuus yksikkö op

    private Code educationCode;

    private List<KoulutusLOS> childEducations = new ArrayList<KoulutusLOS>();

    private I18nText educationDomain;
    private I18nText stydyDomain;

    private Set<Code> teachingLanguages = new HashSet<Code>();

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

    public Set<Code> getTeachingLanguages() {
        return teachingLanguages;
    }

    public void setTeachingLanguages(Set<Code> teachingLanguages) {
        this.teachingLanguages = teachingLanguages;
    }

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

    public I18nText getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(I18nText shortTitle) {
        this.shortTitle = shortTitle;
    }

    public I18nText getGoals() {
        return goals;
    }

    public void setGoals(I18nText goals) {
        this.goals = goals;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Code> getTopics() {
        return topics;
    }

    public void setTopics(List<Code> topics) {
        this.topics = topics;
    }

    public List<Code> getThemes() {
        return themes;
    }

    public void setThemes(List<Code> themes) {
        this.themes = themes;
    }

    public List<Provider> getAdditionalProviders() {
        return additionalProviders;
    }

    public void setAdditionalProviders(List<Provider> additionalProviders) {
        this.additionalProviders = additionalProviders;
    }

    public I18nText getStructure() {
        return structure;
    }

    public void setStructure(I18nText structure) {
        this.structure = structure;
    }

    public I18nText getAccessToFurtherStudies() {
        return accessToFurtherStudies;
    }

    public void setAccessToFurtherStudies(I18nText accessToFurtherStudies) {
        this.accessToFurtherStudies = accessToFurtherStudies;
    }

    public String getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(String educationDegree) {
        this.educationDegree = educationDegree;
    }

    public String getCreditValue() {
        return creditValue;
    }

    public void setCreditValue(String creditValue) {
        this.creditValue = creditValue;
    }

    public I18nText getCreditUnit() {
        return creditUnit;
    }

    public void setCreditUnit(I18nText creditUnit) {
        this.creditUnit = creditUnit;
    }

    public KoulutusLOS getLatestLoi() {
        KoulutusLOS latest = null;
        try {
            Date latestDate = null;
            for (KoulutusLOS koulutus : getChildEducations()) {
                if (latestDate == null || getComparisonDate(koulutus).after(latestDate)) {
                    latestDate = getComparisonDate(koulutus);
                    latest = koulutus;
                    }
                }
        } catch (Exception e) {
            LOG.debug("Calculating the latest child loi date failed.");
        }
        return latest;
    }

    private static Date getComparisonDate(KoulutusLOS koulutus) {
        if (koulutus.getStartDate() != null) {
            return koulutus.getStartDate();
        }
        int month = 0;
        if (koulutus.getStartSeason().getTranslations().get("fi").equals("Syksy")) {
            month = 7;
        }
        GregorianCalendar cal = new GregorianCalendar(koulutus.getStartYear(), month, 0);
        return cal.getTime();
    }

    public List<KoulutusLOS> getChildEducations() {
        return childEducations;
    }

    public void setChildEducations(List<KoulutusLOS> childEducations) {
        this.childEducations = childEducations;
    }

    public Code getEducationCode() {
        return educationCode;
    }

    public void setEducationCode(Code educationCode) {
        this.educationCode = educationCode;
    }

}
