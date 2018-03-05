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

import java.util.*;

/**
 * Learning opportunity result. Can be a specification (LOS) or an instance (LOI).
 *
 * @author Hannu Lyytikainen
 */
public class LOSearchResult {

    private String id;
    private String name;
    private List<String> lopIds = new ArrayList<String>();
    private List<String> lopNames = new ArrayList<String>();
    private String prerequisite;
    private String prerequisiteCode;
    private String parentId;
    private String losId;
    private boolean asOngoing;
    private List<Date> nextApplicationPeriodStarts = new ArrayList<Date>();
    private String type;
    private String credits;
    private String educationType;
    private String educationDegree;
    private String educationDegreeCode;
    private String homeplace;
    private String childName;
    private List<String> subjects;
    private String responsibleProvider;
    private Map<String, List<Code>> aoToRequiredBaseEducations = new HashMap<>();

    public LOSearchResult(String id, String name, List<String> lopIds, List<String> lopNames,
            String prerequisite, String prerequisiteCode, String parentId,
            String losId, String type, String credits, String educationType,
            String educationDegree, String educationDegreeCode, String homeplace, String childName, List<String> subjects,
            String responsibleProvider, Map<String, List<Code>> aoToRequiredBaseEducations) {
        this.id = id;
        this.name = name;
        this.lopIds = lopIds;
        this.lopNames = lopNames;
        this.prerequisite = prerequisite;
        this.prerequisiteCode = prerequisiteCode;
        this.parentId = parentId;
        this.losId = losId;
        this.type = type;
        this.credits = credits;
        this.educationType = educationType;
        this.educationDegree = educationDegree;
        this.educationDegreeCode = educationDegreeCode;
        this.homeplace = homeplace;
        this.childName = childName;
        this.subjects = subjects;
        this.responsibleProvider = responsibleProvider;
        this.aoToRequiredBaseEducations = aoToRequiredBaseEducations;
    }

    public LOSearchResult() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLopIds() {
        return lopIds;
    }

    public void setLopIds(List<String> lopIds) {
        this.lopIds = lopIds;
    }

    public List<String> getLopNames() {
        return lopNames;
    }

    public void setLopNames(List<String> lopNames) {
        this.lopNames = lopNames;
    }

    public String getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(String prerequisite) {
        this.prerequisite = prerequisite;
    }

    public String getPrerequisiteCode() {
        return prerequisiteCode;
    }

    public void setPrerequisiteCode(String prerequisiteCode) {
        this.prerequisiteCode = prerequisiteCode;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getLosId() {
        return losId;
    }

    public void setLosId(String losId) {
        this.losId = losId;
    }

    public boolean isAsOngoing() {
        return asOngoing;
    }

    public void setAsOngoing(boolean asOngoing) {
        this.asOngoing = asOngoing;
    }

    public List<Date> getNextApplicationPeriodStarts() {
        return nextApplicationPeriodStarts;
    }

    public void setNextApplicationPeriodStarts(List<Date> nextApplicationPeriodStarts) {
        this.nextApplicationPeriodStarts = nextApplicationPeriodStarts;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public String getEducationType() {
        return educationType;
    }

    public void setEducationType(String educationType) {
        this.educationType = educationType;
    }

    public String getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(String educationDegree) {
        this.educationDegree = educationDegree;
    }

    public String getEducationDegreeCode() {
        return educationDegreeCode;
    }

    public void setEducationDegreeCode(String educationDegreeCode) {
        this.educationDegreeCode = educationDegreeCode;
    }

    public String getHomeplace() {
        return homeplace;
    }

    public void setHomeplace(String homeplace) {
        this.homeplace = homeplace;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public String getResponsibleProvider() {
        return responsibleProvider;
    }

    public Map<String, List<Code>> getAoToRequiredBaseEducations() {
        return aoToRequiredBaseEducations;
    }

    public void setAoToRequiredBaseEducations(Map<String, List<Code>> aoToRequiredBaseEducations) {
        this.aoToRequiredBaseEducations = aoToRequiredBaseEducations;
    }
}
