window.Config = window.Config || {};
window.Config.app = window.Config.app || {};

/* Configurations common for all environments */
window.Config.app.common = {
	fi: {
		ophUrl: 'http://www.oph.fi/etusivu',
		okmUrl: 'http://www.minedu.fi/OPM/',
		textVersionUrl: '/m/index.html',
		sitemapUrl: '/fi/hakemisto/oppilaitokset/'
	},
	sv: {
    	ophUrl: 'http://www.oph.fi/startsidan',
    	okmUrl: 'http://www.minedu.fi/OPM/?lang=sv',
    	textVersionUrl: '/m/index_sv.html',
    	sitemapUrl: '/sv/hakemisto/oppilaitokset/'
	},
    hakulomakeUrl: '/haku-app/',
    piwikUrl: 'https://analytiikka.opintopolku.fi/piwik/'
};

/* Configurations for production environment */
window.Config.app.prod = {
	fi: {
		frontpageUrl: '/wp/fi/',
		rekisteriselosteUrl: '/wp/fi/rekisteriseloste/',
		navigationUrl: '/wp/fi/api/nav/json_nav/'
	},
	sv: {
		frontpageUrl: '/wp/sv/',
    	rekisteriselosteUrl: '/wp/sv/registerbeskrivning/',
    	navigationUrl: '/wp/sv/api/nav/json_nav/'
	}
};

/* Configurations for koulutus environment */
window.Config.app.koulutus = {
	fi: {
		frontpageUrl: '/wp/fi/',
		rekisteriselosteUrl: '/wp/fi/',
		navigationUrl: '/wp/fi/api/nav/json_nav/'
	},
	sv: {
		frontpageUrl: '/wp/sv/',
    	rekisteriselosteUrl: '/wp/sv/',
    	navigationUrl: '/wp/sv/api/nav/json_nav/'
	}
};

/* Configurations for QA environment */
window.Config.app.qa = {
	fi: {
		frontpageUrl: '/wp/fi/',
		rekisteriselosteUrl: '/wp/fi/rekisteriseloste/',
		navigationUrl: '/wp/fi/api/nav/json_nav/'
	},
	sv: {
		frontpageUrl: '/wp/sv/',
    	rekisteriselosteUrl: '/wp/sv/registerbeskrivning/',
    	navigationUrl: '/wp/sv/api/nav/json_nav/'
	}
};

/* Configurations for reppu environment */
window.Config.app.reppu = {
	fi: {
		frontpageUrl: '/wp/fi/',
		rekisteriselosteUrl: '/wp/fi/rekisteriseloste/',
		navigationUrl: '/wp/fi/api/nav/json_nav/'
	},
	sv: {
		frontpageUrl: '/wp/sv/',
    	rekisteriselosteUrl: '/wp/sv/registerbeskrivning/',
    	navigationUrl: '/wp/sv/api/nav/json_nav/'
	}
};

/* Configurations for development (localhost, luokka, kielistudio) environment */
// NOTE: currently Wordpress related data is fetched from QA environment
window.Config.app.dev = {
	fi: {
		frontpageUrl: 'https://testi.opintopolku.fi/wp/fi/',
		rekisteriselosteUrl: '/wp/fi/',
		navigationUrl: 'https://testi.opintopolku.fi/wp/fi/api/nav/json_nav/'
	},
	sv: {
		frontpageUrl: 'https://testi.opintopolku.fi/wp/sv/',
    	rekisteriselosteUrl: '/wp/sv/',
    	navigationUrl: 'https://testi.opintopolku.fi/wp/sv/api/nav/json_nav/'
	}
};