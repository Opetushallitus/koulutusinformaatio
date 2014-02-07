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

import fi.vm.sade.koulutusinformaatio.domain.LOS;
import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.koulutusinformaatio.service.builder.LearningOpportunityBuilder;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.LOSObjectCreator;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.LearningOpportunityDirector;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.RehabilitatingLearningOpportunityBuilder;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.UpperSecondaryLearningOpportunityBuilder;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.VocationalLearningOpportunityBuilder;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakutuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.TarjoajaHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import javax.ws.rs.WebApplicationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Hannu Lyytikainen
 */
@Service
@Profile("default")
public class TarjontaServiceImpl implements TarjontaService {

    private ConversionService conversionService;
    private KoodistoService koodistoService;
    private ProviderService providerService;
    private LearningOpportunityDirector loDirector;
    private TarjontaRawService tarjontaRawService;
    private LOSObjectCreator creator;


	@Autowired
    public TarjontaServiceImpl(ConversionService conversionService, KoodistoService koodistoService,
                               ProviderService providerService, LearningOpportunityDirector loDirector,
                               TarjontaRawService tarjontaRawService) {
        this.koodistoService = koodistoService;
        this.providerService = providerService;
        this.loDirector = loDirector;
        this.tarjontaRawService = tarjontaRawService;
        this.conversionService = conversionService;
    }

    public TarjontaServiceImpl() {
    }

    @Override
    public List<LOS> findParentLearningOpportunity(String oid) throws TarjontaParseException {
        try {
            KomoDTO komo = tarjontaRawService.getKomo(oid);
            LearningOpportunityBuilder builder = resolveBuilder(komo);
            return loDirector.constructLearningOpportunities(builder);

        } catch (KoodistoException e) {
            throw new TarjontaParseException("An error occurred while building parent LOS " + oid + " with koodisto: " + e.getMessage());
        }
        catch (WebApplicationException e) {
            throw new TarjontaParseException("An error occurred while building parent LOS " + oid
                    + " accessing remote resource: HTTP response code: "
                    + e.getResponse().getStatus() + ",  error message: " + e.getMessage());
        }
    }

    private LearningOpportunityBuilder resolveBuilder(KomoDTO komo) throws KoodistoException, TarjontaParseException {
        String educationType = komo.getKoulutusTyyppiUri();
        if (educationType.equals(TarjontaConstants.VOCATIONAL_EDUCATION_TYPE) &&
                komo.getModuuliTyyppi().equals(TarjontaConstants.MODULE_TYPE_PARENT)) {
            return new VocationalLearningOpportunityBuilder(
                    tarjontaRawService, providerService, koodistoService, komo);
        }
        else if (educationType.equals(TarjontaConstants.UPPER_SECONDARY_EDUCATION_TYPE) &&
                komo.getModuuliTyyppi().equals(TarjontaConstants.MODULE_TYPE_CHILD)) {
            return new UpperSecondaryLearningOpportunityBuilder(
                    tarjontaRawService, providerService, koodistoService, komo);
        }
        else if (educationType.equals(TarjontaConstants.REHABILITATING_EDUCATION_TYPE) &&
                komo.getModuuliTyyppi().equals(TarjontaConstants.MODULE_TYPE_CHILD)) {
            return new RehabilitatingLearningOpportunityBuilder(tarjontaRawService, providerService, koodistoService, komo);
        }
        else {
            throw new TarjontaParseException(String.format("Unknown education degree %s and module type %s incompatible",
                    educationType, komo.getModuuliTyyppi()));
        }
    }

    @Override
    public List<String> listParentLearnignOpportunityOids() {
        return listParentLearnignOpportunityOids(Integer.MAX_VALUE, 0);
    }

    @Override
    public List<String> listParentLearnignOpportunityOids(int count, int startIndex) {
        List<OidRDTO> oids = tarjontaRawService.listParentLearnignOpportunityOids(count, startIndex);
        return Lists.transform(oids, new Function<OidRDTO, String>() {
            @Override
            public String apply(OidRDTO input) {
                return conversionService.convert(input, String.class);
            }
        });
    }

    @Override
    public List<HigherEducationLOS> findHigherEducations() throws KoodistoException {

    	if (creator == null) {
    		creator = new LOSObjectCreator(koodistoService, tarjontaRawService, providerService);
    	}

    	List<HigherEducationLOS> koulutukset = new ArrayList<HigherEducationLOS>();
    	Map<String,List<HigherEducationLOS>> komoToLOSMap = new HashMap<String,List<HigherEducationLOS>>();
    	List<String> parentOids = new ArrayList<String>();
    	ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> rawRes = this.tarjontaRawService.listHigherEducation();
    	HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> results = rawRes.getResult();
    	for (TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> curRes : results.getTulokset()) {
    		for (KoulutusHakutulosV1RDTO curKoulutus : curRes.getTulokset()) {
    			if (!curKoulutus.getTila().toString().equals(TarjontaTila.JULKAISTU.toString())) {
    				continue;
    			}
    			ResultV1RDTO<KoulutusKorkeakouluV1RDTO> koulutusRes = this.tarjontaRawService.getHigherEducationLearningOpportunity(curKoulutus.getOid());
    			KoulutusKorkeakouluV1RDTO koulutusDTO = koulutusRes.getResult();
    			if (koulutusDTO == null) {
    				continue;
    			}
    			try {
    				HigherEducationLOS los = creator.createHigherEducationLOS(koulutusDTO, true);
    				koulutukset.add(los);
    				List<HigherEducationLOS> loss = komoToLOSMap.get(koulutusDTO.getKomoOid());
    				if (loss == null) {
    					loss = new ArrayList<HigherEducationLOS>();
    					loss.add(los);
    					komoToLOSMap.put(koulutusDTO.getKomoOid(), loss);
    				} else {
    					loss.add(los);
    				}
    				parentOids.add(los.getKomoOid());
    			} catch (TarjontaParseException ex) {
    				continue;
    			}
    			
    		}
    	}

    	return createChildHierarchy(koulutukset, komoToLOSMap, parentOids);
    }
    
