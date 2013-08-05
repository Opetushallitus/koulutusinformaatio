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

package fi.vm.sade.koulutusinformaatio.converter;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.ChildLearningOpportunity;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOI;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationSystemDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ParentLearningOpportunityInstanceDTO;

/**
 * @author Mikko Majapuro
 */
public class ParentLOIToDTO {

    public static ParentLearningOpportunityInstanceDTO convert(final ParentLOI parentLOI, final String lang) {
        ParentLearningOpportunityInstanceDTO loi = new ParentLearningOpportunityInstanceDTO();
        loi.setId(parentLOI.getId());
        loi.setPrerequisite(CodeToDTO.convert(parentLOI.getPrerequisite(), lang));
        loi.setSelectingEducation(ConverterUtil.getTextByLanguage(parentLOI.getSelectingEducation(), lang));

        // group by application system for UI
        SetMultimap<ApplicationSystem, ApplicationOption> aoByAs = HashMultimap.create();
        for (ChildLearningOpportunity childLO : parentLOI.getChildren()) {
            for (ApplicationOption ao : childLO.getApplicationOptions()) {
                aoByAs.put(ao.getApplicationSystem(), ao);
            }
        }

        for (ApplicationSystem as : aoByAs.keySet()) {
            ApplicationSystemDTO asDTO = ApplicationSystemToDTO.convert(as, lang);
            for (ApplicationOption ao : aoByAs.get(as)) {
                ApplicationOptionDTO aoDTO = ApplicationOptionToDTO.convert(ao, lang);
                asDTO.getApplicationOptions().add(aoDTO);
            }
            loi.getApplicationSystems().add(asDTO);
        }

        //loi.setChildren(ChildLORefToDTO.convert(parentLOI.getChildRefs(), lang));
        return loi;
    }
}
