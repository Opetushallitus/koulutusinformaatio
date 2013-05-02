package fi.vm.sade.koulutusinformaatio.domain;

public class Credits {

    private I18nText creditValue;
    private I18nText creditUnit;
    
    public Credits() {} 
    
    public Credits(I18nText creditValue, I18nText creditUnit) {
        this.creditValue = creditValue;
        this.creditUnit = creditUnit;
    }

    public I18nText getCreditValue() {
        return creditValue;
    }

    public void setCreditValue(I18nText creditValue) {
        this.creditValue = creditValue;
    }

    public I18nText getCreditUnit() {
        return creditUnit;
    }

    public void setCreditUnit(I18nText creditUnit) {
        this.creditUnit = creditUnit;
    }
}
