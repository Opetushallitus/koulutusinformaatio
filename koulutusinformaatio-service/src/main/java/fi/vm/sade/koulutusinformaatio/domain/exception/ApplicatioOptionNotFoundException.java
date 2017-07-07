package fi.vm.sade.koulutusinformaatio.domain.exception;

public class ApplicatioOptionNotFoundException extends ResourceNotFoundException {
    public ApplicatioOptionNotFoundException(String s) {
        super(s);
    }

    public ApplicatioOptionNotFoundException(String s, Exception e) {
        super(s, e);
    }

}
