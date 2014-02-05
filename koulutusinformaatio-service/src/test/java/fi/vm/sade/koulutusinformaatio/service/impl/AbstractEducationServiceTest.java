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

import com.google.common.collect.Lists;
import com.mongodb.DBCollection;

import fi.vm.sade.koulutusinformaatio.dao.*;
import fi.vm.sade.koulutusinformaatio.dao.entity.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Hannu Lyytikainen
 */
public class AbstractEducationServiceTest {

    protected final static String NOTFOUND = "notfound";
    protected Date lastDataUpdate;

    protected SpecialLearningOpportunitySpecificationDAO mockSpecialDAO() {
        SpecialLearningOpportunitySpecificationEntity entity = new SpecialLearningOpportunitySpecificationEntity();
        entity.setId("specialid");
        SpecialLearningOpportunitySpecificationDAO dao = mock(SpecialLearningOpportunitySpecificationDAO.class);
        when(dao.get(eq(entity.getId()))).thenReturn(entity);
        when(dao.get(eq(NOTFOUND))).thenReturn(null);
        return dao;
    }

    protected UpperSecondaryLearningOpportunitySpecificationDAO mockUpSecDAO() {
        UpperSecondaryLearningOpportunitySpecificationEntity entity = new UpperSecondaryLearningOpportunitySpecificationEntity();
        entity.setId("upsecid");
        UpperSecondaryLearningOpportunitySpecificationDAO dao = mock(UpperSecondaryLearningOpportunitySpecificationDAO.class);
        when(dao.get(eq(entity.getId()))).thenReturn(entity);
        when(dao.get(eq(NOTFOUND))).thenReturn(null);
        return dao;
    }

    protected PictureDAO mockPictureDAO() {
        PictureEntity entity = new PictureEntity();
        entity.setId("pictureid");
        entity.setPictureEncoded("encoded");
        PictureDAO dao = mock(PictureDAO.class);
        when(dao.get(eq(entity.getId()))).thenReturn(entity);
        when(dao.get(eq(NOTFOUND))).thenReturn(null);
        return dao;
    }

    protected DataStatusDAO mockDataStatudDAO() {
        DataStatusEntity entity = new DataStatusEntity();
        entity.setLastUpdateOutcome("SUCCESS");
        entity.setLastUpdateFinished(lastDataUpdate);
        entity.setLastUpdateDuration(1000L);
        DataStatusDAO dao = mock(DataStatusDAO.class);
        when(dao.getLatest()).thenReturn(entity);
        return dao;
    }

    protected ChildLearningOpportunityDAO mockChildDAO() {
        ChildLearningOpportunitySpecificationEntity entity = new ChildLearningOpportunitySpecificationEntity();
        entity.setId("childid");
        ChildLearningOpportunityDAO dao = mock(ChildLearningOpportunityDAO.class);
        when(dao.get(entity.getId())).thenReturn(entity);
        when(dao.get(NOTFOUND)).thenReturn(null);
        return dao;
    }

    protected ParentLearningOpportunitySpecificationDAO mockParentDAO() {
        ParentLearningOpportunitySpecificationDAO parentDAO = mock(ParentLearningOpportunitySpecificationDAO.class);
        DBCollection ploCollection = mock(DBCollection.class);
        ParentLearningOpportunitySpecificationEntity plo = new ParentLearningOpportunitySpecificationEntity();
        String ploOid = "1.2.3";
        plo.setId(ploOid);
        when(parentDAO.getCollection()).thenReturn(ploCollection);
        when(parentDAO.get(eq("1.2.3"))).thenReturn(plo);
        return parentDAO;
    }

    protected ApplicationOptionDAO mockApplicationOptionDAO() {
        ApplicationOptionDAO aoDAO = mock(ApplicationOptionDAO.class);
        DBCollection aoCollection = mock(DBCollection.class);
        when(aoDAO.getCollection()).thenReturn(aoCollection);
        List<ApplicationOptionEntity> aos = new ArrayList<ApplicationOptionEntity>();
        ApplicationOptionEntity ao = new ApplicationOptionEntity();
        ao.setId("8.9.0");
        aos.add(ao);
        when(aoDAO.find(eq("1.1.1"), eq("9.9.9"), eq("1"), eq(true), eq(true))).thenReturn(aos);
        when(aoDAO.find(eq(Lists.newArrayList("8.9.0")))).thenReturn(aos);
        return aoDAO;
     }

    protected LearningOpportunityProviderDAO mockProviderDAO() {
        LearningOpportunityProviderDAO dao = mock(LearningOpportunityProviderDAO.class);
        LearningOpportunityProviderEntity entity = new LearningOpportunityProviderEntity();
        entity.setId("providerid");
        when(dao.get(eq(entity.getId()))).thenReturn(entity);
        when(dao.get(eq(NOTFOUND))).thenReturn(null);
        return dao;
    }    

    protected HigherEducationLOSDAO mockHigherEdDAO() {
    	HigherEducationLOSDAO dao = mock(HigherEducationLOSDAO.class);
    	HigherEducationLOSEntity entity = new HigherEducationLOSEntity();
    	entity.setId("higherEdId");
    	when(dao.get(eq(entity.getId()))).thenReturn(entity);
        when(dao.get(eq(NOTFOUND))).thenReturn(null);
		return dao;
	}

}
