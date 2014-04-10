package fi.vm.sade.koulutusinformaatio.converter;

import java.util.ArrayList;
import java.util.List;

import fi.vm.sade.koulutusinformaatio.domain.ArticleResult;
import fi.vm.sade.koulutusinformaatio.domain.dto.ArticleResultDTO;

public class ArticleResultToDTO {
    
    public static List<ArticleResultDTO> convert(List<ArticleResult> results, int amount) {
        List<ArticleResultDTO> dtos = new ArrayList<ArticleResultDTO>();
        
        amount = results.size() < amount ? results.size() : amount;
        
        for (int i = 0; i < amount ; ++i) {
            dtos.add(convert(results.get(i)));
        }
        
        return dtos;
    }
    
    private static ArticleResultDTO convert(ArticleResult articleRes) {
        ArticleResultDTO dto = new ArticleResultDTO();
        dto.setExcerpt(articleRes.getExcerpt());
        dto.setImageUrl(articleRes.getImageUrl());
        dto.setType(articleRes.getType());
        dto.setUrl(articleRes.getUrl());
        dto.setTitle(articleRes.getTitle());
        return dto;
    }

}
