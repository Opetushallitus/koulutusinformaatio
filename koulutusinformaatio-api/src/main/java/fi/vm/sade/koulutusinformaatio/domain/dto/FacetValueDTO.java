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

package fi.vm.sade.koulutusinformaatio.domain.dto;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * 
 * @author Markus
 *
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class FacetValueDTO {
    
    
    private String facetField;
    private String valueId;
    private String valueName;
    private long count;
    private List<FacetValueDTO> childValues;
    
    public FacetValueDTO() {
        
    }
    
    public FacetValueDTO(String facetField, String valueName, long count, String valueId) {
        this.facetField = facetField;
        this.valueName = valueName;
        this.count = count;
        this.valueId = valueId;
    }
    
    public String getValueName() {
        return valueName;
    }
    public void setValueName(String valueName) {
        this.valueName = valueName;
    }
    public long getCount() {
        return count;
    }
    public void setCount(long count) {
        this.count = count;
    }

    public String getFacetField() {
        return facetField;
    }

    public void setFacetField(String facetField) {
        this.facetField = facetField;
    }

    public String getValueId() {
        return valueId;
    }

    public void setValueId(String valueId) {
        this.valueId = valueId;
    }

    public List<FacetValueDTO> getChildValues() {
        return childValues;
    }

    public void setChildValues(List<FacetValueDTO> childValues) {
        this.childValues = childValues;
    }
}
