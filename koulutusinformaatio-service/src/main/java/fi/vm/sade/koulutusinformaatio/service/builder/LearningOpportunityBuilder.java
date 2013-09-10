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

package fi.vm.sade.koulutusinformaatio.service.builder;

import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import java.util.*;

/**
 * Builds learning opportunity instances.
 *
 * @author Hannu Lyytikainen
 */
public interface LearningOpportunityBuilder {

    public static final Logger LOG = LoggerFactory.getLogger(LearningOpportunityBuilder.class);

    public static final String MODULE_TYPE_PARENT = "TUTKINTO";
    public static final String MODULE_TYPE_CHILD = "TUTKINTO_OHJELMA";
    public static final String BASE_EDUCATION_KOODISTO_URI = "pohjakoulutustoinenaste";

    public LearningOpportunityBuilder resolveParentLOSs() throws TarjontaParseException, KoodistoException, WebApplicationException;
    public LearningOpportunityBuilder resolveChildLOSs() throws TarjontaParseException, KoodistoException, WebApplicationException;
    public LearningOpportunityBuilder reassemble() throws TarjontaParseException, KoodistoException, WebApplicationException;
    public LearningOpportunityBuilder filter();
    public List<ParentLOS> build();

}
