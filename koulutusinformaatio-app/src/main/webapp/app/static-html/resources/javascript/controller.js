/*
function SearchCtrl($scope, LearningOpportunity) {

    $scope.search = function() {
        $scope.loResult = LearningOpportunity.query({q: $scope.q});
    };

}
*/

function SearchCtrl($scope, $http) {
  $http.get('../lo/search/*').success(function(data) {
    $scope.loResult = data;
  });

  $scope.orderProp = 'age';
}

/*
angular.module('kiApp.services', ['ngResource']).
    factory('LearningOpportunity', function($resource){
        return $resource('../lo/search/:q', {}, {
            query: {method:'GET', isArray:true}
        });
    });

    angular.module('kiApp', ['kiApp.services']);
    */