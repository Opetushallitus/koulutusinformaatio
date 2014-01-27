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

import fi.vm.sade.koulutusinformaatio.dao.ChildLearningOpportunityDAO;
import fi.vm.sade.koulutusinformaatio.dao.ParentLearningOpportunitySpecificationDAO;
import fi.vm.sade.koulutusinformaatio.dao.SpecialLearningOpportunitySpecificationDAO;
import fi.vm.sade.koulutusinformaatio.dao.UpperSecondaryLearningOpportunitySpecificationDAO;
import fi.vm.sade.koulutusinformaatio.domain.exception.KIException;
import fi.vm.sade.koulutusinformaatio.service.SnapshotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

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

    private SpecialLearningOpportunitySpecificationDAO specialDAO;
    private ParentLearningOpportunitySpecificationDAO parentDAO;
    private ChildLearningOpportunityDAO childDAO;
    private UpperSecondaryLearningOpportunitySpecificationDAO upsecDAO;
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
                               @Value("${koulutusinformaatio.phantomjs}") String phantomjs,
                               @Value("${koulutusinformaatio.snapshot.script}") String script,
                               @Value("${koulutusinformaatio.snapshot.folder}") String prerenderFolder,
                               @Value("${koulutusinformaatio.baseurl.learningopportunity}") String baseUrl) {
        this.specialDAO = specialDAO;
        this.parentDAO = parentDAO;
        this.childDAO = childDAO;
        this.upsecDAO = upsecDAO;
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
        // todo: handle rehabilitating separately
        LOG.info("Rendering html snapshots finished");
    }

    private void prerender(String type, List<String> ids) {
        for (String id : ids) {
            try {
                invokePhantomJS(type, id);
            } catch (KIException e) {
                LOG.error(e.getMessage());
            }
        }
    }

    private void invokePhantomJS(String type, String id) throws KIException {

        try {
            // "/usr/local/bin/phantomjs /path/to/script.js http://www.opintopolku.fi/some/edu/1.2.3.4.5 /path/to/static/content/"
            Process process = Runtime.getRuntime().exec(String.format("%s %s %s%s/%s %s/%s.html",
                    phantomjs, snapshotScript, baseUrl, type, id, snapshotFolder, id));
            int exitStatus = process.waitFor();

            if (exitStatus != 0) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                StringBuilder stringBuilder = new StringBuilder("");
                String currentLine = null;
                currentLine = bufferedReader.readLine();
                while (currentLine != null) {
                    stringBuilder.append(currentLine);
                    currentLine = bufferedReader.readLine();
                }
                throw new KIException(String.format("Rendering learning opportunity %s failed: %s",
                        id, stringBuilder,toString()));
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
