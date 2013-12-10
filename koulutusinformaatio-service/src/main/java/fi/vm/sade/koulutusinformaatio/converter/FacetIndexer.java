package fi.vm.sade.koulutusinformaatio.converter;

import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.SolrFields.LearningOpportunity;
import org.apache.solr.common.SolrInputDocument;

import java.util.List;
import java.util.Map;

public class FacetIndexer {
    
    private static final String FALLBACK_LANG = "fi";
    private static final String TYPE_FACET = "FASETTI";
    
    
    
    public List<SolrInputDocument> createFacetDocs(UpperSecondaryLOI loi) {
        List<SolrInputDocument> docs = Lists.newArrayList();
        
        //Teaching languages
        this.indexCodeAsFacetDoc(loi.getTeachingLanguages().get(0), docs);
        //Prerequisites
        this.indexCodeAsFacetDoc(loi.getPrerequisite(), docs);
        
        return docs;
    }
    
    /*
     * Creates the solr docs needed in facet search.
     */
    public List<SolrInputDocument> createFacetsDocs(
            ParentLOS parent) {
        List<SolrInputDocument> docs = Lists.newArrayList();
        for (ChildLOS childLOS : parent.getChildren()) {
            for (ChildLOI childLOI : childLOS.getLois()) {
                docs.addAll(createFacetDocs(childLOI));
            }
        }
        return docs;
    }

    /*
     * Creates the solr docs needed in facet search.
     */
    public List<SolrInputDocument> createFacetDocs(ChildLOI childLOI) {
        List<SolrInputDocument> docs = Lists.newArrayList();
        this.indexCodeAsFacetDoc(childLOI.getTeachingLanguages().get(0), docs);
        this.indexCodeAsFacetDoc(childLOI.getPrerequisite(), docs);
        return docs;
    }


    private String getTranslationUseFallback(String lang, Map<String, String> translations) {
        String translation = null;
        translation = translations.get(lang);
        if (translation == null) {
            translation = translations.get(FALLBACK_LANG);
        }
        if (translation == null) {
            translation = translations.values().iterator().next();
        }

        return translation;
    } 
    
    /*
     * Creates a facet document for the given code, and adds to the list of docs given.
     */
    private void indexCodeAsFacetDoc(Code code, List<SolrInputDocument> docs) {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(LearningOpportunity.ID, code.getValue());
        doc.addField(LearningOpportunity.TYPE, TYPE_FACET);
        doc.addField(LearningOpportunity.FI_FNAME, this.getTranslationUseFallback("fi", code.getName().getTranslations()));
        doc.addField(LearningOpportunity.SV_FNAME, this.getTranslationUseFallback("sv", code.getName().getTranslations()));
        doc.addField(LearningOpportunity.EN_FNAME, this.getTranslationUseFallback("en", code.getName().getTranslations())); 
        docs.add(doc);
    }
}
