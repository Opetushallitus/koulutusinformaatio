package fi.vm.sade.koulutusinformaatio.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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
import java.util.Set;

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


//        <copyField source="name" dest="text"/>  - both
//        <copyField source="aoName" dest="text"/> - child
//        <copyField source="qualification" dest="text"/> - child
//        <copyField source="structure" dest="text"/> - parent
//        <copyField source="goals" dest="text"/> - both
//        <copyField source="professionalTitles" dest="text"/> - child
//        <copyField source="lopName" dest="text"/> - both
//        <copyField source="lopDescription" dest="text"/> - both
//        <copyField source="lopAddress" dest="text"/> - both


        SolrInputDocument parentDoc = new SolrInputDocument();
        resolveParentDocument(parentDoc, parent);
        addApplicationSystemDates(parentDoc, Lists.newArrayList(parent.getApplicationOptions()));

        Provider provider = parent.getProvider();
        SolrInputDocument providerDoc = new SolrInputDocument();
        providerDoc.addField("id", provider.getId());
        providerDoc.addField("name", provider.getName().getTranslations().get("fi"));
        Set<String> providerAsIds = Sets.newHashSet();

        List<ParentLOI> lois = parent.getLois();
        for (ParentLOI loi : lois) {
            if (loi.getPrerequisite() != null) {
                // null in parent 1.2.246.562.5.2013060313060064137085
                parentDoc.addField("prerequisites", loi.getPrerequisite().getValue());
            }
            for (ChildLearningOpportunity childLO : loi.getChildren()) {
                SolrInputDocument childLODoc = new SolrInputDocument();
                resolveChildDocument(childLODoc, childLO, parent);
                addApplicationSystemDates(childLODoc, childLO.getApplicationOptions());
                if (childLO.getApplicationSystemIds() != null) {
                    for (String asId : childLO.getApplicationSystemIds()) {
                        providerAsIds.add(asId);
                    }
                }
                docs.add(childLODoc);
            }
        }
        docs.add(parentDoc);

        for (String asId : providerAsIds) {
            providerDoc.addField("asId", asId);
        }

        providerDocs.add(providerDoc);
        lopUpdateHttpSolrServer.add(providerDocs);
        loUpdateHttpSolrServer.add(docs);
    }

    private void resolveParentDocument(SolrInputDocument doc, ParentLOS parent) {
        doc.addField("id", parent.getId());
        doc.addField("name", parent.getName().getTranslations().get("fi"));
        Provider provider = parent.getProvider();
        doc.addField("lopId", provider.getId());
        doc.addField("lopName", provider.getName().getTranslations().get("fi"));
        doc.addField("lopAddress", provider.getVisitingAddress().getPostOffice());
        doc.addField("lopCity", provider.getVisitingAddress().getPostOffice());

        try {
        doc.addField("lopDescription", provider.getDescription().getTranslations().get("fi"));
        doc.addField("structure", parent.getStructureDiagram().getTranslations().get("fi"));
        doc.addField("goals", parent.getGoals().getTranslations().get("fi"));
        }
        catch (NullPointerException npe) {
            // does not matter if this info is null
        }

    }

    private void resolveChildDocument(SolrInputDocument doc, ChildLearningOpportunity childLO, ParentLOS parent) {
        Provider provider = parent.getProvider();
        doc.addField("id", childLO.getId());
        doc.addField("name", childLO.getName().getTranslations().get("fi"));
        doc.addField("lopId", provider.getId());
        doc.addField("parentId", parent.getId());
        doc.addField("prerequisites", childLO.getPrerequisite().getValue());
        doc.addField("lopName", provider.getName().getTranslations().get("fi"));
        doc.addField("lopAddress", provider.getVisitingAddress().getPostOffice());
        doc.addField("lopCity", provider.getVisitingAddress().getPostOffice());

        try {
            doc.addField("lopDescription", provider.getDescription().getTranslations().get("fi"));
            for (I18nText i18n : childLO.getProfessionalTitles()) {
                doc.addField("professionalTitles", i18n.getTranslations().get("fi"));
            }
            doc.addField("qualification", childLO.getQualification().getTranslations().get("fi"));
            doc.addField("goals", childLO.getDegreeGoal().getTranslations().get("fi"));
        } catch (NullPointerException npe) {
            // does not matter if this info is null
        }
    }

    private void addApplicationSystemDates(SolrInputDocument doc, List<ApplicationOption> aos) {

        int parentApplicationDateRangeIndex = 0;
        for (ApplicationOption ao : aos) {
            for (DateRange dr : ao.getApplicationSystem().getApplicationDates()) {
                doc.addField(new StringBuilder().append("asStart").append("_").
                        append(String.valueOf(parentApplicationDateRangeIndex)).toString(), dr.getStartDate());
                doc.addField(new StringBuilder().append("asEnd").append("_").
                        append(String.valueOf(parentApplicationDateRangeIndex)).toString(), dr.getEndDate());
                parentApplicationDateRangeIndex++;
            }
        }

    }

    @Override
    public void commitLOChanges() throws Exception {
        loUpdateHttpSolrServer.commit();
        lopUpdateHttpSolrServer.commit();
    }

}