    /*
     * Creating the learning opportunity hierarchy for higher education
     */
    private List<HigherEducationLOS> createChildHierarchy(List<HigherEducationLOS> koulutukset,
    		Map<String, List<HigherEducationLOS>> komoToLOSMap, List<String> parentOids) {

    	for (HigherEducationLOS curLos : koulutukset) {

    		ResultV1RDTO<Set<String>> childKomoOids = this.tarjontaRawService.getChildrenOfParentHigherEducationLOS(curLos.getKomoOid());
    		ResultV1RDTO<Set<String>> parentKomoOids = this.tarjontaRawService.getParentsOfHigherEducationLOS(curLos.getKomoOid());
    		if (childKomoOids != null && childKomoOids.getResult() != null) {
    			for (String curChildKomoOid : childKomoOids.getResult()) {
    				List<HigherEducationLOS> loss = komoToLOSMap.get(curChildKomoOid);
    				if (loss != null) {
    					curLos.getChildren().addAll(loss);
    				}

    				if (parentOids.contains(curChildKomoOid)) {
    					parentOids.remove(curChildKomoOid);
    				}
    			}
    		}
    		if (parentKomoOids != null && parentKomoOids.getResult() != null) {
    			for (String curParentKomoOid : parentKomoOids.getResult()) {
    				List<HigherEducationLOS> loss = komoToLOSMap.get(curParentKomoOid);
    				if (loss != null) {
    					curLos.getParents().addAll(loss);
    				}
    			}
    		}
    	}
    	List<HigherEducationLOS> parents = new ArrayList<HigherEducationLOS>();
    	for (String curParent : parentOids) {
    		parents.addAll(komoToLOSMap.get(curParent));
    	}
    	return parents;
    }

	@Override
    public HigherEducationLOS findHigherEducationLearningOpportunity(String oid) throws TarjontaParseException, KoodistoException {
    	if (creator == null) {
    		creator = new LOSObjectCreator(koodistoService, tarjontaRawService, providerService);
    	}
    	ResultV1RDTO<KoulutusKorkeakouluV1RDTO> koulutusRes = this.tarjontaRawService.getHigherEducationLearningOpportunity(oid);
		KoulutusKorkeakouluV1RDTO koulutusDTO = koulutusRes.getResult();
		if (koulutusDTO == null) {
			return null;
		}
		
		HigherEducationLOS los = creator.createHigherEducationLOS(koulutusDTO, false);
		los.setStatus(koulutusDTO.getTila().toString());
		
		ResultV1RDTO<Set<String>> childKomoOids = this.tarjontaRawService.getChildrenOfParentHigherEducationLOS(koulutusDTO.getKomoOid());
		ResultV1RDTO<Set<String>> parentKomoOids = this.tarjontaRawService.getParentsOfHigherEducationLOS(koulutusDTO.getKomoOid());
		los.setChildren(getHigherEducationRelatives(childKomoOids, creator));
		los.setParents(getHigherEducationRelatives(parentKomoOids, creator));
		return los;
    }

	private List<HigherEducationLOS> getHigherEducationRelatives(
			ResultV1RDTO<Set<String>> komoOids, LOSObjectCreator creator) throws TarjontaParseException, KoodistoException {
		List<HigherEducationLOS> relatives = new ArrayList<HigherEducationLOS>();
		if (komoOids == null) {
			return relatives;
		}
		for (String curKomoOid : komoOids.getResult()) {
			ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> rawRes = this.tarjontaRawService.getHigherEducationByKomo(curKomoOid);
	    	HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> results = rawRes.getResult();
	    	for (TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> curRes : results.getTulokset()) {
	    		for (KoulutusHakutulosV1RDTO curKoulutus : curRes.getTulokset()) {
	    			ResultV1RDTO<KoulutusKorkeakouluV1RDTO> koulutusRes = this.tarjontaRawService.getHigherEducationLearningOpportunity(curKoulutus.getOid());
	    			KoulutusKorkeakouluV1RDTO koulutusDTO = koulutusRes.getResult();
	    			if (koulutusDTO == null) {
	    				continue;
	    			}
	    			HigherEducationLOS los = creator.createHigherEducationLOSReference(koulutusDTO, false);
	    			relatives.add(los);
	    		}
	    	}
		}
		return relatives;
	}
	

    public LOSObjectCreator getCreator() {
		return creator;
	}

	public void setCreator(LOSObjectCreator creator) {
		this.creator = creator;
	}

	
}
