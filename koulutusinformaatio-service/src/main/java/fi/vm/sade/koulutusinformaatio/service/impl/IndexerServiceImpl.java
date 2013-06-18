package fi.vm.sade.koulutusinformaatio.service.impl;

import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.service.IndexerService;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
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
    private final HttpSolrServer loUpdateHttpSolrServer;
    // solr client for learning opportunity provider index
    private final HttpSolrServer lopUpdateHttpSolrServer;

    @Autowired
    public IndexerServiceImpl(@Qualifier("loUpdateHttpSolrServer") HttpSolrServer loUpdateHttpSolrServer,
                              @Qualifier("lopUpdateHttpSolrServer") HttpSolrServer lopUpdateHttpSolrServer) {
        this.loUpdateHttpSolrServer = loUpdateHttpSolrServer;
        this.lopUpdateHttpSolrServer = lopUpdateHttpSolrServer;
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
        parentDoc.addField("lopAddress", provider.getVisitingAddress().getPostOffice());
        docs.add(parentDoc);

        SolrInputDocument providerDoc = new SolrInputDocument();
        providerDoc.addField("id", provider.getId());
        providerDoc.addField("name", provider.getName().getTranslations().get("fi"));


        for (ChildLOS child : parent.getChildren()) {
            for (ChildLOI loi : child.getChildLOIs()) {
                SolrInputDocument childLOIDoc = new SolrInputDocument();
                childLOIDoc.addField("id", loi.getId());
                String loiName = loi.getName() != null ? loi.getName().getTranslations().get("fi") : "Ei nimeä";
                childLOIDoc.addField("name", loiName);
                for (I18nText i18n : loi.getProfessionalTitles()) {
                    childLOIDoc.addField("professionalTitles", i18n.getTranslations().get("fi"));
                }
                childLOIDoc.addField("lopId", provider.getId());
                childLOIDoc.addField("lopName", provider.getName().getTranslations().get("fi"));
                childLOIDoc.addField("lopAddress", provider.getVisitingAddress().getPostOffice());
                childLOIDoc.addField("parentId", parent.getId());
                childLOIDoc.addField("losId", child.getId());

                if (loi.getApplicationSystemId() != null) {
                    providerDoc.addField("asId", loi.getApplicationSystemId());
                }

                docs.add(childLOIDoc);
            }
        }
        providerDocs.add(providerDoc);
        lopUpdateHttpSolrServer.add(providerDocs);
        loUpdateHttpSolrServer.add(docs);
    }

    @Override
    public void commitLOChanges() throws Exception {
        loUpdateHttpSolrServer.commit();
        lopUpdateHttpSolrServer.commit();
    }

}
