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

directive('toggleCollapse', [function () {
    return {
        restrict: 'A',
        transclude: true,
        controller: function($scope) {
            $scope.toggleExtendedView = function() {
                if($scope.showExtension == 'close') {
                    if(!$scope.extendedLO) {
                        $scope.fetchLOData();
                        $scope.showExtension = 'open';
                    } else {
                        $scope.showExtension = 'open';
                    }
                } else {
                    $scope.showExtension = 'close';
                }  
            }
        },
        link: function (scope, iElement, iAttrs) {
            scope.showExtension = "close";
        },
        template: 
                '<h4 class="collapser float-right" ng-class="showExtension" ng-click="toggleExtendedView()">' + 
                    '<span>Avaa tästä </span>' + 
                    '<span class="icon"></span>' +
                '</h4>' +
                '<div class="clear"></div>' +
                '<div collapse="showExtension == \'close\'">' + 
                    '<div style="padding-top: 15px; border-top: 1px dashed grey; margin-top: 15px" ng-transclude></div>' +
                '</div>'

    };
}]).

directive('srExtendedData', ['ParentLOService', function (ParentLOService) {
    return {    
        restrict: 'A',
        controller: function($scope) {
            $scope.fetchLOData = function() {

                $scope.extendedLO = ParentLOService.query({id: $scope.lo.id});
                $scope.extendedLO.then(function(result) {
                    for(var i = 0 ; result.lo.lois.length < i ; i++) {
                        //todo filter out unnecessary lois
                    }
                }, function(error) {
                    console.error('error fetching extended LO');
                });
            }
        },
    };
}]).

directive('srExtendedOptions', [function () {
    return {
        restrict: 'E',
        require: '^extendedSearchResultData',
        templateUrl: 'templates/searchResultOptions.html'
    };
}]).

directive('srExtendedKoulutustarjonta', [function () {
    return {
        restrict: 'E',
        require: '^extendedSearchResultData',
        templateUrl: 'templates/searchResultExtendedKoulutustarjonta.html'
    };
}]).

directive('srBasicInformation', [function () {
    return {
        restrict: 'E',
        require: '^extendedSearchResultData',
        templateUrl: 'templates/searchResultBasicInformation.html'
    };
}]);