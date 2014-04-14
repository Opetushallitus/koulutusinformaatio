package fi.vm.sade.koulutusinformaatio.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.LocationFields;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil.SolrConstants;
import fi.vm.sade.koulutusinformaatio.service.IndexerService;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
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
            @Qualifier("locationHttpSolrServer") HttpSolrServer locationHttpSolrServer) {
        this.conversionService = conversionService;
        this.loUpdateHttpSolrServer = loUpdateHttpSolrServer;
        this.lopUpdateHttpSolrServer = lopUpdateHttpSolrServer;
        this.locationUpdateHttpSolrServer = locationUpdateHttpSolrServer;
        this.loHttpSolrServer = loHttpSolrServer;
        this.lopHttpSolrServer = lopHttpSolrServer;
        this.locationHttpSolrServer = locationHttpSolrServer;
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
        } else if (los instanceof HigherEducationLOS) {
            HigherEducationLOS uas = (HigherEducationLOS)los;
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

        }

        List<SolrInputDocument> docs = conversionService.convert(los, List.class);

        List<SolrInputDocument> providerDocs = Lists.newArrayList();
        if (provider != null) {
            SolrInputDocument providerDoc = new SolrInputDocument();
            providerDoc.addField("id", provider.getId());

            String nameFi = resolveTextByLang("fi", provider.getName().getTranslations());
            providerDoc.addField("name_fi", nameFi);
            providerDoc.addField("startsWith_fi", nameFi.substring(0, 1).toUpperCase());
            String nameSv = resolveTextByLang("sv", provider.getName().getTranslations());
            providerDoc.addField("name_sv", nameSv);
            providerDoc.addField("startsWith_sv", nameSv.substring(0, 1).toUpperCase());

            // check if provider exists and update base education and as id values
            SolrQuery query = new SolrQuery("id:" + provider.getId());
            QueryResponse response = lopSolr.query(query);//lopUpdateHttpSolrServer.query(query);
            List<SolrDocument> results = response.getResults();
            if (results != null && results.size() > 0) {
                List<String> edus = (List<String>) results.get(0).get("requiredBaseEducations");
                if (edus != null) {
                    requiredBaseEducations.addAll(edus);
                }
                List<String> asids = (List<String>) results.get(0).get("asIds");
                if (asids != null) {
                    providerAsIds.addAll(asids);
                }
                List<String> vocational = (List<String>) results.get(0).get("vocationalAsIds");
                if (vocational != null) {
                    vocationalAsIds.addAll(vocational);
                }
                List<String> nonVocational = (List<String>) results.get(0).get("nonVocationalAsIds");
                if (nonVocational != null) {
                    nonVocationalAsIds.addAll(nonVocational);
                }
            }

            providerDoc.setField("asIds", providerAsIds);
            providerDoc.setField("requiredBaseEducations", requiredBaseEducations);
            providerDoc.setField("vocationalAsIds", vocationalAsIds);
            providerDoc.setField("nonVocationalAsIds", nonVocationalAsIds);
            providerDocs.add(providerDoc);
        }

        lopSolr.add(providerDocs);
        loSolr.add(docs);
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

    private String resolveTextByLang(String lang, Map<String, String> translations) {
        if (translations.containsKey(lang)) {
            return translations.get(lang);
        } else if (translations.containsKey(FALLBACK_LANG)) {
            return translations.get(FALLBACK_LANG);
        } else {
            return translations.values().iterator().next();
        }
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

}
