package fi.vm.sade.koulutusinformaatio.domain;

import java.util.List;

public class Article {

    private String title;
    private String url;
    private String content;
    private List<ArticleAttachment> attachments;
    private String excerpt;
    private String id;
    
    
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

}
