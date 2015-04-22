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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.vm.sade.koulutusinformaatio.domain.AdultUpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOI;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOIRef;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOS;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOI;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOSRef;
import fi.vm.sade.koulutusinformaatio.domain.SpecialLOS;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOI;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataUpdateService;
import fi.vm.sade.koulutusinformaatio.service.IndexerService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakutuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.TarjoajaHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KomoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusGenericV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;

/**
 * 
 * @author Markus
 *
 */
public class IncrementalLOSIndexer {
    
    public static final Logger LOG = LoggerFactory.getLogger(IncrementalLOSIndexer.class);
    
    private TarjontaRawService tarjontaRawService;
    private TarjontaService tarjontaService;
    private EducationIncrementalDataUpdateService dataUpdateService;
    private EducationIncrementalDataQueryService dataQueryService;
    private IndexerService indexerService;
    
    private final HttpSolrServer loHttpSolrServer;
    // solr client for learning opportunity provider index
    private final HttpSolrServer lopHttpSolrServer;

    private final HttpSolrServer locationHttpSolrServer;
    
    List<String> createdLOS = new ArrayList<String>();
    
    private SingleParentLOSBuilder parentLosBuilder;
    private SingleSpecialLOSBuilder specialLosBuilder;
    private SingleUpperSecondaryLOSBuilder upperSecLosBuilder;
    
    private IncrementalHigherEducationLOSIndexer higherEdLOSIndexer;
    private IncrementalAdultLOSIndexer adultLosIndexer;

    private IncrementalKoulutusLOSIndexer koulutusIndexer;
    
    public IncrementalLOSIndexer (TarjontaRawService tarjontaRawService, 
                                    TarjontaService tarjontaService, 
                                    EducationIncrementalDataUpdateService dataUpdateService,
                                    EducationIncrementalDataQueryService dataQueryService,
                                    IndexerService indexerService,
                                    HttpSolrServer loHttpSolrServer,
                                    HttpSolrServer lopHttpSolrServer,
                                    HttpSolrServer locationHttpSolrServer,
                                    SingleParentLOSBuilder parentLosBuilder,
                                    SingleSpecialLOSBuilder specialLosBuilder,
                                    SingleUpperSecondaryLOSBuilder upperSecLosBuilder) {
        
        this.tarjontaRawService = tarjontaRawService;
        this.tarjontaService = tarjontaService;
        this.dataUpdateService = dataUpdateService;
        this.dataQueryService = dataQueryService;
        this.indexerService = indexerService;
        this.loHttpSolrServer = loHttpSolrServer;
        this.lopHttpSolrServer = lopHttpSolrServer;
        this.locationHttpSolrServer = locationHttpSolrServer;
        this.parentLosBuilder = parentLosBuilder;
        this.specialLosBuilder = specialLosBuilder;
        this.upperSecLosBuilder = upperSecLosBuilder;
        this.higherEdLOSIndexer = new IncrementalHigherEducationLOSIndexer(this.tarjontaRawService, 
                                                                            this.tarjontaService, 
                                                                            this.dataUpdateService, 
                                                                            this.dataQueryService, 
                                                                            this.indexerService, 
                                                                            this.loHttpSolrServer, 
                                                                            this.lopHttpSolrServer, 
                                                                            this.locationHttpSolrServer);      
        this.adultLosIndexer = new IncrementalAdultLOSIndexer(this.tarjontaRawService, 
                this.tarjontaService, 
                this.dataUpdateService, 
                this.dataQueryService, 
                this.indexerService, 
                this.loHttpSolrServer, 
                this.lopHttpSolrServer, 
                this.locationHttpSolrServer);  
        this.koulutusIndexer = new IncrementalKoulutusLOSIndexer(tarjontaRawService, tarjontaService, dataUpdateService, dataQueryService, indexerService,
                loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer);
    }
    

    public boolean isLoiAlreadyHandled(List<OidRDTO> aoOidDtos, List<String> changedHakukohdeOids) {

        for (OidRDTO curOidDto : aoOidDtos) {
            if (changedHakukohdeOids.contains(curOidDto.getOid())) {
                return true;
            }
        }

        return false;
    }

