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

package fi.vm.sade.koulutusinformaatio.service;

import java.util.List;

import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;

/**
 * @author Mikko Majapuro
 */
public interface KoodistoService {

    /**
     * Search localized texts by given koodi uri
     * @param koodiUri koodi uri with specified version e.g. "tutkintonimikkeet_10129#1" or the latest version "tutkintonimikkeet_10129"
     * @return list of I18nText objects
     * @throws KoodistoException
     */
    List<Code> search(final String koodiUri) throws KoodistoException;

    /**
     * Search localized texts by given koodi uris
     * @param koodiUris koodi uris with specified versions e.g. "tutkintonimikkeet_10129#1" or the latest version "tutkintonimikkeet_10129"
     * @return list of I18nText objects
     * @throws KoodistoException
     */
    List<Code> searchMultiple(final List<String> koodiUris) throws KoodistoException;

    /**
     * Search localized texts by given koodi uri, returns the first search result
     * @param koodiUri koodi uri with specified version e.g. "tutkintonimikkeet_10129#1" or the latest version "tutkintonimikkeet_10129"
     * @return I18nText object
     * @throws KoodistoException
     */
    Code searchFirst(final String koodiUri) throws KoodistoException;

    List<Code> searchByKoodisto(String koodistoUri, Integer version) throws KoodistoException;

    /**
     * Search codes from koodisto service by given uri.
     * @param koodiUri koodi uri
     * @return list of Code objects
     * @throws KoodistoException
     */
    List<I18nText> searchNames(final String koodiUri) throws KoodistoException;

    /**
     * Search codes from koodisto service by given uri, returns the first search result
     * @param koodiUri koodi uri
     * @return Code object
     * @throws KoodistoException
     */
    I18nText searchFirstName(final String koodiUri) throws KoodistoException;

    /**
     * Search codes from koodisto service by given uri, returns the first search result value
     * @param koodiUri
     * @return
     * @throws KoodistoException
     */
    String searchFirstCodeValue(final String koodiUri) throws KoodistoException;

    /**
     * Searches for codes that are included in the code that corresponds to the uri and version
     * given as parameter
     *
     * @param koodiURIAndVersion    Koodi URI of the parent code, includes version info.
     * @param koodistoURI           Koodisto filter. Defines to which koodisto the sub koodis
     *                              should belong to. Does not include version info.
     * @return List of codes.
     */
    List<Code> searchSubCodes(final String koodiURIAndVersion, final String koodistoURI) throws KoodistoException;

    /**
     *
     * @param koodiURIAndVersion
     * @param koodistoURI
     * @return
     * @throws KoodistoException
     */
    List<Code> searchSuperCodes(final String koodiURIAndVersion, final String koodistoURI) throws KoodistoException;
    
    void clearCache();
}
