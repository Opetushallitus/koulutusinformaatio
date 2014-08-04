/*  Application module */

var kiApp = angular.module('previewApp', 
    [
        'kiApp.services',
        'kiApp.directives',
        'directives.AjaxLoader',
        'ApplicationBasket',
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
                }
            },
            partialUrl: function($rootScope, $route) {
                $rootScope.partialUrl = 'partials/lo/' + $route.current.params.loType + '/';
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
    //$httpProvider.defaults.headers.common['Access-Control-Allow-Origin'] = '*';
})

.constant('kiAppConstants', {
    searchResultsPerPage: 25,
    defaultSortCriteria: '0',
    searchResultsStartPage: 1
})

.filter('escape', function() {
  return window.escape;
})

.filter('encodeURIComponent', function() {
    return window.encodeURIComponent;
})

// adds target blank to links
.filter('externalLinks', function() {
    return function(val) {
        if (val) {
            val = val.replace('<a', '<a target="_blank"');
        }
        
        return val;
    }
})

.filter('tables', function() {
    return function(val) {
        if (val) {
            val = val.replace(/<\s*table.*?>/gi, '<table class="table table-striped table-condensed table-responsive>"');
        }

        return val;
    }
})

// initialize i18n library
.run(['$location', 'LanguageService', 'HostResolver', 'VirkailijaLanguageService', function($location, LanguageService, HostResolver, VirkailijaLanguageService) {
	
	// 1. Setting ui-language based on url-parameter.
	// 2. Setting virkailija ui language
    // 3. Removing the parameter, to enable changing of language from ui
	if ($location.search().lang != undefined 
			&& ($location.search().lang == 'fi' || $location.search().lang == 'sv')) {
		LanguageService.setLanguage($location.search().lang);
        VirkailijaLanguageService.setLanguage(LanguageService.getLanguage());
		$location.search('').replace();
	}

    var currentHost = $location.host();
    var defaultName = 'i18next';
    var i18nCookieName = HostResolver.getCookiePrefixByDomain(currentHost) + defaultName;
	
    // initialize i18next library
    i18n.init({
        resGetPath : 'locales/__ns__-__lng__.json',
        lng : LanguageService.getLanguage(),
        ns: {
            namespaces: ['language', 'tooltip', 'plain'],
            defaultNs: 'language'
        },
        cookieName: i18nCookieName,
        getAsync : false,
        sendMissing : false,
        fallbackLng : 'fi',
        debug : false
    });
}])

.filter('unique', function() {
   return function(collection, keyname) {
      var output = [], 
          keys = [];

      angular.forEach(collection, function(item) {
          var key = item[keyname];
          if(keys.indexOf(key) === -1) {
              keys.push(key);
              output.push(item);
          }
      });

      return output;
   };
})

.filter('unsafe', function($sce) {
    return function(val) {
        return $sce.trustAsHtml(val);
    };
})

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
