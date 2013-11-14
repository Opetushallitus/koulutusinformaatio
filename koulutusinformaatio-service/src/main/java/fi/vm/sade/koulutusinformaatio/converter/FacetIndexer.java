package fi.vm.sade.koulutusinformaatio.converter;

import java.util.List;
import java.util.Map;

import org.apache.solr.common.SolrInputDocument;

import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.domain.ChildLOI;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOS;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOI;
import fi.vm.sade.koulutusinformaatio.domain.SolrFields.LearningOpportunity;

public class FacetIndexer {
    
    private static final String FALLBACK_LANG = "fi";
    private static final String TYPE_FACET = "FASETTI";
    
    
    
    public List<SolrInputDocument> createFacetDocs(UpperSecondaryLOI loi) {
        List<SolrInputDocument> docs = Lists.newArrayList();
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(LearningOpportunity.ID, loi.getTeachingLanguages().get(0).getValue());
        doc.addField(LearningOpportunity.TYPE, TYPE_FACET);
        doc.addField(LearningOpportunity.FI_FNAME, this.getTranslationUseFallback("fi", loi.getTeachingLanguages().get(0).getName().getTranslations()));
        doc.addField(LearningOpportunity.SV_FNAME, this.getTranslationUseFallback("sv", loi.getTeachingLanguages().get(0).getName().getTranslations()));
        doc.addField(LearningOpportunity.EN_FNAME, this.getTranslationUseFallback("en", loi.getTeachingLanguages().get(0).getName().getTranslations()));    
        docs.add(doc);
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
                docs.add(indexTeachingLangFacetDoc(childLOI));
            }
        }
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
     * Creates an solr document for teaching lang facet.
     */
    private SolrInputDocument indexTeachingLangFacetDoc(ChildLOI childLOI) {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(LearningOpportunity.TYPE, TYPE_FACET);
        doc.addField(LearningOpportunity.ID, childLOI.getTeachingLanguages().get(0).getValue());
        doc.addField(LearningOpportunity.FI_FNAME, this.getTranslationUseFallback("fi", childLOI.getTeachingLanguages().get(0).getName().getTranslations()));
        doc.addField(LearningOpportunity.SV_FNAME, this.getTranslationUseFallback("sv", childLOI.getTeachingLanguages().get(0).getName().getTranslations()));
        doc.addField(LearningOpportunity.EN_FNAME, this.getTranslationUseFallback("en", childLOI.getTeachingLanguages().get(0).getName().getTranslations()));
        return doc;
    }
}
