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
            for (ChildLOS childLO : loi.getChildren()) {
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
        Provider provider = parent.getProvider();
        doc.addField("id", parent.getId());
        doc.addField("lopId", provider.getId());

        doc.addField("name_fi", parent.getName().getTranslations().get("fi"));
        doc.addField("name_sv", parent.getName().getTranslations().get("sv"));
        doc.addField("name_en", parent.getName().getTranslations().get("en"));

        doc.addField("lopName_fi", provider.getName().getTranslations().get("fi"));
        doc.addField("lopName_sv", provider.getName().getTranslations().get("sv"));
        doc.addField("lopName_en", provider.getName().getTranslations().get("en"));

        if (provider.getVisitingAddress() != null) {
            doc.addField("lopAddress_fi", provider.getVisitingAddress().getPostOffice());
            doc.addField("lopCity", provider.getVisitingAddress().getPostOffice());
        }
        if (provider.getDescription() != null) {
            doc.addField("lopDescription_fi", provider.getDescription().getTranslations().get("fi"));
            doc.addField("lopDescription_sv", provider.getDescription().getTranslations().get("sv"));
            doc.addField("lopDescription_en", provider.getDescription().getTranslations().get("en"));
        }
        if (parent.getStructureDiagram() != null) {
            doc.addField("structure_fi", parent.getStructureDiagram().getTranslations().get("fi"));
            doc.addField("structure_sv", parent.getStructureDiagram().getTranslations().get("sv"));
            doc.addField("structure_en", parent.getStructureDiagram().getTranslations().get("en"));
        }
        if (parent.getGoals() != null) {
            doc.addField("goals_fi", parent.getGoals().getTranslations().get("fi"));
            doc.addField("goals_sv", parent.getGoals().getTranslations().get("sv"));
            doc.addField("goals_en", parent.getGoals().getTranslations().get("en"));
        }
    }

    private void resolveChildDocument(SolrInputDocument doc, ChildLOS childLO, ParentLOS parent) {
        Provider provider = parent.getProvider();
        doc.addField("id", childLO.getId());
        doc.addField("lopId", provider.getId());
        doc.addField("parentId", parent.getId());
        doc.addField("prerequisites", childLO.getPrerequisite().getValue());

        doc.addField("name_fi", childLO.getName().getTranslations().get("fi"));
        doc.addField("name_sv", childLO.getName().getTranslations().get("sv"));
        doc.addField("name_en", childLO.getName().getTranslations().get("en"));

        doc.addField("lopName_fi", provider.getName().getTranslations().get("fi"));
        doc.addField("lopName_sv", provider.getName().getTranslations().get("sv"));
        doc.addField("lopName_en", provider.getName().getTranslations().get("fi"));

        if (provider.getVisitingAddress() != null) {
            doc.addField("lopAddress_fi", provider.getVisitingAddress().getPostOffice());
            doc.addField("lopCity", provider.getVisitingAddress().getPostOffice());
        }
        if (provider.getDescription() != null) {
            doc.addField("lopDescription_fi", provider.getDescription().getTranslations().get("fi"));
            doc.addField("lopDescription_sv", provider.getDescription().getTranslations().get("sv"));
            doc.addField("lopDescription_en", provider.getDescription().getTranslations().get("en"));
        }
        if (childLO.getProfessionalTitles() != null) {
            for (I18nText i18n : childLO.getProfessionalTitles()) {
                doc.addField("professionalTitles_fi", i18n.getTranslations().get("fi"));
                doc.addField("professionalTitles_sv", i18n.getTranslations().get("sv"));
                doc.addField("professionalTitles_en", i18n.getTranslations().get("en"));
            }
        }
        if (childLO.getQualification() != null) {
            doc.addField("qualification_fi", childLO.getQualification().getTranslations().get("fi"));
            doc.addField("qualification_sv", childLO.getQualification().getTranslations().get("sv"));
            doc.addField("qualification_en", childLO.getQualification().getTranslations().get("en"));
        }
        if (childLO.getDegreeGoal() != null) {
            doc.addField("goals_fi", childLO.getDegreeGoal().getTranslations().get("fi"));
            doc.addField("goals_sv", childLO.getDegreeGoal().getTranslations().get("sv"));
            doc.addField("goals_en", childLO.getDegreeGoal().getTranslations().get("en"));
        }
        if (childLO.getContent() != null) {
            doc.addField("content_fi", childLO.getContent().getTranslations().get("fi"));
            doc.addField("content_sv", childLO.getContent().getTranslations().get("sv"));
            doc.addField("content_en", childLO.getContent().getTranslations().get("en"));
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
