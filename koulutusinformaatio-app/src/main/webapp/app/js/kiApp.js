/*  Application module */

var kiApp = angular.module('kiApp', ['kiApp.services', 'kiApp.directives', 'ui.bootstrap']);
kiApp.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
    $routeProvider.when('/haku/:queryString', {
    	templateUrl: 'partials/hakutulokset.html', 
    	controller: SearchCtrl
    });
    $routeProvider.when('/tutkinto/:parentId', {
    	templateUrl: 'partials/ylataso.html', 
    	controller: InfoCtrl
    });
    $routeProvider.when('/koulutusohjelma/:childId', {
    	templateUrl: 'partials/alataso.html', 
    	controller: InfoCtrl
    });
    $routeProvider.when('/muistilista', {
        templateUrl: 'partials/applicationbasket/applicationbasket.html',
        controller: ApplicationBasketCtrl
    });
    $routeProvider.otherwise({
    	redirectTo: '/haku/'
    });
}]);


kiApp.filter('escape', function() {
  return window.escape;
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

kiApp.constant('kiAppConstants', {
    searchResultsPerPage: 30,
    searchResultsStartPage: 1,
    applicationBasketLimit: 5,
    contextRoot: '../static/'
});

var OPH = OPH || {};

OPH.Common = {
    initHeader: function() {}
};
