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

        List<ParentLOI> lois = parent.getLois();
        for (ParentLOI loi : lois) {
            for (ChildLearningOpportunity childLO : loi.getChildren()) {
                SolrInputDocument childLODoc = new SolrInputDocument();
                childLODoc.addField("id", childLO.getName());
                childLODoc.addField("name", childLO.getName());
                for (I18nText i18n : childLO.getProfessionalTitles()) {
                    childLODoc.addField("professionalTitles", i18n.getTranslations().get("fi"));
                }
                childLODoc.addField("lopId", provider.getId());
                childLODoc.addField("lopName", provider.getName().getTranslations().get("fi"));
                childLODoc.addField("lopAddress", provider.getVisitingAddress().getPostOffice());
                childLODoc.addField("parentId", parent.getId());
                if (childLO.getApplicationSystemIds() != null) {
                    for (String asId : childLO.getApplicationSystemIds()) {
                        providerDoc.addField("asId", asId);
                    }
                }
                docs.add(childLODoc);
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
