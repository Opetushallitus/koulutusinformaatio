package fi.vm.sade.koulutusinformaatio.domain;

public class Credits {

    private String creditValue;
    private String creditUnit;
    
    public Credits() {} 
    
    public Credits(String creditValue, String creditUnit) {
        this.creditValue = creditValue;
        this.creditUnit = creditUnit;
    }
    
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
