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
import fi.vm.sade.koulutusinformaatio.domain.exception.KIConversionException;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioMetaDataRDTO;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Hannu Lyytikainen
 */
public class OrganisaatioRDTOToProvider implements Converter<OrganisaatioRDTO, Provider> {
    
    private static final String SOSIAALINENMEDIA_CODES_NAME = "sosiaalinenmedia";

    private static final Logger LOG = LoggerFactory.getLogger(OrganisaatioRDTOToProvider.class);

    private static final String STREET_ADDRESS = "osoite";
    private static final String POST_OFFICE = "postitoimipaikka";
    private static final String POSTAL_CODE = "postinumeroUri";

    private static final String METADATA_YLEISKUVAUS = "YLEISKUVAUS";
    private static final String METADATA_TERVEYDENHUOLTOPALVELUT = "TERVEYDENHUOLTOPALVELUT";
    private static final String METADATA_ESTEETTOMYYS = "ESTEETOMYYS";
    private static final String METADATA_KUSTANNUKSET = "KUSTANNUKSET";
    private static final String METADATA_OPPIMISYMPARISTO = "OPPIMISYMPARISTO";
    private static final String METADATA_OPISKELIJARUOKAILU = "OPISKELIJARUOKAILU";
    private static final String METADATA_TIETOA_ASUMISESTA = "TIETOA_ASUMISESTA";
    
    private static final String METADATA_VUOSIKELLO = "VUOSIKELLO";
    private static final String METADATA_OPINTOJEN_RAHOITUS = "RAHOITUS";
    private static final String METADATA_TIETOA_VAKUUTUKSISTA = "VAKUUTUKSET";
    private static final String METADATA_VAPAA_AJAN_PALVELUT = "VAPAA_AIKA";
    

    private static final String METADATA_SOCIAL_FACEBOOK = "sosiaalinenmedia_1";
    private static final String METADATA_SOCIAL_LINKEDIN = "sosiaalinenmedia_3";
    private static final String METADATA_SOCIAL_TWITTER = "sosiaalinenmedia_4";
    private static final String METADATA_SOCIAL_GOOGLEPLUS = "sosiaalinenmedia_2";
    private static final String METADATA_SOCIAL_OTHER = "sosiaalinenmedia_5";
    private static final String METADATA_SOCIAL_INSTAGRAM = "sosiaalinenmedia_6";
    private static final String METADATA_SOCIAL_YOUTUBE = "sosiaalinenmedia_7";

    private static final String KI_SOCIAL_FACEBOOK = "facebook";
    private static final String KI_SOCIAL_LINKEDIN = "linked_in";
    private static final String KI_SOCIAL_TWITTER = "twitter";
    private static final String KI_SOCIAL_GOOGLEPLUS = "google_plus";
    private static final String KI_SOCIAL_OTHER = "muu";
    
    private static final HashMap<String, String> SOCIAL_LINKS = new HashMap<String, String>();

    private static final String ATHLETE_EDUCATION_KOODISTO_URI = "urheilijankoulutus_1#1";
    private static final String PLACE_OF_BUSINESS_KOODISTO_URI = "opetuspisteet";
    private static final String OPPILAITOSNUMERO_KOODISTO_URI = "oppilaitosnumero";

    private static final String ADDRESS_DATA_TYPE = "osoiteTyyppi";
    private static final String ADDRESS_DATA_TYPE_VISIT = "kaynti";
    private static final String ADDRESS_DATA_TYPE_POSTAL = "posti";
    private static final String ADDRESS_DATA_TYPE_FOREIGN_VISIT = "ulkomainen_kaynti";
    private static final String ADDRESS_DATA_TYPE_FOREIGN_POSTAL = "ulkomainen_posti";
    private static final String LANG = "kieli";

    private static final String DATA_TYPE = "tyyppi";
    private static final String DATA_TYPE_PHONE = "puhelin";
    private static final String DATA_TYPE_FAX = "faksi";
    private static final String DATA_TYPE_PHONE_NUMBER = "numero";
    private static final String DATA_TYPE_EMAIL = "email";
    private static final String DATA_TYPE_WWW = "www";
    
