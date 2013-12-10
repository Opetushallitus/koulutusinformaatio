package fi.vm.sade.koulutusinformaatio.domain.dto;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * 
 * @author Markus
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class SuggestedTermsResultDTO {
    
    private List<String> loNames;
    private List<String> keywords;
    
    public List<String> getLoNames() {
        return loNames;
    }
    public void setLoNames(List<String> loNames) {
        this.loNames = loNames;
    }
    public List<String> getKeywords() {
        return keywords;
    }
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
    
}
