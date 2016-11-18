package fi.vm.sade.koulutusinformaatio.domain.exception;

public class KISolrException extends KIException {
    
    /**
     * 
     */
    private static final long serialVersionUID = 5605246984649457289L;

    public KISolrException(String s) {
        super(s);
    }
    public KISolrException(String s, Exception e) {
        super(s, e);
    }

    public KISolrException(Exception ex) {
        super(ex.getMessage(), ex);
    }

   

}
