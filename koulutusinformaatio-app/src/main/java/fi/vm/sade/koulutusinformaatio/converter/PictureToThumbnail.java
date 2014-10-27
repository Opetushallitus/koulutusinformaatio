package fi.vm.sade.koulutusinformaatio.converter;

import fi.vm.sade.koulutusinformaatio.domain.Picture;
import fi.vm.sade.koulutusinformaatio.domain.dto.PictureDTO;

public final class PictureToThumbnail {
    
    public static PictureDTO convert(Picture pict) {
        
        PictureDTO thumbnail = new PictureDTO();
        thumbnail.setId(pict.getId());
        thumbnail.setPictureEncoded(pict.getThumbnailEncoded());
        return thumbnail;
        
    }

}