    private static final String CODE_MUNICIPALITY = "kunta";
    private static final String CODE_DISTRICT = "maakunta";

    private KoodistoService koodistoService;

    public OrganisaatioRDTOToProvider(KoodistoService koodistoService) {
        this.koodistoService = koodistoService;
        // Koska KI käyttää kovakoodattuna some kenttiä esimerkiksi CSS säännöissä, niitä ei voi tuoda suoraan koodistosta.
        SOCIAL_LINKS.put(METADATA_SOCIAL_FACEBOOK, KI_SOCIAL_FACEBOOK);
        SOCIAL_LINKS.put(METADATA_SOCIAL_LINKEDIN, KI_SOCIAL_LINKEDIN);
        SOCIAL_LINKS.put(METADATA_SOCIAL_TWITTER, KI_SOCIAL_TWITTER);
        SOCIAL_LINKS.put(METADATA_SOCIAL_GOOGLEPLUS, KI_SOCIAL_GOOGLEPLUS);
        SOCIAL_LINKS.put(METADATA_SOCIAL_OTHER, KI_SOCIAL_OTHER);
        SOCIAL_LINKS.put(METADATA_SOCIAL_INSTAGRAM, KI_SOCIAL_OTHER);
        SOCIAL_LINKS.put(METADATA_SOCIAL_YOUTUBE, KI_SOCIAL_OTHER);
    }

    @Override
    public Provider convert(OrganisaatioRDTO o) {
        Provider p = null;
        try {
            p = new Provider();
            p.setId(o.getOid());
            p.setName(new I18nText(o.getNimi()));
            LOG.debug("Getting postal address for organisation: {}", o.getOid());
            p.setPostalAddress(getLocalizedAddress(o.getYhteystiedot(), ADDRESS_DATA_TYPE_POSTAL, ADDRESS_DATA_TYPE_FOREIGN_POSTAL));
            p.setVisitingAddress(getLocalizedAddress(o.getYhteystiedot(), ADDRESS_DATA_TYPE_VISIT, ADDRESS_DATA_TYPE_FOREIGN_VISIT));
            p.setEmail(getSimpleContactInfo(o.getYhteystiedot(), DATA_TYPE_EMAIL));
            p.setFax(getPhoneNumber(o.getYhteystiedot(), DATA_TYPE_FAX));
            p.setPhone(getPhoneNumber(o.getYhteystiedot(), DATA_TYPE_PHONE));
            p.setWebPage(getSimpleContactInfo(o.getYhteystiedot(), DATA_TYPE_WWW));
            LOG.debug("Setting descriptions: {}", o.getOid());
            p.setDescription(getDataValue(o.getMetadata(), METADATA_YLEISKUVAUS));
            p.setHealthcare(getDataValue(o.getMetadata(), METADATA_TERVEYDENHUOLTOPALVELUT));
            p.setAccessibility(getDataValue(o.getMetadata(), METADATA_ESTEETTOMYYS));
            p.setLivingExpenses(getDataValue(o.getMetadata(), METADATA_KUSTANNUKSET));
            p.setLearningEnvironment(getDataValue(o.getMetadata(), METADATA_OPPIMISYMPARISTO));
            p.setDining(getDataValue(o.getMetadata(), METADATA_OPISKELIJARUOKAILU));
            p.setLiving(getDataValue(o.getMetadata(), METADATA_TIETOA_ASUMISESTA));
            
            p.setYearClock(getDataValue(o.getMetadata(), METADATA_VUOSIKELLO));
            p.setFinancingStudies(getDataValue(o.getMetadata(), METADATA_OPINTOJEN_RAHOITUS));
            p.setInsurances(getDataValue(o.getMetadata(), METADATA_TIETOA_VAKUUTUKSISTA));
            p.setLeisureServices(getDataValue(o.getMetadata(), METADATA_VAPAA_AJAN_PALVELUT));
            LOG.debug("Got descriptions: {}", o.getOid());
            p.setSocial(getSocialLinks(o.getMetadata()));
            p.setPicture(getPicture(o));
            p.setAthleteEducation(isAthleteEducation(o.getToimipistekoodi(), o.getOppilaitosKoodi()));
            p.setPlaceOfBusinessCode(o.getToimipistekoodi());
            p.setHomePlace(koodistoService.searchFirstName(o.getKotipaikkaUri()));
            String districtUri = getDistrictUri(o.getKotipaikkaUri());
            if (districtUri != null) { 
                p.setHomeDistrict(koodistoService.searchFirstName(districtUri));
            }
            p.setApplicationOffice(getApplicationOffice(o.getMetadata()));
            p.setType(koodistoService.searchFirst(o.getOppilaitosTyyppiUri()));

        } catch (Exception e) {
            LOG.error("Problem creatig organisaatio: " + o.getOid(), e);
            throw new KIConversionException("Conversion failed - " + e.getMessage() + ", organisaatio: " + o.getOid());
        }
        return p;
    }
    
