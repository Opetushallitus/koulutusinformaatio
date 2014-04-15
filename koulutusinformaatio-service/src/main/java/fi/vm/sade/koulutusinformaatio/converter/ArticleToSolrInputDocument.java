package fi.vm.sade.koulutusinformaatio.converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LearningOpportunity;
import fi.vm.sade.koulutusinformaatio.domain.Article;
import fi.vm.sade.koulutusinformaatio.domain.ArticleTag;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.service.impl.ArticleServiceImpl;

public class ArticleToSolrInputDocument implements Converter<Article, List<SolrInputDocument>> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleToSolrInputDocument.class);

    @Override
    public List<SolrInputDocument> convert(Article article) {
        
        List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
        docs.add(convertArticle(article));
        return docs;
    }

    private SolrInputDocument convertArticle(Article article) {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(LearningOpportunity.TYPE, SolrUtil.SolrConstants.TYPE_ARTICLE);
        doc.addField(LearningOpportunity.ID, article.getId());
        
        String lang = extractLanguage(article);
        doc.addField(LearningOpportunity.TEACHING_LANGUAGE, lang.toUpperCase());
        doc.addField(LearningOpportunity.ARTICLE_URL, article.getUrl());
        
        if (article.getAttachments() != null && !article.getAttachments().isEmpty()) {
            doc.addField(LearningOpportunity.ARTICLE_PICTURE, article.getAttachments().get(0).getUrl());
        }
        
        doc.addField(LearningOpportunity.ARTICLE_EXCERPT, article.getExcerpt());
        
        doc.addField(LearningOpportunity.NAME, article.getTitle());
        doc.addField(LearningOpportunity.NAME_SORT, article.getTitle());
        doc.setField(LearningOpportunity.ARTICLE_LANG, lang);
        
        for (String curCode : article.getEducationTypeCodes()) {
            LOGGER.debug(curCode);
            doc.addField(LearningOpportunity.EDUCATION_TYPE, curCode);
        }
        
        for (String curCode : article.getEducationCodes()) {
            doc.addField(LearningOpportunity.ARTICLE_EDUCATION_CODE, curCode);
        }
        
        for (ArticleTag curTag : article.getTags()) {
            doc.addField(LearningOpportunity.ARTICLE_CONTENT_TYPE, curTag.getTitle());
        }
        
        indexLangFields(article, doc, lang);
        
        
        return doc;
    }
    
    private void indexLangFields(Article article, SolrInputDocument doc,
            String lang) {
        
        if (lang.equals("fi")) {
            //doc.setField(LearningOpportunity.NAME_FI, article.getTitle());
            doc.addField(LearningOpportunity.ARTICLE_NAME_INDEX_FI, article.getTitle());
            doc.addField(String.format("%s_fi", LearningOpportunity.FREE_AUTO), article.getTitle());
            doc.addField("textBoost_fi", article.getTitle());
            doc.addField("textBoost_fi_whole", article.getTitle());
            
        } else if (lang.equals("sv")) {
            //doc.setField(LearningOpportunity.NAME_SV, article.getTitle());
            doc.addField(LearningOpportunity.ARTICLE_NAME_INDEX_SV, article.getTitle());
            doc.addField(String.format("%s_sv", LearningOpportunity.FREE_AUTO), article.getTitle());
            doc.addField("textBoost_sv", article.getTitle());
            doc.addField("textBoost_sv_whole", article.getTitle());
        } else if (lang.equals("en")) {
            //doc.setField(LearningOpportunity.NAME_EN, article.getTitle());
            doc.addField(LearningOpportunity.ARTICLE_NAME_INDEX_EN, article.getTitle());
            doc.addField(String.format("%s_en", LearningOpportunity.FREE_AUTO), article.getTitle());
            doc.addField("textBoost_en", article.getTitle());
            doc.addField("textBoost_en_whole", article.getTitle());
        } 
        
        if (lang.equals("fi")) {
            doc.addField(LearningOpportunity.CONTENT_FI, article.getContent());
            doc.addField(LearningOpportunity.CONTENT_FI, article.getExcerpt());
        } else if (lang.equals("sv")) {
            doc.addField(LearningOpportunity.CONTENT_SV, article.getContent());
            doc.addField(LearningOpportunity.CONTENT_SV, article.getExcerpt());
        } else if (lang.equals("en")) {
            doc.addField(LearningOpportunity.CONTENT_EN, article.getContent());
            doc.addField(LearningOpportunity.CONTENT_EN, article.getExcerpt());
        } 

        
    }

    private String extractLanguage(Article article) {
        return article.getUrl().split("\\/")[4];
    }

    
    
}
