/*  Application module */

var kiApp = angular.module('kiApp', ['kiApp.services', 'kiApp.directives']);
kiApp.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
	$routeProvider.when('/index/', {
		templateUrl: 'partials/etusivu.html', 
		controller: IndexCtrl
	});
    $routeProvider.when('/haku/:queryString', {
    	templateUrl: 'partials/hakutulokset.html', 
    	controller: SearchCtrl
    });
    $routeProvider.when('/info/:parentId', {
    	templateUrl: 'partials/ylataso.html', 
    	controller: InfoCtrl
    });
    $routeProvider.when('/info/:parentId/:closId/:cloiId', {
    	templateUrl: 'partials/alataso.html', 
    	controller: InfoCtrl
    });
    /*
    $routeProvider.when('/info/:parentId', {
        redirectTo: '/info/:parentId/kuvaus'
    });
    */
    $routeProvider.otherwise({
    	redirectTo: '/index/'
    });

    //$locationProvider.html5Mode(true);
}]);

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

/*
kiApp.run(['$location', '$rootScope', function($location, $rootScope) {
    $rootScope.$on('$routeChangeSuccess', function (event, current, previous) {
    	console.log($rootScope);
        $rootScope.title = current.$route.title;
    });
}]);
*/


var OPH = OPH || {};

OPH.Common = {
    initDropdownMenu: function() {
        //dropDownMenu.build();
    }
};
