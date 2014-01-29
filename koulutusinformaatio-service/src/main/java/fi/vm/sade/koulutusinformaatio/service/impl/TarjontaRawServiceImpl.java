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

//import com.fasterxml.jackson.core.type.TypeReference;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.tarjonta.service.resources.dto.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakutuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.MediaType;

import java.util.List;
import java.util.Set;

/**
 * @author Hannu Lyytikainen
 */
@Service
public class TarjontaRawServiceImpl implements TarjontaRawService {

    private static final String JSON_UTF8 = MediaType.APPLICATION_JSON + ";charset=UTF-8";

    private WebResource komoResource;
    private WebResource komotoResource;
    private WebResource hakuResource;
    private WebResource hakukohdeResource;
    private WebResource higherEducationResource;
    private WebResource higherEducationAOResource;
    private WebResource higherEducationASResource;
    private WebResource higherEducationStructureResource;
    private ConversionService conversionService;
    private KoodistoService koodistoService;
    private ProviderService providerService;

    @Autowired
    public TarjontaRawServiceImpl(@Value("${tarjonta.api.rest.url}") final String tarjontaApiUrl,
                                  ConversionService conversionService, KoodistoService koodistoService, ProviderService providerService) {
        this.koodistoService = koodistoService;
        this.providerService = providerService;
        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(JacksonJsonProvider.class);
        Client clientWithJacksonSerializer = Client.create(cc);
        komoResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "komo");
        komotoResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "komoto");
        hakuResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "haku");
        hakukohdeResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "hakukohde");
        higherEducationResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "v1/koulutus");
        higherEducationAOResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "v1/hakukohde");
        higherEducationASResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "v1/haku");
        higherEducationStructureResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "v1/link");
        
        this.conversionService = conversionService;
    }

    public TarjontaRawServiceImpl() {
    }


    @Override
    public KomoDTO getKomo(String oid) {
        return komoResource
                .path(oid)
                .accept(JSON_UTF8)
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
                .accept(JSON_UTF8)
                .get(new GenericType<List<OidRDTO>>() {
                });
    }

    @Override
    public KomotoDTO getKomoto(String oid) {
        return komotoResource
                .path(oid)
                .accept(JSON_UTF8)
                .get(new GenericType<KomotoDTO>() {
                });
    }

    @Override
    public List<OidRDTO> getHakukohdesByKomoto(String oid) {
        return komotoResource
                .path(oid)
                .path("hakukohde")
                .accept(JSON_UTF8)
                .get(new GenericType<List<OidRDTO>>() {
                });
    }

    @Override
    public KomoDTO getKomoByKomoto(String oid) {
        return komotoResource
                .path(oid)
                .path("komo")
                .accept(JSON_UTF8)
                .get(new GenericType<KomoDTO>() {
                });

    }

    @Override
    public HakukohdeDTO getHakukohde(String oid) {
        return hakukohdeResource
                .path(oid)
                .accept(JSON_UTF8)
                .get(new GenericType<HakukohdeDTO>() {
                });
    }

    @Override
    public HakuDTO getHakuByHakukohde(String oid) {
        return hakukohdeResource
                .path(oid)
                .path("haku")
                .accept(JSON_UTF8)
                .get(new GenericType<HakuDTO>() {
                });
    }

    @Override
    public List<OidRDTO> getKomotosByHakukohde(String oid) {
        return hakukohdeResource
                .path(oid)
                .path("komoto")
                .accept(JSON_UTF8)
                .get(new GenericType<List<OidRDTO>>() {
                });
    }

    @Override
    public HakuDTO getHaku(String oid) {
        return hakuResource
                .path(oid)
                .accept(JSON_UTF8)
                .get(new GenericType<HakuDTO>() {
                });
    }

    @Override
    public List<OidRDTO> listParentLearnignOpportunityOids(int count, int startIndex) {
        return komoResource
                .queryParam("count", String.valueOf(count))
                .queryParam("startIndex", String.valueOf(startIndex))
                .accept(JSON_UTF8)
                .get(new GenericType<List<OidRDTO>>() {
                });
    }

	@Override
	public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> listHigherEducation() {
		return this.higherEducationResource
				.path("search")
				.queryParam("koulutusastetyyppi", "Korkeakoulutus")
				.accept(JSON_UTF8)
				.get(new GenericType<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>() {
				});
	}

	@Override
	public ResultV1RDTO<KoulutusKorkeakouluV1RDTO> getHigherEducationLearningOpportunity(
			String oid) {
		return higherEducationResource
				.path(oid)
	        	.accept(JSON_UTF8)
	        	.get(new GenericType<ResultV1RDTO<KoulutusKorkeakouluV1RDTO>>() {
	        	});
	}

	@Override
	public ResultV1RDTO<List<NimiJaOidRDTO>> getHakukohdesByHigherEducation(
			String oid) {
		return higherEducationResource
				.path(String.format("%s/%s", oid, "hakukohteet"))
				.accept(JSON_UTF8)
				.get(new GenericType<ResultV1RDTO<List<NimiJaOidRDTO>>>() {
	        	});
	}

	@Override
	public ResultV1RDTO<HakukohdeV1RDTO> getHigherEducationHakukohode(String oid) {
		return higherEducationAOResource
				.path(oid)
				.accept(JSON_UTF8)
				.get(new GenericType<ResultV1RDTO<HakukohdeV1RDTO>>() {
	        	});
	}

	@Override
	public ResultV1RDTO<HakuV1RDTO> getHigherEducationHakuByOid(String oid) {
		return higherEducationASResource
				.path(oid)
				.accept(JSON_UTF8)
				.get(new GenericType<ResultV1RDTO<HakuV1RDTO>>() {
	        	});
	}

	@Override
	public ResultV1RDTO<Set<String>> getChildrenOfParentHigherEducationLOS(
			String parentOid) {
		return this.higherEducationStructureResource
				.path(parentOid)
				.accept(JSON_UTF8)
				.get(new GenericType<ResultV1RDTO<Set<String>>>() {
				});
	}

	@Override
	public ResultV1RDTO<Set<String>> getParentsOfHigherEducationLOS(
			String childKomoOid) {
		return this.higherEducationStructureResource
				.path(childKomoOid)
				.path("parents")
				.accept(JSON_UTF8)
				.get(new GenericType<ResultV1RDTO<Set<String>>>() {
				});
	}

	@Override
	public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> getHigherEducationByKomo(
			String komoOid) {
		return this.higherEducationResource
				.path("search")
				.queryParam("komoOid", komoOid)
				.accept(JSON_UTF8)
				.get(new GenericType<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>() {
				});
	}	
}
