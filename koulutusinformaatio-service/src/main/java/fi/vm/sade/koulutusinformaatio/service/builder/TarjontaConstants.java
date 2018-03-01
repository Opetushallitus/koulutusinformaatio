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

package fi.vm.sade.koulutusinformaatio.service.builder;


import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.Set;

/**
 * @author Hannu Lyytikainen
 */
public class TarjontaConstants {

    public static final String STATE_PUBLISHED = "JULKAISTU";
    public static final String STATE_READY = "VALMIS";

    public static final String BASE_EDUCATION_KOODISTO_URI = "pohjakoulutustoinenaste";

    public static final String TYPE_PARENT = "TUTKINTO";
    public static final String TYPE_KK = "KORKEAKOULU";
    public static final String TYPE_ADULT_UPSEC = "AIKUISLUKIO";
    public static final String TYPE_ADULT_VOCATIONAL = "AMMATILLINENAIKUISKOULUTUS";
    public static final String TYPE_ADULT_BASE = "AIKUISTENPERUSOPETUS";
    public static final String TYPE_KOULUTUS = "KOULUTUS";

    public static final String PREPARATORY_VOCATIONAL_EDUCATION_TYPE = "AmmOhjaavaJaValmistavaKoulutus";
    public static final String TENTH_GRADE_EDUCATION_TYPE = "PerusopetuksenLisaopetus";
    public static final String IMMIGRANT_PREPARATORY_VOCATIONAL = "MaahanmAmmValmistavaKoulutus";
    public static final String IMMIGRANT_PREPARATORY_UPSEC = "MaahanmLukioValmistavaKoulutus";
    public static final String KANSANOPISTO_TYPE = "VapaanSivistystyonKoulutus";


    public static final String NUORTEN_KOULUTUS = "koulutuslaji_n";
    public static final String AIKUISKOULUTUS = "koulutuslaji_a";
    public static final String AVOIN_KAIKILLE = "koulutuslaji_av";

    public static final String KOTITALOUSKOODI = "koulutus_038411";

    public static final String KANDI_TUNTEMATON = "699999";

    public static final String ED_DEGREE_URI_AMK = "koulutusasteoph2002_62";
    public static final String ED_DEGREE_URI_YLEMPI_AMK = "koulutusasteoph2002_71";
    public static final String ED_DEGREE_URI_KANDI = "koulutusasteoph2002_63";
    public static final String ED_DEGREE_URI_MAISTERI = "koulutusasteoph2002_72";
    public static final String ED_DEGREE_URI_TUTKIJA = "koulutusasteoph2002_80";
    public static final String ED_DEGREE_URI_LISENSIAATTI = "koulutusasteoph2002_81";
    public static final String ED_DEGREE_URI_TOHTORI = "koulutusasteoph2002_82";
    public static final Set<String> ED_DEGREE_URIS_JATKOKOULUTUS = Collections.unmodifiableSet(Sets.newHashSet(ED_DEGREE_URI_TUTKIJA, ED_DEGREE_URI_LISENSIAATTI, ED_DEGREE_URI_TOHTORI));

    public static final String TUTKINTONIMIKE_KK_KOODISTO_URI =  "tutkintonimikekk";
    public static final String TUTKINTONIMIKEET_KOODISTO_URI =  "tutkintonimikkeet";
    public static final String KOULUTUSTYYPPIFASETTI_KOODISTO_URI =  "koulutustyyppifasetti";
    public static final String POHJAKOULUTUSFASETTI_KOODISTO_URI =  "pohjakoulutusfasetti";
    public static final String POHJAKOULUTUSVAATIMUSTOINENASTE_KOODISTO_URI = "pohjakoulutusvaatimustoinenaste";
    public static final String HAKUKELPOISUUSVAATIMUS = "hakukelpoisuusvaatimusta";

    public static final String HAKUTAPA_YHTEISHAKUV1 = "hakutapa_01#1";
    public static final String HAKUTAPA_YHTEISHAKU = "hakutapa_01";
    public static final String HAKUTAPA_ERILLIS = "hakutapa_02";

    public static final String HAKUTYYPPI_VARSINAINEN = "hakutyyppi_01";
    public static final String HAKUTYYPPI_LISA = "hakutyyppi_03";
    public static final String OPPILAITOSTYYPPIFASETT_OPPISOPIMUS = "oppilaitostyyppifasetti_15";
    public static final String ORG_TYPE_OPPISOPIMUSTOIMIPISTE = "Oppisopimustoimipiste";
    public static final String ORG_TYPE_TOIMIPISTE = "Toimipiste";
    public static final String ORG_TYPE_OPPILAITOS = "Oppilaitos";
    public static final String KOODISTO_OPPILAITOSTYYPPIFASETTI = "oppilaitostyyppifasetti";

    public static final String OPPILAITOSTYYPPI_AMK = "oppilaitostyyppi_41";
    public static final String OPPILAITOSTYYPPI_YLIOPISTO = "oppilaitostyyppi_42";
    public static final String OPPILAITOSTYYPPI_SOTILASKK = "oppilaitostyyppi_43";
    public static final String ATHLETE_EDUCATION_KOODISTO_URI = "urheilijankoulutus_1#1";
    public static final String APPLICATION_OPTIONS_KOODISTO_URI = "hakukohteet";

    public static final String KOHDEJOUKKO_KORKEAKOULUTUS = "haunkohdejoukko_12";
    public static final String KOHDEJOUKONTARKENNE_SIIRTOHAKU = "haunkohdejoukontarkenne_1";
}
