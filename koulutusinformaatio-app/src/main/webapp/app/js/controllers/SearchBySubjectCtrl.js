"use strict";

/**
 *  Search by subject functionality enables user to search learning opportunities by selecting topics and themes.
 */

angular.module('kiApp.SearchBySubject', []).

controller('SearchBySubjectCtrl', [
    '$scope',
    '$rootScope',
    '$location',
    'SearchLearningOpportunityService',
    'TranslationService',
    'LanguageService',
    function($scope, $rootScope, $location, SearchLearningOpportunityService, TranslationService, LanguageService) {
        $rootScope.title = TranslationService.getTranslation('searchbysubject:title-search-by-subject') + ' - ' + TranslationService.getTranslation('sitename');

        SearchLearningOpportunityService.query({
            lang: LanguageService.getLanguage(),
            queryString: '*',
            rows: 0,
            start: 0
        }, true).then(function(result) {
            $scope.themes = result.topicFacet.facetValues;
        });

        $scope.selectTheme = function(theme, $event) {
            if (!$event || ($event && $event.keyCode === 13)) {
                $scope.selectedTheme = theme;
            }
        };

        $scope.selectTopic = function(topic) {
            var action = 'haku/',
                queryString = '*',
                query = '?';

            query += 'facetFilters=topic_ffm:' + topic.valueId;

            $location.url( action + queryString + query );
        };
    }
]);