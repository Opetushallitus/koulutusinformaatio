package fi.vm.sade.koulutusinformaatio.domain;

import java.util.List;

public class Facet {

    List<FacetValue> facetValues;
    
    public List<FacetValue> getFacetValues() {
        return facetValues;
    }

    public void setFacetValues(List<FacetValue> facetValues) {
        this.facetValues = facetValues;
    }
    
    
}
