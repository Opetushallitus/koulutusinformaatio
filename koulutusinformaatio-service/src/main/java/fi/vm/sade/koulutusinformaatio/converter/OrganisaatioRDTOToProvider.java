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

package fi.vm.sade.koulutusinformaatio.converter;

import fi.vm.sade.koulutusinformaatio.domain.Address;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.Provider;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioMetaDataRDTO;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import org.springframework.core.convert.converter.Converter;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Hannu Lyytikainen
 */
public class OrganisaatioRDTOToProvider implements Converter<OrganisaatioRDTO, Provider> {

    private static final String STREET_ADDRESS = "osoite";
    private static final String POST_OFFICE = "postitoimipaikka";
    private static final String POSTAL_CODE = "postinumeroUri";
    
    private static final String METADATA_YLEISKUVAUS = "YLEISKUVAUS";
    private static final String METADATA_TERVEYDENHUOLTOPALVELUT = "TERVEYDENHUOLTOPALVELUT";
    private static final String METADATA_ESTEETTOMYYS = "ESTEETOMYYS";
    private static final String METADATA_KUSTANNUKSET = "KUSTANNUKSET";
    private static final String METADATA_OPPIMISYMPARISTO = "OPPIMISYMPARISTO";
    private static final String METADATA_OPISKELIJARUOKAILU = "OPISKELIJARUOKAILU";

    @Override
    public Provider convert(OrganisaatioRDTO o) {
        Provider p = new Provider();
        p.setId(o.getOid());
        p.setName(new I18nText(o.getNimi()));
        p.setPostalAddress(getAddress(o.getPostiosoite()));
        p.setVisitingAddress(getAddress(o.getKayntiosoite()));
        p.setEmail(o.getEmailOsoite());
        p.setFax(o.getFaksinumero());
        p.setPhone(o.getPuhelinnumero());
        p.setWebPage(o.getWwwOsoite());
        p.setDescription(getMetadataValue(o.getMetadata(), METADATA_YLEISKUVAUS));
        p.setHealthcare(getMetadataValue(o.getMetadata(), METADATA_TERVEYDENHUOLTOPALVELUT));
        p.setAccessibility(getMetadataValue(o.getMetadata(), METADATA_ESTEETTOMYYS));
        p.setLivingExpenses(getMetadataValue(o.getMetadata(), METADATA_KUSTANNUKSET));
        p.setLearningEnvironment(getMetadataValue(o.getMetadata(), METADATA_OPPIMISYMPARISTO));
        p.setDining(getMetadataValue(o.getMetadata(), METADATA_OPISKELIJARUOKAILU));
        return p;
    }

    private Address getAddress(final Map<String, String> addrs) {
        if (addrs != null && !addrs.isEmpty()) {
            Address address = new Address();
            address.setStreetAddress(addrs.get(STREET_ADDRESS));
            address.setPostOffice(addrs.get(POST_OFFICE));
            address.setPostalCode(addrs.get(POSTAL_CODE));
            return address;
        }
        return null;
    }
    
    private I18nText getMetadataValue(OrganisaatioMetaDataRDTO metadata, String key) {
        if (metadata != null) {
            Map<String, Map<String, String>> data = metadata.getData();
            if (data != null && data.containsKey(key)) {
                return new I18nText(data.get(key));
            }
        }
        
        /*
        Map<String, String> temp = new TreeMap<String, String>();
        temp.put("fi", "testiarvo");
        */
        return new I18nText();
    }
}
