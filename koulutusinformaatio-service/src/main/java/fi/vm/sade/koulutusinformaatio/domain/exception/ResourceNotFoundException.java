package fi.vm.sade.koulutusinformaatio.domain.exception;

/**
 * @author Hannu Lyytikainen
 */
public class ResourceNotFoundException extends KIException {

    public ResourceNotFoundException(String s) {
        super(s);
    }
    public ResourceNotFoundException(String s, Exception e) {
        super(s, e);
    }
}
