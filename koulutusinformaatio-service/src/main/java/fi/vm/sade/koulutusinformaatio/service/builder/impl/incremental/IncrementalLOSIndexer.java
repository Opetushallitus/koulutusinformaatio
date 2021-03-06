/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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
package fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental;

import fi.vm.sade.koulutusinformaatio.domain.exception.*;
import fi.vm.sade.koulutusinformaatio.service.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakutuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KomoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Markus
 *
 */
@Component
public class IncrementalLOSIndexer {

    private static final Logger LOG = LoggerFactory.getLogger(IncrementalLOSIndexer.class);

    private TarjontaRawService tarjontaRawService;
    private TarjontaService tarjontaService;
    private EducationIncrementalDataUpdateService dataUpdateService;
    private EducationIncrementalDataQueryService dataQueryService;
    private IndexerService indexerService;

    private final HttpSolrServer loHttpSolrServer;
    // solr client for learning opportunity provider index
    private final HttpSolrServer lopHttpSolrServer;

    private final HttpSolrServer locationHttpSolrServer;

    private IncrementalHigherEducationLOSIndexer higherEdLOSIndexer;
    private IncrementalAdultLOSIndexer adultLosIndexer;

    private IncrementalKoulutusLOSIndexer koulutusIndexer;

    @Autowired
    public IncrementalLOSIndexer (TarjontaRawService tarjontaRawService,
                                    TarjontaService tarjontaService,
                                    EducationIncrementalDataUpdateService dataUpdateService,
                                    EducationIncrementalDataQueryService dataQueryService,
                                    IndexerService indexerService,
                                    HttpSolrServer loHttpSolrServer,
                                    HttpSolrServer lopHttpSolrServer,
                                    HttpSolrServer locationHttpSolrServer) {

        this.tarjontaRawService = tarjontaRawService;
        this.tarjontaService = tarjontaService;
        this.dataUpdateService = dataUpdateService;
        this.dataQueryService = dataQueryService;
        this.indexerService = indexerService;
        this.loHttpSolrServer = loHttpSolrServer;
        this.lopHttpSolrServer = lopHttpSolrServer;
        this.locationHttpSolrServer = locationHttpSolrServer;
        this.higherEdLOSIndexer = new IncrementalHigherEducationLOSIndexer(this.tarjontaRawService,
                                                                            this.tarjontaService,
                                                                            this.dataUpdateService,
                                                                            this.dataQueryService,
                                                                            this.indexerService,
                                                                            this.loHttpSolrServer,
                                                                            this.lopHttpSolrServer,
                                                                            this.locationHttpSolrServer);
        this.adultLosIndexer = new IncrementalAdultLOSIndexer(this.tarjontaService,
                this.dataUpdateService,
                this.indexerService,
                this.loHttpSolrServer,
                this.lopHttpSolrServer,
                this.locationHttpSolrServer);
        this.koulutusIndexer = new IncrementalKoulutusLOSIndexer(tarjontaService, dataUpdateService, dataQueryService, indexerService,
                loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer);
    }

