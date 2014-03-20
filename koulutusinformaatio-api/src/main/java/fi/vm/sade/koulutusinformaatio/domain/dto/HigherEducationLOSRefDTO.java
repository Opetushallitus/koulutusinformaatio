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

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * 
 * @author Markus
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class HigherEducationLOSRefDTO implements Comparable<HigherEducationLOSRefDTO> {
    
    private String id;
    private List<String> asIds;
    private String name;
    private String qualification;
    private CodeDTO prerequisite;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public List<String> getAsIds() {
        return asIds;
    }
    public void setAsIds(List<String> asIds) {
        this.asIds = asIds;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getQualification() {
        return qualification;
    }
    public void setQualification(String qualification) {
        this.qualification = qualification;
    }
    public CodeDTO getPrerequisite() {
        return prerequisite;
    }
    public void setPrerequisite(CodeDTO prerequisite) {
        this.prerequisite = prerequisite;
    }
    
    @Override
    public int compareTo(HigherEducationLOSRefDTO higherEdLOSRefDTO) {
        if (this == higherEdLOSRefDTO) {
            return 0;
        }
        return this.name.compareTo(higherEdLOSRefDTO.name);
    }

}
