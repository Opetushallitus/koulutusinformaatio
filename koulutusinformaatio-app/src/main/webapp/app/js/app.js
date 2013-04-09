'use strict';


// Declare app level module which depends on filters, and services
angular.module('kiApp', ['kiApp.filters', 'kiApp.services', 'kiApp.directives']).
  config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/search', {templateUrl: 'partials/search.html', controller: SearchCtrl});
    $routeProvider.otherwise({redirectTo: '/search'});
  }]);
