package fi.vm.sade.koulutusinformaatio.domain.exception;

public class ApplicationOptionNotFoundException extends ResourceNotFoundException {
    public ApplicationOptionNotFoundException(String s) {
        super(s);
    }

    public ApplicationOptionNotFoundException(String s, Exception e) {
        super(s, e);
    }

}
