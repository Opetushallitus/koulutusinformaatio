'use strict';

/* Controllers */


function SearchCtrl($scope, LearningOpportunity) {

    $scope.search = function() {
        $scope.loResult = LearningOpportunity.query({q: $scope.q});
    };

}


function MyCtrl2() {
}
MyCtrl2.$inject = [];
