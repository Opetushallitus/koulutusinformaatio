package fi.vm.sade.koulutusinformaatio.domain;

public class Social {
    
    private String name;
    private String url;
    
    public Social() {
        
    }
    
    public Social(String name, String url) {
        super();
        this.name = name;
        this.url = url;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    
    

}
