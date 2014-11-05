package fi.vm.sade.koulutusinformaatio.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LearningOpportunity;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LocationFields;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;
import fi.vm.sade.koulutusinformaatio.service.IndexerService;
import fi.vm.sade.koulutusinformaatio.service.impl.query.LearningOpportunityByProviderQuery;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Hannu Lyytikainen
 */
@Service
public class IndexerServiceImpl implements IndexerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexerServiceImpl.class);

    private static final String FALLBACK_LANG = "fi";
    
    private static final String SOLR_ERROR = "Solr search error occured.";

    private final ConversionService conversionService;

    // solr client for learning opportunity index
    private final HttpSolrServer loUpdateHttpSolrServer;
    // solr client for learning opportunity provider index
    private final HttpSolrServer lopUpdateHttpSolrServer;

    // solr client for learning opportunity index
    private final HttpSolrServer loHttpSolrServer;
    // solr client for learning opportunity provider index
    private final HttpSolrServer lopHttpSolrServer;
    private final HttpSolrServer locationUpdateHttpSolrServer;
    private final HttpSolrServer locationHttpSolrServer;
    
    private final HttpSolrServer loAliasHttpSolrServer;
    private final HttpSolrServer lopAliasHttpSolrServer;
    private final HttpSolrServer locationAliasHttpSolrServer;
    

    @Value("${solr.learningopportunity.alias.url:learning_opportunity}")
    private String loHttpAliasName;

    @Value("${solr.learningopportunity.url:learning_opportunity}")
    private String loHttpSolrName;

    @Value("${koulutusinformaatio.wp.harvest-url:harvest}")
    private String articleHarvestUrl;

    @Autowired
    public IndexerServiceImpl(ConversionService conversionService,
            @Qualifier("loUpdateHttpSolrServer") HttpSolrServer loUpdateHttpSolrServer,
            @Qualifier("lopUpdateHttpSolrServer") HttpSolrServer lopUpdateHttpSolrServer,
            @Qualifier("locationUpdateHttpSolrServer") HttpSolrServer locationUpdateHttpSolrServer,
            @Qualifier("loHttpSolrServer") HttpSolrServer loHttpSolrServer,
            @Qualifier("lopHttpSolrServer") HttpSolrServer lopHttpSolrServer,
            @Qualifier("locationHttpSolrServer") HttpSolrServer locationHttpSolrServer,
            @Qualifier("loAliasSolrServer") HttpSolrServer loAliasHttpSolrServer,
            @Qualifier("lopAliasSolrServer") final HttpSolrServer lopAliasHttpSolrServer,
            @Qualifier("locationAliasSolrServer") final HttpSolrServer locationAliasHttpSolrServer) {
        this.conversionService = conversionService;
        this.loUpdateHttpSolrServer = loUpdateHttpSolrServer;
        this.lopUpdateHttpSolrServer = lopUpdateHttpSolrServer;
        this.locationUpdateHttpSolrServer = locationUpdateHttpSolrServer;
        this.loHttpSolrServer = loHttpSolrServer;
        this.lopHttpSolrServer = lopHttpSolrServer;
        this.locationHttpSolrServer = locationHttpSolrServer;
        this.loAliasHttpSolrServer = loAliasHttpSolrServer;
        this.lopAliasHttpSolrServer = lopAliasHttpSolrServer;
        this.locationAliasHttpSolrServer = locationAliasHttpSolrServer;
    }

    @Override
    public void addLearningOpportunitySpecification(LOS los, HttpSolrServer loSolr, HttpSolrServer lopSolr) 
                                                        throws IOException, SolrServerException {
        Provider provider = null;
        Set<String> providerAsIds = Sets.newHashSet();
        Set<String> requiredBaseEducations = Sets.newHashSet();
        Set<String> vocationalAsIds = Sets.newHashSet();
        Set<String> nonVocationalAsIds = Sets.newHashSet();
        //Adding parent los (vocational learning opportunity)
        if (los instanceof ParentLOS) {
            ParentLOS parent = (ParentLOS) los;
            provider = parent.getProvider();
            for (ChildLOS childLOS : parent.getChildren()) {
                for (ChildLOI childLOI : childLOS.getLois()) {
                    for (ApplicationOption ao : childLOI.getApplicationOptions()) {
                        providerAsIds.add(ao.getApplicationSystem().getId());
                        requiredBaseEducations.addAll(ao.getRequiredBaseEducations());
                        if (ao.isVocational()) {
                            vocationalAsIds.add(ao.getApplicationSystem().getId());
                        } else {
                            nonVocationalAsIds.add(ao.getApplicationSystem().getId());
                        }
                    }
                }
            }
            //Adding upper secondary los (high school)
        } else if (los instanceof UpperSecondaryLOS) {
            UpperSecondaryLOS upperLOS = (UpperSecondaryLOS) los;
            provider = upperLOS.getProvider();
            for (UpperSecondaryLOI loi : upperLOS.getLois()) {
                for (ApplicationOption ao : loi.getApplicationOptions()) {
                    providerAsIds.add(ao.getApplicationSystem().getId());
                    requiredBaseEducations.addAll(ao.getRequiredBaseEducations());
                    if (ao.isVocational()) {
                        vocationalAsIds.add(ao.getApplicationSystem().getId());
                    } else {
                        nonVocationalAsIds.add(ao.getApplicationSystem().getId());
                    }
                }
            }
            //Adding special los 
        } else if (los instanceof SpecialLOS) {
            SpecialLOS special = (SpecialLOS) los;
            provider = special.getProvider();

            for (ChildLOI childLOI : special.getLois()) {
                for (ApplicationOption ao : childLOI.getApplicationOptions()) {
                    providerAsIds.add(ao.getApplicationSystem().getId());
                    requiredBaseEducations.addAll(ao.getRequiredBaseEducations());
                    if (ao.isVocational()) {
                        vocationalAsIds.add(ao.getApplicationSystem().getId());
                    } else {
                        nonVocationalAsIds.add(ao.getApplicationSystem().getId());
                    }
                }
            }

            //Adding higher education los
        } else if (los instanceof StandaloneLOS) {
            StandaloneLOS uas = (StandaloneLOS)los;
            provider = uas.getProvider();

            if (uas.getApplicationOptions() != null) {
                for (ApplicationOption ao : uas.getApplicationOptions()) {
                    providerAsIds.add(ao.getApplicationSystem().getId());
                    requiredBaseEducations.addAll(ao.getRequiredBaseEducations());
                    if (ao.isVocational()) {
                        vocationalAsIds.add(ao.getApplicationSystem().getId());
                    } else {
                        nonVocationalAsIds.add(ao.getApplicationSystem().getId());
                    }
                }
            }

        } else if (los instanceof CompetenceBasedQualificationParentLOS) {
            CompetenceBasedQualificationParentLOS cbqpLos = (CompetenceBasedQualificationParentLOS)los;
            provider = cbqpLos.getProvider();

            if (cbqpLos.getApplicationOptions() != null) {
                for (ApplicationOption ao : cbqpLos.getApplicationOptions()) {
                    providerAsIds.add(ao.getApplicationSystem().getId());
                    requiredBaseEducations.addAll(ao.getRequiredBaseEducations());
                    if (ao.isVocational()) {
                        vocationalAsIds.add(ao.getApplicationSystem().getId());
                    } else {
                        nonVocationalAsIds.add(ao.getApplicationSystem().getId());
                    }
                }
            }
        }

        List<SolrInputDocument> docs = conversionService.convert(los, List.class);


        createProviderDocs(provider, 
                            lopSolr, 
                            requiredBaseEducations, 
                            vocationalAsIds, 
                            nonVocationalAsIds, 
                            providerAsIds);
        
        
        if (los instanceof StandaloneLOS) {
            StandaloneLOS uas = (StandaloneLOS)los;
            for (Provider curAddProv : uas.getAdditionalProviders()) {
                createProviderDocs(curAddProv,
                                    lopSolr, 
                                    requiredBaseEducations, 
                                    vocationalAsIds, 
                                    nonVocationalAsIds, 
                                    providerAsIds);
            }
        }
       
        loSolr.add(docs);
    }
    
    

    public void createProviderDocs(Provider provider, 
                                    HttpSolrServer lopSolr, 
                                    Set<String> requiredBaseEducations, 
                                    Set<String> vocationalAsIds,
                                    Set<String> nonVocationalAsIds,
                                    Set<String> providerAsIds) throws SolrServerException, IOException {
        
        List<SolrInputDocument> providerDocs = Lists.newArrayList();
        if (provider != null) {
            SolrInputDocument providerDoc = new SolrInputDocument();
            providerDoc.addField(SolrUtil.ProviderFields.ID, provider.getId());
            providerDoc.addField(SolrUtil.ProviderFields.TYPE, SolrUtil.TYPE_ORGANISATION);

            String nameFi = resolveTextByLangWithFallback("fi", provider.getName().getTranslations());
            if (nameFi != null && !nameFi.isEmpty()) {
                providerDoc.addField(SolrUtil.ProviderFields.NAME_FI, nameFi);
                providerDoc.addField(SolrUtil.ProviderFields.STARTS_WITH_FI, nameFi.substring(0, 1).toUpperCase());
                providerDoc.addField(SolrUtil.ProviderFields.TEXT_FI, nameFi);
            }
            String nameSv = resolveTextByLangWithFallback("sv", provider.getName().getTranslations());
            if (nameSv != null && !nameSv.isEmpty()) {
                providerDoc.addField(SolrUtil.ProviderFields.NAME_SV, nameSv);
                providerDoc.addField(SolrUtil.ProviderFields.STARTS_WITH_SV, nameSv.substring(0, 1).toUpperCase());
                providerDoc.addField(SolrUtil.ProviderFields.TEXT_SV, nameSv);
            }
            String nameEn = resolveTextByLangWithFallback("en", provider.getName().getTranslations());
            if (nameEn != null && !nameEn.isEmpty()) {
                providerDoc.addField(SolrUtil.ProviderFields.NAME_EN, nameEn);
                providerDoc.addField(SolrUtil.ProviderFields.STARTS_WITH_EN, nameEn.substring(0, 1).toUpperCase());
                providerDoc.addField(SolrUtil.ProviderFields.TEXT_EN, nameEn);
            }
            if (provider.getType() != null) {
                providerDoc.setField(SolrUtil.ProviderFields.TYPE_VALUE, provider.getType().getValue());
                providerDoc.setField(SolrUtil.ProviderFields.TYPE_FI, resolveTextByLangWithFallback("fi", provider.getType().getName().getTranslations()));
                providerDoc.setField(SolrUtil.ProviderFields.TYPE_SV, resolveTextByLangWithFallback("sv", provider.getType().getName().getTranslations()));
                providerDoc.setField(SolrUtil.ProviderFields.TYPE_EN, resolveTextByLangWithFallback("en", provider.getType().getName().getTranslations()));
            }
            else {
                providerDoc.setField(SolrUtil.ProviderFields.TYPE_VALUE, SolrConstants.PROVIDER_TYPE_UNKNOWN);
            }

            // check if provider exists and update base education and as id values
            SolrQuery query = new SolrQuery("id:" + provider.getId());
            QueryResponse response = lopSolr.query(query);//lopUpdateHttpSolrServer.query(query);
            List<SolrDocument> results = response.getResults();
            if (results != null && results.size() > 0) {
                List<String> edus = (List<String>) results.get(0).get(SolrUtil.ProviderFields.REQUIRED_BASE_EDUCATIONS);
                if (edus != null) {
                    requiredBaseEducations.addAll(edus);
                }
                List<String> asids = (List<String>) results.get(0).get(SolrUtil.ProviderFields.AS_IDS);
                if (asids != null) {
                    providerAsIds.addAll(asids);
                }
                List<String> vocational = (List<String>) results.get(0).get(SolrUtil.ProviderFields.VOCATIONAL_AS_IDS);
                if (vocational != null) {
                    vocationalAsIds.addAll(vocational);
                }
                List<String> nonVocational = (List<String>) results.get(0).get(SolrUtil.ProviderFields.NON_VOCATIONAL_AS_IDS);
                if (nonVocational != null) {
                    nonVocationalAsIds.addAll(nonVocational);
                }
            }
            
            if (provider.getOlTypes() != null) {
                for (Code curOlType : provider.getOlTypes()) {
                    if (curOlType != null && curOlType.getUri() != null) {
                        providerDoc.addField(SolrUtil.ProviderFields.OL_TYPE, curOlType.getUri());
                    }
                }
            }
            
            
            if (provider.getVisitingAddress() != null) {
                Address visitingAddr = provider.getVisitingAddress();
                String addrEn = this.getAddrStr(visitingAddr, "en");
                if (addrEn != null && !addrEn.isEmpty()) {
                    providerDoc.addField(SolrUtil.ProviderFields.ADDRESS_EN, addrEn);
                    providerDoc.addField(SolrUtil.ProviderFields.TEXT_EN, addrEn);
                }
               
                String addrSv = this.getAddrStr(visitingAddr, "sv");
                if (addrSv != null && !addrSv.isEmpty()) {
                    providerDoc.addField(SolrUtil.ProviderFields.ADDRESS_SV, addrSv);
                    providerDoc.addField(SolrUtil.ProviderFields.TEXT_SV, addrSv);
                }
                
                String addrFi = this.getAddrStr(visitingAddr, "fi");
                if (addrFi != null && !addrFi.isEmpty()) {
                    providerDoc.addField(SolrUtil.ProviderFields.ADDRESS_FI, addrFi);
                    providerDoc.addField(SolrUtil.ProviderFields.TEXT_FI, addrFi);
                }
                
                
            }
            
            if (provider.getHomeDistrict() != null) {
                List<String> locVals = new ArrayList<String>();
                locVals.addAll(provider.getHomeDistrict().getTranslations().values());
                locVals.addAll(provider.getHomePlace().getTranslations().values());
                providerDoc.addField(LearningOpportunity.LOP_HOMEPLACE, locVals);
            } else {
                providerDoc.addField(LearningOpportunity.LOP_HOMEPLACE, provider.getHomePlace().getTranslations().values());
            }

            providerDoc.setField(SolrUtil.ProviderFields.AS_IDS, providerAsIds);
            providerDoc.setField(SolrUtil.ProviderFields.REQUIRED_BASE_EDUCATIONS, requiredBaseEducations);
            providerDoc.setField(SolrUtil.ProviderFields.VOCATIONAL_AS_IDS, vocationalAsIds);
            providerDoc.setField(SolrUtil.ProviderFields.NON_VOCATIONAL_AS_IDS, nonVocationalAsIds);
            providerDocs.add(providerDoc);
            
            if (provider.getOlTypes() != null) {
                for (Code curOlType : provider.getOlTypes()) {
                    SolrUtil.indexCodeAsFacetDoc(curOlType, providerDocs, false);
                }
            }
            
        }
        if (!providerDocs.isEmpty()) {
            lopSolr.add(providerDocs);
        }
        
    }
    
    private String getAddrStr(Address addr, String lang) {
        if (lang.equalsIgnoreCase("en")) {
            return (addr.getStreetAddress() != null && addr.getStreetAddress().getTranslations() != null) ? this.resolveTextByLangEmptyDefault("en", addr.getStreetAddress().getTranslations()) : null;
        }
        if (lang.equalsIgnoreCase("fi") || lang.equalsIgnoreCase("sv")) {
            String addrStr = "";
            addrStr = (addr.getStreetAddress() != null 
                    && addr.getStreetAddress().getTranslations() != null
                    && !addr.getStreetAddress().getTranslations().isEmpty()) 
                    ? this.resolveTextByLangEmptyDefault(lang, addr.getStreetAddress().getTranslations()) : addrStr;
            addrStr = (addr.getPostalCode() != null) ? String.format("%s, %s",  addrStr, addr.getPostalCode()) : addrStr;
            addrStr = (addr.getPostOffice() != null 
                    && addr.getPostOffice().getTranslations() != null
                    && !addr.getPostOffice().getTranslations().isEmpty()) 
                    ? String.format("%s, %s",  addrStr, this.resolveTextByLangWithFallback(lang, addr.getPostOffice().getTranslations())) 
                            : addrStr;
           return addrStr;
        }
        return null;
    }

    @Override
    public void commitLOChanges(HttpSolrServer loUpdateSolr, 
                                HttpSolrServer lopUpdateSolr, 
                                HttpSolrServer locationUpdateSolr, 
                                boolean createTimestamp) throws IOException, SolrServerException {
        
        if (createTimestamp) {
            List<SolrInputDocument> timeStampDocs = new ArrayList<SolrInputDocument>();
            SolrInputDocument timestampDoc = new SolrInputDocument();
            timestampDoc.addField("id", "loUpdateTimestampDocument");
            timestampDoc.addField("name", getTimestampStr());
            timeStampDocs.add(timestampDoc);
            loUpdateSolr.add(timeStampDocs);//loUpdateHttpSolrServer.add(timeStampDocs);
        }
        loUpdateSolr.commit();//loUpdateHttpSolrServer.commit();
        lopUpdateSolr.commit();//lopUpdateHttpSolrServer.commit();
        locationUpdateSolr.commit();
    }

    private String getTimestampStr() {
        DateFormat df = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        return df.format(new Date());
    }

    @Override
    public void addLocations(List<Location> locations, HttpSolrServer locationUpdateSolr) throws IOException, SolrServerException {
        List<SolrInputDocument> locationDocs = Lists.newArrayList();

        for (Location location : locations) {
            SolrInputDocument locationDoc = new SolrInputDocument();
            locationDoc.addField(LocationFields.ID, location.getId());
            locationDoc.addField(LocationFields.NAME, location.getName());
            locationDoc.addField(LocationFields.NAME_AUTO, location.getName());
            locationDoc.addField(LocationFields.CODE, location.getCode());
            locationDoc.addField(LocationFields.LANG, location.getLang());
            locationDoc.addField(LocationFields.TYPE, location.getType());
            if (location.getParent() != null) {
                locationDoc.addField(LocationFields.PARENT, location.getParent());
            }
            locationDocs.add(locationDoc);
        }
        locationUpdateSolr.add(locationDocs);
    }

    /*
     * Getting the lo-collection into which data will be indexed.
     * It is the one that has the older update timestamp.
     */
    @Override
    public HttpSolrServer getLoCollectionToUpdate() {
        if ((this.loHttpAliasName != null) 
                && (this.loHttpSolrName != null) 
                && this.loHttpAliasName.equals(this.loHttpSolrName)) {
            return this.loUpdateHttpSolrServer;
        }

        Date updateLoCoreTimestamp = this.getUpdateTimestamp(this.loUpdateHttpSolrServer);

        Date loCoreTimestamp = this.getUpdateTimestamp(this.loHttpSolrServer);

        if (updateLoCoreTimestamp == null
                || (loCoreTimestamp != null && loCoreTimestamp.after(updateLoCoreTimestamp))) {
            return this.loUpdateHttpSolrServer;
        }
        return this.loHttpSolrServer;
    }
    
    @Override
    public boolean isDocumentInIndex(String docId, HttpSolrServer server) {
        LOGGER.debug("Checking if document is in index: " + docId);
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.addFilterQuery(String.format("id:%s", docId));
        query.setFields("id");
        query.setStart(0);
        query.set("defType", "edismax");
        try {
            QueryResponse response = server.query(query);
            return response.getResults().getNumFound() > 0;
        } catch (Exception ex) {
            LOGGER.error(String.format("Could not check if document in index: %s", ex.getMessage()));
        }
        return false;
    }

    /*
     * Getting the update timestamp for the lo-collection.
     */
    private Date getUpdateTimestamp(HttpSolrServer server) {
        LOGGER.debug("Updating solr timestamp");
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.addFilterQuery("id:loUpdateTimestampDocument");
        query.setFields("id", "name");
        query.setStart(0);
        query.set("defType", "edismax");
        try {
            QueryResponse response = server.query(query);
            for (SolrDocument curDoc : response.getResults()) {
                return new SimpleDateFormat("MM.dd.yyyy HH:mm:ss").parse(String.format("%s", curDoc.getFieldValue("name")));
            }
        } catch (Exception ex) {
            LOGGER.error(String.format("Could not get update timestamp: %s", ex.getMessage()));
        }
        return null;
    }

    /*
     * Getting the lop collection into which data will be indexed.
     * Goes in sync with the lo-collection.
     */
    @Override
    public HttpSolrServer getLopCollectionToUpdate(HttpSolrServer loUpdateSolr) {
        if (loUpdateSolr.getBaseURL().equals(this.loUpdateHttpSolrServer.getBaseURL())) {
            return this.lopUpdateHttpSolrServer;
        }
        return this.lopHttpSolrServer;
    }

    /*
     * Getting the location collection into which data will be indexed.
     * Goes in sync with the location-collection.
     */
    @Override
    public HttpSolrServer getLocationCollectionToUpdate(HttpSolrServer loUpdateSolr) {

        if (loUpdateSolr.getBaseURL().equals(this.loUpdateHttpSolrServer.getBaseURL())) {
            return this.locationUpdateHttpSolrServer;
        }
        return this.locationHttpSolrServer;
    }

    private String resolveTextByLangWithFallback(String lang, Map<String, String> translations) {
        if (translations.containsKey(lang)) {
            return translations.get(lang);
        } else if (translations.containsKey(FALLBACK_LANG)) {
            return translations.get(FALLBACK_LANG);
        } else {
            return translations.values().iterator().next();
        }
    }
    

    private String resolveTextByLangEmptyDefault(String lang, Map<String, String> translations) {
        if (translations.containsKey(lang)) {
            return translations.get(lang);
        } 
        return "";
    }

    @Override
    public void addEdTypeCodes(List<Code> edTypeCodes,
            HttpSolrServer loUpdateSolr) throws IOException,
            SolrServerException {
        List<SolrInputDocument> edTypeDocs = Lists.newArrayList();
        for (Code curEdType : edTypeCodes) {
            SolrUtil.indexCodeAsFacetDoc(curEdType, edTypeDocs, true);
            LOGGER.debug(String.format("Indexed: %s to solr", curEdType));
        }
        loUpdateSolr.add(edTypeDocs);
    }

    @Override
    public void addArticles(HttpSolrServer loUpdateSolr, List<Article> articles) throws IOException, SolrServerException {
        
        for (Article curArticle : articles) {
            List<SolrInputDocument> docs = conversionService.convert(curArticle, List.class);
            loUpdateSolr.add(docs);
        }
        
    }

    @Override
    public void removeLos(LOS curLos, HttpSolrServer loHttpSolrServer)
            throws IOException, SolrServerException {
        
        if (curLos instanceof ParentLOS) {
            ParentLOS parent = (ParentLOS)curLos;
            Map<String,String> prerequisitesMap = new HashMap<String,String>();
            for (ChildLOS childLOS : parent.getChildren()) {
                for (ChildLOI childLOI : childLOS.getLois()) {
                    String prereq = SolrConstants.SPECIAL_EDUCATION.equalsIgnoreCase(childLOI.getPrerequisite().getValue()) 
                            ? SolrConstants.PK 
                                    : childLOI.getPrerequisite().getValue();
                    prerequisitesMap.put(prereq, prereq);
                }
            }

            for (String curPrereq : prerequisitesMap.values()) {
                //docs.add(createParentDoc(parent, curPrereq));
                loHttpSolrServer.deleteById(String.format("%s#%s", curLos.getId(), curPrereq));
            }
        } else if (curLos instanceof SpecialLOS){
            for (ChildLOI curChild : ((SpecialLOS) curLos).getLois()) { 
                loHttpSolrServer.deleteById(curChild.getId());
            }
        } else if (curLos instanceof UpperSecondaryLOS) {
            for (UpperSecondaryLOI curLoi : ((UpperSecondaryLOS) curLos).getLois()) {
                loHttpSolrServer.deleteById(curLoi.getId());
            }
        } else if ((curLos instanceof HigherEducationLOS) 
                    || (curLos instanceof AdultUpperSecondaryLOS)
                    || (curLos instanceof CompetenceBasedQualificationParentLOS)) {
            loHttpSolrServer.deleteById(curLos.getId());
        } 
    }

    @Override
    public void removeArticles() throws SearchException, IOException {
        
        SolrQuery query = new SolrQuery("*");
        query.addFilterQuery(String.format("%s:%s", LearningOpportunity.TYPE,  SolrConstants.TYPE_ARTICLE));
        
        QueryResponse response = null;
        try {
            if (query != null) {
                response = loHttpSolrServer.query(query);
                //SolrDocumentList docList =  response.getResults();
                
            }
            if (response != null) {
                for (SolrDocument result : response.getResults()) {
                    String articleId = result.getFieldValue(SolrUtil.LearningOpportunity.ID).toString();
                    this.loAliasHttpSolrServer.deleteById(articleId);
                }
            }
            
            
        } catch (SolrServerException e) {
            throw new SearchException(SOLR_ERROR);
        }
        
        
    }

    @Override
    public void addArticles(List<Article> articles) throws IOException, SolrServerException {
       
        this.addArticles(this.loAliasHttpSolrServer, articles);
        this.commitLOChanges(this.loAliasHttpSolrServer, this.lopAliasHttpSolrServer, this.locationAliasHttpSolrServer, true);
        
    }
    
    @Override
    public void rollbackIncrementalSolrChanges() throws SolrServerException, IOException {
        loAliasHttpSolrServer.rollback();
        lopAliasHttpSolrServer.rollback();
        locationAliasHttpSolrServer.rollback();
    }

    @Override
    public void indexASToSolr(CalendarApplicationSystem as, HttpSolrServer loUpdateSolr) throws SolrServerException, IOException {
       
        SolrInputDocument asDoc = new SolrInputDocument();
        
        asDoc.addField(SolrUtil.LearningOpportunity.ID, as.getId());
        asDoc.addField(SolrUtil.LearningOpportunity.TYPE, SolrUtil.SolrConstants.TYPE_APPLICATION_SYSTEM);
        
        
        
        String nameFi = resolveTextByLangWithFallback("fi", as.getName().getTranslations());
        if (nameFi != null && !nameFi.isEmpty()) {
            asDoc.addField(SolrUtil.LearningOpportunity.NAME_FI, nameFi);
            asDoc.addField(SolrUtil.LearningOpportunity.NAME_DISPLAY_FI, nameFi);
        }
        String nameSv = resolveTextByLangEmptyDefault("sv", as.getName().getTranslations());
        if (nameSv != null && !nameSv.isEmpty()) {
            asDoc.addField(SolrUtil.LearningOpportunity.NAME_SV, nameSv);
            asDoc.addField(SolrUtil.LearningOpportunity.NAME_DISPLAY_SV, nameSv);
        }
        String nameEn = resolveTextByLangEmptyDefault("en", as.getName().getTranslations());
        if (nameEn != null && !nameEn.isEmpty()) {
            asDoc.addField(SolrUtil.LearningOpportunity.NAME_EN, nameEn);
            asDoc.addField(SolrUtil.LearningOpportunity.NAME_DISPLAY_EN, nameEn);
        }
        
        int parentApplicationDateRangeIndex = 0;
        
        for (ApplicationPeriod ap : as.getApplicationPeriods()) {//getApplicationDates()) {
            
            DateRange dr = ap.getDateRange();

            asDoc.addField(new StringBuilder().append("asStart").append("_").
                    append(String.valueOf(parentApplicationDateRangeIndex)).toString(), dr.getStartDate());
            asDoc.addField(new StringBuilder().append("asEnd").append("_").
                    append(String.valueOf(parentApplicationDateRangeIndex)).toString(), dr.getEndDate());

            String nimiFi = null;
            String nimiSv = null;
            String nimiEn = null;
            if (ap.getName() != null && ap.getName().getTranslations() != null && !ap.getName().getTranslations().isEmpty()) { 

                nimiFi = this.resolveTextByLangEmptyDefault("fi", ap.getName().getTranslations());
                nimiSv = this.resolveTextByLangEmptyDefault("sv", ap.getName().getTranslations());
                nimiEn = this.resolveTextByLangEmptyDefault("en", ap.getName().getTranslations());

            }
            
            asDoc.addField(new StringBuilder().append("asPeriodName").append("_").
                    append(String.valueOf(parentApplicationDateRangeIndex)).append("_fi_ss").toString(), nimiFi);
            asDoc.addField(new StringBuilder().append("asPeriodName").append("_").
                    append(String.valueOf(parentApplicationDateRangeIndex)).append("_sv_ss").toString(), nimiSv);
            asDoc.addField(new StringBuilder().append("asPeriodName").append("_").
                    append(String.valueOf(parentApplicationDateRangeIndex)).append("_en_ss").toString(), nimiEn);
            
            parentApplicationDateRangeIndex++;
            
            
            
        }
        
        
        
        loUpdateSolr.add(asDoc);
        
        
        
        
    }

}
