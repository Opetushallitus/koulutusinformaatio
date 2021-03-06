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

package fi.vm.sade.koulutusinformaatio.service;

import javax.annotation.Nullable;
import java.util.Date;

/**
 * Used to handle SEO related operations like
 * updating sitemaps and rendering html snapshots.
 *
 * @author Hannu Lyytikainen
 */
public interface SEOService {

    /**
     * Update koulutusinformaatio sitemap and render all html snapshots of all learning opportunity views.
     */
    void update();
    void createSitemap();

    /**
     * Update koulutusinformaatio sitemap and render modified html snapshots of all learning opportunity views.
     */
    void updateLastModified();

    /**
     * Is update operation running.
     *
     * @return
     */
    boolean isRunning();

    /**
     * Returns the last time the learning opportunity sitemap file has been changed.
     *
     * @return sitemap file timestamp
     */
    @Nullable
    Date getSitemapTimestamp();
}
