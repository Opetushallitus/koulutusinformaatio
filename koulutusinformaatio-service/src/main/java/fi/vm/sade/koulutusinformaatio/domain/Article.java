/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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
package fi.vm.sade.koulutusinformaatio.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author Markus
 */
public class Article {

    private String title;
    private String url;
    private String content;
    private List<ArticleAttachment> attachments;
    private String excerpt;
    private String id;
    
    private List<ArticleCode> taxonomy_oph_koulutus;
    private List<ArticleCode> taxonomy_oph_koulutustyyppi;
    
    private List<String> educationCodes;
    private List<String> educationTypeCodes;
     
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public List<ArticleAttachment> getAttachments() {
        return attachments;
    }
    public void setAttachments(List<ArticleAttachment> attachments) {
        this.attachments = attachments;
    }
    public String getExcerpt() {
        return excerpt;
    }
    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public List<ArticleCode> getTaxonomy_oph_koulutus() {
        return taxonomy_oph_koulutus;
    }
    @JsonProperty("taxonomy_oph-koulutus")
    public void setTaxonomy_oph_koulutus(List<ArticleCode> taxonomy_oph_koulutus) {
        this.taxonomy_oph_koulutus = taxonomy_oph_koulutus;
    }
    public List<ArticleCode> getTaxonomy_oph_koulutustyyppi() {
        return taxonomy_oph_koulutustyyppi;
    }
    @JsonProperty("taxonomy_oph-koulutustyyppi")
    public void setTaxonomy_oph_koulutustyyppi(
            List<ArticleCode> taxonomy_oph_koulutustyyppi) {
        this.taxonomy_oph_koulutustyyppi = taxonomy_oph_koulutustyyppi;
    }
    public List<String> getEducationCodes() {
        return educationCodes;
    }
    public void setEducationCodes(List<String> educationCodes) {
        this.educationCodes = educationCodes;
    }
    public List<String> getEducationTypeCodes() {
        return educationTypeCodes;
    }
    public void setEducationTypeCodes(List<String> educationTypeCodes) {
        this.educationTypeCodes = educationTypeCodes;
    }

}
