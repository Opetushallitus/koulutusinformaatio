package fi.vm.sade.koulutusinformaatio.service.impl;

import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.service.IndexerService;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
@Service
public class IndexerServiceImpl implements IndexerService {

    public static final Logger LOGGER = LoggerFactory.getLogger(IndexerServiceImpl.class);

    // solr client for learning opportunity index
    private final HttpSolrServer loHttpSolrServer;
    // solr client for learning opportunity provider index
    private final HttpSolrServer lopHttpSolrServer;

    @Autowired
    public IndexerServiceImpl(@Qualifier("loHttpSolrServer") HttpSolrServer loHttpSolrServer,
                              @Qualifier("lopHttpSolrServer") HttpSolrServer lopHttpSolrServer) {
        this.loHttpSolrServer = loHttpSolrServer;
        this.lopHttpSolrServer = lopHttpSolrServer;
    }

    @Override
    public void dropLOs() throws Exception {
        try {
            loHttpSolrServer.deleteByQuery("*:*");
            loHttpSolrServer.commit();
            loHttpSolrServer.optimize();
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void dropLOPs() throws Exception {
        try {
            lopHttpSolrServer.deleteByQuery("*:*");
            lopHttpSolrServer.commit();
            lopHttpSolrServer.optimize();
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void addParentLearningOpportunity(ParentLOS parent) throws Exception {
        // TODO: index all languages
        List<SolrInputDocument> docs = Lists.newArrayList();
        List<SolrInputDocument> providerDocs = Lists.newArrayList();

        SolrInputDocument parentDoc = new SolrInputDocument();
        parentDoc.addField("id", parent.getId());
        parentDoc.addField("name", parent.getName().getTranslations().get("fi"));
        Provider provider = parent.getProvider();
        parentDoc.addField("lopId", provider.getId());
        parentDoc.addField("lopName", provider.getName().getTranslations().get("fi"));
        docs.add(parentDoc);

        SolrInputDocument providerDoc = new SolrInputDocument();
        providerDoc.addField("id", provider.getId());
        providerDoc.addField("name", provider.getName().getTranslations().get("fi"));


        for (ChildLOS child : parent.getChildren()) {
            for (ChildLOI loi : child.getChildLOIs()) {
                SolrInputDocument childLOIDoc = new SolrInputDocument();
                childLOIDoc.addField("id", loi.getId());
                childLOIDoc.addField("name", loi.getName().getTranslations().get("fi"));
                childLOIDoc.addField("lopId", provider.getId());
                childLOIDoc.addField("lopName", provider.getName().getTranslations().get("fi"));
                childLOIDoc.addField("parentId", parent.getId());
                childLOIDoc.addField("losId", child.getId());

                if (loi.getApplicationSystemId() != null) {
                    providerDoc.addField("asId", loi.getApplicationSystemId());
                }

                docs.add(childLOIDoc);
            }
        }
        providerDocs.add(providerDoc);
        lopHttpSolrServer.add(providerDocs);
        loHttpSolrServer.add(docs);
    }

    @Override
    public void commitLOChnages() throws Exception {
        loHttpSolrServer.commit();
        lopHttpSolrServer.commit();
    }

}
