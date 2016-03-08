package fi.vm.sade.koulutusinformaatio.domain.exception;

/**
 * @author Hannu Lyytikainen
 */
public class OrganisaatioException extends KIException {

    public OrganisaatioException(String s) {
        super(s);
    }
    public OrganisaatioException(String s, Exception e) {
        super(s, e);
    }
}
