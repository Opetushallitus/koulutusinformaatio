window.Config = window.Config || {};
window.Config.app = window.Config.app || {};

window.Config.app.common = {
	fi: {
		ophUrl: 'http://www.oph.fi/etusivu',
		okmUrl: 'http://www.minedu.fi/OPM/'
	},
	sv: {
    	ophUrl: 'http://www.oph.fi/startsidan',
    	okmUrl: 'http://www.minedu.fi/OPM/?lang=sv'
	},
    hakulomakeUrl: '/haku-app/',
    piwikUrl: 'https://analytiikka.opintopolku.fi/piwik/'
};

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

window.Config.app.dev = {
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