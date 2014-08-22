"use strict";

/*
 *  Directives to implement collapsible content blocks
 */
angular.module('kiApp.directives.TextBlocks', []).

directive('kiTextBlock', [function() {
    return {
        restrict: 'A',
        templateUrl: 'templates/textBlock.html',
        scope: {
            title: '@',
            content: '=',
            level: '@'
        },
        controller: function($rootScope, $scope) {
            $rootScope.$watch('translationLanguage', function(value) {
                $scope.translationLanguage = value;
            });
        }
    };
}]).

directive('kiCollapseTextBlock', [function() {
    return {
        restrict: 'A',
        templateUrl: 'templates/collapseTextBlock.html',
        scope: {
            title: '@',
            content: '=',
            closed: '@',
            anchor: '@'
        },
        controller: function($rootScope, $scope) {
            $rootScope.$watch('translationLanguage', function(value) {
                $scope.translationLanguage = value;
            });

            $scope.toggleState = function() {
                $scope.isClose = !$scope.isClose;
            };
        },
        link: function($scope, element, attrs) {
            if ($scope.closed === undefined) {
                $scope.isClose = true;
            }
        }
    };
}]).

directive('kiCollapseBlock', ['CollapseBlockService', function(CollapseBlockService) {
    return {
        restrict: 'A',
        transclude: true,
        templateUrl: 'templates/collapseBlock.html',
        scope: {
            content: '=',
            title: '@',
            titleValue: '=',
            closed: '@',
            anchor: '@'
        },
        controller: function($rootScope, $scope) {
            $scope.blockId = $scope.$id;

            $rootScope.$watch('translationLanguage', function(value) {
                $scope.translationLanguage = value;
            });

            $scope.toggleState = function() {
                $scope.isClose = !$scope.isClose;
            };

            $scope.isVisible = function() {
                return CollapseBlockService.getBlock($scope.blockId);
            };

        },
        link: function($scope, element, attrs, ctrl, transclude) {
            if ($scope.closed === undefined) {
                $scope.isClose = true;
            }

            // transcluded content uses directives isolated scope
            transclude($scope, function(clone, $scope) {
                element.find('.collapse-content-container').append(clone);
            });

        }
    };
}]);