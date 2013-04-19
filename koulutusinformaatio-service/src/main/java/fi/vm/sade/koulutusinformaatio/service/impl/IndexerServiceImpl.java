package fi.vm.sade.koulutusinformaatio.service.impl;

import fi.vm.sade.koulutusinformaatio.client.SolrClient;
import fi.vm.sade.koulutusinformaatio.client.TarjontaClient;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.service.IndexerService;
import fi.vm.sade.tarjonta.publication.types.*;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
@Service
public class IndexerServiceImpl implements IndexerService {

    public static final Logger LOGGER = LoggerFactory.getLogger(IndexerServiceImpl.class);

    private final HttpSolrServer httpSolrServer;
    // solr client for learning opportunity index
    private final HttpSolrServer loHttpSolrServer;
    // solr client for learning opportunity provider index
    private final HttpSolrServer lopHttpSolrServer;

    private final TarjontaClient tarjontaClient;

    private final SolrClient client;


    /*@Autowired
    EventListener listener;*/

    @Autowired
    public IndexerServiceImpl(@Qualifier("HttpSolrServer") HttpSolrServer httpSolrServer,
                              @Qualifier("loHttpSolrServer") HttpSolrServer loHttpSolrServer,
                              @Qualifier("lopHttpSolrServer") HttpSolrServer lopHttpSolrServer,
                              TarjontaClient tarjontaClient, SolrClient client) {
        this.httpSolrServer = httpSolrServer;
        this.loHttpSolrServer = loHttpSolrServer;
        this.lopHttpSolrServer = lopHttpSolrServer;
        this.tarjontaClient = tarjontaClient;
        this.client = client;
    }

    @Override
    public void updateIndexes(LearningOpportunityData data) throws IOException, SolrServerException {
        Collection<SolrInputDocument> loDocuments =
                resolveLODocuments(data.getParentLearningOpportinities());
        loHttpSolrServer.add(loDocuments);
        loHttpSolrServer.commit();
        loHttpSolrServer.optimize();

        Collection<SolrInputDocument> lopDocuments = resolveLOPDocuments(data.getProviders());
        lopHttpSolrServer.add(lopDocuments);
        lopHttpSolrServer.commit();
        lopHttpSolrServer.optimize();

    }

    public Collection<SolrInputDocument> resolveLODocuments(List<ParentLearningOpportunity> parentLosList) {
        Collection<SolrInputDocument> solrDocuments = new ArrayList<SolrInputDocument>();

        for (ParentLearningOpportunity parentLos : parentLosList) {
            SolrInputDocument parentDocument = new SolrInputDocument();
            parentDocument.addField("id", parentLos.getId());
            parentDocument.addField("name", parentLos.getName());
            parentDocument.addField("lopId", parentLos.getProvider().getId());
            parentDocument.addField("lopName", parentLos.getProvider().getName());
            solrDocuments.add(parentDocument);

            for (ChildLearningOpportunity childLos : parentLos.getChildren()) {
                SolrInputDocument childDocument = new SolrInputDocument();
                childDocument.addField("id", childLos.getId());
                childDocument.addField("name", childLos.getName());
                childDocument.addField("lopId", parentLos.getProvider().getId());
                childDocument.addField("lopName", parentLos.getProvider().getName());
                childDocument.addField("parentId", parentLos.getId());
                solrDocuments.add(childDocument);
            }
        }
        return solrDocuments;
    }

    public Collection<SolrInputDocument> resolveLOPDocuments(List<LearningOpportunityProvider> lops) {
        Collection<SolrInputDocument> solrDocuments = new ArrayList<SolrInputDocument>();

        for (LearningOpportunityProvider lop : lops) {
            SolrInputDocument document = new SolrInputDocument();
            document.addField("id", lop.getId());
            document.addField("name", lop.getName());
            for (String asId : lop.getApplicationSystemIDs()) {
                document.addField("asId", asId);
            }
            solrDocuments.add(document);
        }
        return solrDocuments;
    }


    @Override
    public String update() {

        final Source source = tarjontaClient.retrieveTarjontaAsSource();

        try {
            Collection<SolrInputDocument> documents = parseDocuments(source);
            httpSolrServer.add(documents);
            httpSolrServer.commit();
            httpSolrServer.optimize();
        } catch (Exception e) {
            LOGGER.error("Indeksin päivitys epäonnistui ", e);
            return "error";
        }
        return "index update ok";

            //final ByteArrayOutputStream result = transform(source);
            //return client.update(result);
    }



