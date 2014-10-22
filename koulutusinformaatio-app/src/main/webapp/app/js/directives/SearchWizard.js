"use strict";

angular.module('kiApp.directives.SearchWizard', []).

/*
 *  Location selector directive for location selection phase
 */
directive('kiSearchwizardLocationSelector', ['SearchLocationService', 'SearchWizardSelectionsService', 'TranslationService',
    function(SearchLocationService, SearchWizardSelectionsService, TranslationService) {
        return {
            restrict: 'A',
            templateUrl: 'partials/searchwizard/locationSelector.html',
            link: function(scope, element, attrs) {

                scope.add = function() {
                    SearchWizardSelectionsService.addLocation(scope.location);
                }

                scope.getLocations = function($viewValue) {
                    return SearchLocationService.query($viewValue);
                }

                scope.placeholder = TranslationService.getTranslation('location-filter-placeholder');
            }
        }
    }
]).


/*
 *  Ajax loader directive for phase model loading.
 */
directive('phaseLoader', function () {
    return {
        restrict: 'A',
        template: 
            '<div class="ajax-loader text-center">' +
                '<img src="img/ajax-loader-big.gif" />' +
            '</div>',
        scope: {
            active: '='
        },
        link: function (scope, elm, attrs) {
            scope.$watch('active', function (v) {
                if (v) {
                    elm.show();
                } else {
                    elm.hide();
                }
            });
        }
    };
});