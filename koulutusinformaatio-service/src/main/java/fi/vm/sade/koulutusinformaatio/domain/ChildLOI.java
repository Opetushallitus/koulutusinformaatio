package fi.vm.sade.koulutusinformaatio.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.code.morphia.annotations.Embedded;

import fi.vm.sade.koulutusinformaatio.dao.entity.I18nTextEntity;

/**
 * Child learning opportunity instance.
 *
 * @author Hannu Lyytikainen
 */
public class ChildLOI {

    private String id;
    private I18nText name;

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

}
