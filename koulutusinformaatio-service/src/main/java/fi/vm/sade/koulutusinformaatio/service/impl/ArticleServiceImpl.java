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

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.javautils.httpclient.OphHttpClient;
import fi.vm.sade.javautils.httpclient.OphHttpResponse;
import fi.vm.sade.javautils.httpclient.OphHttpResponseHandler;
import fi.vm.sade.koulutusinformaatio.configuration.HttpClient;
import fi.vm.sade.koulutusinformaatio.domain.Article;
import fi.vm.sade.koulutusinformaatio.domain.ArticleCode;
import fi.vm.sade.koulutusinformaatio.domain.ArticleResults;
import fi.vm.sade.koulutusinformaatio.service.ArticleService;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    private final OphHttpClient client;

    @Autowired
    public ArticleServiceImpl(KoodistoService koodistoService, HttpClient client) {
        this.koodistoService = koodistoService;
        this.client = client.getClient();
    }

    @Override
    public List<Article> fetchArticles() {
        List<Article> articles = new ArrayList<>();
        
        ObjectMapper mapper = HttpClient.createJacksonMapper();
        
        articles.addAll(fetchArticlesByLang(mapper, "fi"));
        LOGGER.debug("Fetched finnish articles");
        articles.addAll(fetchArticlesByLang(mapper, "sv"));
        LOGGER.debug("Fetched swedish articles");

        return articles;
    }


    private List<Article> fetchArticlesByLang(ObjectMapper mapper, String lang) {
        List<Article> articles = new ArrayList<>();
        articles.addAll(getArticlesByExtension(mapper, lang, false));
        articles.addAll(getArticlesByExtension(mapper, lang, true));
        for (Article article : articles) {
            article.setLanguageCode(lang);
        }
        return articles;
    }
    
    private List<Article> getArticlesByExtension(ObjectMapper mapper, String lang, boolean story) {
        int page = 1;
        List<Article> articles = new ArrayList<>();
        
        ArticleResults articlesRes = getArticlesByLang(mapper, lang, story, page);
        int pages = articlesRes.getPages();

        while (pages > 0) {

            articles.addAll(articlesRes.getPosts());
            articlesRes = getArticlesByLang(mapper, lang, story, ++page);
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

    private ArticleResults getArticlesByLang(final ObjectMapper mapper, String lang, boolean story, int page) {
        String urlKey = story ? "wp.article.story" : "wp.article";
        try {
            return client.get(urlKey, lang, " ", page).execute(new OphHttpResponseHandler<ArticleResults>() {
                @Override
                public ArticleResults handleResponse(OphHttpResponse response) throws IOException {
                    return mapper.readValue(response.asInputStream(), ArticleResults.class);
                }
            });
        } catch (Exception ex) {
            LOGGER.debug("No articles", ex);
            ArticleResults articles = new ArticleResults();
            articles.setPosts(new ArrayList<Article>());
            articles.setCount(0);
            articles.setCount_total(0);
            articles.setPages(0);
            return articles;
        }
        
    }

}
