package fi.vm.sade.koulutusinformaatio.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.koulutusinformaatio.converter.SolrUtil;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.service.OrganisaatioRawService;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class OrganisaatioRawServiceImpl  implements OrganisaatioRawService{

    private final String organisaatioResourceUrl;

    @Autowired
    public OrganisaatioRawServiceImpl(@Value("${organisaatio.api.rest.url}") final String organisaatioResourceUrl) {
        this.organisaatioResourceUrl = organisaatioResourceUrl;
    }

    @Override
    public OrganisaatioRDTO getOrganisaatio(String oid) throws ResourceNotFoundException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        HttpURLConnection conn = null;
        try {
            URL orgUrl = new URL(String.format("%s/%s", this.organisaatioResourceUrl, oid));
            conn = (HttpURLConnection) (orgUrl.openConnection());
            conn.setRequestMethod(SolrUtil.SolrConstants.GET);
            conn.connect();
            return mapper.readValue(conn.getInputStream(), OrganisaatioRDTO.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Organization "+oid+" not found: "+e.getMessage());
        }
    }
}
