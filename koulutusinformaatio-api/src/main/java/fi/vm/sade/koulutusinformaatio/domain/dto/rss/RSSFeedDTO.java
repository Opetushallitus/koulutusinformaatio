package fi.vm.sade.koulutusinformaatio.domain.dto.rss;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/*
 * POJO representing RSS feed
 */
@XmlRootElement(name = "rss")
public class RSSFeedDTO {

    private RSSChannelDTO channel;
    private String version;
    
    @XmlElement
    public RSSChannelDTO getChannel() {
        return channel;
    }

    public void setChannel(RSSChannelDTO channel) {
        this.channel = channel;
    }
    
    @XmlAttribute
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
