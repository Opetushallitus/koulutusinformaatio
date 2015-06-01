/*  Preview application module */

var kiApp = angular.module('previewApp', 
    [
        'kiApp.filters',
        'kiApp.services',
        'kiApp.directives',
        'ApplicationBasket',
        'kiApp.SearchWizard',
        'SearchResult', 
        'ui.bootstrap', 
        'angulartics', 
        'angulartics.piwik',
        'underscore',
        'ngRoute',
        'ngSanitize',
        'ngTouch'
    ])

// initialize piwik analytics tool
.config(['$analyticsProvider', function( $analyticsProvider) {
    OPH.Common.initPiwik(window.Config.app.common.piwikUrl);
    $analyticsProvider.virtualPageviews(true);
    $analyticsProvider.firstPageview(false);
}])

// routes provided by previewApp
.config(['$routeProvider', function($routeProvider) {
	
    $routeProvider.when('/:loType/:id', {
        templateUrl: 'partials/learningopportunity.html', 
        controller: InfoCtrl,
        reloadOnSearch: false,
        resolve: {
            loResource: function($route, $location, HigherEducationPreviewLOService) {
                switch($route.current.params.loType) {
                    case 'lukio':
                        return HigherEducationPreviewLOService;
                    case 'koulutusohjelma':
                        return HigherEducationPreviewLOService;
                    case 'tutkinto':
                        return HigherEducationPreviewLOService;
                    case 'erityisopetus':
                        return HigherEducationPreviewLOService;
                    case 'valmentava':
                        return HigherEducationPreviewLOService;
                    case 'korkeakoulu':
                        return HigherEducationPreviewLOService;
                    case 'aikuislukio':
                        return HigherEducationPreviewLOService;
                    case 'aikuistenperusopetus':
                        return HigherEducationPreviewLOService;
                    case 'ammatillinenaikuiskoulutus':
                        return HigherEducationPreviewLOService;
                    case 'koulutus':
                        return HigherEducationPreviewLOService;
                }
            },
            partialUrl: function($rootScope, $route) {
                $rootScope.partialUrl = 'partials/lo/' + UtilityService.getTemplateByLoType($route.current.params.loType) + '/';
                $rootScope.partialCommonUrl = 'partials/lo/common/';
            }
            
        }
    });
}])

.config(['$locationProvider', function($locationProvider) {
    $locationProvider.html5Mode(false);
    $locationProvider.hashPrefix('!');
}])


.config(function($httpProvider){
    $httpProvider.defaults.useXDomain = true;
    delete $httpProvider.defaults.headers.common['X-Requested-With'];
})

// general constants used in previewApp
.constant('kiAppConstants', {
    searchResultsPerPage: 25,
    defaultSortCriteria: '0',
    searchResultsStartPage: 1
})

// LO type constants
.constant('LOTypes', {
    TUTKINTO: 'tutkinto',
    KOULUTUSOHJELMA: 'koulutusohjelma',
    LUKIO: 'lukio',
    KORKEAKOULU: 'korkeakoulu',
    ERITYISOPETUS: 'erityisopetus',
    VALMENTAVA: 'valmentava',
    VALMISTAVA: 'valmistava',
    AMMATILLINENAIKUISKOULUTUS: 'ammatillinenaikuiskoulutus',
    KOULUTUS: 'koulutus',
    AIKUISLUKIO: 'aikuislukio'
})

// initialize i18n library
.run(['$location', 'LanguageService', 'HostResolver', 'VirkailijaLanguageService', function($location, LanguageService, HostResolver, VirkailijaLanguageService) {
    // 1. Setting ui-language based on url-parameter.
    // 2. Setting virkailija ui language
    // 3. Removing the parameter, to enable changing of language from ui
    var allowedLangs = ['fi', 'sv', 'en'];
    if (_.contains(allowedLangs, $location.search().lang)) {
        LanguageService.setLanguage($location.search().lang);
        VirkailijaLanguageService.setLanguage(LanguageService.getLanguage());
        $location.search('').replace();
    }

    // initialize i18next library
    i18n.init({
        resGetPath : 'locales/__ns__-__lng__.json',
        lng : LanguageService.getLanguage(),
        preload: ['fi', 'sv', 'en'],
        ns: {
            namespaces: ['language', 'tooltip', 'plain'],
            defaultNs: 'language'
        },
        cookieName: 'i18next',
        getAsync : false,
        sendMissing : false,
        fallbackLng : 'fi',
        debug : false
    });
}])

// create config object
.value('appConfig', window.Config.app)
.factory('Config', function($location, appConfig, LanguageService, HostResolver) {
    return {
        get: function(property) {
            var lang = LanguageService.getLanguage();
            var host = HostResolver.resolve($location.host());
            var mappedHost = HostResolver.mapHostToConf(host);
            if (appConfig[mappedHost][lang][property]) {
                return appConfig[mappedHost][lang][property];
            } else if (appConfig.common[lang][property]) {
                return appConfig.common[lang][property];
            } else {
                return appConfig.common[property];
            }
        }
    }
})

// Piwik analytics
var OPH = OPH || {};
OPH.Common = {
    initHeader: function() {},
    initPiwik: function(piwikUrl) {
        var siteDomain = document.domain;
        var piwikSiteId = 2;
        if(siteDomain=='opintopolku.fi'){
            piwikSiteId = 4;
        }else if(siteDomain=='virkailija.opintopolku.fi'){
            piwikSiteId = 3;
        }else if(siteDomain=='testi.opintopolku.fi'){
            piwikSiteId = 1;
        }else if(siteDomain=='testi.virkailija.opintopolku.fi'){
            piwikSiteId = 5;
        }else{
            piwikSiteId = 2;
        }

        window._paq = window._paq || [];
        _paq.push(["setDocumentTitle", document.domain + "/" + document.title]);
        _paq.push(["enableLinkTracking"]);

        (function() {
            var u = piwikUrl;
            _paq.push(["setTrackerUrl", u+"piwik.php"]);
            _paq.push(["setSiteId", piwikSiteId]);
            var d=document, g=d.createElement("script"), s=d.getElementsByTagName("script")[0]; g.type="text/javascript";
            g.defer=true; g.async=true; g.src=u+"piwik.js"; s.parentNode.insertBefore(g,s);
        })();
    }
};
