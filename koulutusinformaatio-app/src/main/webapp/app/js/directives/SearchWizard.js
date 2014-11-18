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
]);