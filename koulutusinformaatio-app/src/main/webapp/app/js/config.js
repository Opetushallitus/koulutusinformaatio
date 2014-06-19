window.Config = window.Config || {};
window.Config.app = window.Config.app || {};

/* Configurations common for all environments */
window.Config.app.common = {
	fi: {
		ophUrl: 'http://www.oph.fi/etusivu',
		okmUrl: 'http://www.minedu.fi/OPM/',
		textVersionUrl: '/m/index.html',
		sitemapUrl: '/fi/hakemisto/oppilaitokset/A'
	},
	sv: {
    	ophUrl: 'http://www.oph.fi/startsidan',
    	okmUrl: 'http://www.minedu.fi/OPM/?lang=sv',
    	textVersionUrl: '/m/index_sv.html',
    	sitemapUrl: '/sv/hakemisto/oppilaitokset/A'
	},
    en: {
        ophUrl: 'http://www.oph.fi/english',
        okmUrl: 'http://www.minedu.fi/OPM/?lang=en',
        textVersionUrl: '/m/index.html',
        sitemapUrl: '/en/hakemisto/oppilaitokset/'
    },
    hakulomakeUrl: '/haku-app/',
    piwikUrl: 'https://analytiikka.opintopolku.fi/piwik/'
};

/* Configurations for production environment */
window.Config.app.prod = {
	fi: {
		frontpageUrl: '/wp/fi/',
		rekisteriselosteUrl: '/wp/fi/rekisteriseloste/',
		navigationUrl: '/wp/fi/api/nav/json_nav/',
		tarjontaUrl: 'https://virkailija.opintopolku.fi/tarjonta-app/#'
	},
	sv: {
		frontpageUrl: '/wp/sv/',
    	rekisteriselosteUrl: '/wp/sv/registerbeskrivning/',
    	navigationUrl: '/wp/sv/api/nav/json_nav/',
		tarjontaUrl: 'https://virkailija.opintopolku.fi/tarjonta-app/#'
	},
    en: {
        frontpageUrl: '/wp2/en/',
        rekisteriselosteUrl: '/wp2/en/register/',
        navigationUrl: '/wp2/en/api/nav/json_nav/',
        tarjontaUrl: 'https://virkailija.opintopolku.fi/tarjonta-app/#'
    }
};

/* Configurations for koulutus environment */
window.Config.app.koulutus = {
	fi: {
		frontpageUrl: '/wp/fi/',
		rekisteriselosteUrl: '/wp/fi/',
		navigationUrl: '/wp/fi/api/nav/json_nav/',
		tarjontaUrl: 'https://koulutus.virkailija.opintopolku.fi/tarjonta-app/#'
	},
	sv: {
		frontpageUrl: '/wp/sv/',
    	rekisteriselosteUrl: '/wp/sv/',
    	navigationUrl: '/wp/sv/api/nav/json_nav/',
		tarjontaUrl: 'https://koulutus.virkailija.opintopolku.fi/tarjonta-app/#'
	},
    en: {
        frontpageUrl: '/wp2/en/',
        rekisteriselosteUrl: '/wp2/en/',
        navigationUrl: '/wp2/en/api/nav/json_nav/',
        tarjontaUrl: 'https://koulutus.virkailija.opintopolku.fi/tarjonta-app/#'
    }
};

/* Configurations for QA environment */
window.Config.app.qa = {
	fi: {
		frontpageUrl: '/wp/fi/',
		rekisteriselosteUrl: '/wp/fi/rekisteriseloste/',
		navigationUrl: '/wp/fi/api/nav/json_nav/',
		tarjontaUrl: 'https://testi.virkailija.opintopolku.fi/tarjonta-app/#'
	},
	sv: {
		frontpageUrl: '/wp/sv/',
    	rekisteriselosteUrl: '/wp/sv/registerbeskrivning/',
    	navigationUrl: '/wp/sv/api/nav/json_nav/',
    	tarjontaUrl: 'https://testi.virkailija.opintopolku.fi/tarjonta-app/#'
	},
    en: {
        frontpageUrl: '/wp2/en/',
        rekisteriselosteUrl: '/wp2/en/register/',
        navigationUrl: '/wp2/en/api/nav/json_nav/',
        tarjontaUrl: 'https://testi.virkailija.opintopolku.fi/tarjonta-app/#'
    }
};

/* Configurations for reppu environment */
window.Config.app.reppu = {
	fi: {
		frontpageUrl: '/wp/fi/',
		rekisteriselosteUrl: '/wp/fi/rekisteriseloste/',
		navigationUrl: '/wp/fi/api/nav/json_nav/',
		tarjontaUrl: 'https://test-virkailija.oph.ware.fi/tarjonta-app/#'
	},
	sv: {
		frontpageUrl: '/wp/sv/',
		rekisteriselosteUrl: '/wp/sv/registerbeskrivning/',
    	navigationUrl: '/wp/sv/api/nav/json_nav/',
    	tarjontaUrl: 'https://test-virkailija.oph.ware.fi/tarjonta-app/#'
	},
    en: {
        frontpageUrl: '/wp2/en/',
        rekisteriselosteUrl: '/wp2/en/register/',
        navigationUrl: 'https://testi.opintopolku.fi/wp2/en/api/nav/json_nav/',
        tarjontaUrl: 'https://test-virkailija.oph.ware.fi/tarjonta-app/#'
    }
};

/* Configurations for development (localhost, luokka, kielistudio) environment */
// NOTE: currently Wordpress related data is fetched from QA environment
window.Config.app.dev = {
	fi: {
		frontpageUrl: 'https://testi.opintopolku.fi/wp/fi/',
		rekisteriselosteUrl: '/wp/fi/',
		navigationUrl: 'https://testi.opintopolku.fi/wp/fi/api/nav/json_nav/',
		tarjontaUrl: 'https://itest-virkailija.oph.ware.fi/tarjonta-app/#'
	},
	sv: {
		frontpageUrl: 'https://testi.opintopolku.fi/wp/sv/',
    	rekisteriselosteUrl: '/wp/sv/',
    	navigationUrl: 'https://testi.opintopolku.fi/wp/sv/api/nav/json_nav/',
    	tarjontaUrl: 'https://itest-virkailija.oph.ware.fi/tarjonta-app/#'
	},
    en: {
        frontpageUrl: 'https://testi.opintopolku.fi/wp2/en/',
        rekisteriselosteUrl: 'https://testi.opintopolku.fi/wp2/en/rekisteriseloste',
        navigationUrl: 'https://testi.opintopolku.fi/wp2/en/api/nav/json_nav/',
        tarjontaUrl: 'https://itest-virkailija.oph.ware.fi/tarjonta-app/#'
    }
};
