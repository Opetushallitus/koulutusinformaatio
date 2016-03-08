package fi.vm.sade.koulutusinformaatio.integrationtest;

import java.util.List;

import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;

public class ProviderServiceMock implements ProviderService {
    @Override
    public Provider getByOID(String oid) {
        Provider provider = new Provider();
        provider.setId(oid);

        I18nText i18nText = new I18nText();
        i18nText.put("fi", oid);
        provider.setName(i18nText);

        return provider;
    }

    @Override
    public List<OrganisaatioPerustieto> fetchOpplaitokset() {
        return Lists.newArrayList(new OrganisaatioPerustieto());
    }

    @Override
    public List<OrganisaatioPerustieto> fetchToimipisteet() {
        return Lists.newArrayList(new OrganisaatioPerustieto());
    }

    @Override
    public void clearCache() {

    }

    @Override
    public List<OrganisaatioPerustieto> fetchOppisopimusToimipisteet() {
        return Lists.newArrayList(new OrganisaatioPerustieto());
    }

    @Override
    public String getOppilaitosTyyppiByOID(String oid) {
        return TarjontaConstants.OPPILAITOSTYYPPI_YLIOPISTO;
    }
}
