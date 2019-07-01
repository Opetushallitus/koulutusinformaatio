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
    $http.defaults.headers.common['Caller-Id'] = "1.2.246.562.10.00000000001.koulutusinformaatio.koulutusinformaatio-app.frontend";
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
          function getLanguageFromHost(host) {
              if (!host)
                  host = document.location.host;
              var x = host.split('.');
              if (x.length < 2) return 'fi';
              var domain = x[x.length - 2];
              if (domain.indexOf('opintopolku') > -1) {
                  return 'fi';
              } else if (domain.indexOf('studieinfo') > -1) {
                  return 'sv';
              } else if (domain.indexOf('studyinfo') > -1) {
                  return 'en'
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
    $routeProvider.when('/haku', {
        redirectTo: '/haku/*'
    });

    $routeProvider.when('/haku/:queryString*', {
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
                    case 'tutkinto':
                        return ParentLOService;
                    case 'koulutus':
                        return KoulutusLOService;
                    case 'korkeakoulu':
                        return HigherEducationLOService;
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
    KORKEAKOULU: 'korkeakoulu',
    KOULUTUS: 'koulutus',
    AMMATILLINENAIKUISKOULUTUS: 'ammatillinenaikuiskoulutus',
    AIKUISLUKIO: 'aikuislukio',
    AIKUISTENPERUSOPETUS: 'aikuistenperusopetus'
})

// initialize i18n and recaptcha libraries
.run(['$location', '$rootScope', 'LanguageService', 'vcRecaptchaService', function($location, $rootScope, LanguageService, recaptcha) {
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

    if(!recaptcha && !vcRecapthaApiLoaded){
        console.error("vcRecaptchaService is not defined on load.")
    }

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
        var piwikSiteId;
        switch (siteDomain) {
            case "opintopolku.fi":
                piwikSiteId = 4;
                break;
            case "studieinfo.fi":
                piwikSiteId = 13;
                break;
            case "studyinfo.fi":
                piwikSiteId = 14;
                break;
            case "virkailija.opintopolku.fi":
                piwikSiteId = 3;
                break;
            case "testi.opintopolku.fi":
            case "testi.studieinfo.fi":
            case "testi.studyinfo.fi":
                piwikSiteId = 1;
                break;
            case "testi.virkailija.opintopolku.fi":
                piwikSiteId = 5;
                break;
            case "demo-opintopolku.fi":
            case "demo-studieinfo.fi":
            case "demo-studyinfo.fi":
                piwikSiteId = 15;
                break;
            default:
                piwikSiteId = 2; // Kehitys
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
