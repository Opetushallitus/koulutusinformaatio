package fi.vm.sade.koulutusinformaatio.converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.springframework.core.convert.converter.Converter;

import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.UniversityAppliedScienceLOS;
import fi.vm.sade.koulutusinformaatio.domain.SolrFields.LearningOpportunity;

public class UasLOSToSolrInputDocment implements Converter<UniversityAppliedScienceLOS, List<SolrInputDocument>> {

	@Override
	public List<SolrInputDocument> convert(UniversityAppliedScienceLOS los) {
		System.out.println("In converter");
		List<SolrInputDocument> docs = Lists.newArrayList();
        FacetIndexer fIndexer = new FacetIndexer();

        /*for (UpperSecondaryLOI loi : los.getLois()) {
            docs.add(createDoc(los, loi));
            docs.addAll(fIndexer.createFacetDocs(loi, los));
        }
        
        docs*/
        
        docs.add(createDoc(los));

        return docs;
	}

	private SolrInputDocument createDoc(UniversityAppliedScienceLOS los) {
		
		System.out.println("Converting now!!!!");
		
		SolrInputDocument doc = new SolrInputDocument();
		
		doc.addField(LearningOpportunity.TYPE, los.getType());
        Provider provider = los.getProvider();
        doc.addField(LearningOpportunity.ID, los.getId());
        doc.addField(LearningOpportunity.LOS_ID, los.getId());
        doc.addField(LearningOpportunity.LOP_ID, provider.getId());
        /*doc.addField(LearningOpportunity.PREREQUISITES, SolrConstants.SPECIAL_EDUCATION.equalsIgnoreCase(loi.getPrerequisite().getValue()) 
                                        ? SolrConstants.PK : loi.getPrerequisite().getValue());

        doc.setField(LearningOpportunity.PREREQUISITE, SolrUtil.resolveTranslationInTeachingLangUseFallback(
                loi.getTeachingLanguages(), loi.getPrerequisite().getName().getTranslations()));
        doc.addField(LearningOpportunity.PREREQUISITE_CODE, loi.getPrerequisite().getValue());*/

        if (los.getCreditValue() != null) {
            doc.addField(LearningOpportunity.CREDITS, String.format("%s %s", los.getCreditValue(), ""));
                    /*SolrUtil.resolveTranslationInTeachingLangUseFallback(los.getTeachingLanguages(),
                           los.getCreditUnit().getTranslationsShortName())));*/
        }
        
        String teachingLang = los.getTeachingLanguages().isEmpty() ? "EXC" : los.getTeachingLanguages().get(0).getValue().toLowerCase();
        
        String losName = SolrUtil.resolveTranslationInTeachingLangUseFallback(
                los.getTeachingLanguages(), los.getName().getTranslationsShortName());
        

        doc.setField(LearningOpportunity.NAME, losName);
        
        
        if (teachingLang.equals("fi")) {
            doc.addField(LearningOpportunity.NAME_FI, los.getName().getTranslations().get("fi"));
        } else if (teachingLang.equals("sv")) {
            doc.addField(LearningOpportunity.NAME_SV, los.getName().getTranslations().get("sv"));
        } else if (teachingLang.equals("en")) {
            doc.addField(LearningOpportunity.NAME_EN, los.getName().getTranslations().get("en"));
        } else {
            doc.addField(LearningOpportunity.NAME_FI, losName);
        }

        doc.setField(LearningOpportunity.LOP_NAME, SolrUtil.resolveTranslationInTeachingLangUseFallback(
                los.getTeachingLanguages(), provider.getName().getTranslations()));
        
        
        if (teachingLang.equals("sv")) {
            doc.addField(LearningOpportunity.LOP_NAME_SV, provider.getName().getTranslations().get("sv"));
        } else if (teachingLang.equals("en")) {
            doc.addField(LearningOpportunity.LOP_NAME_EN, provider.getName().getTranslations().get("en"));
        } else {
            doc.addField(LearningOpportunity.LOP_NAME_FI, provider.getName().getTranslations().get("fi"));
        }
        
        if (provider.getHomeDistrict() != null) {
            
            List<String> locVals = new ArrayList<String>();
            locVals.addAll(provider.getHomeDistrict().getTranslations().values());
            locVals.addAll(provider.getHomePlace().getTranslations().values());
            doc.addField(LearningOpportunity.LOP_HOMEPLACE, locVals);
        } else {
            doc.addField(LearningOpportunity.LOP_HOMEPLACE, provider.getHomePlace().getTranslations().values());
        }

        if (provider.getVisitingAddress() != null) {
            doc.addField(LearningOpportunity.LOP_ADDRESS_FI, provider.getVisitingAddress().getPostOffice());
        }
        if (provider.getDescription() != null) {
            doc.addField(LearningOpportunity.LOP_DESCRIPTION_FI, provider.getDescription().getTranslations().get("fi"));
            doc.addField(LearningOpportunity.LOP_DESCRIPTION_SV, provider.getDescription().getTranslations().get("sv"));
            doc.addField(LearningOpportunity.LOP_DESCRIPTION_EN, provider.getDescription().getTranslations().get("en"));
        }
        if (los.getQualification() != null) {
            
            if (teachingLang.equals("sv")) {
                doc.addField(LearningOpportunity.QUALIFICATION_SV, los.getQualification().getTranslations().get("sv"));
            } else if (teachingLang.equals("en")) {
                doc.addField(LearningOpportunity.QUALIFICATION_EN, los.getQualification().getTranslations().get("en"));
            } else {
                doc.addField(LearningOpportunity.QUALIFICATION_FI, los.getQualification().getTranslations().get("fi"));
            }
            
        }
        if (los.getGoals() != null) {
            
            if (teachingLang.equals("sv")) {
                doc.addField(LearningOpportunity.GOALS_SV, los.getGoals().getTranslations().get("sv"));
            } else if (teachingLang.equals("en")) {
                doc.addField(LearningOpportunity.GOALS_EN, los.getGoals().getTranslations().get("en"));
            } else {
                doc.addField(LearningOpportunity.GOALS_FI, los.getGoals().getTranslations().get("fi"));
            }
            
        }
        if (los.getContent() != null) {
            
            if (teachingLang.equals("sv")) {
                doc.addField(LearningOpportunity.CONTENT_SV, los.getContent().getTranslations().get("sv"));
            } else if (teachingLang.endsWith("en")) {
                doc.addField(LearningOpportunity.CONTENT_EN, los.getContent().getTranslations().get("en"));
            } else {
                doc.addField(LearningOpportunity.CONTENT_FI, los.getContent().getTranslations().get("fi"));
            }
        }

        for (ApplicationOption ao : los.getApplicationOptions()) {
            if (ao.getApplicationSystem() != null) {
                doc.addField(LearningOpportunity.AS_NAME_FI, ao.getApplicationSystem().getName().getTranslations().get("fi"));
                doc.addField(LearningOpportunity.AS_NAME_SV, ao.getApplicationSystem().getName().getTranslations().get("sv"));
                doc.addField(LearningOpportunity.AS_NAME_EN, ao.getApplicationSystem().getName().getTranslations().get("en"));
            }
        }

        SolrUtil.addApplicationDates(doc, los.getApplicationOptions());
        
        //Fields for sorting
        doc.addField(LearningOpportunity.START_DATE_SORT, los.getStartDate());
        //indexDurationField(loi, doc);
        doc.addField(LearningOpportunity.NAME_SORT, String.format("%s, %s",
                SolrUtil.resolveTranslationInTeachingLangUseFallback(los.getTeachingLanguages(), provider.getName().getTranslations()),
                SolrUtil.resolveTranslationInTeachingLangUseFallback(los.getTeachingLanguages(), los.getName().getTranslationsShortName())));
        
        
        //For faceting
        //indexFacetFields(doc, los, loi);
		
		return doc;
	}
	
}
