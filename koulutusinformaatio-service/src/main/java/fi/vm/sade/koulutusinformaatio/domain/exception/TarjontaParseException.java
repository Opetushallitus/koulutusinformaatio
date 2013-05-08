package fi.vm.sade.koulutusinformaatio.domain.exception;

/**
 * Indicates that an error has occurred while parsing
 * data retrieved from the Tarjonta service.
 *
 * @author Hannu Lyytikainen
 */
public class TarjontaParseException extends KIException {
    public TarjontaParseException(String s) {
        super(s);
    }
}
