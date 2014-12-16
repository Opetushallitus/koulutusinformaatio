package fi.vm.sade.koulutusinformaatio.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApplicationSystemParameters {
    
    private ParameterDateRange shownInFacetedSearch;

    public ParameterDateRange getShownInFacetedSearch() {
        return shownInFacetedSearch;
    }

    @JsonProperty("PH_YNH")
    public void setShownInFacetedSearch(ParameterDateRange shownInFacetedSearch) {
        this.shownInFacetedSearch = shownInFacetedSearch;
    }

}
