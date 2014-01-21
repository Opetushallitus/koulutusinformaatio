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
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaRawService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;
import fi.vm.sade.koulutusinformaatio.service.builder.TarjontaConstants;
import fi.vm.sade.koulutusinformaatio.service.builder.LearningOpportunityBuilder;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.LearningOpportunityDirector;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.RehabilitatingLearningOpportunityBuilder;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.UpperSecondaryLearningOpportunityBuilder;
import fi.vm.sade.koulutusinformaatio.service.builder.impl.VocationalLearningOpportunityBuilder;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import javax.ws.rs.WebApplicationException;
import java.util.List;

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
}
