/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.koulutusinformaatio.service.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;
import fi.vm.sade.koulutusinformaatio.domain.Article;
import fi.vm.sade.koulutusinformaatio.domain.ArticleCode;
import fi.vm.sade.koulutusinformaatio.domain.ArticleResults;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.ArticleService;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;

/**
 * 
 * @author Markus
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleServiceImpl.class);
    
    @Value("${koulutusinformaatio.wp.harvest-url:harvest}")
    private String articleHarvestUrl;
    
    @Value("${koulutusinformaatio.wp.harvest-url-en:harvest-en}")
    private String articleHarvestUrlEn;
    
    private KoodistoService koodistoService;
    
    @Autowired
    public ArticleServiceImpl(KoodistoService koodistoService) {
        this.koodistoService = koodistoService;
    }
    
    
    @Override
    public List<Article> fetchArticles() throws IOException, KoodistoException {
        List<Article> articles = new ArrayList<Article>();
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        articles.addAll(fetchArticlesByLang(mapper, "fi"));
        LOGGER.debug("Fetched finnish articles");
        articles.addAll(fetchArticlesByLang(mapper, "sv"));
        LOGGER.debug("Fetched swedish articles");
        try {
            articles.addAll(fetchEnglishArticles(mapper));//fetchArticlesByLang(mapper, "en"));
        } catch (Exception ex) {
            LOGGER.warn("English wp indexing problem: " + ex.getMessage());
        }
        LOGGER.debug("Fetched english articles");
        
        return articles;
    }
    
    private List<Article> fetchEnglishArticles(
            ObjectMapper mapper) throws Exception {
        
        
        List<Article> articles = new ArrayList<Article>();
        articles.addAll(getEnglishArticlesByExtension(mapper, ""));
        articles.addAll(getEnglishArticlesByExtension(mapper, "/story"));
        return articles;
    }


    private List<Article> getEnglishArticlesByExtension(
            ObjectMapper mapper, String extension) throws Exception {
        
        int page = 1;
        List<Article> articles = new ArrayList<Article>();
        
        ArticleResults articlesRes = getEnglishArticles(mapper, extension, page);
        int pages = articlesRes.getPages();

        while (pages > 0) {

            articles.addAll(articlesRes.getPosts());
            articlesRes = getEnglishArticles(mapper, extension, ++page);
            pages = articlesRes.getPages();
        
        }
        
        for (Article curArticle : articles) {
            transformArticleCodes(curArticle);
        }
        
        return articles;
        
    }

    private ArticleResults getEnglishArticles(ObjectMapper mapper,
            String extension, int page) {
        
        String url = String.format("%s%s/?s=%s&json=1&page=%s", this.articleHarvestUrlEn, extension, URLEncoder.encode(" "), page);
        LOGGER.debug("Article search url: " + url);
        
        try { 
            URL orgUrl = new URL(url);        

            HttpURLConnection conn = (HttpURLConnection) (orgUrl.openConnection());

            conn.setRequestMethod(SolrConstants.GET);
            conn.connect();

            ArticleResults articles = mapper.readValue(conn.getInputStream(), ArticleResults.class);
            return articles;
        } catch (Exception ex) {
            LOGGER.debug("No articles for url: " + url);
            ArticleResults articles = new ArticleResults();
            articles.setPosts(new ArrayList<Article>());
            articles.setCount(0);
            articles.setCount_total(0);
            articles.setPages(0);
            return articles;
        }
        
    }


    private List<Article> fetchArticlesByLang(ObjectMapper mapper, String lang) throws IOException, KoodistoException {
        List<Article> articles = new ArrayList<Article>();
        articles.addAll(getArticlesByExtension(mapper, lang, ""));
        articles.addAll(getArticlesByExtension(mapper, lang, "/story"));
        return articles;
    }
    
    private List<Article> getArticlesByExtension(ObjectMapper mapper, String lang, String extension) throws IOException, KoodistoException {
        int page = 1;
        List<Article> articles = new ArrayList<Article>();
        
        ArticleResults articlesRes = getArticlesByLang(mapper, lang, extension, page);
        int pages = articlesRes.getPages();

        while (pages > 0) {

            articles.addAll(articlesRes.getPosts());
            articlesRes = getArticlesByLang(mapper, lang, extension, ++page);
            pages = articlesRes.getPages();
        
        }
        
        for (Article curArticle : articles) {
            transformArticleCodes(curArticle);
        }
        
        return articles;
    }
    
    private void transformArticleCodes(Article article) throws KoodistoException {

        List<String> edTypeVals = new ArrayList<String>();
        if (article.getTaxonomy_oph_koulutustyyppi() != null) {
            for (ArticleCode curCode : article.getTaxonomy_oph_koulutustyyppi()) {
                try {
                    String codeUri = curCode.getSlug().substring(0, curCode.getSlug().lastIndexOf('_'));
                    LOGGER.debug(String.format("edTypeUrl: %s", codeUri));
                    String curVal = koodistoService.searchFirstCodeValue(codeUri);
                    if (curVal != null) {
                        LOGGER.debug(String.format("edTypeVal: %s", curVal));
                        edTypeVals.add(curVal.trim());
                    }
                } catch (Exception ex) {
                    continue;
                }
            }
        }
        article.setEducationTypeCodes(edTypeVals);

        List<String> edVals = new ArrayList<String>();
        if (article.getTaxonomy_oph_koulutus() != null) {
            for (ArticleCode curCode : article.getTaxonomy_oph_koulutus()) {
                try {
                    String codeUri = curCode.getSlug().substring(0, curCode.getSlug().lastIndexOf('_'));
                    edVals.add(codeUri);
                } catch (Exception ex) {
                    continue;
                }
            }
        }
        article.setEducationCodes(edVals);
    }

    private ArticleResults getArticlesByLang(ObjectMapper mapper, String lang, String extension, int page) throws IOException {
        String url = String.format("%s%s%s/?s=%s&json=1&page=%s", this.articleHarvestUrl, lang, extension, URLEncoder.encode(" "), page);
        LOGGER.debug("Article search url: " + url);
        
        try { 
            URL orgUrl = new URL(url);        

            HttpURLConnection conn = (HttpURLConnection) (orgUrl.openConnection());

            conn.setRequestMethod(SolrConstants.GET);
            conn.connect();

            ArticleResults articles = mapper.readValue(conn.getInputStream(), ArticleResults.class);
            return articles;
        } catch (Exception ex) {
            LOGGER.debug("No articles for url: " + url);
            ArticleResults articles = new ArticleResults();
            articles.setPosts(new ArrayList<Article>());
            articles.setCount(0);
            articles.setCount_total(0);
            articles.setPages(0);
            return articles;
        }
        
    }

}