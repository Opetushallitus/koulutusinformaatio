package fi.vm.sade.koulutusinformaatio.domain.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class CreditsDTO {
    
    private String creditValue;
    private String creditUnit;
    
    public String getCreditValue() {
        return creditValue;
    }
    public void setCreditValue(String creditValue) {
        this.creditValue = creditValue;
    }
    public String getCreditUnit() {
        return creditUnit;
    }
    public void setCreditUnit(String creditUnit) {
        this.creditUnit = creditUnit;
    }

}
