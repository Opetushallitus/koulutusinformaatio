/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.koulutusinformaatio.service.impl;

import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.service.ParserService;
import fi.vm.sade.tarjonta.publication.types.*;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hannu Lyytikainen
 */
@Service
public class ParserServiceImpl implements ParserService {

    @Override
    public LearningOpportunityData parse(Source source) throws JAXBException {
        LearningOpportunityData learningOpportunityData = new LearningOpportunityData();

        Unmarshaller unmashaller = JAXBContext.newInstance(LearningOpportunityDownloadDataType.class.getPackage().getName()).createUnmarshaller();
        LearningOpportunityDownloadDataType downloadData = (LearningOpportunityDownloadDataType)unmashaller.unmarshal(source);;
        List<LearningOpportunityInstanceType> loiList = downloadData.getLearningOpportunityInstance();

        // temporary map that holds child
        Map<String, ChildLearningOpportunity> children = new HashMap<String, ChildLearningOpportunity>();

        Map<String, ParentLearningOpportunity> parents = new HashMap<String, ParentLearningOpportunity>();

        Map<String, LearningOpportunityProvider> providers = new HashMap<String, LearningOpportunityProvider>();

        // providers
        for (LearningOpportunityProviderType lopType : downloadData.getLearningOpportunityProvider()) {
            LearningOpportunityProvider lop = parseLearningOpportunityProvider(lopType);
            providers.put(lop.getId(), lop);
        }

        // child learning opportunities
        for (LearningOpportunityInstanceType loi : loiList) {
            ChildLearningOpportunity newChild = parseLearningOpportunityChild(loi);
            children.put(newChild.getId(), newChild);
        }

        // parent learning opportunities
        List<LearningOpportunitySpecificationType> losList = downloadData.getLearningOpportunitySpecification();
        for (LearningOpportunitySpecificationType los : losList) {
            if (los.getChildLOSRefs() != null && !los.getChildLOSRefs().isEmpty()) {
                ParentLearningOpportunity parent = parseLearningOpportunityParent(los, children, providers);
                parents.put(parent.getId(), parent);
            }
        }

        // application option related
        List<ApplicationOption> applicationOptions = new ArrayList<ApplicationOption>();
        List<ApplicationOptionType> applicationOptionTypes = downloadData.getApplicationOption();
        for (ApplicationOptionType applicationOptionType : applicationOptionTypes) {
            ApplicationOption ao = new ApplicationOption();
            ao.setId(applicationOptionType.getIdentifier().getValue());
            ao.setName(resolveFinnishText(applicationOptionType.getTitle().getLabel()));
            ao.setApplicationSystemId(applicationOptionType.getApplicationSystemRef().getOidRef());

            // add application option to parent learning opportunities
            LearningOpportunitySpecificationType aoParentLos =
                    (LearningOpportunitySpecificationType)applicationOptionType.getLearningOpportunities().getParentRef().getRef();
            ParentLearningOpportunity parent = parents.get(aoParentLos.getId());
            parent.getApplicationOptions().add(ao);

            // update application system refs to providers
            LearningOpportunityProvider provider = providers.get(parent.getProvider().getId());
            provider.getApplicationSystemIDs().add(ao.getApplicationSystemId());

            // set provider to application option
            ao.setProvider(parent.getProvider());

            // add application option to child learning opportunities and
            // add names of child LOs to application option
            List<LearningOpportunityInstanceRefType> aoChildLosRefs =
                    applicationOptionType.getLearningOpportunities().getInstanceRef();
            for (LearningOpportunityInstanceRefType aoChildLosRef : aoChildLosRefs) {
                // AO to LO
                LearningOpportunityInstanceType loi = (LearningOpportunityInstanceType) aoChildLosRef.getRef();
                LearningOpportunitySpecificationType los =
                        (LearningOpportunitySpecificationType) loi.getSpecificationRef().getRef();
                children.get(los.getId()).getApplicationOptions().add(ao);

                // LO name to AO
                ao.getChildLONames().add(children.get(los.getId()).getName());
            }

            // add education degree info to application option
            ao.setEducationDegree(parent.getEducationDegree());
            applicationOptions.add(ao);
        }

        learningOpportunityData.setApplicationOptions(applicationOptions);
        learningOpportunityData.setParentLearningOpportinities(new ArrayList<ParentLearningOpportunity>(parents.values()));
        learningOpportunityData.setProviders(new ArrayList<LearningOpportunityProvider>(providers.values()));

        return learningOpportunityData;
    }

    private ParentLearningOpportunity parseLearningOpportunityParent(LearningOpportunitySpecificationType los,
                                                                     Map<String, ChildLearningOpportunity> children,
                                                                     Map<String, LearningOpportunityProvider> providers) {
        ParentLearningOpportunity parent = new ParentLearningOpportunity();
        parent.setId(los.getId());
        parent.setName(resolveFinnishText(los.getName()));
        parent.setEducationDegree(los.getClassification().getEducationDegree().getCode().getValue());
        List<ChildLearningOpportunity> childList = new ArrayList<ChildLearningOpportunity>();
        for (LearningOpportunitySpecificationRefType ref : los.getChildLOSRefs()) {
            LearningOpportunitySpecificationType child = (LearningOpportunitySpecificationType) ref.getRef();
            childList.add(children.get(child.getId()));
        }
        parent.setChildren(childList);

        String lopId = los.getOrganizationRef().getOidRef();
        parent.setProvider(providers.get(lopId));

        return parent;
    }

    private ChildLearningOpportunity parseLearningOpportunityChild(LearningOpportunityInstanceType loi) {

        LearningOpportunitySpecificationType los = (LearningOpportunitySpecificationType) loi.getSpecificationRef().getRef();

        ChildLearningOpportunity child = new ChildLearningOpportunity(los.getId(), resolveFinnishText(los.getName()));

        return child;
    }

    private LearningOpportunityProvider parseLearningOpportunityProvider(LearningOpportunityProviderType type) {
        LearningOpportunityProvider learningOpportunityProvider = new LearningOpportunityProvider(type.getOrganizationRef().getOidRef(),
                resolveFinnishText(type.getInstitutionInfo().getName()));
        return learningOpportunityProvider;
    }

    private String resolveFinnishText(List<ExtendedStringType> strings) {
        for (ExtendedStringType string : strings) {
            if (string.getLang().equals("fi")) {
                return string.getValue();
            }
        }
        return "TEXT NOT FOUND";
    }

}
