package fi.vm.sade.koulutusinformaatio.domain.exception;

/**
 * @author Hannu Lyytikainen
 */
public class IndexingException extends KIException {

    public IndexingException(String s) {
        super(s);
    }
    public IndexingException(String s, Exception e) {
        super(s, e);
    }
}
