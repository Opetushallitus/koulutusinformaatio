/*  Application module */

var kiApp = angular.module('kiApp', 
    [
        'kiApp.filters',
        'kiApp.services',
        'kiApp.directives',
        'ApplicationBasket',
        'SearchWizard',
        'Intro',
        'SearchResult', 
        'ui.bootstrap', 
        'angulartics', 
        'angulartics.piwik',
        'underscore',
        'ngRoute'
    ])

.config(['$analyticsProvider', function( $analyticsProvider) {
    // initialize piwik analytics tool
    OPH.Common.initPiwik(window.Config.app.common.piwikUrl);
    $analyticsProvider.virtualPageviews(true);
    $analyticsProvider.firstPageview(false);
}])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/haku/:queryString?', {
    	templateUrl: 'partials/search/search.html', 
    	controller: SearchCtrl,
        reloadOnSearch: false
    });

    $routeProvider.when('/hakuwizard', {
        templateUrl: 'partials/searchwizard/searchwizard.html',
        controller: 'SearchWizardCtrl',
        reloadOnSearch: false,
        resolve: {
            factory: function($rootScope, $location) {
                // studyinfo has no search wizard
                if ($rootScope.isStudyInfo) {
                    $location.path('/haku/');
                }
            }
        }
    });

    $routeProvider.when('/:loType/:id', {
        templateUrl: 'partials/learningopportunity.html', 
        controller: InfoCtrl,
        reloadOnSearch: false,
        resolve: {
            loResource: function($route, $location, UpperSecondaryLOService, ChildLOService, ParentLOService, SpecialLOService, HigherEducationLOService, AdultUpperSecondaryLOService, AdultVocationalLOService) {
                switch($route.current.params.loType) {
                    case 'lukio':
                        return UpperSecondaryLOService;
                    case 'koulutusohjelma':
                        return ChildLOService;
                    case 'tutkinto':
                        return ParentLOService;
                    case 'erityisopetus':
                        return SpecialLOService;
                    case 'valmentava':
                        return SpecialLOService;
                    case 'korkeakoulu':
                    	return HigherEducationLOService;
                    case 'valmistava':
                        return SpecialLOService;
                    case 'aikuislukio':
                        return AdultUpperSecondaryLOService;
                    case 'ammatillinenaikuiskoulutus':
                        return AdultVocationalLOService;
                }
            },
            partialUrl: function($rootScope, $route) {
                $rootScope.partialUrl = 'partials/lo/' + $route.current.params.loType + '/';
                $rootScope.partialCommonUrl = 'partials/lo/common/';
            }
        }
    });

    $routeProvider.when('/muistilista', {
        templateUrl: 'partials/applicationbasket/applicationbasket.html',
        controller: 'AppBasketCtrl'
    });

    $routeProvider.otherwise({
    	redirectTo: '/haku/'
    });
}])

.config(['$locationProvider', function($locationProvider) {
    $locationProvider.html5Mode(false);
    $locationProvider.hashPrefix('!');
}])


.config(['$httpProvider', function($httpProvider){
    $httpProvider.defaults.useXDomain = true;
    delete $httpProvider.defaults.headers.common['X-Requested-With'];
}])

.constant('kiAppConstants', {
    searchResultsPerPage: 25,
    defaultSortCriteria: '0',
    searchResultsStartPage: 1
})

// initialize i18n library
.run(['$location', '$rootScope', 'LanguageService', 'HostResolver', function($location, $rootScope, LanguageService, HostResolver) {
    var defaultName = 'i18next';
    var currentHost = $location.host();
    var i18nCookieName = HostResolver.getCookiePrefixByDomain(currentHost) + defaultName;

    i18n.init({
        resGetPath : 'locales/__ns__-__lng__.json',
        lng : LanguageService.getLanguage(),
        preload: ['fi', 'sv', 'en'],
        ns: {
            namespaces: ['language', 'tooltip', 'plain', 'searchwizard', 'appbasket'],
            defaultNs: 'language'
        },
        cookieName: i18nCookieName,
        getAsync : false,
        sendMissing : false,
        fallbackLng : 'fi',
        debug : false
    });

    // set global flag when ui language is English
    $rootScope.isStudyInfo = LanguageService.getLanguage() === 'en';
}])

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
});

var OPH = OPH || {};

OPH.Common = {
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
