package fi.vm.sade.koulutusinformaatio.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.service.IndexerService;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Hannu Lyytikainen
 */
@Service
public class IndexerServiceImpl implements IndexerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexerServiceImpl.class);

    private static final String FALLBACK_LANG = "fi";

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
//        <copyField source="goals" dest="text"/> - both
//        <copyField source="professionalTitles" dest="text"/> - child
//        <copyField source="lopName" dest="text"/> - both
//        <copyField source="lopDescription" dest="text"/> - both
//        <copyField source="lopAddress" dest="text"/> - both

        SolrInputDocument parentDoc = new SolrInputDocument();
        resolveParentDocument(parentDoc, parent);

        Provider provider = parent.getProvider();
        SolrInputDocument providerDoc = new SolrInputDocument();
        providerDoc.addField("id", provider.getId());
        providerDoc.addField("name", provider.getName().getTranslations().get("fi"));
        Set<String> providerAsIds = Sets.newHashSet();
        Set<String> requiredBaseEducations = Sets.newHashSet();

        for (ChildLOS childLOS : parent.getChildren()) {
            for (ChildLOI childLOI : childLOS.getLois()) {
                SolrInputDocument childLODoc = new SolrInputDocument();
                resolveChildDocument(childLODoc, childLOS, childLOI, parent);
                for (ApplicationOption ao : childLOI.getApplicationOptions()) {
                    providerAsIds.add(ao.getApplicationSystem().getId());
                    requiredBaseEducations.addAll(ao.getRequiredBaseEducations());
                }
                docs.add(childLODoc);
            }
        }

        docs.add(parentDoc);

        // check if provider exists and update base education and as id values
        SolrQuery query = new SolrQuery("id:" + provider.getId());
        QueryResponse response = lopUpdateHttpSolrServer.query(query);
        List<SolrDocument> results = response.getResults();
        if (results != null && results.size() > 0) {
            LOGGER.info("Provider already indexed: " + provider.getId());
            List<String> edus = (List<String>) results.get(0).get("requiredBaseEducations");
            if (edus != null) {
                LOGGER.info("Previously indexed base educations: " + edus.toString());
                LOGGER.info("New base educations: " + requiredBaseEducations.toString());
                requiredBaseEducations.addAll(edus);
                LOGGER.info("Combined base educations: " + requiredBaseEducations.toString());
            }
            List<String> asids = (List<String>) results.get(0).get("asIds");
            if (asids != null) {
                LOGGER.info("Previously indexed application systems: " + asids.toString());
                LOGGER.info("New application systems: " + providerAsIds.toString());
                providerAsIds.addAll(asids);
                LOGGER.info("Combined application systems: " + providerAsIds.toString());
            }
        }

        providerDoc.setField("asIds", providerAsIds);
        providerDoc.setField("requiredBaseEducations", requiredBaseEducations);

        providerDocs.add(providerDoc);
        lopUpdateHttpSolrServer.add(providerDocs);
        loUpdateHttpSolrServer.add(docs);
    }

    private void resolveParentDocument(SolrInputDocument doc, ParentLOS parent) {
        Provider provider = parent.getProvider();
        doc.addField("id", parent.getId());
        doc.addField("lopId", provider.getId());

        doc.setField("name", parent.getName().getTranslations().get("fi"));
        doc.addField("name_fi", parent.getName().getTranslations().get("fi"));
        doc.addField("name_sv", parent.getName().getTranslations().get("sv"));
        doc.addField("name_en", parent.getName().getTranslations().get("en"));

        doc.setField("lopName", provider.getName().getTranslations().get("fi"));
        doc.addField("lopName_fi", provider.getName().getTranslations().get("fi"));
        doc.addField("lopName_sv", provider.getName().getTranslations().get("sv"));
        doc.addField("lopName_en", provider.getName().getTranslations().get("en"));

        doc.addField("lopHomeplace", provider.getHomePlace().getTranslations().values());

        if (provider.getVisitingAddress() != null) {
            doc.addField("lopAddress_fi", provider.getVisitingAddress().getPostOffice());
        }
        if (provider.getDescription() != null) {
            doc.addField("lopDescription_fi", provider.getDescription().getTranslations().get("fi"));
            doc.addField("lopDescription_sv", provider.getDescription().getTranslations().get("sv"));
            doc.addField("lopDescription_en", provider.getDescription().getTranslations().get("en"));
        }
        if (parent.getGoals() != null) {
            doc.addField("goals_fi", parent.getGoals().getTranslations().get("fi"));
            doc.addField("goals_sv", parent.getGoals().getTranslations().get("sv"));
            doc.addField("goals_en", parent.getGoals().getTranslations().get("en"));
        }

        List<ApplicationOption> applicationOptions = Lists.newArrayList();
        for (ParentLOI parentLOI : parent.getLois()) {
            applicationOptions.addAll(parentLOI.getApplicationOptions());
            for (ApplicationOption ao : parentLOI.getApplicationOptions()) {
                if (ao.getApplicationSystem() != null) {
                    doc.addField("asName_fi", ao.getApplicationSystem().getName().getTranslations().get("fi"));
                    doc.addField("asName_sv", ao.getApplicationSystem().getName().getTranslations().get("sv"));
                    doc.addField("asName_en", ao.getApplicationSystem().getName().getTranslations().get("en"));
                    doc.addField("applicationSystems", ao.getApplicationSystem().getId());
                }
            }

        }
        addApplicationDates(doc, applicationOptions);

        Set<String> prerequisites = Sets.newHashSet();
        for (ChildLOS childLOS : parent.getChildren()) {
            for (ChildLOI childLOI : childLOS.getLois()) {
                prerequisites.add(childLOI.getPrerequisite().getValue());
            }
        }
        doc.setField("prerequisites", prerequisites);
    }

    private void resolveChildDocument(SolrInputDocument doc, ChildLOS childLOS, ChildLOI childLOI, ParentLOS parent) {
        Provider provider = parent.getProvider();
        doc.addField("id", childLOI.getId());
        doc.addField("losId", childLOS.getId());
        doc.addField("lopId", provider.getId());
        doc.addField("parentId", parent.getId());
        doc.addField("prerequisites", childLOI.getPrerequisite().getValue());

        doc.setField("prerequisite", resolveTranslationInTeachingLangUseFallback(
                childLOI.getTeachingLanguages(), childLOI.getPrerequisite().getName().getTranslations()));
        doc.addField("prerequisiteCode", childLOI.getPrerequisite().getValue());

        doc.setField("name", resolveTranslationInTeachingLangUseFallback(
                childLOI.getTeachingLanguages(), childLOS.getName().getTranslationsShortName()));
        doc.addField("name_fi", childLOS.getName().getTranslations().get("fi"));
        doc.addField("name_sv", childLOS.getName().getTranslations().get("sv"));
        doc.addField("name_en", childLOS.getName().getTranslations().get("en"));

        doc.setField("lopName", resolveTranslationInTeachingLangUseFallback(
                childLOI.getTeachingLanguages(), provider.getName().getTranslations()));
        doc.addField("lopName_fi", provider.getName().getTranslations().get("fi"));
        doc.addField("lopName_sv", provider.getName().getTranslations().get("sv"));
        doc.addField("lopName_en", provider.getName().getTranslations().get("en"));

        doc.addField("lopHomeplace", provider.getHomePlace().getTranslations().values());

        if (provider.getVisitingAddress() != null) {
            doc.addField("lopAddress_fi", provider.getVisitingAddress().getPostOffice());
        }
        if (provider.getDescription() != null) {
            doc.addField("lopDescription_fi", provider.getDescription().getTranslations().get("fi"));
            doc.addField("lopDescription_sv", provider.getDescription().getTranslations().get("sv"));
            doc.addField("lopDescription_en", provider.getDescription().getTranslations().get("en"));
        }
        if (childLOI.getProfessionalTitles() != null) {
            for (I18nText i18n : childLOI.getProfessionalTitles()) {
                doc.addField("professionalTitles_fi", i18n.getTranslations().get("fi"));
                doc.addField("professionalTitles_sv", i18n.getTranslations().get("sv"));
                doc.addField("professionalTitles_en", i18n.getTranslations().get("en"));
            }
        }
        if (childLOS.getQualification() != null) {
            doc.addField("qualification_fi", childLOS.getQualification().getTranslations().get("fi"));
            doc.addField("qualification_sv", childLOS.getQualification().getTranslations().get("sv"));
            doc.addField("qualification_en", childLOS.getQualification().getTranslations().get("en"));
        }
        if (childLOS.getGoals() != null) {
            doc.addField("goals_fi", childLOS.getGoals().getTranslations().get("fi"));
            doc.addField("goals_sv", childLOS.getGoals().getTranslations().get("sv"));
            doc.addField("goals_en", childLOS.getGoals().getTranslations().get("en"));
        }
        if (childLOI.getContent() != null) {
            doc.addField("content_fi", childLOI.getContent().getTranslations().get("fi"));
            doc.addField("content_sv", childLOI.getContent().getTranslations().get("sv"));
            doc.addField("content_en", childLOI.getContent().getTranslations().get("en"));
        }

        for (ApplicationOption ao : childLOI.getApplicationOptions()) {
            if (ao.getApplicationSystem() != null) {
                doc.addField("asName_fi", ao.getApplicationSystem().getName().getTranslations().get("fi"));
                doc.addField("asName_sv", ao.getApplicationSystem().getName().getTranslations().get("sv"));
                doc.addField("asName_en", ao.getApplicationSystem().getName().getTranslations().get("en"));
                doc.addField("applicationSystems", ao.getApplicationSystem().getId());
            }
        }

        addApplicationDates(doc, childLOI.getApplicationOptions());
    }

    private String resolveTranslationInTeachingLangUseFallback(List<Code> teachingLanguages, Map<String, String> translations) {
        String translation = null;
        for (Code teachingLanguage : teachingLanguages) {
            for (String key : translations.keySet()) {
                if (teachingLanguage.getValue().equalsIgnoreCase(key)) {
                    translation = translations.get(key);
                }
            }
        }
        if (translation == null) {
            translation = translations.get(FALLBACK_LANG);
        }
        if (translation == null) {
            translation = translations.values().iterator().next();
        }

        return translation;
    }

    private void addApplicationDates(SolrInputDocument doc, List<ApplicationOption> applicationOptions) {
        int parentApplicationDateRangeIndex = 0;
        for (ApplicationOption ao : applicationOptions) {
            if (ao.isSpecificApplicationDates()) {
                doc.addField(new StringBuilder().append("asStart").append("_").
                        append(String.valueOf(parentApplicationDateRangeIndex)).toString(), ao.getApplicationStartDate());
                doc.addField(new StringBuilder().append("asEnd").append("_").
                        append(String.valueOf(parentApplicationDateRangeIndex)).toString(), ao.getApplicationEndDate());
                parentApplicationDateRangeIndex++;
            } else {
                for (DateRange dr : ao.getApplicationSystem().getApplicationDates()) {
                    doc.addField(new StringBuilder().append("asStart").append("_").
                            append(String.valueOf(parentApplicationDateRangeIndex)).toString(), dr.getStartDate());
                    doc.addField(new StringBuilder().append("asEnd").append("_").
                            append(String.valueOf(parentApplicationDateRangeIndex)).toString(), dr.getEndDate());
                    parentApplicationDateRangeIndex++;
                }
            }
        }
    }

    @Override
    public void commitLOChanges() throws Exception {
        loUpdateHttpSolrServer.commit();
        lopUpdateHttpSolrServer.commit();
    }

}
