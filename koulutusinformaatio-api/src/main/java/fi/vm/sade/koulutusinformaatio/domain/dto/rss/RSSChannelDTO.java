package fi.vm.sade.koulutusinformaatio.domain.dto.rss;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/*
 * POJO representing RSS channel
 */
@XmlType (propOrder={"title", "link", "description", "language", "items"})
public class RSSChannelDTO {
    
    private String title;
    private String link;
    private String description;
    private String language;
    private List<RSSChannelItemDTO> items;
    
    @XmlElement
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    @XmlElement
    public String getLink() {
        return link;
    }
    
    public void setLink(String link) {
        this.link = link;
    }
    
    @XmlElement
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @XmlElement(name = "language")
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @XmlElement(name = "item")
    public List<RSSChannelItemDTO> getItems() {
        return items;
    }

    public void setItems(List<RSSChannelItemDTO> items) {
        this.items = items;
    }

}
