package fi.vm.sade.koulutusinformaatio.domain;

/**
 * Child learning opportunity instance.
 *
 * @author Hannu Lyytikainen
 */
public class ChildLOI {

    private String id;
    private I18nText name;
    private ApplicationOption applicationOption;
    private String applicationSystemId;

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

    public ApplicationOption getApplicationOption() {
        return applicationOption;
    }

    public void setApplicationOption(ApplicationOption applicationOption) {
        this.applicationOption = applicationOption;
    }

    public String getApplicationSystemId() {
        return applicationSystemId;
    }

    public void setApplicationSystemId(String applicationSystemId) {
        this.applicationSystemId = applicationSystemId;
    }
}
