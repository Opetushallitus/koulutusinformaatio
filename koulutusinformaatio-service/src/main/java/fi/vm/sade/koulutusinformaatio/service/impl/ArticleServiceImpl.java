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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;
import fi.vm.sade.koulutusinformaatio.domain.Article;
import fi.vm.sade.koulutusinformaatio.domain.ArticleCode;
import fi.vm.sade.koulutusinformaatio.domain.ArticleResults;
import fi.vm.sade.koulutusinformaatio.service.ArticleService;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.properties.OphProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Markus
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleServiceImpl.class);
    
    private KoodistoService koodistoService;
    private OphProperties urlProperties;

    @Autowired
    public ArticleServiceImpl(KoodistoService koodistoService, OphProperties urlProperties) {
        this.koodistoService = koodistoService;
        this.urlProperties = urlProperties;
    }

    @Override
    public List<Article> fetchArticles() {
        List<Article> articles = new ArrayList<>();
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        articles.addAll(fetchArticlesByLang(mapper, "fi"));
        LOGGER.debug("Fetched finnish articles");
        articles.addAll(fetchArticlesByLang(mapper, "sv"));
        LOGGER.debug("Fetched swedish articles");

        return articles;
    }


    private List<Article> fetchArticlesByLang(ObjectMapper mapper, String lang) {
        List<Article> articles = new ArrayList<>();
        articles.addAll(getArticlesByExtension(mapper, lang, ""));
        articles.addAll(getArticlesByExtension(mapper, lang, "/story"));
        for (Article article : articles) {
            article.setLanguageCode(lang);
        }
        return articles;
    }
    
    private List<Article> getArticlesByExtension(ObjectMapper mapper, String lang, String extension) {
        int page = 1;
        List<Article> articles = new ArrayList<>();
        
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
    
    private void transformArticleCodes(Article article) {

        List<String> edTypeVals = new ArrayList<>();
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
                } catch (Exception ignored) {
                }
            }
        }
        article.setEducationTypeCodes(edTypeVals);

        List<String> edVals = new ArrayList<>();
        if (article.getTaxonomy_oph_koulutus() != null) {
            for (ArticleCode curCode : article.getTaxonomy_oph_koulutus()) {
                try {
                    String codeUri = curCode.getSlug().substring(0, curCode.getSlug().lastIndexOf('_'));
                    edVals.add(codeUri);
                } catch (Exception ignored) {
                }
            }
        }
        article.setEducationCodes(edVals);
    }

    private ArticleResults getArticlesByLang(ObjectMapper mapper, String lang, String extension, int page) {
        String url = String.format("%s%s%s/?s=%s&json=1&page=%s", urlProperties.url("wp.base"), lang, extension, URLEncoder.encode(" "), page);
        LOGGER.debug("Article search url: {}", url);
        
        try { 
            URL orgUrl = new URL(url);        

            HttpURLConnection conn = (HttpURLConnection) (orgUrl.openConnection());

            conn.setRequestMethod(SolrConstants.GET);
            conn.connect();

            return mapper.readValue(conn.getInputStream(), ArticleResults.class);
        } catch (Exception ex) {
            LOGGER.debug("No articles for url: {}", url);
            ArticleResults articles = new ArticleResults();
            articles.setPosts(new ArrayList<Article>());
            articles.setCount(0);
            articles.setCount_total(0);
            articles.setPages(0);
            return articles;
        }
        
    }

}
