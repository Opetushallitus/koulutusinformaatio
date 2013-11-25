/*  Application module */

var kiApp = angular.module('kiApp', 
    [
        'kiApp.services', 
        'kiApp.directives',
        'ApplicationBasket',
        'SearchResult', 
        'ui.bootstrap', 
        'angulartics', 
        'angulartics.piwik'
    ])

.config(['$analyticsProvider', function( $analyticsProvider) {
    // initialize piwik analytics tool
    OPH.Common.initPiwik(window.Config.app.piwikUrl);
    $analyticsProvider.virtualPageviews(true);
    $analyticsProvider.firstPageview(false);
}])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/haku/:queryString', {
    	templateUrl: 'partials/search/search.html', 
    	controller: SearchCtrl
    });

    $routeProvider.when('/:loType/:id', {
        templateUrl: 'partials/learningopportunity.html', 
        controller: InfoCtrl,
        reloadOnSearch: false,
        resolve: {
            loResource: function($route, UpperSecondaryLOService, ChildLOService, ParentLOService) {
                switch($route.current.params.loType) {
                    case 'lukio':
                        return UpperSecondaryLOService;
                    case 'koulutusohjelma':
                        return ChildLOService;
                    case 'tutkinto':
                        return ParentLOService;
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
        controller: 'ApplicationBasketCtrl'
    });
    
    $routeProvider.otherwise({
    	redirectTo: '/haku/'
    });
}])

.constant('kiAppConstants', {
    searchResultsPerPage: 25,
    defaultSortCriteria: '0',
    searchResultsStartPage: 1,
    applicationBasketLimit: 5
})

.filter('escape', function() {
  return window.escape;
})

.filter('encodeURIComponent', function() {
    return window.encodeURIComponent;
})

// initialize i18n library
.run(['LanguageService', function(LanguageService) {
    i18n.init({
        resGetPath : 'locales/__ns__-__lng__.json',
        lng : LanguageService.getLanguage(),
        ns: 'language',
        getAsync : false,
        sendMissing : false,
        fallbackLng : 'fi',
        debug : false
    });
}])

.value('appConfig', window.Config.app)
.factory('Config', function(appConfig, LanguageService) {
    return {
        get: function(property) {
            var lang = LanguageService.getLanguage();
            if (appConfig[lang][property]) {
                return appConfig[lang][property];
            } else {
                return appConfig[property];
            }
        }
    }
})

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
