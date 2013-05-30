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

package fi.vm.sade.koulutusinformaatio.domain.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

/**
 * @author Mikko Majapuro
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class BasketApplicationOptionDTO {

    private String id;
    private String name;
    private String educationDegree;
    private String providerName;
    private String providerId;
    private String providerLocation;
    private String qualification;
    private String baseEducationRequirement;
    private ParentLOSRefDTO parent;
    private List<ChildLORefDTO> children;

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

    public String getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(String educationDegree) {
        this.educationDegree = educationDegree;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProviderLocation() {
        return providerLocation;
    }

    public void setProviderLocation(String providerLocation) {
        this.providerLocation = providerLocation;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getBaseEducationRequirement() {
        return baseEducationRequirement;
    }

    public void setBaseEducationRequirement(String baseEducationRequirement) {
        this.baseEducationRequirement = baseEducationRequirement;
    }

    public ParentLOSRefDTO getParent() {
        return parent;
    }

    public void setParent(ParentLOSRefDTO parent) {
        this.parent = parent;
    }

    public List<ChildLORefDTO> getChildren() {
        return children;
    }

    public void setChildren(List<ChildLORefDTO> children) {
        this.children = children;
    }
}
