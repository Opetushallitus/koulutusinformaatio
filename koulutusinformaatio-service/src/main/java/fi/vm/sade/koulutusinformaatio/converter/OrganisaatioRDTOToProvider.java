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

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioMetaDataRDTO;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.Map;

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
    
    private static final String METADATA_SOCIAL_FACEBOOK = "FACEBOOK";
    private static final String METADATA_SOCIAL_LINKEDIN = "LINKED_IN";
    private static final String METADATA_SOCIAL_TWITTER = "TWITTER";
    private static final String METADATA_SOCIAL_GOOGLEPLUS = "GOOGLE_PLUS";
    private static final String METADATA_SOCIAL_OTHER = "MUU";
    private static final String[] SOCIAL_LINKS = {METADATA_SOCIAL_FACEBOOK, METADATA_SOCIAL_LINKEDIN, METADATA_SOCIAL_TWITTER, METADATA_SOCIAL_GOOGLEPLUS, METADATA_SOCIAL_OTHER};

    KoodistoService koodistoService;

    public OrganisaatioRDTOToProvider(KoodistoService koodistoService) {
        this.koodistoService = koodistoService;
    }

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
        p.setSocial(getSocialLinks(o.getMetadata(), SOCIAL_LINKS));
        p.setPicture(getPicture(o));
        p.setPlaceOfBusinessCode(o.getToimipistekoodi());
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

        return new I18nText();
    }
    
    private List<Social> getSocialLinks(final OrganisaatioMetaDataRDTO metadata, String... keys) {
        List<Social> social = Lists.newArrayList();
        for (String key : keys) { 
            Social socialItem = getSocial(metadata, key);
            if (socialItem != null) {
                social.add(getSocial(metadata, key));
            }
        }
        
        return social;
    }
    
    private Social getSocial(final OrganisaatioMetaDataRDTO metadata, String key) {
        if (metadata != null) {
            Map<String, Map<String, String>> data = metadata.getData();
            if (data != null && data.containsKey(key)) {
                Map<String, String> valueMap = data.get(key);
                if (valueMap != null) {
                    List<String> value = Lists.newArrayList(valueMap.values());
                    if (value != null && value.size() > 0) {
                        return new Social(key.toLowerCase(), value.get(0));
                    }
                }
            }
        }
        
        return null;
    }

    private Picture getPicture(final OrganisaatioRDTO o) {
        OrganisaatioMetaDataRDTO metadata = o.getMetadata();
        if (metadata != null && !Strings.isNullOrEmpty(metadata.getKuvaEncoded())) {
            Picture pic = new Picture();
            pic.setId(o.getOid());
            pic.setPictureEncoded(metadata.getKuvaEncoded());
            return pic;
        }
        return null;
    }
}
