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

import com.google.common.collect.Sets;
import fi.vm.sade.koulutusinformaatio.dao.transaction.TransactionManager;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.KISolrException;
import fi.vm.sade.koulutusinformaatio.service.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author Hannu Lyytikainen
 */
@Service
public class UpdateServiceImpl implements UpdateService {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateServiceImpl.class);

    private TarjontaService tarjontaService;
    private IndexerService indexerService;
    private EducationDataUpdateService educationDataUpdateService;
    private ArticleService articleService;
    private GeneralUpdateServiceImpl generalUpdateService;

    private TransactionManager transactionManager;
    private boolean running = false;
    private long runningSince = 0;
    private long fullIndexingStartTime;

    @Value("${koulutusinformaatio.error.report.recipients}")
    private String RECIPIENTS;

    @Value("${smtp.host:mta.i.ware.fi}")
    private String SMTP_HOST;

    @Value("${host.oppija:localhost}")
    private String ENVIRONMENT;

    @Autowired
    public UpdateServiceImpl(TarjontaService tarjontaService, IndexerService indexerService,
                             EducationDataUpdateService educationDataUpdateService,
                             TransactionManager transactionManager,
                             ArticleService articleService,
                             GeneralUpdateServiceImpl generalUpdateService) {
        this.tarjontaService = tarjontaService;
        this.indexerService = indexerService;
        this.educationDataUpdateService = educationDataUpdateService;
        this.transactionManager = transactionManager;
        this.articleService = articleService;
        this.generalUpdateService = generalUpdateService;
    }

    private void switchTask(StopWatch s, String task){
        if(s.isRunning()) {
            s.stop();
        }
        s.start(task);
    }

    @Override
    @Async
    public synchronized void updateAllEducationData() {
        HttpSolrServer loUpdateSolr = this.indexerService.getLoCollectionToUpdate();
        HttpSolrServer lopUpdateSolr = this.indexerService.getLopCollectionToUpdate(loUpdateSolr);
        HttpSolrServer locationUpdateSolr = this.indexerService.getLocationCollectionToUpdate(loUpdateSolr);
        fullIndexingStartTime = System.currentTimeMillis();

        StopWatch stopwatch = new StopWatch("Full indexing");
        stopwatch.start("Lukio koulutukset");
        try {
            LOG.info("Starting full education data update");
            running = true;
            runningSince = System.currentTimeMillis();

            this.transactionManager.beginTransaction(loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
            tarjontaService.clearProcessedLists();

            List<KoulutusHakutulosV1RDTO> lukioEducations = this.tarjontaService.findLukioKoulutusDTOs();
            LOG.info("Found lukio educations: " + lukioEducations.size());
            int i = 0;
            for (KoulutusHakutulosV1RDTO curDTO : lukioEducations) {
                LOG.debug("{}/{} Indexing lukio education: {}", ++i, lukioEducations.size(), curDTO.getOid());
                KoulutusLOS los = tarjontaService.createLukioKoulutusLOS(curDTO);
                if (los != null) {
                    indexToSolr(los, loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
                    this.educationDataUpdateService.save(los);
                }
                if(i % 100 == 0) {
                    LOG.info("Indexed {}/{} lukio educations", i, lukioEducations.size());
                }
            }
            LOG.info("Lukio educations saved.");
            tarjontaService.clearProcessedLists();
            switchTask(stopwatch, "Ammatilliset koulutukset");

            Set<String> savedTutkintos = Sets.newHashSet();
            List<KoulutusHakutulosV1RDTO> vocationalEducations = this.tarjontaService.findAmmatillinenKoulutusDTOs();
            LOG.info("Found vocational educations: " + vocationalEducations.size());
            i = 0;
            for (KoulutusHakutulosV1RDTO curDTO : vocationalEducations) {
                LOG.debug("{}/{} Indexing vocational education: {}", ++i, vocationalEducations.size(), curDTO.getOid());
                List<KoulutusLOS> losses = tarjontaService.createAmmatillinenKoulutusLOS(curDTO);
                if (losses != null && !losses.isEmpty()) {
                    for (KoulutusLOS curLOS : losses) {
                        this.educationDataUpdateService.save(curLOS);
                        if (curLOS.getTutkinto() == null) {
                            indexToSolr(curLOS, loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
                        } else if(!savedTutkintos.contains(curLOS.getTutkinto().getId())){
                            indexToSolr(curLOS.getTutkinto(), loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
                            this.educationDataUpdateService.save(curLOS.getTutkinto());
                            savedTutkintos.add(curLOS.getTutkinto().getId());
                        }
                    }
                }
                if(i % 100 == 0) {
                    LOG.info("Indexed {}/{} vocational educations", i, vocationalEducations.size());
                }
            }
            LOG.info("Vocational educations saved.");
            tarjontaService.clearProcessedLists();
            switchTask(stopwatch, "Korkeakoulujen koulutukset");

            List<HigherEducationLOS> higherEducations = this.tarjontaService.findHigherEducations();
            LOG.info("Found higher educations: {}", higherEducations.size());
            i = 0;
            for (HigherEducationLOS curLOS : higherEducations) {
                LOG.debug("{}/{} Saving higher education: {}", ++i, higherEducations.size(), curLOS.getId());

                indexToSolr(curLOS, loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
                this.educationDataUpdateService.save(curLOS);
                if(i % 100 == 0) {
                    LOG.info("Saved {}/{} higher educations", i, higherEducations.size());
                }
            }
            LOG.info("Higher educations saved.");
            tarjontaService.clearProcessedLists();
            switchTask(stopwatch, "Opintojaksot korkeakouluista");

            List<KoulutusHakutulosV1RDTO> opintojaksot = this.tarjontaService.findKorkeakouluOpinnot();
            LOG.info("Löytyi {} opintojaksoa.", opintojaksot.size());
            i = 0;
            for (KoulutusHakutulosV1RDTO dto : opintojaksot) {
                LOG.debug("{}/{} Luodaan ja tallennetaan opintojakso: {}", ++i, opintojaksot.size(), dto.getOid());
                List<KoulutusLOS> allLoses = tarjontaService.createKorkeakouluopinto(dto);
                for (KoulutusLOS los : allLoses) {
                    indexToSolr(los, loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
                    this.educationDataUpdateService.save(los);
                }
                if(i % 100 == 0) {
                    LOG.info("Tallennettu {}/{} opintojaksoa", i, opintojaksot.size());
                }
            }

            LOG.info("Korkeakouluopinnot tallennettu.");
            tarjontaService.clearProcessedLists();
            switchTask(stopwatch, "Aikuislukio ja aikuisten perusopetus");

            // Includes Aikuisten lukiokoulutus and Aikuisten perusopetus
            List<KoulutusLOS> adultEducations = this.tarjontaService.findAdultUpperSecondariesAndBaseEducation();
            LOG.info("Found adult upper secondary and base educations: {}", adultEducations.size());

            for (KoulutusLOS curLOS : adultEducations) {
                LOG.debug("Saving adult education: {}", curLOS.getId());
                indexToSolr(curLOS, loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
                this.educationDataUpdateService.save(curLOS);
            }
            LOG.info("Adult upper secondary and base educations saved.");
            switchTask(stopwatch, "Aikuisten ammaillinen koulutus");

            List<CompetenceBasedQualificationParentLOS> adultVocationals = this.tarjontaService.findAdultVocationals();
            LOG.info("Found adult vocational educations: {}", adultVocationals.size());
            i = 0;
            for (CompetenceBasedQualificationParentLOS curLOS : adultVocationals) {
                LOG.debug("{}/{} Saving adult vocational los: {} with name: {}",  ++i, adultVocationals.size(), curLOS.getId(), curLOS.getName().get("fi"));
                indexToSolr(curLOS, loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
                this.educationDataUpdateService.save(curLOS);
                if(i % 100 == 0) {
                    LOG.info("Saved {}/{} adult vocational educations", i, adultVocationals.size());
                }
            }
            LOG.info("Adult vocational educations saved.");
            switchTask(stopwatch, "Valmistava koulutus");

            List<KoulutusLOS> valmistavaList = this.tarjontaService.findValmistavaKoulutusEducations();
            LOG.info("Found valmistava educations: {}", valmistavaList.size());
            for (KoulutusLOS curLOS : valmistavaList) {
                LOG.debug("Saving adult valmistava los: {} with name: {}", curLOS.getId(), curLOS.getName().get("fi"));
                indexToSolr(curLOS, loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
                this.educationDataUpdateService.save(curLOS);
            }
            LOG.info("Valmistava educations saved.");
            switchTask(stopwatch, "Yleiskäyttöinen indeksointi");

            generalUpdateService.updateGeneralData(loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
            LOG.info("General information saved.");
            switchTask(stopwatch, "Tietojen commitointi");

            indexerService.commitLOChanges(loUpdateSolr, lopUpdateSolr, locationUpdateSolr, true);
            LOG.debug("Committed to solr");
            this.transactionManager.commit(loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
            LOG.debug("Transaction completed");
            educationDataUpdateService.save(new DataStatus(new Date(), System.currentTimeMillis() - runningSince, "SUCCESS", getProgressCounter()));

            LOG.info("Education data update successfully finished");
        } catch (Exception e) {
            LOG.error("Education data update failed ", e);
            rollBackUpdate(loUpdateSolr, lopUpdateSolr, locationUpdateSolr, e.getMessage());
            sendMailOnException(e);
        } catch (Throwable t) {
            LOG.error("Education data update failed ", t);
            rollBackUpdate(loUpdateSolr, lopUpdateSolr, locationUpdateSolr, t.getMessage());
            sendMailOnException(new RuntimeException(t));
        } finally {
            tarjontaService.clearProcessedLists();
            running = false;
            runningSince = 0;
            if(stopwatch.isRunning())
                stopwatch.stop();
            LOG.info("Koulutusindeksointi on valmis. Vaiheiden kestot:\n" + stopwatch.prettyPrint());
        }
    }

    private void rollBackUpdate(HttpSolrServer loUpdateSolr, HttpSolrServer lopUpdateSolr, HttpSolrServer locationUpdateSolr, String cause) {
        try {
            this.transactionManager.rollBack(loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
        } catch (KISolrException e) {
            LOG.error("Rollback failed", e);
        }
        educationDataUpdateService.save(new DataStatus(new Date(), System.currentTimeMillis() - runningSince, String.format("FAIL: %s", cause)));
    }

    public long getProgressCounter() {
        return System.currentTimeMillis() - fullIndexingStartTime;
    }

    private void indexToSolr(LOS curLOS,
                             HttpSolrServer loUpdateSolr, HttpSolrServer lopUpdateSolr, HttpSolrServer locationUpdateSolr) throws KISolrException {
        try {
            this.indexerService.addLearningOpportunitySpecification(curLOS, loUpdateSolr, lopUpdateSolr);
            this.indexerService.commitLOChanges(loUpdateSolr, lopUpdateSolr, locationUpdateSolr, false);
            if (curLOS instanceof HigherEducationLOS) {
                for (HigherEducationLOS curChild : ((HigherEducationLOS) curLOS).getChildren()) {
                    indexToSolr(curChild, loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
                }
            }
        } catch (Exception e) {
            throw new KISolrException("Indexing LOS " + curLOS.getId() + " to solr failed", e);
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public long getRunningSince() {
        return runningSince;
    }

    @Override
    @Async
    public void updateArticles() {

        if (this.running) {
            return;
        }

        LOG.info("Indexing articles");

        try {
            running = true;
            runningSince = System.currentTimeMillis();
            this.indexerService.removeArticles();

            List<Article> articles = this.articleService.fetchArticles();
            LOG.debug("Articles fetched");
            indexerService.addArticles(articles);

            //educationDataUpdateService.save(new DataStatus(new Date(), System.currentTimeMillis() - runningSince, "SUCCESS"));
            LOG.info("Articles succesfully indexed");
        } catch (Exception ex) {
            try {
                indexerService.rollbackIncrementalSolrChanges();
            } catch (KISolrException e) {
                LOG.error("Rollback failed", e);
            }
            educationDataUpdateService.save(new DataStatus(new Date(), System.currentTimeMillis() - runningSince, String.format("FAIL: Article indexing %s",
                    ex.getMessage())));
            LOG.warn("Article update failed ", ex);

        } finally {
            running = false;
            runningSince = 0;
        }
    }

    private void sendMailOnException(Exception exception) {
        String subject = "Koulutusinformaation indeksointi epäonnistui: " + ENVIRONMENT;
        String body = "Koulutusinformaation indeksointi epäonnistui ympäristössä " + ENVIRONMENT + "\nVirhe:\n\n";
        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        body += sw.toString();

        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        Session session = Session.getDefaultInstance(props, null);

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("admin@oph.fi", "admin@oph.fi"));
            msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(RECIPIENTS));
            msg.setSubject(subject);
            msg.setText(body);
            Transport.send(msg);
            LOG.info("Error mail successfully sent");
        } catch (Exception e) {
            LOG.error("Failed to send error mail.", e);
        }
    }
}
