package fi.vm.sade.koulutusinformaatio.domain.dto;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * 
 * @author Markus
 *
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class AdultVocationalChildLOSDTO extends HigherEducationLOSDTO {
    
    private boolean valmistavaKoulutus;
    private String charge;
    
    private String targetGroup;
    private String personalization;
    
    private List<ContactPersonDTO> preparatoryContactPersons = new ArrayList<ContactPersonDTO>();
    private String degreeOrganizer;
    

    public boolean isValmistavaKoulutus() {
        return valmistavaKoulutus;
    }

    public void setValmistavaKoulutus(boolean valmistavaKoulutus) {
        this.valmistavaKoulutus = valmistavaKoulutus;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String string) {
        this.charge = string;
    }

    public String getTargetGroup() {
        return targetGroup;
    }

    public void setTargetGroup(String targetGroup) {
        this.targetGroup = targetGroup;
    }

    public String getPersonalization() {
        return personalization;
    }

    public void setPersonalization(String personalization) {
        this.personalization = personalization;
    }

    public List<ContactPersonDTO> getPreparatoryContactPersons() {
        return preparatoryContactPersons;
    }

    public void setPreparatoryContactPersons(
            List<ContactPersonDTO> preparatoryContactPersons) {
        this.preparatoryContactPersons = preparatoryContactPersons;
    }

    public void setDegreeOrganizer(String organizerName) {
        this.degreeOrganizer = organizerName;    
    }

    public String getDegreeOrganizer() {
        return degreeOrganizer;
    }

}
