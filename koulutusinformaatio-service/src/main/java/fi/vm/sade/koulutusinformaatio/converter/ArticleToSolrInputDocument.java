package fi.vm.sade.koulutusinformaatio.converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.springframework.core.convert.converter.Converter;

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LearningOpportunity;
import fi.vm.sade.koulutusinformaatio.domain.Article;

public class ArticleToSolrInputDocument implements Converter<Article, List<SolrInputDocument>> {

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
        
        
        indexLangFields(article, doc, lang);
        
        
        return doc;
    }
    
    private void indexLangFields(Article article, SolrInputDocument doc,
            String lang) {
        
        if (lang.equals("fi")) {
            doc.setField(LearningOpportunity.NAME_FI, article.getTitle());
        } else if (lang.equals("sv")) {
            doc.setField(LearningOpportunity.NAME_SV, article.getTitle());
        } else if (lang.equals("en")) {
            doc.setField(LearningOpportunity.NAME_EN, article.getTitle());
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