    private I18nText getPhoneNumber(List<Map<String, String>> yhteystiedot,
            String phoneType) throws KoodistoException {
        Map<String,String> phoneTransls = new HashMap<String,String>();
        for (Map<String,String> curYht : yhteystiedot) {
            String key = koodistoService.searchFirstCodeValue(curYht.get(LANG));
            if (curYht.get(DATA_TYPE) != null && curYht.get(DATA_TYPE).equals(phoneType)) {
                phoneTransls.put(key.toLowerCase(), curYht.get(DATA_TYPE_PHONE_NUMBER));
            }
        }
        
        if (!phoneTransls.isEmpty()) {
            return new I18nText(phoneTransls);
        }
        
        return null;
    }

    private I18nText getSimpleContactInfo(List<Map<String, String>> yhteystiedot,
            String dataType) throws KoodistoException {
        Map<String,String> contactInfoTransls = new HashMap<String,String>();
        for (Map<String,String> curYht : yhteystiedot) {
            String key = koodistoService.searchFirstCodeValue(curYht.get(LANG));
            if (curYht.get(dataType) != null) {
                contactInfoTransls.put(key.toLowerCase(), curYht.get(dataType));
            }
        }
        if (!contactInfoTransls.isEmpty()) {
            return new I18nText(contactInfoTransls);
        }
        return null;
    }

    private Address getLocalizedAddress(List<Map<String,String>> yhteystiedot, String addressType, String foreignAddressType) throws KoodistoException {
        
        Map<String,String> streetAddrTransls = new HashMap<String,String>();
        Map<String,String> postOfficeTransls = new HashMap<String,String>();
        Map<String,String> postalCodeTransls = new HashMap<String,String>();
               
        
        LOG.debug("Getting {}", addressType);
        
        for (Map<String,String> curYht : yhteystiedot) {
            if (curYht.containsKey(ADDRESS_DATA_TYPE) && (curYht.get(ADDRESS_DATA_TYPE).equals(addressType) || curYht.get(ADDRESS_DATA_TYPE).equals(foreignAddressType))) {
                
                LOG.debug("Yhteystieto: {}", addressType);
                
                String key = koodistoService.searchFirstCodeValue(curYht.get(LANG));
                
                LOG.debug("Lang key is: {}", key);
                if (curYht.get(STREET_ADDRESS) != null) {
                    streetAddrTransls.put(key.toLowerCase(), curYht.get(STREET_ADDRESS));
                    
                    LOG.debug("there is street address: {}", curYht.get(STREET_ADDRESS));
                    
                } 
                if (curYht.get(POST_OFFICE) != null) {
                    postOfficeTransls.put(key.toLowerCase(), curYht.get(POST_OFFICE));
                }
                if (curYht.get(POSTAL_CODE) != null) {
                    String searchFirstCodeValue = koodistoService.searchFirstCodeValue(curYht.get(POSTAL_CODE));
                    postalCodeTransls.put(key.toLowerCase(), searchFirstCodeValue);
                }
            }
        }
        
        Address addr = new Address();
        addr.setStreetAddress(new I18nText(streetAddrTransls));
        addr.setPostOffice(new I18nText(postOfficeTransls));
        addr.setPostalCode(new I18nText(postalCodeTransls));
        return addr;
    }

