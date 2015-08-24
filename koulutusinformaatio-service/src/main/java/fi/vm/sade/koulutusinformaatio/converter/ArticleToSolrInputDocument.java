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

public class ArticleToSolrInputDocument implements Converter<Article, List<SolrInputDocument>> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleToSolrInputDocument.class);

    @Override
    public List<SolrInputDocument> convert(Article article) {
        
        List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
        docs.add(convertArticle(article));
        return docs;
    }

    private SolrInputDocument convertArticle(Article article) {
        String fixedTitle = article.getTitle();
        if (article.getTitle() != null) {
            fixedTitle = SolrUtil.fixString(article.getTitle());
        }
        
        SolrInputDocument doc = new SolrInputDocument();
        
        doc.addField(LearningOpportunity.TYPE, SolrUtil.SolrConstants.TYPE_ARTICLE);
        doc.addField(LearningOpportunity.ID, article.getId());
        
        String lang = article.getLanguageCode();
        doc.addField(LearningOpportunity.TEACHING_LANGUAGE, lang.toUpperCase());
        doc.addField(LearningOpportunity.ARTICLE_URL, article.getUrl());
        
        if (article.getThumbnailImages() != null && article.getThumbnailImages().getThumbnail() != null) {
            doc.addField(LearningOpportunity.ARTICLE_PICTURE, article.getThumbnailImages().getThumbnail().getUrl());
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
        
        indexLangFields(article, doc, lang, fixedTitle);
        
        
        return doc;
    }
    
    private void indexLangFields(Article article, SolrInputDocument doc,
            String lang, String fixedTitle) {
        
        if (lang.equals("fi")) {
            //doc.setField(LearningOpportunity.NAME_FI, article.getTitle());
            doc.addField(LearningOpportunity.ARTICLE_NAME_INDEX_FI, fixedTitle);
            doc.addField(String.format("%s_fi", LearningOpportunity.FREE_AUTO), article.getTitle());
            doc.addField("textBoost_fi", fixedTitle);
            doc.addField("textBoost_fi_whole", fixedTitle);
            
        } else if (lang.equals("sv")) {
            //doc.setField(LearningOpportunity.NAME_SV, article.getTitle());
            doc.addField(LearningOpportunity.ARTICLE_NAME_INDEX_SV, fixedTitle);
            doc.addField(String.format("%s_sv", LearningOpportunity.FREE_AUTO), article.getTitle());
            doc.addField("textBoost_sv", fixedTitle);
            doc.addField("textBoost_sv_whole", fixedTitle);
        } else if (lang.equals("en")) {
            //doc.setField(LearningOpportunity.NAME_EN, article.getTitle());
            doc.addField(LearningOpportunity.ARTICLE_NAME_INDEX_EN, fixedTitle);
            doc.addField(String.format("%s_en", LearningOpportunity.FREE_AUTO), article.getTitle());
            doc.addField("textBoost_en", fixedTitle);
            doc.addField("textBoost_en_whole", fixedTitle);
        } 
        
        if (lang.equals("fi")) {
            doc.addField(LearningOpportunity.ARTICLE_CONTENT_FI, article.getContent());
            doc.addField(LearningOpportunity.ARTICLE_CONTENT_FI, article.getExcerpt());
        } else if (lang.equals("sv")) {
            doc.addField(LearningOpportunity.ARTICLE_CONTENT_SV, article.getContent());
            doc.addField(LearningOpportunity.ARTICLE_CONTENT_SV, article.getExcerpt());
        } else if (lang.equals("en")) {
            doc.addField(LearningOpportunity.ARTICLE_CONTENT_EN, article.getContent());
            doc.addField(LearningOpportunity.ARTICLE_CONTENT_EN, article.getExcerpt());
        } 

        
    }

    
}
