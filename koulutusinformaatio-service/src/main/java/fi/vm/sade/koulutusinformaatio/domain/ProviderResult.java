package fi.vm.sade.koulutusinformaatio.domain;

public class ProviderResult {
    
    private String id;
    private String name;
    private String address;
    private String thumbnailEncoded;
    private boolean providerOrg;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String description) {
        this.address = description;
    }
    public String getThumbnailEncoded() {
        return thumbnailEncoded;
    }
    public void setThumbnailEncoded(String thumbnailEncoded) {
        this.thumbnailEncoded = thumbnailEncoded;
    }
    public boolean isProviderOrg() {
        return providerOrg;
    }
    public void setProviderOrg(boolean providerOrg) {
        this.providerOrg = providerOrg;
    }

}
