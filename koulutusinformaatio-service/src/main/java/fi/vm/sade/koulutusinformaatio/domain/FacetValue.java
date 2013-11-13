package fi.vm.sade.koulutusinformaatio.domain;

public class FacetValue {

    private String facetField;
    private String valueId;
    private String valueName;
    private long count;
    
    public FacetValue(String facetField, String valueName, long count, String valueId) {
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
    
}
