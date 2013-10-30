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

import fi.vm.sade.tarjonta.service.resources.dto.*;

import java.util.List;

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

    public List<OidRDTO> getKomotosByHakukohde(String oid);

    public HakuDTO getHaku(String oid);

    public List<OidRDTO> listParentLearnignOpportunityOids(int count, int startIndex);
}