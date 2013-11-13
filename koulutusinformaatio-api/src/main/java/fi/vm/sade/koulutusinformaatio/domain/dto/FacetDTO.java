package fi.vm.sade.koulutusinformaatio.domain.dto;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class FacetDTO {
    
List<FacetValueDTO> facetValues;
    
    public List<FacetValueDTO> getFacetValues() {
        return facetValues;
    }

    public void setFacetValues(List<FacetValueDTO> facetValues) {
        this.facetValues = facetValues;
    }
    
}
