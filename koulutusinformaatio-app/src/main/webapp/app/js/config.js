window.Config = window.Config || {};
window.Config.app = window.Config.app || {};

/* Configurations common for all environments, some are language specific */
window.Config.app.common = {
	fi: {
        frontpageUrl: '/wp/fi/',
        introUrl: '/wp/fi/api/get_search_results/?search=+&post_type=oph-helptext'
	},
	sv: {
        frontpageUrl: '/wp/sv/',
        introUrl: '/wp/sv/api/get_search_results/?search=+&post_type=oph-helptext'
	},
    en: {
        frontpageUrl: '/wp2/en/',
        introUrl: '/wp2/en/api/get_search_results/?search=+&post_type=oph-helptext'
    },
    hakulomakeUrl: '/haku-app/',
    piwikUrl: 'https://analytiikka.opintopolku.fi/piwik/',
    loginUrl: '/omatsivut/login'
};

/* Configurations for production environment */
window.Config.app.prod = {
	fi: {
		tarjontaUrl: 'https://virkailija.opintopolku.fi/tarjonta-app/#'
	},
	sv: {
		tarjontaUrl: 'https://virkailija.opintopolku.fi/tarjonta-app/#'
	},
    en: {
        tarjontaUrl: 'https://virkailija.opintopolku.fi/tarjonta-app/#'
    }
};

/* Configurations for koulutus environment */
window.Config.app.koulutus = {
	fi: {
		tarjontaUrl: 'https://koulutus.virkailija.opintopolku.fi/tarjonta-app/#'
	},
	sv: {
		tarjontaUrl: 'https://koulutus.virkailija.opintopolku.fi/tarjonta-app/#'
	},
    en: {
        tarjontaUrl: 'https://koulutus.virkailija.opintopolku.fi/tarjonta-app/#'
    }
};

/* Configurations for QA environment */
window.Config.app.qa = {
	fi: {
		tarjontaUrl: 'https://testi.virkailija.opintopolku.fi/tarjonta-app/#'
	},
	sv: {
    	tarjontaUrl: 'https://testi.virkailija.opintopolku.fi/tarjonta-app/#'
	},
    en: {
        tarjontaUrl: 'https://testi.virkailija.opintopolku.fi/tarjonta-app/#'
    }
};

/* Configurations for reppu environment */
window.Config.app.reppu = {
	fi: {
		tarjontaUrl: 'https://test-virkailija.oph.ware.fi/tarjonta-app/#'
	},
	sv: {
    	tarjontaUrl: 'https://test-virkailija.oph.ware.fi/tarjonta-app/#'
	},
    en: {
        tarjontaUrl: 'https://test-virkailija.oph.ware.fi/tarjonta-app/#'
    }
};

/* Configurations for development (localhost, luokka, kielistudio) environment */
// NOTE: currently Wordpress related data is fetched from QA environment
window.Config.app.dev = {
	fi: {
		tarjontaUrl: 'https://itest-virkailija.oph.ware.fi/tarjonta-app/#',
        introUrl: 'https://testi.opintopolku.fi/wp/fi/api/get_search_results/?search=+&post_type=oph-helptext'
	},
	sv: {
    	tarjontaUrl: 'https://itest-virkailija.oph.ware.fi/tarjonta-app/#',
        introUrl: 'https://testi.opintopolku.fi/wp/sv/api/get_search_results/?search=+&post_type=oph-helptext'
	},
    en: {
        tarjontaUrl: 'https://itest-virkailija.oph.ware.fi/tarjonta-app/#',
        introUrl: 'https://testi.opintopolku.fi/wp2/en/api/get_search_results/?search=+&post_type=oph-helptext'
    }
};