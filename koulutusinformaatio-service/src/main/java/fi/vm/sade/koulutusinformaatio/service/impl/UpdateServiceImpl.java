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

import fi.vm.sade.koulutusinformaatio.client.TarjontaClient;
import fi.vm.sade.koulutusinformaatio.domain.LearningOpportunityData;
import fi.vm.sade.koulutusinformaatio.service.*;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import java.io.IOException;

/**
 * @author Hannu Lyytikainen
 */
@Service
public class UpdateServiceImpl implements UpdateService {

    private TarjontaService tarjontaService;
    private ParserService parserService;
    private IndexerService indexerService;
    private EducationDataService educationDataService;

    @Autowired
    public UpdateServiceImpl(TarjontaService tarjontaService, ParserService parserService,
                             IndexerService indexerService, EducationDataService educationDataService) {
        this.tarjontaService = tarjontaService;
        this.parserService = parserService;
        this.indexerService = indexerService;
        this.educationDataService = educationDataService;
    }

    @Override
    public void updateEducationData() {
        tarjontaService.findLearningOpportunity("1234");


//        Source source = tarjontaClient.retrieveTarjontaAsSource();
//        try {
//            LearningOpportunityData loData = parserService.parse(source);
//            this.indexerService.updateIndexes(loData);
//            this.educationDataService.save(loData);
//        } catch (JAXBException e) {
//            e.printStackTrace();
//        } catch (SolrServerException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}
