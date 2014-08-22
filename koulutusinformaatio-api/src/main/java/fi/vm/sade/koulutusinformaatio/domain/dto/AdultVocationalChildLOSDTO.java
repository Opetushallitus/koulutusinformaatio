package fi.vm.sade.koulutusinformaatio.domain.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * 
 * @author Markus
 *
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class AdultVocationalChildLOSDTO extends HigherEducationLOSDTO {
    
    private boolean valmistavaKoulutus;

    public boolean isValmistavaKoulutus() {
        return valmistavaKoulutus;
    }

    public void setValmistavaKoulutus(boolean valmistavaKoulutus) {
        this.valmistavaKoulutus = valmistavaKoulutus;
    }

}
