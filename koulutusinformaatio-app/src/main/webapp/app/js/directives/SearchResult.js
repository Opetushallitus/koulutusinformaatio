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
            '<h4 class="collapser float-right" data-ng-class="showExtension" data-ng-click="toggleExtendedView()">' + 
                '<span>Avaa tästä </span>' + 
                '<span class="icon"></span>' +
            '</h4>' +
            '<div class="clear"></div>' +
            '<div collapse="showExtension == \'close\'">' + 
                '<div style="padding-top: 15px; border-top: 1px dashed grey; margin-top: 15px" data-ng-transclude></div>' +
            '</div>'

    };
}]).

directive('extendedSearchresultData', ['ParentLOService', 'SpecialLOService', 'UpperSecondaryLOService', function (ParentLOService, SpecialLOService, UpperSecondaryLOService) {
    return {    
        restrict: 'A',
        link: function($scope, ielement, iAttrs) {
            $scope.fetchLOData = function() {
                $scope.extendedLO = undefined;
                
                if(iAttrs.extendedSearchresultData === "tutkinto") {
                    $scope.extendedLO = ParentLOService.query({id: $scope.lo.id});
                } else if(iAttrs.extendedSearchresultData === "valmentava" || iAttrs.extendedSearchresultData === "erityisopetus") {
                    $scope.extendedLO = SpecialLOService.query({id: $scope.lo.id});
                } else if(iAttrs.extendedSearchresultData === "lukio") {
                    $scope.extendedLO = UpperSecondaryLOService.query({id: $scope.lo.id});
                }
                    
                $scope.extendedLO.then(function(result) {
                    if ($scope.lo.prerequisiteCode) {
                        for(var i = 0; i < result.lo.lois.length; i++) {
                            // filter out unnecessary lois by prerequisite
                            var loi = result.lo.lois[i];
                            if ($scope.lo.prerequisiteCode != loi.prerequisite.value) {
                                result.lo.lois = result.lo.lois.splice(i, 1);
                            }
                        }
                    }

                    $scope.extendedLO = result;
                }, function(error) {
                    //console.error('error fetching extended LO');
                });
            }
        }
    };
}]).

directive('srApplicationBasket', ['ApplicationBasketService', 'TranslationService', function (ApplicationBasketService, TranslationService) {
    return {
        restrict: 'A',
        controller: function($scope) {
            $scope.isItemAddedToBasket = function(applicationoptionId) {
                return ApplicationBasketService.itemExists(applicationoptionId);
            }

            $scope.addToBasket = function(applicationoptionId) {
                var basketType = ApplicationBasketService.getType();
                if (!basketType || $scope.$parent.$parent.$parent.lo.prerequisite.value == basketType) {
                    ApplicationBasketService.addItem(applicationoptionId, $scope.$parent.$parent.$parent.lo.prerequisite.value);
                } else {
                    $scope.popoverTitle = TranslationService.getTranslation('popover-title-error');
                    $scope.popoverContent = "<div>" + TranslationService.getTranslation('popover-content-error') + "</div><a href='#/muistilista'>" + TranslationService.getTranslation('popover-content-link-to-application-basket') + "</a>";
                }

            }
        },
        link: function (scope, iElement, iAttrs) {
        }
    };
}]).

directive('srHakukohteet', [function () {
    return {
        restrict: 'A',
        transclude: true,
        template: 
            '<div data-ng-repeat="lo in extendedLO.lo.lois">' +
                '<div data-ng-repeat="applicationsystem in lo.applicationSystems">' + 
                    '<div data-ng-repeat="applicationoption in applicationsystem.applicationOptions">' +
                        '<div class="bold margin-bottom-1">{{applicationoption.name}}</div><div data-ng-transclude></div>' +
                    '</div>' +
                '</div>' + 
            '</div>'
    };
}]).

directive('srExtendedOptions', ['TranslationService','Config', function (TranslationService, Config) {
    return {
        restrict: 'A',
        require: '^extendedSearchresultData',
        templateUrl: 'templates/searchResultOptions.html',
        controller: function($scope) {
            $scope.hakuAppUrl = Config.get('hakulomakeUrl');
            $scope.locales = {
                hakuaika: TranslationService.getTranslation('application-period'),
                hakukaynnissa: TranslationService.getTranslation('application-system-active-present'),
                hakupaattynyt: TranslationService.getTranslation('application-system-active-past')
            } 
        }
    };
}]).

directive('srExtendedKoulutustarjonta', [function () {
    return {
        restrict: 'A',
        require: '^extendedSearchresultData',
        templateUrl: 'templates/searchResultExtendedKoulutustarjonta.html'
    };
}]).

directive('srBasicInformation', [function () {
    return {
        restrict: 'A',
        require: '^extendedSearchresultData',
        templateUrl: 'templates/searchResultBasicInformation.html'
    };
}]);