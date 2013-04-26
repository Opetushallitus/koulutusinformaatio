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

package fi.vm.sade.koulutusinformaatio.service;

import fi.vm.sade.koulutusinformaatio.domain.ParentLOS;
import org.apache.solr.client.solrj.SolrServerException;

import java.io.IOException;

public interface IndexerService {

    /**
     *
     */
    void dropLOs() throws Exception;

    /**
     *
     */
    void dropLOPs()  throws Exception;

    /**
     * Adds an learning opportunity parent and it's children into solar.
     * The data is not committed to index.
     */
    void addParentLearningOpportunity(ParentLOS parent) throws Exception;

    /**
     * Commits learning opportunities from memory to index.
     */
    void commitLOChnages() throws Exception;
}