    @Override
    public boolean drop() {
        boolean dropped = false;
        try {
            httpSolrServer.deleteByQuery("*:*");
            httpSolrServer.commit();
            httpSolrServer.optimize();
            dropped = true;
        } catch (Exception e) {
            LOGGER.error("drop failed", e);
        }
        return dropped;
    }

    public Collection<SolrInputDocument> parseDocuments(final Source source) throws JAXBException, MalformedURLException {
        Unmarshaller unmashaller = JAXBContext.newInstance(LearningOpportunityDownloadDataType.class.getPackage().getName()).createUnmarshaller();
        LearningOpportunityDownloadDataType learningOpportunityDownloadDataType = (LearningOpportunityDownloadDataType)unmashaller.unmarshal(source);;
        Collection<SolrInputDocument> solrDocuments = new ArrayList<SolrInputDocument>();

        // application options
        for (ApplicationOptionType applicationOptionType : learningOpportunityDownloadDataType.getApplicationOption()) {
            SolrInputDocument solrDocument = new SolrInputDocument();

            addApplicationOption(solrDocument, applicationOptionType);

            String applicationSystemRef = applicationOptionType.getApplicationSystemRef().getOidRef();
            // application system
            for (ApplicationSystemType applicationSystemType : learningOpportunityDownloadDataType.getApplicationSystem()) {
                if (applicationSystemType.getIdentifier().equals(applicationSystemRef)) {
                    addApplicationSystemType(solrDocument, applicationSystemType);
                }
                break;
            }

            // learning opportunity
            ApplicationOptionType.LearningOpportunities learningOpportunities = applicationOptionType.getLearningOpportunities();
            LearningOpportunityInstanceRefType loi = learningOpportunities.getInstanceRef().get(0);
            LearningOpportunityInstanceType ref = (LearningOpportunityInstanceType) loi.getRef();
            String organizationRef = ref.getOrganizationRef().getOidRef();

            for (LearningOpportunityProviderType providerType : learningOpportunityDownloadDataType.getLearningOpportunityProvider()) {
                if (providerType.getOrganizationRef().getOidRef().equals(organizationRef)) {
                    addLearningOpportunityProviderType(solrDocument, providerType);
                    break;
                }
            }

            solrDocument.addField("LOIPrerequisite", ref.getPrerequisite().getCode().getValue().trim());
            
            //addLearningOpportunitySpecification(solrDocument, ref1);
            //addLearningOpportunityInstance(solrDocument, ref);


            // tmp dev fields
//            solrDocument.addField("tmpAOApplyAdditionalInfo", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam tortor nisi, egestas id pellentesque ac, scelerisque in tortor. Morbi accumsan libero erat. Quisque nisl erat, fringilla quis ullamcorper vel, viverra eu leo. Nulla facilisi. Fusce a leo id tellus molestie imperdiet vel ut augue. Suspendisse interdum malesuada iaculis. Sed et urna ante, id varius ipsum. Fusce imperdiet sapien convallis purus mattis euismod. Quisque et metus sit amet nulla pharetra consequat at vel tellus. Proin vulputate eros at quam rutrum id dignissim magna dictum. ");
//            solrDocument.addField("tmpLOSEducationField", "Opintojesi aikana erikoistut joko markkinointiin, laskentaan ja rahoitukseen tai työyhteisön kehittämiseen.");
//            Calendar cal = GregorianCalendar.getInstance();
//            solrDocument.addField("tmpASStart", cal.getTime());
//            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH + 1));
//            solrDocument.addField("tmpASEnd", cal.getTime());
//            solrDocument.addField("tmpAOLastYearQualified", 45);
//            solrDocument.addField("tmpHakuId", "test");
//            solrDocument.addField("tmpLomakeId", "yhteishaku");

            solrDocuments.add(solrDocument);
        }

        return solrDocuments;
    }

    private void addApplicationSystemType(SolrInputDocument solrDocument, ApplicationSystemType applicationSystemType) {
        if (applicationSystemType != null) {
            solrDocument.addField("ASName", getValueOfExtendedString(applicationSystemType.getName()));
            solrDocument.addField("ASId", applicationSystemType.getIdentifier());
        }
    }


