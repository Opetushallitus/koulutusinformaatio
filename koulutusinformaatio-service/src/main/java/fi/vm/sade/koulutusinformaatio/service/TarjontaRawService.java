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

package fi.vm.sade.koulutusinformaatio.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakutuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.AmmattitutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KomoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusAikuistenPerusopetusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusAmmatillinenPerustutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusGenericV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusLukioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.ValmistavaKoulutusV1RDTO;

/**
 * Can be used to access tarjonta APIs. Returns raw tarjonta DTO objects as they are
 * returned from API.
 *
 * @author Hannu Lyytikainen
 */
public interface TarjontaRawService {

    public KomoDTO getKomo(String oid);

    public List<OidRDTO> getKomotosByKomo(String oid, int count, int startIndex);

    public KomotoDTO getKomoto(String oid);

    public List<OidRDTO> getHakukohdesByKomoto(String oid);

    public KomoDTO getKomoByKomoto(String oid);

    public HakukohdeDTO getHakukohde(String oid);

    public HakuDTO getHakuByHakukohde(String oid);
    
    public List<OidRDTO> getHakukohdesByHaku(String oid);

    public List<OidRDTO> getKomotosByHakukohde(String oid);

    public HakuDTO getHaku(String oid);

    public List<OidRDTO> listParentLearnignOpportunityOids(int count, int startIndex);
    
    public Map<String, List<String>> listModifiedLearningOpportunities(long updatePeriod);
    
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> listEducations(String educationType);
    
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> listEducationsByToteutustyyppi(String... educationType);
    
    public ResultV1RDTO<KoulutusKorkeakouluV1RDTO> getHigherEducationLearningOpportunity(String oid);

    public ResultV1RDTO<ValmistavaKoulutusV1RDTO> getValmistavaKoulutusLearningOpportunity(String oid);

    public ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>> findHakukohdesByEducationOid(String oid);
    
    public ResultV1RDTO<HakukohdeV1RDTO> getV1EducationHakukohode(String oid);
    
    public ResultV1RDTO<HakuV1RDTO> getV1EducationHakuByOid(String oid);
    
    public ResultV1RDTO<Set<String>> getChildrenOfParentHigherEducationLOS(String parentOid);

	public ResultV1RDTO<Set<String>> getParentsOfHigherEducationLOS(
			String komoOid);

	public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> getHigherEducationByKomo(
			String curKomoOid);
	
	public ResultV1RDTO<List<KuvaV1RDTO>> getStructureImages(String koulutusOid);

    ResultV1RDTO<List<NimiJaOidRDTO>> getHigherEducationByHakukohode(
            String hakukohdeOid);

    ResultV1RDTO<KoulutusLukioV1RDTO> getUpperSecondaryLearningOpportunity(
            String oid);

    ResultV1RDTO<KoulutusAikuistenPerusopetusV1RDTO> getAdultBaseEducationLearningOpportunity(String oid);

    ResultV1RDTO<KomoV1RDTO> getV1Komo(String oid);

    ResultV1RDTO<AmmattitutkintoV1RDTO> getAdultVocationalLearningOpportunity(
            String oid);

    ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> getAdultEducationByKomo(
            String komoOid);

    public ResultV1RDTO<List<String>> searchHakus(String hakutapaYhteishaku);

    ResultV1RDTO<KoulutusGenericV1RDTO> getV1KoulutusLearningOpportunity(String oid);

    public ResultV1RDTO<KoulutusAmmatillinenPerustutkintoV1RDTO> getAmmatillinenPerustutkintoLearningOpportunity(String oid);

    ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> searchEducation(String oid);

}