/*  Application module */

var kiApp = angular.module('kiApp', ['kiApp.services', 'kiApp.directives', 'ngCookies']);
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
    $routeProvider.when('/info/:parentId/:childId', {
    	templateUrl: 'partials/alataso.html', 
    	controller: InfoCtrl
    });
    $routeProvider.otherwise({
    	redirectTo: '/index/'
    });

    //$locationProvider.html5Mode(true);
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
        dropDownMenu.build();
    }
};
