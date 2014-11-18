angular.module('kiApp.directives.ThemeIcon', []).

constant('SearchResultConstants', {
    themes: {
        teemat_1: 'yleissivistava',
        teemat_2: 'kielet',
        teemat_3: 'historia',
        teemat_4: 'laki',
        teemat_5: 'kauppa',
        teemat_6: 'liikenne',
        teemat_7: 'luonnontieteet',
        teemat_8: 'kasvatus',
        teemat_9: 'matkailu',
        teemat_10: 'taide',
        teemat_11: 'tekniikka',
        teemat_12: 'terveys',
        teemat_13: 'turvallisuus',
        teemat_14: 'metsatalous'
    }
}).

directive('kiThemeIcon', [function () {
    return {
        restrict: 'A',
        scope: {
            theme: '='
        },
        template: '<div class="{{themes[theme.valueId]}}-icon" title="{{theme.valueName}}"></div>',
        controller: function($scope, SearchResultConstants) {
            $scope.themes = SearchResultConstants.themes;
        }
    };
}]);