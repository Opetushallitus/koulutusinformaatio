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

/**
 * Learning opportunity result. Can be a specification (LOS) or an instance (LOI).
 *
 * @author Hannu Lyytikainen
 */
public class LOSearchResult {

    private String id;
    private String name;
    private String lopId;
    private String lopName;
    private String prerequisite;
    private String parentId;
    private String losId;
    private boolean asOngoing;
    private DateRange nextAs;


    public LOSearchResult(String id, String name, String lopId, String lopName,
                          String prerequisite, String parentId, String losId) {
        this.id = id;
        this.name = name;
        this.lopId = lopId;
        this.lopName = lopName;
        this.prerequisite = prerequisite;
        this.parentId = parentId;
        this.losId = losId;
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

    public String getLopId() {
        return lopId;
    }

    public void setLopId(String lopId) {
        this.lopId = lopId;
    }

    public String getLopName() {
        return lopName;
    }

    public void setLopName(String lopName) {
        this.lopName = lopName;
    }

    public String getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(String prerequisite) {
        this.prerequisite = prerequisite;
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

    public DateRange getNextAs() {
        return nextAs;
    }

    public void setNextAs(DateRange nextAs) {
        this.nextAs = nextAs;
    }
}
