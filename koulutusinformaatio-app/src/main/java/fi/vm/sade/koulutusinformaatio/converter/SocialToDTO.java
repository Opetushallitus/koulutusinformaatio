package fi.vm.sade.koulutusinformaatio.converter;

import java.util.List;

import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.domain.Social;
import fi.vm.sade.koulutusinformaatio.domain.dto.SocialDTO;

public class SocialToDTO {
    public static List<SocialDTO> convert(List<Social> social) {
        List<SocialDTO> converted = Lists.newArrayList();

        if (social != null && !social.isEmpty()) {
            for (Social socialItem : social) {
                SocialDTO socialDTO = new SocialDTO();
                socialDTO.setName(socialItem.getName());
                socialDTO.setUrl(socialItem.getUrl());
                
                converted.add(socialDTO);
            }
        }
        
        return converted;
    }
}
