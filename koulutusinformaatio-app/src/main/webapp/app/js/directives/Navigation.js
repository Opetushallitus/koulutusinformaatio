angular.module('kiApp.Navigation', []).

directive('kiSkipNavigation', function() {
    return {
        restrict: 'A',
        template: '<div id="skip-link"><a href="javascript:void(0)" data-ki-i18n="skip-to-content" data-ng-click="skipNavigation()"></a></div>',
        controller: function($scope, $location, $anchorScroll) {
            $scope.skipNavigation = function() {
                $location.hash('page');
                $anchorScroll();
                angular.element('#search-field').focus();
            }
        }
    }
});