    //Indexes changed loi data
    public void indexLoiData(String komotoOid) throws KISolrException, ResourceNotFoundException, KoodistoException, NoValidApplicationOptionsException, TarjontaParseException, OrganisaatioException {

        LOG.info(String.format("Indexing loi: %s", komotoOid));
        ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> dto = this.tarjontaRawService.searchEducation(komotoOid);
        if (dto == null ||
                dto.getResult() == null ||
                dto.getResult().getTulokset() == null ||
                dto.getResult().getTulokset().isEmpty() ||
                dto.getResult().getTulokset().get(0) == null ||
                dto.getResult().getTulokset().get(0).getTulokset() == null ||
                dto.getResult().getTulokset().get(0).getTulokset().isEmpty() ||
                dto.getResult().getTulokset().get(0).getTulokset().get(0) == null) {
            return;
        }
        KoulutusHakutulosV1RDTO koulutusDTO = dto.getResult().getTulokset().get(0).getTulokset().get(0);
        LOG.debug(String.format("Loi: %s, status: %s", komotoOid, koulutusDTO.getTila()));

        switch (koulutusDTO.getToteutustyyppiEnum()) {

        case KORKEAKOULUTUS:
            LOG.debug(String.format("It is higer education komoto: %s", komotoOid));
            ResultV1RDTO<KoulutusV1RDTO> koulutusRes = this.tarjontaRawService.getV1KoulutusLearningOpportunity(komotoOid);
            if (koulutusRes != null && koulutusRes.getResult() != null && koulutusRes.getResult().getKomoOid() != null) {
                this.higherEdLOSIndexer.indexHigherEdKomo(koulutusRes.getResult().getKomoOid());
            }
            break;

        case LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA:
        case EB_RP_ISH:
            LOG.debug("Aikuislukio ja Yhteishaun ulkopuolinen lukiokoulutus: {}", koulutusDTO.getKomoOid());
            this.koulutusIndexer.indexSingleKoulutusWithoutRelations(koulutusDTO);
            break;

        case AMMATTITUTKINTO:
        case ERIKOISAMMATTITUTKINTO:
        case AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA:
            LOG.debug("Adult vocational komo: {}", koulutusDTO.getKomoOid());
            this.adultLosIndexer.indexAdultVocationalKomoto(komotoOid);
            break;

        case VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS:
        case PERUSOPETUKSEN_LISAOPETUS:
        case AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA:
        case AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA_ER:
        case VAPAAN_SIVISTYSTYON_KOULUTUS: // Kansanopistot
        case PELASTUSALAN_KOULUTUS:
        case MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS:
            LOG.debug("Valma/Telma/VaSi/Pelastusalan koulutus: {}", koulutusDTO.getKomoOid());
            this.koulutusIndexer.indexSingleKoulutusWithoutRelations(koulutusDTO);
            break;

        case AMMATILLINEN_PERUSTUTKINTO: // Ammatillinen
        case AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA: // Ammatillinen
            LOG.debug("Ammatillinen koulutus: {}", koulutusDTO.getOid());
            koulutusIndexer.indexAmmatillinenKoulutusKomoto(koulutusDTO);
            break;

        case AMMATILLINEN_PERUSTUTKINTO_ALK_2018: // Ammatillinen
            LOG.debug("Ammatillinen koulutus ALK 2018: {}", koulutusDTO.getOid());
            koulutusIndexer.indexAmmatillinenKoulutusALK2018Komoto(koulutusDTO);
            break;

        case LUKIOKOULUTUS: // Lukiokoulutus
            LOG.debug("Lukiokoulutus: {}", koulutusDTO.getKomoOid());
            koulutusIndexer.indexSingleKoulutusWithoutRelations(koulutusDTO);
            break;

        case KORKEAKOULUOPINTO: // Opintokokonaisuus ja opintojakso
            LOG.debug("Korkeakouluopinto: {}", koulutusDTO.getOid());
            koulutusIndexer.indexKorkeakouluopintoKomoto(koulutusDTO);
            break;

        case AIKUISTEN_PERUSOPETUS: // Tulossa
        case AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS: // Poistunut
        case MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS: // Poistunut
        case ESIOPETUS: // Ei käytössä
        case PERUSOPETUS: // Ei käytössä
            break;

        default:
            break;
        }
    }

    public boolean isHigherEdKomo(String komoOid) {
        //this.tarjontaRawService.getHigherEducationByKomo(curKomoOid)
        //KomoDTO komo = this.tarjontaRawService.getKomo(komoOid);

        ResultV1RDTO<KomoV1RDTO> komoRes = this.tarjontaRawService.getV1Komo(komoOid);
        return komoRes != null && komoRes.getResult() != null && komoRes.getResult().getKoulutusasteTyyppi().value().equals(KoulutusasteTyyppi.KORKEAKOULUTUS.value());

        //return komo != null && komo.getKoulutustyyppi() != null && komo.getKoulutustyyppi().equals("KORKEAKOULUTUS");
    }

    public void indexHigherEdKomo(String komoOid) throws KISolrException {
        this.higherEdLOSIndexer.indexHigherEdKomo(komoOid);
    }

    public void indexKoulutusLos(String komotoOid) throws KISolrException, ResourceNotFoundException, KoodistoException, NoValidApplicationOptionsException, TarjontaParseException, OrganisaatioException {
        if (!tarjontaService.hasAlreadyProcessedOid(komotoOid))
            indexLoiData(komotoOid);
    }

}
