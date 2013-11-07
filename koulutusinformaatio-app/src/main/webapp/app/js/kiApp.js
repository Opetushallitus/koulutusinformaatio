/*  Application module */

var kiApp = angular.module('kiApp', ['kiApp.services', 'kiApp.directives', 'SearchResult', 'ui.bootstrap', 'angulartics', 'angulartics.piwik']);
kiApp.config(['$routeProvider', '$analyticsProvider', function($routeProvider, $analyticsProvider) {

    // initialize piwik analytics tool
    OPH.Common.initPiwik(window.Config.app.piwikUrl);
    $analyticsProvider.virtualPageviews(true);
    $analyticsProvider.firstPageview(false);

    $routeProvider.when('/haku/:queryString', {
    	templateUrl: 'partials/search/searchresults.html', 
    	controller: SearchCtrl
    });

    $routeProvider.when('/:loType/:id', {
        templateUrl: 'partials/learningopportunity.html', 
        controller: InfoCtrl,
        reloadOnSearch: false
        /*,
        resolve: {
            subcontroller: function($route) {
                switch($route.current.params.loType) {
                    case 'lukio':
                        return UpSecCtrl;
                    case 'koulutusohjelma':
                        return ChildCtrl;
                    case 'tutkinto':
                        return ParentCtrl;
                }
            }
        }
        */
    });

    $routeProvider.when('/muistilista', {
        templateUrl: 'partials/applicationbasket/applicationbasket.html',
        controller: ApplicationBasketCtrl
    });
    
    $routeProvider.otherwise({
    	redirectTo: '/haku/'
    });
    
}]);

kiApp.constant('kiAppConstants', {
    searchResultsPerPage: 30,
    searchResultsStartPage: 1,
    applicationBasketLimit: 5
});

kiApp.filter('escape', function() {
  return window.escape;
});

kiApp.filter('encodeURIComponent', function() {
    return window.encodeURIComponent;
});

// initialize i18n library
kiApp.run(['LanguageService', function(LanguageService) {
    i18n.init({
        resGetPath : 'locales/__ns__-__lng__.json',
        lng : LanguageService.getLanguage(),
        ns: 'language',
        getAsync : false,
        sendMissing : false,
        fallbackLng : 'fi',
        debug : false
    });
}]);

kiApp.value('appConfig', window.Config.app);
kiApp.factory('Config', function(appConfig, LanguageService) {
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
