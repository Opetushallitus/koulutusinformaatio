window.Config = window.Config || {};
window.Config.app = window.Config.app || {};

/* Configurations common for all environments, some are language specific */
window.Config.app.common = {
    fi : {
        frontpageUrl : '/wp/fi/',
        introUrl : '/wp/fi/api/get_search_results/?search=+&post_type=oph-helptext'
    },
    sv : {
        frontpageUrl : '/wp/sv/',
        introUrl : '/wp/sv/api/get_search_results/?search=+&post_type=oph-helptext'
    },
    en : {
        frontpageUrl : '/wp2/en/',
        introUrl : '/wp2/en/api/get_search_results/?search=+&post_type=oph-helptext'
    },
};

/* Configurations for production environment */
window.Config.app.prod = {
    tarjontaUrl : 'https://virkailija.opintopolku.fi/tarjonta-app/#'
};

/* Configurations for demo environment */
window.Config.app.demo = {
    frontpageUrl: '/app/demo.html',
    introUrl: 'https://opintopolku.fi/wp/fi/api/get_search_results/?search=+&post_type=oph-helptext',
    tarjontaUrl: 'https://virkailija.opintopolku.fi/tarjonta-app/#'
};
/* Configurations for koulutus environment */
window.Config.app.koulutus = {
    tarjontaUrl : 'https://koulutus.virkailija.opintopolku.fi/tarjonta-app/#'
};

/* Configurations for QA environment */
window.Config.app.qa = {
    tarjontaUrl : 'https://testi.virkailija.opintopolku.fi/tarjonta-app/#'
};

/* Configurations for luokka environment */
window.Config.app.luokka = {
    tarjontaUrl : 'https://itest-virkailija.oph.ware.fi/tarjonta-app/#',
    introUrl : 'https://testi.opintopolku.fi/wp/fi/api/get_search_results/?search=+&post_type=oph-helptext',
    frontpageUrl : 'https://itest-oppija.oph.ware.fi/app/'
};

/* Configurations for development environment */
// NOTE: currently Wordpress related data is fetched from QA environment
window.Config.app.dev = {
    tarjontaUrl : 'https://itest-virkailija.oph.ware.fi/tarjonta-app/#',
    introUrl : 'https://testi.opintopolku.fi/wp/fi/api/get_search_results/?search=+&post_type=oph-helptext'
};

window.Config.educationTypes = {
    LUKIOKOULUTUS: 'et01.01',
    AIKUISLUKIO: 'et01.01.02',
    KAKSOISTUTKINTO: 'et01.02',
    AMMATILLINEN_KOULUTUS: 'et01.03.01',
    AMMATILLINEN_ERITYISOPETUS: 'et01.03.02',
    AMMATTITUTKINTO: 'et01.03.03',
    ERIKOISAMMATTITUTKINTO: 'et01.03.04',
    AMMATTIKORKEAKOULUTUTKINTO: 'et01.04.01',
    YLEMPI_AMMATTIKORKEAKOULUTUTKINTO: 'et01.04.02',
    AVOIN_AMMATTIKORKEAKOULUTUTKINTO: 'et01.04.03',
    YLIOPISTO: 'et01.05',
    ALEMPI_YLIOPISTO: 'et01.05.01',
    YLEMPI_YLIOPISTO: 'et01.05.02',
    AVOIN_YLIOPISTO: 'et01.05.03',

    KYMPPILUOKKA: 'et02.01.01',
    MAAHANMUUTTAJIEN_LUKIOON_VALMISTAVA: 'et02.01.04',
    VALMA: 'et02.01.06',
    AIKUISTENPERUSOPETUS: 'et02.03',
    KANSANOPISTOJEN_PITKAT_LINJAT: 'et02.05',
    TELMA: 'et02.12.01',
    VALMA_ER: 'et02.12.02'
};
