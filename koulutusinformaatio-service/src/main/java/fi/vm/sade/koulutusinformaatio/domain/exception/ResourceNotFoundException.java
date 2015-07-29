package fi.vm.sade.koulutusinformaatio.domain.exception;

/**
 * @author Hannu Lyytikainen
 */
public class ResourceNotFoundException extends KIException {

    public ResourceNotFoundException(Exception e) {
        super(e);
    }

    public ResourceNotFoundException(String s) {
        super(s);
    }
}
