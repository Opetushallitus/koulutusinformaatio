package fi.vm.sade.koulutusinformaatio.domain;

public class ValmaLOSRef {

    private String id;
    private I18nText name;
    private String losType;

    public ValmaLOSRef() {}

    public ValmaLOSRef(String id, I18nText name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public I18nText getName() {
        return name;
    }

    public void setName(I18nText name) {
        this.name = name;
    }

    public String getLosType() {
        return losType;
    }

    public void setLosType(String losType) {
        this.losType = losType;
    }
}
