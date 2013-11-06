package fi.vm.sade.koulutusinformaatio.dao.entity;

import org.mongodb.morphia.annotations.Embedded;

/**
 * @author Hannu Lyytikainen
 */
@Embedded
public class EmphasizedSubjectEntity {

    private String value;
    @Embedded
    private I18nTextEntity subject;

    public EmphasizedSubjectEntity() {
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public I18nTextEntity getSubject() {
        return subject;
    }

    public void setSubject(I18nTextEntity subject) {
        this.subject = subject;
    }
}