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

import fi.vm.sade.koulutusinformaatio.dao.*;
import fi.vm.sade.koulutusinformaatio.dao.entity.CodeEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.HigherEducationLOSEntity;
import fi.vm.sade.koulutusinformaatio.domain.exception.IndexingException;
import fi.vm.sade.koulutusinformaatio.domain.exception.KIException;
import fi.vm.sade.koulutusinformaatio.service.SnapshotService;
import fi.vm.sade.koulutusinformaatio.util.StreamReaderHelper;
import fi.vm.sade.properties.OphProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
    private static final String TYPE_HIGHERED = "korkeakoulu";
    private static final String TYPE_ADULT_VOCATIONAL = "ammatillinenaikuiskoulutus";
    private static final String TYPE_ADULT_UPSEC = "aikuislukio";
    private static final String QUERY_PARAM_LANG = "descriptionLang";

    private HigherEducationLOSDAO higheredDAO;
    private AdultVocationalLOSDAO adultvocDAO;
    private KoulutusLOSDAO adultupecDAO;
    private String phantomjs;
    private String snapshotScript;
    private String snapshotFolder;
    private String baseUrl;

    @Autowired
    public SnapshotServiceImpl(@Qualifier("higherEducationLOSDAO") HigherEducationLOSDAO higheredDAO,
                               @Qualifier("adultVocationalLOSDAO") AdultVocationalLOSDAO adultvocDAO,
                               @Qualifier("koulutusLOSDAO") KoulutusLOSDAO adultupsecDAO,
                               @Value("${koulutusinformaatio.phantomjs}") String phantomjs,
                               @Value("${koulutusinformaatio.snapshot.script}") String script,
                               @Value("${koulutusinformaatio.snapshot.folder}") String prerenderFolder,
                               OphProperties urlProperties) {
        this.higheredDAO = higheredDAO;
        this.adultvocDAO = adultvocDAO;
        this.adultupecDAO = adultupsecDAO;
        this.phantomjs = phantomjs;
        this.snapshotScript = script;
        this.snapshotFolder = prerenderFolder;
        this.baseUrl = urlProperties.url("koulutusinformaatio.learningopportunity.base");
    }

    @Override
    public void renderSnapshots() throws IndexingException {
        LOG.info("Rendering html snapshots");
        prerenderWithTeachingLanguages(TYPE_HIGHERED, higheredDAO.findIds());
        LOG.debug("HigherEd LOs rendered");
        prerender(TYPE_ADULT_VOCATIONAL, adultvocDAO.findIds());
        LOG.debug("Adult vocational LOs rendered");
        prerender(TYPE_ADULT_UPSEC, adultupecDAO.findIds());
        LOG.debug("Adult upper secondary LOs rendered");
        // todo: handle rehabilitating separately
        LOG.info("Rendering html snapshots finished");
    }
    
    private void prerenderWithTeachingLanguages(String type, List<String> ids) throws IndexingException {
        for (String id : ids) {
            HigherEducationLOSEntity los = higheredDAO.get(id);
            
            // generate snapshot for each teaching language
            for (CodeEntity teachingLang : los.getTeachingLanguages()) {
                try {
                    String lang = "";
                    if (teachingLang != null && teachingLang.getValue() != null) {
                        lang = teachingLang.getValue().toLowerCase();
                    }
                    String cmd = generatePhantomJSCommand(type, id, lang);
                    invokePhantomJS(cmd, id);
                } catch (KIException e) {
                    LOG.error(e.getMessage());
                }
            }
            
            // generate default snapshot
            String cmd = generatePhantomJSCommand(type, id);
            invokePhantomJS(cmd, id);
        }
    }

    private void prerender(String type, List<String> ids) throws IndexingException {
        for (String id : ids) {
            String cmd = generatePhantomJSCommand(type, id);
            invokePhantomJS(cmd, id);
        }
    }
    
    private String generatePhantomJSCommand(String type, String id) {
        return String.format("%s %s %s%s/%s %s/%s.html",
                phantomjs, snapshotScript, baseUrl, type, id, snapshotFolder, id);
    }
    
    private String generatePhantomJSCommand(String type, String id, String lang) {
        return String.format("%s %s %s%s/%s?%s=%s %s/%s_%s.html",
                phantomjs, snapshotScript, baseUrl, type, id, QUERY_PARAM_LANG, lang, snapshotFolder, id, lang);
    }
    
    

    private void invokePhantomJS(String cmd, String id) throws IndexingException {

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
                throw new IndexingException(String.format("Rendering snapshot for learning opportunity %s failed with exit status: %d",
                        id, exitStatus));
            }

        } catch (IOException e) {
            throw new IndexingException(String.format("Rendering learning opportunity %s failed due to IOException: %s",
                    id, e.getMessage()));
        } catch (InterruptedException e) {
            throw new IndexingException(String.format("Rendering learning opportunity %s failed due to InterruptedException: %s",
                    id, e.getMessage()));
        }
    }
}
