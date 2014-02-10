package fi.vm.sade.koulutusinformaatio.converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.springframework.core.convert.converter.Converter;

import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.SolrFields.LearningOpportunity;
import fi.vm.sade.koulutusinformaatio.domain.SolrFields.SolrConstants;

public class HigherEducationLOSToSolrInputDocment implements Converter<HigherEducationLOS, List<SolrInputDocument>> {

	@Override
	public List<SolrInputDocument> convert(HigherEducationLOS los) {
		List<SolrInputDocument> docs = Lists.newArrayList();
        FacetIndexer fIndexer = new FacetIndexer();
        
        docs.add(createDoc(los));
        docs.addAll(fIndexer.createFacetDocs(los));

        return docs;
	}

	private SolrInputDocument createDoc(HigherEducationLOS los) {

		SolrInputDocument doc = new SolrInputDocument();

		doc.addField(LearningOpportunity.TYPE, los.getType());
		Provider provider = los.getProvider();
		doc.addField(LearningOpportunity.ID, los.getId());
		doc.addField(LearningOpportunity.LOS_ID, los.getId());
		doc.addField(LearningOpportunity.LOP_ID, provider.getId());
		if (los.getPrerequisites() != null && !los.getPrerequisites().isEmpty()) {
			for (Code curPrereq : los.getPrerequisites()) {
				doc.addField(LearningOpportunity.PREREQUISITES, curPrereq.getValue());
			}
		}

		if (los.getCreditValue() != null) {
			doc.addField(LearningOpportunity.CREDITS, String.format("%s %s", los.getCreditValue(), SolrUtil.resolveTranslationInTeachingLangUseFallback(los.getTeachingLanguages(),
                    los.getCreditUnit().getTranslations())));
		}

		String teachingLang = los.getTeachingLanguages().isEmpty() ? "EXC" : los.getTeachingLanguages().get(0).getValue().toLowerCase();

		String losName = SolrUtil.resolveTranslationInTeachingLangUseFallback(
				los.getTeachingLanguages(), los.getName().getTranslationsShortName());


		doc.setField(LearningOpportunity.NAME, losName);

		doc.addField(LearningOpportunity.EDUCATION_DEGREE, SolrUtil.resolveTextWithFallback(teachingLang,  los.getEducationDegreeLang().getTranslations()));
		
		doc.addField(LearningOpportunity.EDUCATION_DEGREE_CODE, los.getEducationDegree());
		
		//SolrUtil.resolveTextWithFallback("sv",provider.getName().getTranslations()));
		if (teachingLang.equals("fi")) {
			doc.addField(LearningOpportunity.NAME_FI, SolrUtil.resolveTextWithFallback("fi", los.getName().getTranslations()));
		} else if (teachingLang.equals("sv")) {
			doc.addField(LearningOpportunity.NAME_SV, SolrUtil.resolveTextWithFallback("sv", los.getName().getTranslations()));
		} else if (teachingLang.equals("en")) {
			doc.addField(LearningOpportunity.NAME_EN, SolrUtil.resolveTextWithFallback("en", los.getName().getTranslations()));
		} else {
			doc.addField(LearningOpportunity.NAME_FI, losName);
		}

		doc.setField(LearningOpportunity.LOP_NAME, SolrUtil.resolveTranslationInTeachingLangUseFallback(
				los.getTeachingLanguages(), provider.getName().getTranslations()));


		if (teachingLang.equals("sv")) {
			doc.addField(LearningOpportunity.LOP_NAME_SV, SolrUtil.resolveTextWithFallback("sv", provider.getName().getTranslations()));
		} else if (teachingLang.equals("en")) {
			doc.addField(LearningOpportunity.LOP_NAME_EN, SolrUtil.resolveTextWithFallback("en", provider.getName().getTranslations()));
		} else {
			doc.addField(LearningOpportunity.LOP_NAME_FI, SolrUtil.resolveTextWithFallback("fi", provider.getName().getTranslations()));
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
			doc.addField(LearningOpportunity.LOP_DESCRIPTION_FI, SolrUtil.resolveTextWithFallback("fi",  provider.getDescription().getTranslations()));
			doc.addField(LearningOpportunity.LOP_DESCRIPTION_SV, SolrUtil.resolveTextWithFallback("sv",  provider.getDescription().getTranslations()));
			doc.addField(LearningOpportunity.LOP_DESCRIPTION_EN, SolrUtil.resolveTextWithFallback("en",  provider.getDescription().getTranslations()));
		}
		if (los.getQualification() != null) {

			if (teachingLang.equals("sv")) {
				doc.addField(LearningOpportunity.QUALIFICATION_SV, SolrUtil.resolveTextWithFallback("sv",  los.getQualification().getTranslations()));
			} else if (teachingLang.equals("en")) {
				doc.addField(LearningOpportunity.QUALIFICATION_EN, SolrUtil.resolveTextWithFallback("en",  los.getQualification().getTranslations()));
			} else {
				doc.addField(LearningOpportunity.QUALIFICATION_FI, SolrUtil.resolveTextWithFallback("fi",  los.getQualification().getTranslations()));
			}

		}
		if (los.getGoals() != null) {

			if (teachingLang.equals("sv")) {
				doc.addField(LearningOpportunity.GOALS_SV, SolrUtil.resolveTextWithFallback("sv",  los.getGoals().getTranslations()));
			} else if (teachingLang.equals("en")) {
				doc.addField(LearningOpportunity.GOALS_EN, SolrUtil.resolveTextWithFallback("en",  los.getGoals().getTranslations()));
			} else {
				doc.addField(LearningOpportunity.GOALS_FI, SolrUtil.resolveTextWithFallback("fi",  los.getGoals().getTranslations()));
			}

		}
		if (los.getContent() != null) {

			if (teachingLang.equals("sv")) {
				doc.addField(LearningOpportunity.CONTENT_SV, SolrUtil.resolveTextWithFallback("sv",  los.getContent().getTranslations()));
			} else if (teachingLang.endsWith("en")) {
				doc.addField(LearningOpportunity.CONTENT_EN, SolrUtil.resolveTextWithFallback("en",  los.getContent().getTranslations()));
			} else {
				doc.addField(LearningOpportunity.CONTENT_FI, SolrUtil.resolveTextWithFallback("fi",  los.getContent().getTranslations()));
			}
		}

		if (los.getApplicationOptions() != null) {
			for (ApplicationOption ao : los.getApplicationOptions()) {
				if (ao.getApplicationSystem() != null) {
					doc.addField(LearningOpportunity.AS_NAME_FI, SolrUtil.resolveTextWithFallback("fi",  ao.getApplicationSystem().getName().getTranslations()));
					doc.addField(LearningOpportunity.AS_NAME_SV, SolrUtil.resolveTextWithFallback("sv",  ao.getApplicationSystem().getName().getTranslations()));
					doc.addField(LearningOpportunity.AS_NAME_EN, SolrUtil.resolveTextWithFallback("en",  ao.getApplicationSystem().getName().getTranslations()));
				}
			}

			SolrUtil.addApplicationDates(doc, los.getApplicationOptions());
		}
		//Fields for sorting
		doc.addField(LearningOpportunity.START_DATE_SORT, los.getStartDate());
		//indexDurationField(loi, doc);
		doc.addField(LearningOpportunity.NAME_SORT, String.format("%s, %s",
				SolrUtil.resolveTranslationInTeachingLangUseFallback(los.getTeachingLanguages(), provider.getName().getTranslations()),
				SolrUtil.resolveTranslationInTeachingLangUseFallback(los.getTeachingLanguages(), los.getName().getTranslationsShortName())));


		//For faceting
		indexFacetFields(doc, los);

		return doc;
	}

	private void indexFacetFields(SolrInputDocument doc,
			HigherEducationLOS los) {
		doc.addField(LearningOpportunity.TEACHING_LANGUAGE, los.getTeachingLanguages().get(0).getValue());
        doc.addField(LearningOpportunity.EDUCATION_TYPE, SolrConstants.ED_TYPE_AMK);

        for (Code curTopic : los.getTopics()) {
            doc.addField(LearningOpportunity.TOPIC, curTopic.getUri());
        }
        
        for (Code curTopic : los.getThemes()) {
            doc.addField(LearningOpportunity.THEME, curTopic.getUri());
        }
		
	}
	
}