    private String getDistrictUri(String kotipaikkaUri) throws KoodistoException {
        if (kotipaikkaUri == null) {
            return null;
        }
        List<Code> maakuntaCodes = koodistoService.searchSubCodes(kotipaikkaUri, CODE_DISTRICT);
        
        String maakuntaVal =  (maakuntaCodes != null &&!maakuntaCodes.isEmpty()) ? maakuntaCodes.get(0).getValue() : null;
        return (maakuntaVal != null) ? String.format("%s_%s", CODE_DISTRICT, maakuntaVal) : null;
    }

    private ApplicationOffice getApplicationOffice(OrganisaatioMetaDataRDTO metadata) throws KoodistoException {
        if (metadata == null || metadata.getYhteystiedot() == null) {
            return null;
        } else {
            Address visitingAddress = null;
            Address postalAddress = null;
            I18nText phone = null;
            I18nText email = null;
            I18nText www = null;

            visitingAddress = getLocalizedAddress(metadata.getYhteystiedot(), ADDRESS_DATA_TYPE_VISIT, ADDRESS_DATA_TYPE_FOREIGN_VISIT);
            postalAddress = getLocalizedAddress(metadata.getYhteystiedot(), ADDRESS_DATA_TYPE_POSTAL, ADDRESS_DATA_TYPE_FOREIGN_POSTAL);
            
            
            phone = getPhoneNumber(metadata.getYhteystiedot(), DATA_TYPE_PHONE);
            www = getSimpleContactInfo(metadata.getYhteystiedot(), DATA_TYPE_WWW);
            email = this.getSimpleContactInfo(metadata.getYhteystiedot(), DATA_TYPE_EMAIL); 
            
            return new ApplicationOffice(getI18nText(metadata.getHakutoimistonNimi()), phone, email, www,
                    visitingAddress, postalAddress);
        }
    }

    private boolean isAthleteEducation(final String toimipistekoodi, final String oppilaitoskoodi) {
        LOG.info("isAthleteEducation, toimipistekoodi {}, oppilaitoskoodi {}", toimipistekoodi, oppilaitoskoodi);
        try {
        if (!Strings.isNullOrEmpty(toimipistekoodi) || !Strings.isNullOrEmpty(oppilaitoskoodi)) {
            List<Code> superCodesToimipiste = null;
            List<Code> superCodesOppilaitos = null;
            try {
                superCodesToimipiste = koodistoService.searchSuperCodes(ATHLETE_EDUCATION_KOODISTO_URI,
                        PLACE_OF_BUSINESS_KOODISTO_URI);
                superCodesOppilaitos = koodistoService.searchSuperCodes(ATHLETE_EDUCATION_KOODISTO_URI,
                        OPPILAITOSNUMERO_KOODISTO_URI);
            } catch (KoodistoException e) {
                throw new KIConversionException("Conversion failed - " + e.getMessage());
            }
            LOG.info("Koodis for toimipistees: {}, for oppilaitokses: {}", superCodesToimipiste.size(), superCodesOppilaitos.size());
            if (superCodesOppilaitos != null) {
                for (Code code : superCodesOppilaitos) {
                    if (code.getValue() != null && code.getValue().equals(oppilaitoskoodi)) {
                        LOG.info("isAthleteEducation changed result for ({}), ({})", toimipistekoodi, oppilaitoskoodi);
                        return true;
                    }
                }
            }
            if (superCodesToimipiste != null) {
                for (Code code : superCodesToimipiste) {
                    if (code.getValue() != null && code.getValue().equals(toimipistekoodi)) {
                        LOG.info("isAthleteEducation was always true result for ({}), ({})", toimipistekoodi, oppilaitoskoodi);
                        return true;
                    }
                }
            }
        }
        } catch (Exception ex) {
            LOG.error("Error while deducing isAthleteEducation: ", ex);
            return false;
        }
        return false;
    }

