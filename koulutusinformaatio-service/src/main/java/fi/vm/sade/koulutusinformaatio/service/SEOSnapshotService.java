package fi.vm.sade.koulutusinformaatio.service;

import fi.vm.sade.koulutusinformaatio.domain.dto.SnapshotDTO;

/**
 * Hakukohteiden hakukoneoptimoidun HTML-muotoisen kuvauksen tallennus ja haku
 *
 * @see fi.vm.sade.koulutusinformaatio.resource.SnapshotResource
 */
public interface SEOSnapshotService {
    /**
     * Hakukohteen hakukoneoptimoidun HTML-muotoisen kuvauksen haku oid:llä.
     *
     * @param oid Oid, josta HTML-muotoinen kuvaus on generoitu
     * @return Oid:n mukainen HTML-muotoinen kuvaus
     */
    SnapshotDTO getSnapshot(final String oid);

    /**
     * Tallentaa hakukohteen hakukoneoptimoidun HTML-kuvauksen tietokantaan.
     *
     * @param snapshot Hakukohteen HTML-muotoinen kuvaus
     */
    void createSnapshot(final SnapshotDTO snapshot);
}