package fi.vm.sade.koulutusinformaatio.domain.exception;

public class KICommitException extends KIException {
    
    /**
     * 
     */
    private static final long serialVersionUID = 5605246984649457289L;

    public KICommitException(String s) {
        super(s);
    }
    
    public KICommitException(Exception ex) {
        super(ex.getMessage());
    }

   

}
