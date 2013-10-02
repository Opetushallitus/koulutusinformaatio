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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;
import fi.vm.sade.tarjonta.service.resources.dto.*;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
@Service
@Profile("default")
public class TarjontaServiceImpl implements TarjontaService {

    private WebResource komoResource;
    private WebResource komotoResource;
    private WebResource hakuResource;
    private WebResource hakukohdeResource;
    private ConversionService conversionService;

    @Autowired
    public TarjontaServiceImpl(@Value("${tarjonta.api.rest.url}") final String tarjontaApiUrl,
                               ConversionService conversionService) {
        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(JacksonJsonProvider.class);
        Client clientWithJacksonSerializer = Client.create(cc);
        komoResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "komo");
        komotoResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "komoto");
        hakuResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "haku");
        hakukohdeResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "hakukohde");
        this.conversionService = conversionService;
    }

    public TarjontaServiceImpl() {
    }

    @Override
    public KomoDTO getKomo(String oid) {
        return komoResource
                .path(oid)
                .accept(getMediaType())
                .get(new GenericType<KomoDTO>() {
                });
    }

    @Override
    public List<OidRDTO> getKomotosByKomo(String oid, int count, int startIndex) {
        return komoResource
                .path(oid)
                .path("komoto")
                .queryParam("count", String.valueOf(count))
                .queryParam("startIndex", String.valueOf(startIndex))
                .accept(getMediaType())
                .get(new GenericType<List<OidRDTO>>() {
                });
    }

    @Override
    public KomotoDTO getKomoto(String oid) {
        return komotoResource
                .path(oid)
                .accept(getMediaType())
                .get(new GenericType<KomotoDTO>() {
                });
    }

    @Override
    public List<OidRDTO> getHakukohdesByKomoto(String oid) {
        return komotoResource
                .path(oid)
                .path("hakukohde")
                .accept(getMediaType())
                .get(new GenericType<List<OidRDTO>>() {
                });
    }

    @Override
    public KomoDTO getKomoByKomoto(String oid) {
        return komotoResource
                .path(oid)
                .path("komo")
                .accept(getMediaType())
                .get(new GenericType<KomoDTO>() {});

    }

    @Override
    public HakukohdeDTO getHakukohde(String oid) {
        return hakukohdeResource
                .path(oid)
                .accept(getMediaType())
                .get(new GenericType<HakukohdeDTO>() {
                });
    }

    @Override
    public HakuDTO getHakuByHakukohde(String oid) {
        return hakukohdeResource
                .path(oid)
                .path("haku")
                .accept(getMediaType())
                .get(new GenericType<HakuDTO>() {});
    }

    @Override
    public List<OidRDTO> getKomotosByHakukohde(String oid) {
        return hakukohdeResource
                .path(oid)
                .path("komoto")
                .accept(getMediaType())
                .get(new GenericType<List<OidRDTO>>() {});
    }

    @Override
    public HakuDTO getHaku(String oid) {
        return hakuResource
                .path(oid)
                .accept(getMediaType())
                .get(new GenericType<HakuDTO>() {
                });
    }

    @Override
    public List<ParentLOS> findParentLearningOpportunity(String oid) throws TarjontaParseException, KoodistoException {
        return null;
    }

    @Override
    public List<String> listParentLearnignOpportunityOids() {
        return listParentLearnignOpportunityOids(Integer.MAX_VALUE, 0);
    }

    @Override
    public List<String> listParentLearnignOpportunityOids(int count, int startIndex) {
        List<OidRDTO> oids = komoResource
                .queryParam("count", String.valueOf(count))
                .queryParam("startIndex", String.valueOf(startIndex))
                .accept(getMediaType())
                .get(new GenericType<List<OidRDTO>>() {
                });
        return Lists.transform(oids, new Function<OidRDTO, String>() {
            @Override
            public String apply(OidRDTO input) {
                return conversionService.convert(input, String.class);
            }
        });
    }

    private String getMediaType() {
        return MediaType.APPLICATION_JSON + ";charset=UTF-8";
    }

}
