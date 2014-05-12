package fi.vm.sade.koulutusinformaatio.domain;

import java.util.HashMap;
import java.util.Map;

public class I18nPicture {
    
    private Map<String, Picture> pictureTranslations;
    
    public I18nPicture() {
        this.setPictureTranslations(new HashMap<String, Picture>());
    }

    public Map<String, Picture> getPictureTranslations() {
        return pictureTranslations;
    }

    public void setPictureTranslations(Map<String, Picture> pictureTranslations) {
        this.pictureTranslations = pictureTranslations;
    }

}
