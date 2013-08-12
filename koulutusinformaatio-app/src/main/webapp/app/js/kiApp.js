/*  Application module */

var kiApp = angular.module('kiApp', ['kiApp.services', 'kiApp.directives', 'ui.bootstrap']);
kiApp.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
    //$locationProvider.hashPrefix('!');
    /*
	$routeProvider.when('/index/', {
		templateUrl: 'partials/etusivu.html', 
		controller: IndexCtrl
	});
    */
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
kiApp.run(['LanguageService', '$location', function(LanguageService, $location) {
    var lang;
    if ($location && $location.$$search && $location.$$search.lang) {
        lang = $location.$$search.lang;
        LanguageService.setLanguage(lang);
        $location.search('lang', null);
    }

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
    contextRoot: '/koulutusinformaatio-app'
});

var OPH = OPH || {};

OPH.Common = {
    initHeader: function() {
        //dropDownMenu.build();
        //popover.build();
        //ApplicationBasket.load();
    }
};