    //Indexes changed loi data
    public void indexLoiData(String komotoOid) throws Exception {
        LOG.debug(String.format("Indexing loi: %s", komotoOid));
        KoulutusGenericV1RDTO koulutusDTO = this.tarjontaRawService.getV1KoulutusLearningOpportunity(komotoOid).getResult();
        if(koulutusDTO == null){
            return;
        }
        LOG.debug(String.format("Loi: %s, status: %s", komotoOid, koulutusDTO.getTila()));
        
        switch (koulutusDTO.getToteutustyyppi()) {
        
        case KORKEAKOULUTUS:
            LOG.debug(String.format("It is higer education komoto: %s", komotoOid));
            ResultV1RDTO<KoulutusKorkeakouluV1RDTO> koulutusRes = this.tarjontaRawService.getHigherEducationLearningOpportunity(komotoOid);
            if (koulutusRes != null && koulutusRes.getResult() != null && koulutusRes.getResult().getKomoOid() != null) {
                this.higherEdLOSIndexer.indexHigherEdKomo(koulutusRes.getResult().getKomoOid());
            }
            return;
        
        case LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA:
            LOG.debug("Adult upsec komo: " + koulutusDTO.getKomoOid());
            this.indexAdultUpsecKomo(koulutusDTO.getKomoOid());
            return;

        case AMMATTITUTKINTO:
        case ERIKOISAMMATTITUTKINTO:
        case AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA:
            LOG.debug("Adult vocational komo: " + koulutusDTO.getKomoOid());
            this.indexAdultVocationalKomoto(komotoOid);
            return;

        case VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS:
        case PERUSOPETUKSEN_LISAOPETUS:
        case AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA:
        case AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA_ER:
        case MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS:
            LOG.debug("Valma/Telma koulutus: " + koulutusDTO.getKomoOid());
            this.indexValmentavaKomoto(komotoOid);
            return;
            
        case AMMATILLINEN_PERUSTUTKINTO: // Ammatillinen
        case AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA: // Ammatillinen
        case LUKIOKOULUTUS: // Lukiokoulutus
        case EB_RP_ISH: // Lukiokoulutus
        case VAPAAN_SIVISTYSTYON_KOULUTUS: // Kansanopistot
        case KORKEAKOULUOPINTO: // Tulossa
        case AIKUISTEN_PERUSOPETUS: // Tulossa
        case AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS: // Poistunut
        case MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS: // Poistunut
        case ESIOPETUS: // Ei käytössä
        case PERUSOPETUS: // Ei käytössä
            break;

        default:
            break;
        }
        
        
        // TODO: Nämä liitettäväksi ylläolevaan luetteloon V1 muutosten yhteydessä
        KomotoDTO komotoDto = null;
        try {
            komotoDto = this.tarjontaRawService.getKomoto(komotoOid);
        } catch (Exception ex) {
            return;
        }
        if (isSecondaryEducation(komotoDto)) {
            if (!isLoiProperlyPublished(komotoDto)) {
                LOG.debug(String.format("Loi need to be removed: %s", komotoOid));
                handleLoiRemoval(komotoDto);
            } else {
                LOG.debug(String.format("Loi need to be indexed: %s", komotoOid));
                handleLoiAdditionOrUpdate(komotoDto);
            }
        }
    }

    private void indexValmentavaKomoto(String komotoOid) throws Exception {
        this.koulutusIndexer.indexKoulutusKomoto(komotoOid);
    }


    public void indexAdultVocationalKomoto(String komotoOid) throws Exception {
        this.adultLosIndexer.indexAdultVocationalKomoto(komotoOid);
    }


