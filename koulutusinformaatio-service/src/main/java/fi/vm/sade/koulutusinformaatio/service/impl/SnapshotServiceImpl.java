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

import fi.vm.sade.koulutusinformaatio.dao.AdultVocationalLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.HigherEducationLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.KoulutusLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.TutkintoLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.entity.CodeEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.HigherEducationLOSEntity;
import fi.vm.sade.koulutusinformaatio.domain.exception.IndexingException;
import fi.vm.sade.koulutusinformaatio.domain.exception.KIException;
import fi.vm.sade.koulutusinformaatio.service.SnapshotService;
import fi.vm.sade.properties.OphProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

/**
 * @author Hannu Lyytikainen
 */
@Component
public class SnapshotServiceImpl implements SnapshotService {

    private static final Logger LOG = LoggerFactory.getLogger(SnapshotServiceImpl.class);

    private static final String TYPE_HIGHERED = "korkeakoulu";
    private static final String TYPE_ADULT_VOCATIONAL = "ammatillinenaikuiskoulutus";
    private static final String TYPE_KOULUTUS = "koulutus";
    private static final String TYPE_TUTKINTO = "tutkinto";
    private static final String QUERY_PARAM_LANG = "descriptionLang";

    private HigherEducationLOSDAO higheredDAO;
    private AdultVocationalLOSDAO adultvocDAO;
    private KoulutusLOSDAO koulutusDAO;
    private TutkintoLOSDAO tutkintoLOSDAO;

    private String phantomjs;
    private String snapshotScript;
    private String snapshotFolder;
    private String baseUrl;

    @Autowired
    public SnapshotServiceImpl(@Qualifier("higherEducationLOSDAO") HigherEducationLOSDAO higheredDAO,
                               @Qualifier("adultVocationalLOSDAO") AdultVocationalLOSDAO adultvocDAO,
                               @Qualifier("koulutusLOSDAO") KoulutusLOSDAO koulutusDAO,
                               @Qualifier("tutkintoLOSDAO") TutkintoLOSDAO tutkintoLOSDAO,
                               @Value("${koulutusinformaatio.phantomjs}") String phantomjs,
                               @Value("${koulutusinformaatio.snapshot.script}") String script,
                               @Value("${koulutusinformaatio.snapshot.folder}") String prerenderFolder,
                               OphProperties urlProperties) {
        this.higheredDAO = higheredDAO;
        this.adultvocDAO = adultvocDAO;
        this.koulutusDAO = koulutusDAO;
        this.tutkintoLOSDAO = tutkintoLOSDAO;
        this.phantomjs = phantomjs;
        this.snapshotScript = script;
        this.snapshotFolder = prerenderFolder;
        this.baseUrl = urlProperties.url("koulutusinformaatio-app-web.learningopportunity.base");
    }

    @Override
    public void renderSnapshots() throws IndexingException {
        LOG.info("Rendering html snapshots");
        prerenderWithTeachingLanguages(TYPE_HIGHERED, higheredDAO.findIds());
        LOG.debug("HigherEd LOs rendered");
        prerender(TYPE_ADULT_VOCATIONAL, adultvocDAO.findIds());
        LOG.debug("Adult vocational LOs rendered");
        prerender(TYPE_KOULUTUS, koulutusDAO.findIds());
        LOG.debug("Koulutus LOs rendered");
        prerender(TYPE_TUTKINTO, tutkintoLOSDAO.findIds());
        LOG.debug("Tutkinto LOs rendered");
        LOG.info("Rendering html snapshots finished");
    }

    private void prerenderWithTeachingLanguages(String type, List<String> ids) throws IndexingException {
        double count = 0; int percents = 0;
        for(String id : ids) {
            if(count / ids.size() > percents) { //Log progress once per %
                LOG.info("Rendering {} {}%", type, ++percents);
            }
            HigherEducationLOSEntity los = higheredDAO.get(id);

            // generate snapshot for each teaching language
            for(CodeEntity teachingLang : los.getTeachingLanguages()) {
                try {
                    String lang = "";
                    if(teachingLang != null && teachingLang.getValue() != null) {
                        lang = teachingLang.getValue().toLowerCase();
                    }
                    String[] cmd = generatePhantomJSCommand(type, id, lang);
                    invokePhantomJS(cmd, id);
                } catch(KIException e) {
                    LOG.error(e.getMessage());
                }
            }

            // generate default snapshot
            String[] cmd = generatePhantomJSCommand(type, id);
            invokePhantomJS(cmd, id);
        }
    }

    private void prerender(String type, List<String> ids) throws IndexingException {
        double toLog = ids.size() / 20.0, count = toLog; int fivePercents = 0;
        for(String id : ids) {
            if(--count < 0) { //Log progress every 5 %
                count = toLog;
                LOG.info("Rendering {} {}%", type, ++fivePercents);
            }
            String[] cmd = generatePhantomJSCommand(type, id);
            invokePhantomJS(cmd, id);
        }
    }

    private String[] generatePhantomJSCommand(String type, String id) {
        String url = format("%s%s/%s", baseUrl, type, id);
        String filename = format("%s/%s.html", snapshotFolder, id);
        return new String[]{phantomjs, snapshotScript, url, filename};
    }

    private String[] generatePhantomJSCommand(String type, String id, String lang) {
        String url = format("%s%s/%s?%s=%s", baseUrl, type, id, QUERY_PARAM_LANG, lang);
        String filename = format("%s/%s_%s.html", snapshotFolder, id, lang);
        return new String[]{phantomjs, snapshotScript, url, filename};
    }


    private void invokePhantomJS(String[] cmd, String id) throws IndexingException {
        try {
            LOG.debug(Arrays.toString(cmd));

            // "/usr/bin/phantomjs /path/to/script.js http://www.opintopolku.fi/app/#!/koulutus/1.2.3.4.5 /path/to/file"
            ProcessBuilder ps = new ProcessBuilder(cmd);

            ps.redirectErrorStream(true);

            Process pr = ps.start();
            BufferedReader phantomOutput = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;
            while((line = phantomOutput.readLine()) != null) {
                LOG.info(line);
            }
            int exitStatus = pr.waitFor();

            phantomOutput.close();

            if(exitStatus != 0) {
                throw new IndexingException(format("Rendering snapshot for learning opportunity %s failed with exit status: %d",
                        id, exitStatus));
            }

        } catch(IOException e) {
            throw new IndexingException(format("Rendering learning opportunity %s failed due to IOException: %s",
                    id, e.getMessage()));
        } catch(InterruptedException e) {
            throw new IndexingException(format("Rendering learning opportunity %s failed due to InterruptedException: %s",
                    id, e.getMessage()));
        }
    }
}
