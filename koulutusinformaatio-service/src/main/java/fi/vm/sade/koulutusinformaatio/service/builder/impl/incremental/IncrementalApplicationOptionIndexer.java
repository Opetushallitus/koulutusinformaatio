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

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataUpdateService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.CreatorUtil;
import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 
 * @author Markus
 *
 */
@Component
public class IncrementalApplicationOptionIndexer {
    
    public static final Logger LOG = LoggerFactory.getLogger(IncrementalApplicationOptionIndexer.class);
    
    private TarjontaRawService tarjontaRawService;
    private EducationIncrementalDataQueryService dataQueryService;
    private EducationIncrementalDataUpdateService dataUpdateService;
    
    private IncrementalLOSIndexer losIndexer;
    
    @Autowired
    public IncrementalApplicationOptionIndexer(TarjontaRawService tarjontaRawService, EducationIncrementalDataQueryService dataQueryService, EducationIncrementalDataUpdateService dataUpdateService, IncrementalLOSIndexer losIndexer) {
        this.tarjontaRawService = tarjontaRawService;
        this.dataQueryService = dataQueryService;
        this.dataUpdateService = dataUpdateService;
        this.losIndexer = losIndexer;
        
    }
    
    public void indexApplicationOptionData(HakukohdeV1RDTO aoDto, HakuV1RDTO asDto) throws Exception {
        boolean toRemove = !TarjontaConstants.STATE_PUBLISHED.equals(asDto.getTila()) || !TarjontaConstants.STATE_PUBLISHED.equals(aoDto.getTila());

        String toteutusTyyppi = aoDto.getToteutusTyyppi();

        switch (ToteutustyyppiEnum.valueOf(toteutusTyyppi)) {
        case KORKEAKOULUTUS:
            LOG.debug("Indexing  higher education ao");
            this.indexHigherEdAo(aoDto.getOid(), toRemove);
            return;

        case LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA:
        case AMMATTITUTKINTO:
        case ERIKOISAMMATTITUTKINTO:
        case AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA:
            LOG.debug("Indexing  adult uppersecondary ao");
            this.indexAdultUpsecEdAo(aoDto.getOid(), toRemove);
            return;

        case VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS:
        case PERUSOPETUKSEN_LISAOPETUS:
        case AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA:
        case AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA_ER:
        case MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS:
        case VAPAAN_SIVISTYSTYON_KOULUTUS: // Kansanopistot
        case LUKIOKOULUTUS:
            LOG.debug("Indexing  valmistava ao");
            this.indexValmentavaEdAo(aoDto.getOid(), toRemove);
            return;

        case AIKUISTEN_PERUSOPETUS:
        case AMMATILLINEN_PERUSTUTKINTO:
        case AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA:
        case KORKEAKOULUOPINTO:
        case AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS:
        case MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS:
        case EB_RP_ISH: // Lukiokoulutus
        case ESIOPETUS:
        case PERUSOPETUS:

            break;

        default:
            break;
        }

        // TODO: Lisää ylläolevaan listaan kun V1 siirto on valmis
        if (CreatorUtil.isSecondaryAS(asDto)) {
            LOG.debug("Indexing  secondary ao");
            if (!TarjontaConstants.STATE_PUBLISHED.equals(asDto.getTila()) || !TarjontaConstants.STATE_PUBLISHED.equals(aoDto.getTila())) {
//                removeApplicationOption(aoDto.getOid());
            } else {
                try {
                    ApplicationOption ao = this.dataQueryService.getApplicationOption(aoDto.getOid());
//                    updateApplicationOption(ao, aoDto);
                } catch (ResourceNotFoundException ex) {
                    LOG.debug(ex.getMessage());
//                    addApplicationOption(aoDto);
                }
            }
        }

    }
    
    public void indexAdultUpsecEdAo(String aoOid, boolean toRemove) throws Exception {
       
        LOG.debug("Indexing adultupsec ed ao: {}", aoOid);
        ResultV1RDTO<HakukohdeV1RDTO> aoRes = this.tarjontaRawService.getV1EducationHakukohode(aoOid);
        if (aoRes != null) {
            HakukohdeV1RDTO curAo = aoRes.getResult();
            if (curAo != null) {

                    ResultV1RDTO<List<NimiJaOidRDTO>> koulutusOidRes = this.tarjontaRawService.getHigherEducationByHakukohode(curAo.getOid());

                    if (koulutusOidRes != null && koulutusOidRes.getResult() != null) {
                        for (NimiJaOidRDTO curKoulOid : koulutusOidRes.getResult()) {
                            ResultV1RDTO<KoulutusV1RDTO> koulutusRes = this.tarjontaRawService.getV1KoulutusLearningOpportunity(curKoulOid.getOid());
                            if (koulutusRes != null && koulutusRes.getResult() != null && koulutusRes.getResult().getKomoOid() != null) {
                                if (!toRemove) {
                                LOG.debug("Indexing adult upsec komo: {}", koulutusRes.getResult().getKomoOid());
                                    //if (koulutusRe)
                                    if (this.losIndexer.isAdultUpsecKomo(koulutusRes.getResult().getKomoOid())) {
                                        this.losIndexer.indexAdultUpsecKomo(koulutusRes.getResult().getKomoOid());
                                    } else {
                                        this.losIndexer.indexAdultVocationalKomoto(curKoulOid.getOid());
                                    }
                                } else {
                                    if (this.losIndexer.isAdultUpsecKomo(koulutusRes.getResult().getKomoOid())) {
                                        this.losIndexer.removeAdultUpsecEd(curKoulOid.getOid(), koulutusRes.getResult().getKomoOid());
                                    } else {
                                        this.losIndexer.removeAdultVocationalEd(curKoulOid.getOid());
                                    }
                                }
                            }

                        }
                    }
                    
                    if (!curAo.getTila().equals(TarjontaConstants.STATE_PUBLISHED) || toRemove) {
                        LOG.debug("Removing ao: " + curAo.getOid() + " with tila: " + curAo.getTila());
                        try {
                            ApplicationOption ao = this.dataQueryService.getApplicationOption(aoOid);
                        if (ao != null) {
                            this.dataUpdateService.deleteAo(ao);
                        }
                        } catch (ResourceNotFoundException ex) {
                            LOG.debug("Ao not found in mongo not doing anything");
                        }

                    }
                    
                }

            }
    }

