package fi.vm.sade.koulutusinformaatio.domain.dto.rss;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class RSSChannelItemGuidDTO {
    
    private String guid;
    private boolean permaLink;
    
    @XmlValue
    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }
    
    @XmlAttribute(name = "isPermaLink")
    public boolean isPermaLink() {
        return permaLink;
    }

    public void setPermaLink(boolean permaLink) {
        this.permaLink = permaLink;
    }

}
