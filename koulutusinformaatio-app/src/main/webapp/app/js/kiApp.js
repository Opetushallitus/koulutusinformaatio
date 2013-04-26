/*  Application module */

angular.module('kiApp', ['kiApp.services', 'kiApp.directives', 'ngCookies']).
config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
    $routeProvider.when('/haku/:queryString', {templateUrl: 'partials/hakutulokset.html', controller: SearchCtrl});
    $routeProvider.when('/index/', {templateUrl: 'partials/etusivu.html', controller: IndexCtrl});
    $routeProvider.when('/info/:parentId', {templateUrl: 'partials/ylataso.html', controller: InfoCtrl});
    $routeProvider.when('/info/:parentId/:childId', {templateUrl: 'partials/alataso.html', controller: InfoCtrl});
    $routeProvider.otherwise({redirectTo: '/index/'});

    //$locationProvider.html5Mode(true);
}]);

var OPH = OPH || {};

OPH.Common = {
    initDropdownMenu: function() {
        dropDownMenu.build();
    }
};
