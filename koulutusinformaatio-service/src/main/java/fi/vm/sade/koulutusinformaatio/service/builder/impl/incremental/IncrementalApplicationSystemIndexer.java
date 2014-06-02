package fi.vm.sade.koulutusinformaatio.service.builder.impl.incremental;

import java.util.ArrayList;
import java.util.List;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOI;
import fi.vm.sade.koulutusinformaatio.domain.ChildLOS;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOI;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.SpecialLOS;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOI;
import fi.vm.sade.koulutusinformaatio.domain.UpperSecondaryLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.service.EducationIncrementalDataQueryService;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.ApplicationSystemCreator;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.CreatorUtil;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;

public class IncrementalApplicationSystemIndexer {
    
    private TarjontaRawService tarjontaRawService;
    private EducationIncrementalDataQueryService dataQueryService;
    private KoodistoService koodistoService;
    
    private IncrementalApplicationOptionIndexer aoIndexer;
    private IncrementalLOSIndexer losIndexer;
    
    public IncrementalApplicationSystemIndexer(TarjontaRawService tarjontaRawService, 
                                                EducationIncrementalDataQueryService dataQueryService, 
                                                KoodistoService koodistoService,
                                                IncrementalApplicationOptionIndexer aoIndexer,
                                                IncrementalLOSIndexer losIndexer) {
        this.tarjontaRawService = tarjontaRawService;
        this.dataQueryService = dataQueryService;
        this.koodistoService = koodistoService;
        this.aoIndexer = aoIndexer;
        this.losIndexer = losIndexer;
    }
    
    //Indexing of based on changes in application systems
    public void indexApplicationSystemData(String asOid) throws Exception {
        HakuDTO asDto = this.tarjontaRawService.getHaku(asOid);
        if (CreatorUtil.isSecondaryAS(asDto)) {
            indexSecondaryEducationAsData(asDto);
        } else {

            indexHigherEducationAsData(asOid);

        }
    }
    
    private void indexHigherEducationAsData(String asOid) throws Exception {
        
        ResultV1RDTO<HakuV1RDTO> hakuRes = this.tarjontaRawService.getHigherEducationHakuByOid(asOid);
        if (hakuRes != null) {
            HakuV1RDTO asDto = hakuRes.getResult();
            
            List<String> lossesInAS = new ArrayList<String>();
            
            if (asDto.getTila().equals(TarjontaConstants.STATE_PUBLISHED)) {
                lossesInAS = this.dataQueryService.getLearningOpportunityIdsByAS(asDto.getOid());

                ApplicationSystemCreator asCreator = new ApplicationSystemCreator(koodistoService);
                ApplicationSystem as = asCreator.createHigherEdApplicationSystem(asDto);
                
                for (String curLosId : lossesInAS) {
                    HigherEducationLOS curLos = this.dataQueryService.getHigherEducationLearningOpportunity(curLosId);
                    this.reIndexAsDataForHigherEdLOS(curLos, asDto, as);
                    this.losIndexer.updateHigherEdLos(curLos);
                }
                
            }
            
            if (lossesInAS.isEmpty()) {
            
                for (String curHakukohde : asDto.getHakukohdeOids()) {
                    this.aoIndexer.indexHigherEdAo(curHakukohde, !asDto.getTila().equals(TarjontaConstants.STATE_PUBLISHED));
                }
            }
        }
    }


    private void indexSecondaryEducationAsData(HakuDTO asDto) throws Exception {
        //Indexing application options connected to the changed application system

        List<String> lossesInAS = new ArrayList<String>();

        if (asDto.getTila().equals(TarjontaConstants.STATE_PUBLISHED)) {

            lossesInAS = this.dataQueryService.getLearningOpportunityIdsByAS(asDto.getOid());

            ApplicationSystemCreator asCreator = new ApplicationSystemCreator(koodistoService);
            ApplicationSystem as = asCreator.createApplicationSystem(asDto);
            
            for (String curLosId : lossesInAS) {
                LOS curLos = this.dataQueryService.getLos(curLosId);
                if (curLos instanceof ChildLOS) {
                    ParentLOS parent = this.dataQueryService.getParentLearningOpportunity(((ChildLOS) curLos).getParent().getId());
                    reIndexAsDataForParentLOS(parent, asDto, as);
                    this.losIndexer.updateParentLos(parent);

                } else if (curLos instanceof SpecialLOS) {
                    reIndexAsDataForSpecialLOS((SpecialLOS)curLos, asDto, as);
                    this.losIndexer.updateSpecialLos((SpecialLOS)curLos);
                } else if (curLos instanceof UpperSecondaryLOS) {
                    reIndexAsDataForUpsecLOS((UpperSecondaryLOS)curLos, asDto, as);
                    this.losIndexer.updateUpsecLos((UpperSecondaryLOS)curLos);
                }
            }
        }

        if (lossesInAS.isEmpty()) {

            List<OidRDTO> hakukohdeOids = this.tarjontaRawService.getHakukohdesByHaku(asDto.getOid());



            if (hakukohdeOids != null && !hakukohdeOids.isEmpty()) {
                for (OidRDTO curOid : hakukohdeOids) {
                    HakukohdeDTO aoDto = this.tarjontaRawService.getHakukohde(curOid.getOid());
                    this.aoIndexer.indexApplicationOptionData(aoDto, asDto);
                }
            }
        }
    }
    
    

    private void reIndexAsDataForHigherEdLOS(HigherEducationLOS curLos,
            HakuV1RDTO asDto, ApplicationSystem as) {
        
     for (ApplicationOption curAo : curLos.getApplicationOptions()) {
         if (as != null && curAo.getApplicationSystem().getId().equals(as.getId())) {
             curAo.setApplicationSystem(as);
         }
     }
        
    }
    
    private void reIndexAsDataForUpsecLOS(UpperSecondaryLOS curLos,
            HakuDTO asDto, ApplicationSystem as) throws KoodistoException {
        for (UpperSecondaryLOI curUpsecLoi : curLos.getLois()) {
            for (ApplicationOption curAo : curUpsecLoi.getApplicationOptions()) {
                if (as != null && curAo.getApplicationSystem().getId().equals(as.getId())) {
                    curAo.setApplicationSystem(as);
                }
            }
        }

    }

    private void reIndexAsDataForSpecialLOS(SpecialLOS curLos, HakuDTO asDto,
            ApplicationSystem as) throws KoodistoException {

        for (ChildLOI curChildLoi : curLos.getLois()) {
            for (ApplicationOption curAo : curChildLoi.getApplicationOptions()) {
                if (as != null && curAo.getApplicationSystem().getId().equals(as.getId())) {
                    curAo.setApplicationSystem(as);
                }
            }
        }

    }

    private void reIndexAsDataForParentLOS(ParentLOS parent, HakuDTO hakuDTO, ApplicationSystem as) throws KoodistoException {
        for (ParentLOI parentLoi : parent.getLois()) {
            for (ApplicationOption curAo : parentLoi.getApplicationOptions()) {
                if (as != null && curAo.getApplicationSystem().getId().equals(as.getId())) {
                    curAo.setApplicationSystem(as);
                }
            }
        }

    }

}
