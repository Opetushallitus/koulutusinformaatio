package fi.vm.sade.koulutusinformaatio.service;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.DataStatus;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.LOS;

public interface EducationIncrementalDataUpdateService {
    
    void save(final LOS learningOpportunitySpecification);

    void save(final DataStatus dataStatus);
    
    void deleteLos(LOS los);
    
    void deleteAo(ApplicationOption ao);

    void clearHigherEducations(IndexerService indexerService, HttpSolrServer loHttpSolrServer) throws IOException, SolrServerException;

    void updateHigherEdLos(HigherEducationLOS curParent);

}
