angular.module('SearchResult', []).

/**
 *  Updates the title element of the page.
 */
directive('searchResult', ['FilterService', 'TranslationService', function(FilterService, TranslationService) {
    return {
        restrict: 'A',
        template: '<div data-ng-include="getTemplate()"></div>',
        link: function(scope, element, attrs) {

            scope.locales = {
                openEducation: TranslationService.getTranslation('tooltip:open-education-view')
            }

        	scope.getTemplate = function() {
        		return 'templates/' + scope.lo.type + '/searchResult.html';
        	}

            scope.lo.type = scope.lo.type.toLowerCase();
            scope.lo.linkHref = '#!/' + scope.lo.type + '/' + scope.lo.id;

            var prerequisite = scope.lo.prerequisiteCode || FilterService.getPrerequisite();
            if (prerequisite && scope.lo.id.indexOf('#') === -1) {
            	scope.lo.linkHref += '#' + prerequisite;
            }
        }
    }
}]).

directive('searchResultTarjonta', [function () {
    return {
        restrict: 'E',
        link: function (scope, iElement, iAttrs) {
        }
    };
}]).

directive('srExtendedKuvaus', ['ParentLOService', function (ParentLOService) {
    return {
        restrict: 'E',
        templateUrl: 'templates/searchResultKuvaus.html',
        link: function(scope, iElement, iAttrs) {
            scope.extendedLO = ParentLOService.query({id: scope.currentLO.id});
            scope.extendedLO.then(function(result) {
                for(var i = 0 ; result.lo.lois.length < i ; i++) {
                    
                }
            }, function(error) {
                console.error('error fething extended LO');
            });
        }
    };
}]);