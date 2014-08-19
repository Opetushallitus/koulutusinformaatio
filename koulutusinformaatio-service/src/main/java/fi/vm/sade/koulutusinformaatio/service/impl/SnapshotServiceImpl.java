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

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.vm.sade.koulutusinformaatio.dao.ChildLearningOpportunityDAO;
import fi.vm.sade.koulutusinformaatio.dao.HigherEducationLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.ParentLearningOpportunitySpecificationDAO;
import fi.vm.sade.koulutusinformaatio.dao.SpecialLearningOpportunitySpecificationDAO;
import fi.vm.sade.koulutusinformaatio.dao.UpperSecondaryLearningOpportunitySpecificationDAO;
import fi.vm.sade.koulutusinformaatio.dao.entity.CodeEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.HigherEducationLOSEntity;
import fi.vm.sade.koulutusinformaatio.domain.exception.KIException;
import fi.vm.sade.koulutusinformaatio.service.SnapshotService;
import fi.vm.sade.koulutusinformaatio.util.StreamReaderHelper;

/**
 * @author Hannu Lyytikainen
 */
@Component
public class SnapshotServiceImpl implements SnapshotService {

    private static final Logger LOG = LoggerFactory.getLogger(SnapshotServiceImpl.class);

    private static final String TYPE_SPECIAL = "erityisopetus";
    private static final String TYPE_PARENT = "tutkinto";
    private static final String TYPE_CHILD = "koulutusohjelma";
    private static final String TYPE_UPSEC = "lukio";
    private static final String TYPE_HIGHERED = "korkeakoulu";

    private SpecialLearningOpportunitySpecificationDAO specialDAO;
    private ParentLearningOpportunitySpecificationDAO parentDAO;
    private ChildLearningOpportunityDAO childDAO;
    private UpperSecondaryLearningOpportunitySpecificationDAO upsecDAO;
    private HigherEducationLOSDAO higheredDAO;
    private String phantomjs;
    private String snapshotScript;
    private String snapshotFolder;
    private String baseUrl;

    @Autowired
    public SnapshotServiceImpl(@Qualifier("specialLearningOpportunitySpecificationDAO")
                               SpecialLearningOpportunitySpecificationDAO specialDAO,
                               @Qualifier("parentLearningOpportunitySpecificationDAO")
                               ParentLearningOpportunitySpecificationDAO parentDAO,
                               @Qualifier("childLearningOpportunityDAO") ChildLearningOpportunityDAO childDAO,
                               @Qualifier("upperSecondaryLearningOpportunitySpecificationDAO")
                               UpperSecondaryLearningOpportunitySpecificationDAO upsecDAO,
                               @Qualifier("higherEducationLOSDAO") HigherEducationLOSDAO higheredDAO,
                               @Value("${koulutusinformaatio.phantomjs}") String phantomjs,
                               @Value("${koulutusinformaatio.snapshot.script}") String script,
                               @Value("${koulutusinformaatio.snapshot.folder}") String prerenderFolder,
                               @Value("${koulutusinformaatio.baseurl.learningopportunity}") String baseUrl) {
        this.specialDAO = specialDAO;
        this.parentDAO = parentDAO;
        this.childDAO = childDAO;
        this.upsecDAO = upsecDAO;
        this.higheredDAO = higheredDAO;
        this.phantomjs = phantomjs;
        this.snapshotScript = script;
        this.snapshotFolder = prerenderFolder;
        this.baseUrl = baseUrl;
    }

    @Override
    public void renderSnapshots() {
        LOG.info("Rendering html snapshots");
        prerender(TYPE_SPECIAL, specialDAO.findIds());
        LOG.debug("Special LOs rendered");
        prerender(TYPE_PARENT, parentDAO.findIds());
        LOG.debug("Parent LOs rendered");
        prerender(TYPE_CHILD, childDAO.findIds());
        LOG.debug("Child LOs rendered");
        prerender(TYPE_UPSEC, upsecDAO.findIds());
        LOG.debug("Upsec LOs rendered");
        prerenderHigherEd(higheredDAO.findIds());
        LOG.debug("HigherEd LOs rendered");
        // todo: handle rehabilitating separately
        LOG.info("Rendering html snapshots finished");
    }
    
    private void prerenderHigherEd(List<String> ids) {
        for (String id : ids) {
            HigherEducationLOSEntity los = higheredDAO.get(id);
            for (CodeEntity teachingLang : los.getTeachingLanguages()) {
                try {
                    String lang = "";
                    if (teachingLang != null && teachingLang.getValue() != null) {
                        lang = teachingLang.getValue().toLowerCase();
                    }
                    String cmd = generatePhantomJSCommand(TYPE_HIGHERED, id, lang);
                    invokePhantomJS(cmd, id);
                } catch (KIException e) {
                    LOG.error(e.getMessage());
                }
            }
        }
    }

    private void prerender(String type, List<String> ids) {
        for (String id : ids) {
            try {
                String cmd = generatePhantomJSCommand(type, id);
                invokePhantomJS(cmd, id);
            } catch (KIException e) {
                LOG.error(e.getMessage());
            }
        }
    }
    
    private String generatePhantomJSCommand(String type, String id) {
        return String.format("%s %s %s%s/%s %s/%s.html",
                phantomjs, snapshotScript, baseUrl, type, id, snapshotFolder, id);
    }
    
    private String generatePhantomJSCommand(String type, String id, String lang) {
        return String.format("%s %s %s%s/%s?uilang=%s %s/%s_%s.html",
                phantomjs, snapshotScript, baseUrl, type, id, lang, snapshotFolder, id, lang);
    }
    
    

    private void invokePhantomJS(String cmd, String id) throws KIException {

        try {
            // "/usr/local/bin/phantomjs /path/to/script.js http://www.opintopolku.fi/some/edu/1.2.3.4.5 /path/to/static/content/"            
            Process process = Runtime.getRuntime().exec(cmd);
            
            //Set up two threads to read on the output of the external process.
            Thread stdout = new Thread(new StreamReaderHelper(process.getInputStream()));
            Thread stderr = new Thread(new StreamReaderHelper(process.getErrorStream()));
            
            stdout.start();
            stderr.start();
            
            int exitStatus = process.waitFor();
            process.destroy();
            
            if (exitStatus != 0) {
                throw new KIException(String.format("Rendering snapshot for learning opportunity %s failed with exit status: %d",
                        id, exitStatus));
            }

        } catch (IOException e) {
            throw new KIException(String.format("Rendering learning opportunity %s failed due to IOException: %s",
                    id, e.getMessage()));
        } catch (InterruptedException e) {
            throw new KIException(String.format("Rendering learning opportunity %s failed due to InterruptedException: %s",
                    id, e.getMessage()));
        }
    }
}
