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
import fi.vm.sade.koulutusinformaatio.domain.ParentLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;
import fi.vm.sade.koulutusinformaatio.service.impl.builder.LOBuilder;
import fi.vm.sade.tarjonta.service.resources.KomoResource;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.WebApplicationException;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
@Service
public class TarjontaServiceImpl implements TarjontaService {

    @Autowired
    private LOBuilder loBuilder;
    @Autowired
    private KomoResource komoResource;

    @Override
    public ParentLOS findParentLearningOpportunity(String oid) throws TarjontaParseException {
        try {
            return loBuilder.buildParentLOS(oid);
        } catch (KoodistoException e) {
            throw new TarjontaParseException("An error occurred while building parent LOS " + oid + " with koodisto: " + e.getMessage());
        }
        catch (WebApplicationException e) {
            throw new TarjontaParseException("An error occurred while building parent LOS " + oid
                    + " accessing remote resource: HTTP response code: "
                    + e.getResponse().getStatus() + ",  error message: " + e.getMessage());
        }
    }

    @Override
    public List<OidRDTO> listParentLearnignOpportunityOids() {
        return komoResource.search(null, Integer.MAX_VALUE, 0, null, null);
    }

    @Override
    public List<OidRDTO> listParentLearnignOpportunityOids(int count, int startIndex) {
        return komoResource.search(null, count, startIndex, null, null);
    }
}
