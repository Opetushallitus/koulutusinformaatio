angular.module('kiApp.services', ['ngResource']).
factory('LearningOpportunity', function($resource){
    return $resource('../lo/search/:q', {}, {
        query: {method:'GET', isArray:true}
    });
});


angular.module('kiApp', ['kiApp.services']).
config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/search/:q', {templateUrl: 'partials/hakutulokset.html', controller: SearchCtrl});
    $routeProvider.otherwise({redirectTo: '/search/'});
}]);



function SearchCtrl($scope, $routeParams, LearningOpportunity) {
    if ($routeParams.q) {
        $scope.loResult = LearningOpportunity.query({q: $routeParams.q});
        $scope.q = $routeParams.q;
        $scope.showFilters = $scope.q ? true : false;
    }

    $scope.search = function() {
        $scope.loResult = LearningOpportunity.query({q: $scope.q});
        $scope.showFilters = $scope.q ? true : false;
    };

    $scope.$on('$viewContentLoaded', ngReady);
}

/*
function SearchCtrl($scope, $http) {
  $http.get('../lo/search/*').success(function(data) {
    $scope.loResult = data;
  });
}
*/



