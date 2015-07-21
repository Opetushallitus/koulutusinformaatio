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
 * European Union  Licence for more details.
 */

package fi.vm.sade.koulutusinformaatio.service;

import fi.vm.sade.tarjonta.service.resources.dto.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Can be used to access tarjonta APIs. Returns raw tarjonta DTO objects as they are
 * returned from API.
 *
 * @author Hannu Lyytikainen
 */
public interface TarjontaRawService {

     List<OidV1RDTO> getHakukohdesByHaku(String oid);

    List<OidRDTO> getHakukohdesByKomoto(String oid);

     Map<String, List<String>> listModifiedLearningOpportunities(long updatePeriod);
    
     ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> listEducations(String educationType);
    
     ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> listEducationsByToteutustyyppi(String... educationType);
    
     ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>> findHakukohdesByEducationOid(String oid);
    
     ResultV1RDTO<HakukohdeV1RDTO> getV1EducationHakukohde(String oid);
    
     ResultV1RDTO<HakuV1RDTO> getV1EducationHakuByOid(String oid);
    
     ResultV1RDTO<Set<String>> getChildrenOfParentHigherEducationLOS(String parentOid);

	 ResultV1RDTO<Set<String>> getParentsOfHigherEducationLOS(
			String komoOid);

	 ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> getHigherEducationByKomo(
			String curKomoOid);
	
	 ResultV1RDTO<List<KuvaV1RDTO>> getStructureImages(String koulutusOid);

    ResultV1RDTO<KoulutusAikuistenPerusopetusV1RDTO> getAdultBaseEducationLearningOpportunity(String oid);

    ResultV1RDTO<KomoV1RDTO> getV1Komo(String oid);

    ResultV1RDTO<AmmattitutkintoV1RDTO> getAdultVocationalLearningOpportunity(
            String oid);

     ResultV1RDTO<List<String>> searchHakus(String hakutapaYhteishaku);

    ResultV1RDTO<KoulutusV1RDTO> getV1KoulutusLearningOpportunity(String oid);

    ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> searchEducation(String oid);

}