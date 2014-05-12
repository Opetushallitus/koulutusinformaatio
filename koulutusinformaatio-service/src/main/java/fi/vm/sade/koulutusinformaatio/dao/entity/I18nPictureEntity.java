package fi.vm.sade.koulutusinformaatio.dao.entity;

import java.util.Map;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Reference;

@Embedded
public class I18nPictureEntity {
    
    @Reference
    private Map<String, PictureEntity> pictureTranslations;
    
    public I18nPictureEntity() {}

    public Map<String, PictureEntity> getPictureTranslations() {
        return pictureTranslations;
    }

    public void setPictureTranslations(Map<String, PictureEntity> pictureTranslations) {
        this.pictureTranslations = pictureTranslations;
    }


}
