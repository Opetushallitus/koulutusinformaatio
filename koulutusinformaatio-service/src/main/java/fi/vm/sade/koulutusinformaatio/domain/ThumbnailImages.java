package fi.vm.sade.koulutusinformaatio.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ThumbnailImages {
    @JsonProperty("oph-medium")
    private ThumbnailImage thumbnail;

    public ThumbnailImage getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(ThumbnailImage thumbnail) {
        this.thumbnail = thumbnail;
    }
}
