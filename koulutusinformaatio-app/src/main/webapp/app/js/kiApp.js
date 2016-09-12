var kiApp = angular.module('kiApp',
    [
        'kiApp.filters',
        'kiApp.services',
        'kiApp.directives',
        'ApplicationBasket',
        'kiApp.SearchWizard',
        'kiApp.SearchBySubject',
        'Intro',
        'SearchResult', 
        'ui.bootstrap', 
        'angulartics', 
        'angulartics.piwik',
        'underscore',
        'ngRoute',
        'ngSanitize',
        'ngTouch',
        'ngAnimate',
        'ngCookies',
        'vcRecaptcha',
        'pascalprecht.translate'
    ])
.run(function($http, $cookies) {
    $http.defaults.headers.common['clientSubSystemCode'] = "koulutusinformaatio.koulutusinformaatio-app.frontend";
    if($cookies['CSRF']) {
        $http.defaults.headers.common['CSRF'] = $cookies['CSRF'];
    }
})
.factory('useFinnishWhenMissing', function () {
  return function (translationID) {
      return localeJSON ? localeJSON[translationID + ".fi"] : translationID
  };
})
.config(['$translateProvider', function ($translateProvider) {
      try {
          var l = localeJSON
          function isLang(lang) {
              return function(v,k) {
                  return !(_.endsWith(k, lang));
              };
          };
          function removePostfix(val, key) {
              return key.substring(0, key.length - 3)
          }
          $translateProvider.translations('fi', _.mapKeys(_.omit(l, isLang('fi')), removePostfix));
          $translateProvider.translations('sv', _.mapKeys(_.omit(l, isLang('sv')), removePostfix));
          $translateProvider.translations('en', _.mapKeys(_.omit(l, isLang('en')), removePostfix));
          function getLanguageFromHost() {
            var x = window.location.host.split('.')
            if (x.length < 2)
                return 'fi'
            switch (x[x.length - 2]) {
            case 'opintopolku': return 'fi'
            case 'studieinfo': return 'sv'
            case 'studyinfo': return 'en'
            }
            return 'fi'
          }
          $translateProvider.useMissingTranslationHandler('useFinnishWhenMissing');
          $translateProvider.preferredLanguage(getLanguageFromHost());
      } catch(e) {

      }
}])
// initialize piwik analytics tool
.config(['$analyticsProvider', function( $analyticsProvider) {
    OPH.Common.initPiwik();
    $analyticsProvider.virtualPageviews(true);
    $analyticsProvider.firstPageview(false);
}])

// routes provided by kiApp
.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/haku/:queryString?', {
    	templateUrl: 'partials/search/search.html', 
    	controller: SearchCtrl,
        reloadOnSearch: false
    });

    $routeProvider.when('/hakuwizard', {
        templateUrl: 'partials/searchwizard/searchwizard.html',
        controller: 'SearchWizardCtrl',
        resolve: {
            factory: function($rootScope, $location) {
                // studyinfo has no search wizard
                if ($rootScope.isStudyInfo) {
                    $location.path('/haku/');
                }
            }
        }
    });

    $routeProvider.when('/selailu/aihe', {
        templateUrl: 'partials/searchbysubject/searchbysubject.html',
        controller: 'SearchBySubjectCtrl'
    });

    $routeProvider.when('/organisaatio/:id', {
        templateUrl: 'partials/organisation/organisation.html',
        controller: OrganisationCtrl
    });

    $routeProvider.when('/:loType/:id', {
        templateUrl: 'partials/learningopportunity.html', 
        controller: InfoCtrl,
        reloadOnSearch: false,
        resolve: {
            loResource : function(
                    $route,
                    $location,
                    UpperSecondaryLOService,
                    KoulutusLOService,
                    ChildLOService,
                    ParentLOService,
                    SpecialLOService,
                    HigherEducationLOService,
                    AdultVocationalLOService) {
                
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
                    case 'koulutus':
                        return KoulutusLOService;
                    case 'korkeakoulu':
                        return HigherEducationLOService;
                    case 'valmistava':
                        return SpecialLOService;
                    case 'aikuislukio':
                        return KoulutusLOService;
                    case 'ammatillinenaikuiskoulutus':
                        return AdultVocationalLOService;
                    case 'aikuistenperusopetus':
                        return KoulutusLOService;
                }
            },
            partialUrl: function($rootScope, $route, UtilityService) {
                $rootScope.partialUrl = 'partials/lo/' + UtilityService.getTemplateByLoType($route.current.params.loType) + '/';
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

// general constants used in kiApp
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
    KOULUTUS: 'koulutus',
    AMMATILLINENAIKUISKOULUTUS: 'ammatillinenaikuiskoulutus',
    AIKUISLUKIO: 'aikuislukio',
    AIKUISTENPERUSOPETUS: 'aikuistenperusopetus'
})

// initialize i18n and recaptcha libraries
.run(['$location', '$rootScope', 'LanguageService', function($location, $rootScope, LanguageService) {
    i18n.init({
        resGetPath : 'locales/__ns__-__lng__.json',
        lng : LanguageService.getLanguage(),
        preload: ['fi', 'sv', 'en'],
        ns: {
            namespaces: ['language', 'tooltip', 'plain', 'searchwizard', 'appbasket', 'searchbysubject'],
            defaultNs: 'language'
        },
        cookieName: 'i18next',
        getAsync : false,
        sendMissing : false,
        fallbackLng : 'fi',
        debug : false
    });

    // set global flag when ui language is English
    $rootScope.isStudyInfo = LanguageService.getLanguage() === 'en';

    var recaptchaElem = document.createElement("script");
    recaptchaElem.src =  "https://www.google.com/recaptcha/api.js?onload=vcRecapthaApiLoaded&render=explicit&hl=" + LanguageService.getLanguage();
    recaptchaElem.defer = true;
    recaptchaElem.async = true;
    document.getElementsByTagName("head")[0].appendChild(recaptchaElem);
}])

// create config object
.value('appConfig', window.Config.app)
.factory('Config', function($location, appConfig, LanguageService, HostResolver) {
    return {
        get: function(property) {
            var lang = LanguageService.getLanguage();
            var mappedHost = HostResolver.mapHostToConf($location.host());
            return appConfig[mappedHost][property] ||
                appConfig.common[lang][property] ||
                appConfig.common[property];
        }
    }
});

// Piwik analytics
var OPH = OPH || {};
OPH.Common = {
    initPiwik: function() {
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
            var u = window.url("piwik.url");
            _paq.push(["setTrackerUrl", u+"piwik.php"]);
            _paq.push(["setSiteId", piwikSiteId]);
            var d=document, g=d.createElement("script"), s=d.getElementsByTagName("script")[0]; g.type="text/javascript";
            g.defer=true; g.async=true; g.src=u+"piwik.js"; s.parentNode.insertBefore(g,s);
        })();
    }
};