    private boolean isAdultVocationalKomo(String komoOid) {
        ResultV1RDTO<KomoV1RDTO> komoRes = this.tarjontaRawService.getV1Komo(komoOid);
        
        
        boolean isAdultVoc = komoRes != null 
                            && komoRes.getResult() != null 
                            && (komoRes.getResult().getKoulutusasteTyyppi().name().equals(KoulutusasteTyyppi.AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA.name())
                             || komoRes.getResult().getKoulutusasteTyyppi().name().equals(KoulutusasteTyyppi.AMMATTITUTKINTO.name())
                             || komoRes.getResult().getKoulutusasteTyyppi().name().equals(KoulutusasteTyyppi.ERIKOISAMMATTITUTKINTO.name())
                             || komoRes.getResult().getKoulutusasteTyyppi().name().equals(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS.name()));
        
        if (!isAdultVoc) {
            LOG.debug("Is adult vocational komo, returning: " + isAdultVoc);
            return false;
        }
        
        
        LOG.debug("Komo: " + komoRes.getResult().getKoulutusasteTyyppi().name());
        
        ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> komotoRes = this.tarjontaRawService.getHigherEducationByKomo(komoOid);
        
        if (komotoRes != null 
                && komotoRes.getResult() != null 
                && komotoRes.getResult().getTulokset() != null 
                && !komotoRes.getResult().getTulokset().isEmpty()) {


            for (TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> tarjResult :  komotoRes.getResult().getTulokset()) {
                if (tarjResult.getTulokset() !=  null && !tarjResult.getTulokset().isEmpty()) {
                    for (KoulutusHakutulosV1RDTO curKoul : tarjResult.getTulokset()) {
                        if (curKoul.getKoulutuslajiUri() != null && curKoul.getKoulutuslajiUri().contains(TarjontaConstants.AIKUISKOULUTUS)) {
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }


    private boolean isLoiProperlyPublished(KomotoDTO komotoDto) {

        if (!komotoDto.getTila().name().equals(TarjontaConstants.STATE_PUBLISHED)) {
            return false;
        }

        List<OidRDTO> hakukohdeOids = this.tarjontaRawService.getHakukohdesByKomoto(komotoDto.getOid());

        if (hakukohdeOids != null) {
            for (OidRDTO curOidDto : hakukohdeOids) {
                HakukohdeDTO aoDto = this.tarjontaRawService.getHakukohde(curOidDto.getOid());
                HakuDTO hakuDto = this.tarjontaRawService.getHaku(aoDto.getHakuOid());
                if (hakuDto.getTila().equals(TarjontaConstants.STATE_PUBLISHED) && hakuDto.getTila().equals(TarjontaConstants.STATE_PUBLISHED)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isSecondaryEducation(KomotoDTO komotoDto) {
        boolean isNotHigherEd = !isHigherEdKomo(komotoDto.getKomoOid());
        boolean hasCorrectKoulutuslaji = (komotoDto.getKoulutuslajiUris() != null 
                && !komotoDto.getKoulutuslajiUris().isEmpty() &&
                !komotoDto.getKoulutuslajiUris().get(0).contains("koulutuslaji_a"))
                || (komotoDto.getKoulutuslajiUris() == null || komotoDto.getKoulutuslajiUris().isEmpty());
        boolean isRehabLOS = this.isRehabLOS(komotoDto.getKomoOid());
        return isNotHigherEd && (hasCorrectKoulutuslaji || isRehabLOS);
    }

    private boolean isRehabLOS(String komoOid) {
        KomoDTO komo = this.tarjontaRawService.getKomo(komoOid);
        if (komo != null) {
            return this.isRehabLOS(komo);
        }
        return false;
    }


    public boolean isHigherEdKomo(String komoOid) {
        //this.tarjontaRawService.getHigherEducationByKomo(curKomoOid)
        //KomoDTO komo = this.tarjontaRawService.getKomo(komoOid);
        
        ResultV1RDTO<KomoV1RDTO> komoRes = this.tarjontaRawService.getV1Komo(komoOid);
        return komoRes != null && komoRes.getResult() != null && komoRes.getResult().getKoulutusasteTyyppi().value().equals(KoulutusasteTyyppi.KORKEAKOULUTUS.value());
        
        //return komo != null && komo.getKoulutustyyppi() != null && komo.getKoulutustyyppi().equals("KORKEAKOULUTUS");
    }
    

    public boolean isAdultUpsecKomo(String komoOid) {
        ResultV1RDTO<KomoV1RDTO> komoRes = this.tarjontaRawService.getV1Komo(komoOid);
        
        boolean isLukio = komoRes != null && komoRes.getResult() != null && komoRes.getResult().getKoulutusasteTyyppi().value().equals(KoulutusasteTyyppi.LUKIOKOULUTUS.value());

        if (komoRes.getResult() != null && komoRes.getResult().getKoulutusasteTyyppi() != null) {
            LOG.debug("komo koulutusaste: " + komoRes.getResult().getKoulutusasteTyyppi().name());
        } else {
            LOG.debug("some wierd komo");
        }
        
        if (!isLukio) {
            return false;
        }
        
        ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> komotoRes = this.tarjontaRawService.getHigherEducationByKomo(komoOid);
        
        if (komotoRes != null 
                && komotoRes.getResult() != null 
                && komotoRes.getResult().getTulokset() != null 
                && !komotoRes.getResult().getTulokset().isEmpty()) {


            for (TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> tarjResult :  komotoRes.getResult().getTulokset()) {
                if (tarjResult.getTulokset() !=  null && !tarjResult.getTulokset().isEmpty()) {
                    for (KoulutusHakutulosV1RDTO curKoul : tarjResult.getTulokset()) {
                        if (curKoul.getKoulutuslajiUri() != null && curKoul.getKoulutuslajiUri().contains(TarjontaConstants.AIKUISKOULUTUS)) {
                            return true;
                        }
                    }
                }
            }
        }
        
        
        return false;
    }

    private void handleLoiAdditionOrUpdate(KomotoDTO komotoDto) throws KoodistoException, SolrServerException, IOException, TarjontaParseException {
        LOG.debug(String.format("Handling addition or update: %s", komotoDto.getOid()));
        List<LOS> referencedLoss = this.dataQueryService.findLearningOpportunitiesByLoiId(komotoDto.getOid());
        //Loi is in mongo, thus it is updated
        if (referencedLoss != null && !referencedLoss.isEmpty()) {
            LOG.debug(String.format("Loi is in mongo, it should be updated: %s", komotoDto.getOid()));
            handleLoiRemoval(komotoDto);
            handleSecondaryLoiAddition(komotoDto);
            //Loi is not in mongo, so it is added    
        } else {
            LOG.debug(String.format("Loi is not in mongo, it should be added: %s", komotoDto.getOid()));
            handleSecondaryLoiAddition(komotoDto);;
        }

    }

    //Handles removal of loi
    private void handleLoiRemoval(KomotoDTO komotoDto) throws TarjontaParseException, KoodistoException, SolrServerException, IOException {


        List<LOS> referencedLoss = this.dataQueryService.findLearningOpportunitiesByLoiId(komotoDto.getOid());
        if (referencedLoss != null && !referencedLoss.isEmpty()) {
            for (LOS referencedLos : referencedLoss) {
                if (referencedLos instanceof ParentLOS) {
                    handleLoiRemovalFromParentLOS((ParentLOS)referencedLos, komotoDto.getOid());
                } else if (referencedLos instanceof SpecialLOS) {
                    handleLoiRemovalFromSpecialLOS((SpecialLOS)referencedLos, komotoDto.getOid());
                } else if (referencedLos instanceof ChildLOS) {
                    handleLoiRemovalFromChildLos((ChildLOS)referencedLos, komotoDto.getOid());      
                } else if (referencedLos instanceof UpperSecondaryLOS) {
                    handleLoiRemovalFromUpperSecondarLos((UpperSecondaryLOS)referencedLos);
                }
            }
        } 
    }

    private void handleLoiRemovalFromSpecialLOS(SpecialLOS los, String komotoOid) throws TarjontaParseException, KoodistoException, SolrServerException, IOException {
        KomotoDTO komotoDto = this.tarjontaRawService.getKomoto(komotoOid);
        KomoDTO komo = this.tarjontaRawService.getKomoByKomoto(komotoOid);
        this.reCreateSpecialLOS(los, komotoDto, komo, komotoDto.getTarjoajaOid());
    }

    private void handleLoiRemovalFromParentLOS(ParentLOS los, String komotoOid) throws TarjontaParseException, KoodistoException, SolrServerException, IOException {
        LOG.debug("handling removal from parent los");
        this.reCreateParentLOS(los.getId().split("_")[0], los);
    }

    private void handleLoiRemovalFromChildLos(ChildLOS referencedLos, String komotoOid) throws TarjontaParseException, KoodistoException, SolrServerException, IOException {
        this.reCreateChildLOS(referencedLos);

    }

    private void handleLoiRemovalFromUpperSecondarLos(UpperSecondaryLOS los) throws TarjontaParseException, KoodistoException, IOException, SolrServerException {

        this.reCreateUpperSecondaryLOS(los);

    }

    public void handleSecondaryLoiAddition(KomotoDTO komotoDto) throws KoodistoException, SolrServerException, IOException {
        LOG.debug(String.format("Adding loi: %s", komotoDto.getOid()));
        try {
            String providerOid = komotoDto.getTarjoajaOid();
            String komoOid = komotoDto.getKomoOid();
            KomoDTO komo = this.tarjontaRawService.getKomo(komotoDto.getKomoOid());
            String basicLosId = String.format("%s_%s", komoOid, providerOid);

            if (isRehabLOS(komo)) {
                basicLosId = String.format("%s_%s", basicLosId, komotoDto.getOid());
            } else if (isSpecialVocationalLos(komotoDto)) {
                basicLosId = String.format("%s_er", basicLosId);
            }

            if (this.createdLOS.contains(basicLosId)) {
                return;
            }

            LOS los = this.dataQueryService.getLos(basicLosId);
            if (los != null && los instanceof ChildLOS) {
                LOG.debug(String.format("A suitable child los for loi: %s is in mongo, updating los: %s", komotoDto.getOid(), los.getId()));
                reCreateChildLOS((ChildLOS)los);
            } else if (los != null && los instanceof UpperSecondaryLOS) {
                LOG.debug(String.format("A suitable upper secondary los for loi: %s is in mongo, updating los: %s", komotoDto.getOid(), los.getId()));
                this.reCreateUpperSecondaryLOS((UpperSecondaryLOS)los);
            } else if (los != null && los instanceof SpecialLOS) {
                LOG.debug(String.format("A suitable special los for loi: %s is in mongo, updating los: %s", komotoDto.getOid(), los.getId()));
                this.reCreateSpecialLOS((SpecialLOS)los, komotoDto, komo, komotoDto.getTarjoajaOid());
                //Trying to find parent los
            } else if (los == null) {
                List<String> ylamoduulit = komo.getYlaModuulit();
                if (ylamoduulit != null && ylamoduulit.size() > 0) {
                    los = this.dataQueryService.getLos(String.format("%s_%s", ylamoduulit.get(0), providerOid));
                }

            }
            //If parent los is found, it is recreated
            if (los != null && los instanceof ParentLOS) {
                LOG.debug(String.format("A suitable parent los for loi: %s is in mongo, updating los: %s", komotoDto.getOid(), los.getId()));
                KomoDTO parentKomo = this.tarjontaRawService.getKomoByKomoto(komotoDto.getParentKomotoOid());
                los = reCreateParentLOS(parentKomo.getOid(), (ParentLOS)los);
                LOG.debug("Parent los recreated");
            } 

            //if no matching los was there to be updated, a new los is created completely
            if (los == null) {
                LOG.debug(String.format("There was no los for komoto: %s, creating it", komotoDto.getOid()));
                createNewLos(komo, komotoDto);
            }

        } catch (TarjontaParseException ex) {
            LOG.warn(String.format("Problem adding loi incrementally with loi: %s. %s",  komotoDto.getOid(), ex.getMessage()));
        }
    }

    private void createNewLos(KomoDTO komo, KomotoDTO komotoDto) throws TarjontaParseException, KoodistoException, SolrServerException, IOException {
        LOS los = null;
        String komoOid = komo.getOid();
        String educationType = komo.getKoulutusTyyppiUri();
        if (educationType.equals(TarjontaConstants.VOCATIONAL_EDUCATION_TYPE) && !this.isSpecialVocationalLos(komotoDto)) {

            if (komo.getModuuliTyyppi().equals(TarjontaConstants.MODULE_TYPE_CHILD)) {
                komoOid = komo.getYlaModuulit().get(0);
            }
            los = createParentLOS(komoOid, komotoDto.getTarjoajaOid());
            LOG.debug(String.format("Created parentLos %s for komoto: %s, creating it", (los != null ? los.getId() : null), komotoDto.getOid()));
        } else if (this.isSpecialVocationalLos(komotoDto) || this.isRehabLOS(komo)) {
            los = createSpecialLOS(komo, komotoDto, komotoDto.getTarjoajaOid());

        } else if (educationType.equals(TarjontaConstants.UPPER_SECONDARY_EDUCATION_TYPE) && komo.getModuuliTyyppi().equals(TarjontaConstants.MODULE_TYPE_CHILD)) {
            los = this.createUpperSecondaryLOS(komo, komotoDto.getTarjoajaOid());
        }
        if (los == null) {
            LOG.warn("Los is still null after everything was tried. Komo oid is: " + komo.getOid());
        }

    }

    private boolean isSpecialVocationalLos(KomotoDTO komotoDto) {
        return komotoDto.getPohjakoulutusVaatimusUri() != null && komotoDto.getPohjakoulutusVaatimusUri().contains(TarjontaConstants.PREREQUISITE_URI_ER);        
    }

    private boolean isRehabLOS(KomoDTO komo) {

        String educationType  = komo.getKoulutusTyyppiUri();

        if (educationType.equals(TarjontaConstants.REHABILITATING_EDUCATION_TYPE) &&
                komo.getModuuliTyyppi().equals(TarjontaConstants.MODULE_TYPE_CHILD)) {
            return true;
        } 
        else if ((educationType.equals(TarjontaConstants.PREPARATORY_VOCATIONAL_EDUCATION_TYPE) 
                || educationType.equals(TarjontaConstants.TENTH_GRADE_EDUCATION_TYPE)
                || educationType.equals(TarjontaConstants.IMMIGRANT_PREPARATORY_VOCATIONAL)
                || educationType.equals(TarjontaConstants.IMMIGRANT_PREPARATORY_UPSEC)
                || educationType.endsWith(TarjontaConstants.KANSANOPISTO_TYPE))
                && komo.getModuuliTyyppi().equals(TarjontaConstants.MODULE_TYPE_CHILD)) {
            return true;
        }
        return false;
    }

    private void reCreateChildLOS(ChildLOS los) throws TarjontaParseException, KoodistoException, SolrServerException, IOException {

        ParentLOSRef parentRef = ((ChildLOS) los).getParent();
        LOG.debug("parent to retrieve: " + parentRef.getId());
        ParentLOS parent = (ParentLOS)(this.dataQueryService.getLos(parentRef.getId()));
        if (parent != null) {
            this.reCreateParentLOS(parent.getId().split("_")[0], parent);
        } else {
            LOG.error("Parent: " + parentRef.getId() + " not found");
        }

    }

    public ParentLOS reCreateParentLOS(String parentKomoOid, ParentLOS los) throws TarjontaParseException, KoodistoException, SolrServerException, IOException {
        LOG.debug("Recreating parent los: " + los.getId());

        this.removeParentLOS(los);
        ParentLOS parent = createParentLOS(parentKomoOid, los.getProvider().getId());

        return parent;
    }
    
    public void removeParentLOS(ParentLOS los) throws IOException, SolrServerException {
        this.deleteParentLOSRecursively(los);
        this.indexerService.removeLos(los, loHttpSolrServer);
        this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
    }

    private ParentLOS createParentLOS(String parentKomoOid, String providerId) throws TarjontaParseException, KoodistoException, SolrServerException, IOException {
        LOG.debug("Creating parent los: " + parentKomoOid + "_" + providerId);

        KomoDTO parentKomo = this.tarjontaRawService.getKomo(parentKomoOid);

        ParentLOS parent = this.parentLosBuilder.createParentLOS(parentKomo, providerId);
        LOG.debug("Creating child losses for parent: " + parent.getId());
        List<ChildLOS> children = this.parentLosBuilder.createChildLoss(parentKomo, parent);
        LOG.debug("Assembing child losses which amount to: " + children.size());
        this.parentLosBuilder.assembleParentLos(parent, children);
        LOG.debug("Filtering child losses which amount to: " + parent.getChildren().size());
        parent = this.parentLosBuilder.filterParentLos(parent);


        if (parent != null) {
            LOG.debug("Saving parent los: " +parent.getId() + " with " + parent.getChildren().size() + " children");
            this.dataUpdateService.save(parent);
            this.indexerService.addLearningOpportunitySpecification(parent, loHttpSolrServer, lopHttpSolrServer);
            this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
            //this.updateSolrMaps(parent, ADDITION);
            this.createdLOS.add(parent.getId());
        } 

        

        return parent;
    }

    public SpecialLOS reCreateSpecialLOS(SpecialLOS los, KomotoDTO komotoDto, KomoDTO komo, String providerId) throws SolrServerException, IOException, TarjontaParseException, KoodistoException {
        this.removeSpecialLOS(los);
        SpecialLOS specialLos = this.createSpecialLOS(komo, komotoDto, providerId);
        return specialLos;

    }
    
    public void removeSpecialLOS(SpecialLOS los) throws IOException, SolrServerException {
        this.deleteSpecialLosRecursively(los);
        this.indexerService.removeLos(los, loHttpSolrServer);
        this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
    }

    public void updateParentLos(ParentLOS los) throws IOException, SolrServerException  {
        this.deleteParentLOSRecursively(los);
        this.indexerService.removeLos(los, loHttpSolrServer);
        this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
        this.dataUpdateService.save(los);
        this.indexerService.addLearningOpportunitySpecification(los, loHttpSolrServer, lopHttpSolrServer);
        this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);

    }

    public void updateSpecialLos(SpecialLOS los) throws IOException, SolrServerException  {
        this.deleteSpecialLosRecursively(los);
        this.indexerService.removeLos(los, loHttpSolrServer);
        this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
        this.dataUpdateService.save(los);
        this.indexerService.addLearningOpportunitySpecification(los, loHttpSolrServer, lopHttpSolrServer);
        this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
    }

    public void updateUpsecLos(UpperSecondaryLOS los) throws IOException, SolrServerException  {
        this.deleteUpperSecondaryLosRecursive(los);
        this.indexerService.removeLos(los, loHttpSolrServer);
        this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
        this.dataUpdateService.save(los);
        this.indexerService.addLearningOpportunitySpecification(los, loHttpSolrServer, lopHttpSolrServer);
        this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
    }
    

    public void updateHigherEdLos(HigherEducationLOS curLos) throws Exception {
        
        this.higherEdLOSIndexer.updateHigherEdLos(curLos);
        
    }
    
    public void updateAdultUpsecLos(AdultUpperSecondaryLOS curLos) throws Exception {
        this.adultLosIndexer.updateAdultUpsecLos(curLos);
    }


    private SpecialLOS createSpecialLOS(KomoDTO komo, KomotoDTO komoto, String providerId) throws SolrServerException, IOException, TarjontaParseException, KoodistoException {
        SpecialLOS specialLos = this.specialLosBuilder.createSpecialLOS(komo, komoto, this.isRehabLOS(komo), providerId);

        if (specialLos != null) {
            LOG.debug("Saving special los: " + specialLos.getId() + " with " + specialLos.getLois().size() + " children");
            this.dataUpdateService.save(specialLos);
            this.indexerService.addLearningOpportunitySpecification(specialLos, loHttpSolrServer, lopHttpSolrServer);
            this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
            //this.updateSolrMaps(specialLos, ADDITION);
            this.createdLOS.add(specialLos.getId());
        }
        
        return specialLos;
    }

    private UpperSecondaryLOS reCreateUpperSecondaryLOS(UpperSecondaryLOS los) throws IOException, SolrServerException, TarjontaParseException, KoodistoException {
        LOG.debug("recreating upper secondary los: " + los.getId());
        this.removeUpperSecondaryLOS(los);
        KomoDTO komo = this.tarjontaRawService.getKomo(los.getId().split("_")[0]);
        return this.createUpperSecondaryLOS(komo, los.getProvider().getId());
    }
    
    public void removeUpperSecondaryLOS(UpperSecondaryLOS los) throws IOException, SolrServerException {
        this.deleteUpperSecondaryLosRecursive(los);
        this.indexerService.removeLos(los, loHttpSolrServer);
        this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
    }

    private UpperSecondaryLOS createUpperSecondaryLOS(KomoDTO komo, String providerId) throws TarjontaParseException, KoodistoException, IOException, SolrServerException {
        UpperSecondaryLOS newLos = this.upperSecLosBuilder.createUpperSecondaryLOS(komo, providerId);
        if (newLos != null) {
            LOG.debug("Saving special los: " + newLos.getId() + " with " + newLos.getLois().size() + " children");
            this.dataUpdateService.save(newLos);
            this.indexerService.addLearningOpportunitySpecification(newLos, loHttpSolrServer, lopHttpSolrServer);
            this.indexerService.commitLOChanges(loHttpSolrServer, lopHttpSolrServer, locationHttpSolrServer, true);
            //this.updateSolrMaps(newLos, ADDITION);
            this.createdLOS.add(newLos.getId());
        }
        
        return newLos;
    }

    private void deleteUpperSecondaryLosRecursive(UpperSecondaryLOS los) {
        this.dataUpdateService.deleteLos(los);
        for (UpperSecondaryLOI curLoi : los.getLois()) {
            for (ApplicationOption curAo : curLoi.getApplicationOptions()) {
                this.dataUpdateService.deleteAo(curAo);
            }
        }

    }

    private void deleteSpecialLosRecursively(SpecialLOS los) {
        this.dataUpdateService.deleteLos(los);
        for (ChildLOI curChildLoi : los.getLois()) {
            for (ApplicationOption curAo :curChildLoi.getApplicationOptions()) {
                this.dataUpdateService.deleteAo(curAo);
            }
        }
    }

    private void deleteParentLOSRecursively(ParentLOS los) {

        this.dataUpdateService.deleteLos(los); 

        for (ParentLOI curParentLOI : los.getLois()) {
            for (ApplicationOption curAo : curParentLOI.getApplicationOptions()) {
                this.dataUpdateService.deleteAo(curAo);
            }
        }

        for (ChildLOS curChild : los.getChildren()) {
            this.dataUpdateService.deleteLos(curChild);
            for (ChildLOI curChildLoi : curChild.getLois()) {
                for (ApplicationOption curAo : curChildLoi.getApplicationOptions()) {
                    this.dataUpdateService.deleteAo(curAo);
                }
            }
        }
    }

    

    //Removal based on child loi references
    public void handleLoiRefRemoval(ApplicationOption ao) throws TarjontaParseException, KoodistoException, SolrServerException, IOException {
        LOG.debug("In handleLoiRefRemoval from ao ");
        for (ChildLOIRef curChildRef : ao.getChildLOIRefs()) {
            LOS referencedLos = this.dataQueryService.getLos(curChildRef.getLosId());
            if (referencedLos instanceof UpperSecondaryLOS) {
                LOG.debug("The referenced los is UpperSecondary");
                handleUpperSecondarLosReferenceRemoval((UpperSecondaryLOS)referencedLos);
            } else if (referencedLos instanceof SpecialLOS) {
                LOG.debug("The referenced los is SpecialLOS");
                this.handleSpecialLOSReferenceRemoval((SpecialLOS)referencedLos);
            }
        }
    }

    private void handleUpperSecondarLosReferenceRemoval(
            UpperSecondaryLOS los) throws IOException, SolrServerException, TarjontaParseException, KoodistoException {
        LOG.debug("Handling uppersecondary los reference removal, related to ao");
        this.reCreateUpperSecondaryLOS(los);
    }

    public void handleSpecialLOSReferenceRemoval(SpecialLOS los) throws TarjontaParseException, KoodistoException, SolrServerException, IOException {
        String komoOid = los.getId().split("_")[0];
        String tarjoaja = los.getId().split("_")[1];

        KomoDTO komo = this.tarjontaRawService.getKomo(komoOid);

        if (los.getType().equals(TarjontaConstants.TYPE_SPECIAL)) {
            this.reCreateSpecialLOS(los, null, komo, tarjoaja);
        } else {
            KomotoDTO komoto = this.tarjontaRawService.getKomoto(los.getId().split("_")[2]);
            this.reCreateSpecialLOS(los, komoto, komo, tarjoaja);
        }
    }
    
    public void handleParentLOSReferenceRemoval(ParentLOS los) throws TarjontaParseException, KoodistoException, SolrServerException, IOException {
        LOG.debug("Currently in parent los reference removal, with  parent los: " + los.getId());
        String parentKomoOid = los.getId().split("_")[0];
        LOG.debug("parent komo to handle: " + parentKomoOid);
        this.reCreateParentLOS(parentKomoOid, los);
    }

    public void clearCreatedLOS() {
        this.createdLOS = new ArrayList<String>();
        
    }


    public void indexHigherEdKomo(String komoOid) throws Exception {
        this.higherEdLOSIndexer.indexHigherEdKomo(komoOid);
    }

    public void removeHigherEd(String oid, String komoOid) throws Exception {
        this.higherEdLOSIndexer.removeHigherEd(oid, komoOid);        
    }
    
    public void indexKoulutusKomo(String komoOid) throws Exception {
        this.koulutusIndexer.indexKoulutusKomo(komoOid);
    }

    public void removeKoulutus(String oid) throws Exception {
        this.koulutusIndexer.removeKoulutusLOS(oid);        
    }
    
    public void removeAdultUpsecEd(String oid, String komoOid) throws Exception {
        this.adultLosIndexer.removeAdultUpsecEd(oid, komoOid);
    }
    
    public void removeAdultVocationalEd(String oid, String komoOid) throws Exception {
        this.adultLosIndexer.removeAdultVocationalEd(oid);
    }


    public void indexAdultUpsecKomo(String curKomoOid) throws Exception {
        
        this.adultLosIndexer.indexAdultUpsecKomo(curKomoOid);
        
    }





}