    public void indexHigherEdAo(String aoOid, boolean toRemove) throws Exception {

        ResultV1RDTO<HakukohdeV1RDTO> aoRes = this.tarjontaRawService.getV1EducationHakukohode(aoOid);
        if (aoRes != null) {
            HakukohdeV1RDTO curAo = aoRes.getResult();
            if (curAo != null) {

                    ResultV1RDTO<List<NimiJaOidRDTO>> koulutusOidRes = this.tarjontaRawService.getHigherEducationByHakukohode(curAo.getOid());

                    if (koulutusOidRes != null && koulutusOidRes.getResult() != null) {
                        for (NimiJaOidRDTO curKoulOid : koulutusOidRes.getResult()) {
                            ResultV1RDTO<KoulutusV1RDTO> koulutusRes = this.tarjontaRawService.getV1KoulutusLearningOpportunity(curKoulOid.getOid());
                            if (koulutusRes != null && koulutusRes.getResult() != null && koulutusRes.getResult().getKomoOid() != null) {
                                if (!toRemove || hasOtherAos(koulutusRes.getResult().getOid(), aoOid)) {
                                    this.losIndexer.indexHigherEdKomo(koulutusRes.getResult().getKomoOid());
                                } else {
                                    this.losIndexer.removeHigherEd(curKoulOid.getOid(), koulutusRes.getResult().getKomoOid());
                                }
                            }

                        }
                    }
                    
                    if (!curAo.getTila().equals(TarjontaConstants.STATE_PUBLISHED) || toRemove) {
                        LOG.debug("Removing ao: " + curAo.getOid() + " with tila: " + curAo.getTila());
                        try {
                        ApplicationOption ao = this.dataQueryService.getApplicationOption(aoOid);
                        if (ao != null) {
                            this.dataUpdateService.deleteAo(ao);
                        }
                        } catch (ResourceNotFoundException ex) {
                            LOG.debug("Ao not found in mongo not doing anything");
                        }

                    }
                    
                }

            }

    }

    private boolean hasOtherAos(String oid, String aoOid) {
        try {
            HigherEducationLOS los = this.dataQueryService.getHigherEducationLearningOpportunity(oid);
            if (los != null && los.getApplicationOptions() != null) {
                for (ApplicationOption curAo : los.getApplicationOptions()) {
                    if (!curAo.getId().equals(aoOid)) {
                        return true;
                    }
                }
            }
        } catch (Exception ex) {
            LOG.debug("No higher ed: {}", oid);
        }
        return false;
    }
    
    
    private void indexValmentavaEdAo(String aoOid, boolean toRemove) throws Exception {
        ResultV1RDTO<HakukohdeV1RDTO> aoRes = this.tarjontaRawService.getV1EducationHakukohode(aoOid);
        if (aoRes != null) {
            HakukohdeV1RDTO curAo = aoRes.getResult();
            if (curAo != null) {

                    ResultV1RDTO<List<NimiJaOidRDTO>> koulutusOidRes = this.tarjontaRawService.getHigherEducationByHakukohode(curAo.getOid());

                    if (koulutusOidRes != null && koulutusOidRes.getResult() != null) {
                        for (NimiJaOidRDTO curKoulOid : koulutusOidRes.getResult()) {
                            KoulutusV1RDTO koulutus = this.tarjontaRawService.getV1KoulutusLearningOpportunity(curKoulOid.getOid()).getResult();
                            if (koulutus != null && koulutus != null && koulutus.getKomoOid() != null) {
                                if (!toRemove || hasOtherAos(koulutus.getOid(), aoOid)) {
                                    this.losIndexer.indexKoulutusKomo(koulutus.getKomoOid());
                                } else {
                                    this.losIndexer.removeKoulutus(curKoulOid.getOid());
                                }
                            }
                        }
                    }
                    
                    if (!curAo.getTila().equals(TarjontaConstants.STATE_PUBLISHED) || toRemove) {
                        LOG.debug("Removing ao: " + curAo.getOid() + " with tila: " + curAo.getTila());
                        try {
                        ApplicationOption ao = this.dataQueryService.getApplicationOption(aoOid);
                        if (ao != null) {
                            this.dataUpdateService.deleteAo(ao);
                        }
                        } catch (ResourceNotFoundException ex) {
                            LOG.debug("Ao not found in mongo not doing anything");
                        }
                    }
                    
                }

            }

        
        
    }



}
