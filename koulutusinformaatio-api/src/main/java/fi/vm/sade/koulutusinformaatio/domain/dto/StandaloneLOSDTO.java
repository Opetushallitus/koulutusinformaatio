package fi.vm.sade.koulutusinformaatio.domain.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * 
 * @author Markus
 *
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class StandaloneLOSDTO extends LOSDTO {

}