    private void addLearningOpportunityProviderType(SolrInputDocument solrDocument, LearningOpportunityProviderType learningOpportunityProviderType) {
        if (learningOpportunityProviderType != null) {
            LearningOpportunityProviderType learningOpportunityProvider = (LearningOpportunityProviderType) learningOpportunityProviderType;
            solrDocument.addField("LOPId", learningOpportunityProvider.getOrganizationRef().getOidRef());

            LearningOpportunityProviderType.InstitutionInfo institutionInfo = learningOpportunityProvider.getInstitutionInfo();
            //opetuspiste
            solrDocument.addField("LOPInstitutionInfoName", getValueOfExtendedString(institutionInfo.getName()));
            //solrDocument.addField("LOPInstitutionInfoGeneralDescription", getValueOfTextType(institutionInfo.getGeneralDescription()));
        }
    }

    private void addApplicationOption(SolrInputDocument solrDocument, ApplicationOptionType applicationOptionType) {
        solrDocument.addField("AOId", applicationOptionType.getIdentifier().getValue());
        solrDocument.addField("AOTitle", getValueOfExtendedString(applicationOptionType.getTitle().getLabel()));

        if (applicationOptionType.getLearningOpportunities().getParentRef() != null) {

            LearningOpportunitySpecificationType learningOpportunitySpecificationType =
                    (LearningOpportunitySpecificationType)applicationOptionType.getLearningOpportunities().getParentRef().getRef();
            solrDocument.addField("AOEducationDegree", learningOpportunitySpecificationType.getClassification().getEducationDegree().
                    getCode().getValue());
        }

    }


//    private void addApplicationOption(SolrInputDocument solrDocument, ApplicationOptionType applicationOptionType) {
//        solrDocument.addField("AOId", applicationOptionType.getIdentifier().getValue());
////      solrDocument.addField("AODescription", getValueOfExtendedString(applicationOptionType.getDescription()));
//        solrDocument.addField("AOTitle", getValueOfExtendedString(applicationOptionType.getTitle()));
//        solrDocument.addField("AOEligibilityRequirements", getValueOfExtendedString(applicationOptionType.getEligibilityRequirements().getDescription()));
//        solrDocument.addField("AODescription", getValueOfExtendedString(applicationOptionType.getSelectionCriterions().getDescription()));
//        solrDocument.addField("AOLastYearMaxScore", applicationOptionType.getSelectionCriterions().getLastYearMaxScore());
//        solrDocument.addField("AOLastYearMinScore", applicationOptionType.getSelectionCriterions().getLastYearMinScore());
//        solrDocument.addField("AOLastYearTotalApplicants", applicationOptionType.getSelectionCriterions().getLastYearTotalApplicants());
//        solrDocument.addField("AOStartingQuota", applicationOptionType.getSelectionCriterions().getStartingQuota());
//        solrDocument.addField("AOSelectionCriteriaDescription", getValueOfExtendedString(applicationOptionType.getSelectionCriterions().getDescription()));
//        List<AttachmentCollectionType.Attachment> attachments = applicationOptionType.getSelectionCriterions().getAttachments().getAttachment();
//        for (AttachmentCollectionType.Attachment attachment : attachments) {
//            solrDocument.addField("AOAttachmentDescription", getValueOfExtendedString(attachment.getDescription().getText()));
//            solrDocument.addField("AOAttachmentType", getValueOfExtendedString(attachment.getType().getLabel()));
//            solrDocument.addField("AOAttachmentReturnDueDate", attachment.getReturn().getDueDate());
//            solrDocument.addField("AOAttachmentReturnTo", attachment.getReturn().getTo());
//        }
//        List<SelectionCriterionsType.EntranceExaminations.Examination> examinations = applicationOptionType.getSelectionCriterions().getEntranceExaminations().getExamination();
//        for (SelectionCriterionsType.EntranceExaminations.Examination examination : examinations) {
//            solrDocument.addField("AOExaminationDescription", getValueOfExtendedString(examination.getDescription()));
//            solrDocument.addField("AOExaminationTitle", getValueOfExtendedString(examination.getExaminationType().getLabel()));
//            List<ExaminationEventType> examinationEvents = examination.getExaminationEvent();
//            for (ExaminationEventType examinationEvent : examinationEvents) {
//                solrDocument.addField("AOExaminationStart", examinationEvent.getStart());
//                solrDocument.addField("AOExaminationStartDate", examinationEvent.getStart());
//                solrDocument.addField("AOExaminationEnd", examinationEvent.getEnd());
//                List<ExaminationLocationType> locations = examinationEvent.getLocations().getLocation();
//                for (ExaminationLocationType location : locations) {
//                    solrDocument.addField("AOExaminationLocationName", location.getName());
//                    solrDocument.addField("AOExaminationLocationAddressLine", location.getAddressLine());
//                    solrDocument.addField("AOExaminationLocationPostalCode", location.getPostalCode());
//                    solrDocument.addField("AOExaminationLocationCity", location.getCity());
//                }
//            }
//            solrDocument.addField("AOExaminationDescription", examination.getExaminationEvent());
//        }
//    }

//    private void addLearningOpportunityInstance(SolrInputDocument solrDocument, LearningOpportunityInstanceType ref) {
////        solrDocument.addField("LOIId", ref.getId());
//        List<CodeValueCollectionType.Code> languages = ref.getLanguagesOfInstruction().getCode();
//        for (CodeValueCollectionType.Code lang : languages) {
//            solrDocument.addField("LOILanguagesOfInstruction", lang);
//        }
//
//        List<CodeValueCollectionType.Code> formsOfTeaching = ref.getFormsOfTeaching().getCode();
//        for (CodeValueCollectionType.Code formOfTeaching : formsOfTeaching) {
//            solrDocument.addField("LOIFormOfTeaching", formOfTeaching.getValue());
//        }
////        solrDocument.addField("LOIAcademicYear", ref.getAcademicYear());
////        solrDocument.addField("LOIAssessments", getValueOfExtendedString(ref.getAssessments().getAssessment()).trim());
//        //solrDocument.addField("LOICostOfEducation", ref.getCostOfEducation().getNoCost());
////        solrDocument.addField("LOIDuration", ref.getDuration());
//        //solrDocument.addField("LOIFinalExamination", ref.getFinalExamination());
//        //solrDocument.addField("LOIFormOfEducation", ref.getFormOfEducation());
////        solrDocument.addField("LOIKeywords", ref.getKeywords());
//        solrDocument.addField("LOIPrerequisite", ref.getPrerequisite().getValue().trim());
////        solrDocument.addField("LOIProfession", ref.getProfession());
////        solrDocument.addField("LOIScholarship", ref.getScholarship());
////        solrDocument.addField("LOIStartDate", ref.getStartDate());
//
//        List<WebLinkCollectionType.Link> links = ref.getWebLinks().getLink();
//        for (WebLinkCollectionType.Link link : links) {
//            solrDocument.addField("LOIWebLink" + link.getType() + "Uri", link.getUri());
//            solrDocument.addField("LOIWebLink" + link.getType() + "Label", getValueOfExtendedString(link.getLabel()));
//        }
//    }
//
//    private void addLearningOpportunitySpecification(SolrInputDocument solrDocument, LearningOpportunitySpecificationType los) {
//        solrDocument.addField("LOSId", los.getId());
//        solrDocument.addField("LOSType", los.getType().value());
//        solrDocument.addField("LOSName", getValueOfExtendedString(los.getName()));
//        solrDocument.addField("LOSCredits", los.getCredits().getValue());
//        solrDocument.addField("LOSCreditsUnit", los.getCredits().getUnit());
////        solrDocument.addField("LOSIdentifier", los.getId());
//        solrDocument.addField("LOSDegreeTitle", los.getDegreeTitle().getValue());
//        solrDocument.addField("LOSQualification", getValueOfExtendedString(los.getQualification().getTitle()));
//        solrDocument.addField("LOSDescriptionStructureDiagram", los.getDescription().getStructureDiagram());
//        solrDocument.addField("LOSDescriptionAccessToFurtherStudies", los.getDescription().getAccessToFurtherStudies());
//        solrDocument.addField("LOSDescriptionEducationAndProfessionalGoals", los.getDescription().getEducationAndProfessionalGoals());
////        solrDocument.addField("LOSDescriptionGeneralDescription", los.getDescription().getGeneralDescription());
//
//        LearningOpportunitySpecificationType.Classification classification = los.getClassification();
//        solrDocument.addField("LOSEducationDomain", classification.getEducationDomain());
//        solrDocument.addField("LOSEducationDegree", classification.getEducationDegree());
//        solrDocument.addField("LOSStydyDomain", classification.getStudyDomain());
//        solrDocument.addField("LOSEducationClassification", classification.getEducationClassification());
//    }

    private static String getValueOfExtendedString(List<ExtendedStringType> ref1) {
        if (ref1.isEmpty()) {
            return "no label found";
        }
        else {
            for (ExtendedStringType extendedStringType : ref1) {
                if (extendedStringType.getLang().equals("fi")) {
                    return extendedStringType.getValue();
                }
            }
        }
        return ref1.get(0).getValue();
    }

    private static String getValueOfTextType(List<TextType> ref1) {
        return ref1.get(0).getContent();
    }
}