    private I18nText getI18nText(final Map<String, String> texts) throws KoodistoException {
        if (texts != null && !texts.isEmpty()) {
            Map<String, String> translations = new HashMap<String, String>();
            Iterator<Map.Entry<String, String>> i = texts.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<String, String> entry = i.next();
                if (!Strings.isNullOrEmpty(entry.getKey()) && !Strings.isNullOrEmpty(entry.getValue())) {
                    String key = koodistoService.searchFirstCodeValue(entry.getKey());
                    if (!Strings.isNullOrEmpty(key)) {
                        translations.put(key.toLowerCase(), entry.getValue());
                    }
                }
            }
            I18nText i18nText = new I18nText();
            i18nText.setTranslations(translations);
            return i18nText;
        }
        return null;
    }

    private I18nText getDataValue(OrganisaatioMetaDataRDTO metadata, String key) throws KoodistoException {
        if (metadata != null) {
            Map<String, Map<String, String>> data = metadata.getData();
            if (data != null && data.containsKey(key)) {
                return getI18nText(data.get(key));
            }
        }
        return null;
    }

    private List<Social> getSocialLinks(final OrganisaatioMetaDataRDTO metadata) {
        List<Social> social = Lists.newArrayList();
        if (metadata != null && metadata.getData() != null) {
            for (Entry<String, Map<String, String>> entry : metadata.getData().entrySet()) {
                if (entry.getKey() != null && entry.getKey().startsWith(SOSIAALINENMEDIA_CODES_NAME)) {
                    Social socialItem = getSocial(entry.getKey(), entry.getValue());
                    if (socialItem != null) {
                        social.add(socialItem);
                    }
                }
            }
        }
        return social;
    }

    private Social getSocial(String key, Map<String, String> map) {
        if (map != null) {
            List<String> value = Lists.newArrayList(map.values());
            if (value != null && value.size() > 0) {
                return new Social(SOCIAL_LINKS.get(key.split("#")[0]), value.get(0));
            }
        }
        return null;
    }

    private Picture getPicture(final OrganisaatioRDTO o) {
        OrganisaatioMetaDataRDTO metadata = o.getMetadata();
        if (metadata != null && !Strings.isNullOrEmpty(metadata.getKuvaEncoded())) {
            Picture pic = new Picture();
            pic.setId(o.getOid());
            pic.setPictureEncoded(resizePicture(800, metadata.getKuvaEncoded(), o.getOid()));
            pic.setThumbnailEncoded(resizePicture(104, metadata.getKuvaEncoded(), o.getOid()));
            return pic;
        }
        return null;
    }

    private BASE64Decoder decoder = new BASE64Decoder();
    private BASE64Encoder encoder = new BASE64Encoder();

    private String resizePicture(int width, String kuvaEncoded, String orgOid) {
        LOG.debug("Resizing picture to " + width);
        if (kuvaEncoded == null || orgOid == null) {
            return null;
        }
        try {
            byte[] imageByte = decoder.decodeBuffer(kuvaEncoded);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            BufferedImage image = ImageIO.read(bis);
            bis.close();

            if(image.getWidth() < width){
                return kuvaEncoded;
            }

            double ratio = width / image.getWidth();
            int height = (int)(ratio * image.getHeight());
            BufferedImage resizedPicture = Scalr.resize(image, width, height);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(resizedPicture, "jpeg", bos);
            imageByte = bos.toByteArray();
            String encoded = encoder.encode(imageByte);
            bos.close();
            LOG.debug("Picture resized");
            return encoded;
        } catch (Exception ex) {
            LOG.warn("Problem resizing picture for " + orgOid + ", cause: " + ex.getMessage());
        }
        return null;
    }


}
