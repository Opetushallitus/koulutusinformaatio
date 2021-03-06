package fi.vm.sade.koulutusinformaatio.domain.dto.rss;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/*
 * POJO representing RSS channel item
 */
@XmlType (propOrder={"title", "description", "guid"})
public class RSSChannelItemDTO {

    private String title;
    private String description;
    private RSSChannelItemGuidDTO guid;
    
    private Date timestamp;
    
    @XmlElement
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    @XmlElement
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement
    public RSSChannelItemGuidDTO getGuid() {
        return guid;
    }

    public void setGuid(RSSChannelItemGuidDTO guid) {
        this.guid = guid;
    }

    @XmlTransient
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    
}
