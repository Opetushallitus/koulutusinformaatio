package fi.vm.sade.koulutusinformaatio.integrationtest;

import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

public class ProviderServiceMock implements ProviderService {
    @Override
    public Provider getByOID(String oid) throws KoodistoException, MalformedURLException, IOException, ResourceNotFoundException {
        Provider provider = new Provider();
        provider.setId(oid);

        I18nText i18nText = new I18nText();
        i18nText.put("fi", oid);
        provider.setName(i18nText);

        return provider;
    }

    @Override
    public List<OrganisaatioPerustieto> fetchOpplaitokset() throws MalformedURLException, IOException, ResourceNotFoundException {
        return Lists.newArrayList(new OrganisaatioPerustieto());
    }

    @Override
    public List<OrganisaatioPerustieto> fetchToimipisteet() throws MalformedURLException, IOException, ResourceNotFoundException {
        return Lists.newArrayList(new OrganisaatioPerustieto());
    }

    @Override
    public void clearCache() {

    }

    @Override
    public List<OrganisaatioPerustieto> fetchOppisopimusToimipisteet() throws MalformedURLException, IOException, ResourceNotFoundException {
        return Lists.newArrayList(new OrganisaatioPerustieto());
    }
}
