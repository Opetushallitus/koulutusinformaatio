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

import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

/**
 * @author Hannu Lyytikainen
 */
public class TarjontaConstants {

    public static final String MODULE_TYPE_PARENT = "TUTKINTO";
    public static final String MODULE_TYPE_CHILD = "TUTKINTO_OHJELMA";
    public static final String STATE_PUBLISHED = "JULKAISTU";

    public static final String BASE_EDUCATION_KOODISTO_URI = "pohjakoulutustoinenaste";
    public static final String FORM_OF_EDUCATION_FACET_KOODISTO_URI = "opetuspaikkakk";
    public static final String TIME_OF_EDUCATION_FACET_KOODISTO_URI = "opetusaikakk";
    public static final String FORM_OF_STUDY_FACET_KOODISTO_URI = "opetusmuotokk";

    public static final String LANG_FI = "fi";

    public static final String TYPE_PARENT = "TUTKINTO";
    public static final String TYPE_CHILD = "KOULUTUSOHJELMA";
    public static final String TYPE_SPECIAL = "ERITYISOPETUS";
    public static final String TYPE_REHAB = "VALMENTAVA";
    public static final String TYPE_PREP = "VALMISTAVA";
    public static final String TYPE_UPSEC = "LUKIO";
    public static final String TYPE_KK = "KORKEAKOULU";
    public static final String TYPE_ADULT_UPSEC = "AIKUISLUKIO";
    public static final String TYPE_ADULT_VOCATIONAL = "AMMATILLINENAIKUISKOULUTUS";

    public static final String VOCATIONAL_EDUCATION_TYPE = "AmmatillinenPeruskoulutus";
    public static final String UPPER_SECONDARY_EDUCATION_TYPE = "Lukiokoulutus";
    public static final String REHABILITATING_EDUCATION_TYPE = "ValmentavaJaKuntouttavaOpetus";
    public static final String PREPARATORY_VOCATIONAL_EDUCATION_TYPE = "AmmOhjaavaJaValmistavaKoulutus";
    public static final String TENTH_GRADE_EDUCATION_TYPE = "PerusopetuksenLisaopetus";
    public static final String IMMIGRANT_PREPARATORY_VOCATIONAL = "MaahanmAmmValmistavaKoulutus";
    public static final String IMMIGRANT_PREPARATORY_UPSEC = "MaahanmLukioValmistavaKoulutus";
    public static final String KANSANOPISTO_TYPE = "VapaanSivistystyonKoulutus";
    public static final String HIGHER_EDUCATION_TYPE = "Korkeakoulutus";
    
    
    public static final String NUORTEN_KOULUTUS = "koulutuslaji_n";
    public static final String AIKUISKOULUTUS = "koulutuslaji_a";
    
    public static final String KOTITALOUSKOODI = "koulutus_038411";
    
    public static final String KANDI_TUNTEMATON = "699999";

    public static final String PREREQUISITE_URI_ER = "pohjakoulutusvaatimustoinenaste_er";
    
    public static final String ED_DEGREE_URI_AMK = "koulutusasteoph2002_62";
    public static final String ED_DEGREE_URI_YLEMPI_AMK = "koulutusasteoph2002_71";
    public static final String ED_DEGREE_URI_KANDI = "koulutusasteoph2002_63";
    public static final String ED_DEGREE_URI_MAISTERI = "koulutusasteoph2002_72";

    public static final String ATHLETE_EDUCATION_KOODISTO_URI = "urheilijankoulutus_1#1";
    public static final String APPLICATION_OPTIONS_KOODISTO_URI = "hakukohteet";
    
    public static final String TUTKINTONIMIKE_KK_KOODISTO_URI =  "tutkintonimikekk";
}
