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
import fi.vm.sade.koulutusinformaatio.dao.AdultVocationalLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.HigherEducationLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.KoulutusLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.TutkintoLOSDAO;
import fi.vm.sade.koulutusinformaatio.dao.entity.CodeEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.HigherEducationLOSEntity;
import fi.vm.sade.koulutusinformaatio.domain.exception.IndexingException;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;


/**
 * @author Hannu Lyytikainen
 */
@Component
public class SnapshotServiceImpl implements SnapshotService {

    private static final Logger LOG = LoggerFactory.getLogger(SnapshotServiceImpl.class);

    @Value("${koulutusinformaatio.phantomjs.threads ?: 2}")
    private static int THREADS_TO_RUN_PHANTOMJS;

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
        List<String[]> cmds = Lists.newArrayList();
        for (String id : ids) {
            HigherEducationLOSEntity los = higheredDAO.get(id);

            // generate snapshot for each teaching language
            for (CodeEntity teachingLang : los.getTeachingLanguages()) {
                String lang = "";
                if (teachingLang != null && teachingLang.getValue() != null) {
                    lang = teachingLang.getValue().toLowerCase();
                }
                String[] cmd = generatePhantomJSCommand(type, id, lang);
                cmds.add(cmd);
            }
            // generate default snapshot
            String[] cmd = generatePhantomJSCommand(type, id);
            cmds.add(cmd);
        }
        invokePhantomJS(cmds);
    }

    private void prerender(String type, List<String> ids) throws IndexingException {
        List<String[]> cmds = Lists.newArrayList();
        for (String id : ids) {
            cmds.add(generatePhantomJSCommand(type, id));
        }
        invokePhantomJS(cmds);
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

    private void invokePhantomJS(List<String[]> cmds) throws IndexingException {
        ExecutorService executor = Executors.newFixedThreadPool(THREADS_TO_RUN_PHANTOMJS);
        for (String[] cmd : cmds) {
            InvokePhantomJs worker = new InvokePhantomJs(cmd);
            executor.execute(worker);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            throw new IndexingException("Failed to render snapshots in time", e);
        }
    }

    private class InvokePhantomJs implements Runnable {
        private String[] cmd;

        public InvokePhantomJs(String[] cmd) {
            this.cmd = cmd;
        }

        @Override
        public void run() {
            try {
                LOG.debug(Arrays.toString(cmd));
                // "/usr/bin/phantomjs /path/to/script.js http://www.opintopolku.fi/app/#!/koulutus/1.2.3.4.5 /path/to/file"
                ProcessBuilder ps = new ProcessBuilder(cmd);

                ps.redirectErrorStream(true);

                Process pr = ps.start();
                BufferedReader phantomOutput = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                String line;
                while ((line = phantomOutput.readLine()) != null) {
                    LOG.info(line);
                }
                try {
                    int exitStatus = pr.waitFor();
                    if (exitStatus != 0) {
                        LOG.warn(format("Rendering %s failed with exit status: %d.",
                                Arrays.toString(cmd), exitStatus));
                    }
                } catch (InterruptedException e) {
                    LOG.error("Failed to render snapshots in time", e);
                } finally {
                    phantomOutput.close();
                }
            } catch (IOException e) {
                LOG.warn(format("Rendering %s failed.", Arrays.toString(cmd)), e);
            }
        }
    }
}
