angular.module('kiApp.directives.FacetTitle', []).

/**
 *  Sets tooltips for faceted search
 */
directive('facetTitle', ['TranslationService', function(TranslationService) {
    return {
        restrict: 'A',
        scope: {
            facetNode: '=facetTitle'
        },
        link: function($scope, element, attrs) {
            var uasFacets = ['ak', 'et01.04', 'et01.04.01', 'et01.04.02', 'et02.08', 'et02.09'];

            if ($scope.facetNode) {
                if (uasFacets.indexOf($scope.facetNode.valueId) >= 0) {
                    element.attr('title', TranslationService.getTranslation('tooltip:uas'))
                } else {
                    element.attr('title', $scope.facetNode.valueName);
                }
            }
        }
    }
}]);