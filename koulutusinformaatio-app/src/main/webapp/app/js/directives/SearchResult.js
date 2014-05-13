angular.module('SearchResult', []).

/**
 *  Updates the title element of the page.
 */
directive('searchResult', ['FilterService', 'TranslationService', function(FilterService, TranslationService) {
    return {
        restrict: 'A',
        template: '<div data-ng-include="getTemplate()" class="search-result"></div>',
        link: function(scope, element, attrs) {

            scope.locales = {
                openEducation: TranslationService.getTranslation('tooltip:open-education-view')
            }

        	scope.getTemplate = function() {
        		return 'templates/' + scope.lo.type + '/searchResult.html';
        	}

            var hashIndex = scope.lo.id.indexOf('#');
            if ( hashIndex > -1) {
                scope.lo.id = scope.lo.id.substring(0, hashIndex);
            }

            scope.lo.type = scope.lo.type.toLowerCase();
            scope.lo.linkHref = '#!/' + scope.lo.type + '/' + scope.lo.id;

            var prerequisite = scope.lo.prerequisiteCode || FilterService.getPrerequisite();
            if (prerequisite && scope.lo.id.indexOf('#') === -1) {
                scope.lo.linkHref += '?prerequisite=' + prerequisite;
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
            '<div class="clear"></div>' +
            '<div data-collapse="showExtension == \'close\'">' +
                '<div class="search-result-extended" data-ng-transclude></div>' +
            '</div>'

    };
}]).

directive('extendedSearchresultData', ['ParentLOService', 'SpecialLOService', 'UpperSecondaryLOService', 'HigherEducationLOService', function (ParentLOService, SpecialLOService, UpperSecondaryLOService, HigherEducationLOService) {
    return {    
        restrict: 'A',
        link: function($scope, ielement, iAttrs) {
            $scope.fetchLOData = function() {
                $scope.extendedLO = undefined;
                
                if(iAttrs.extendedSearchresultData === "tutkinto") {
                    $scope.extendedLO = ParentLOService.query({id: $scope.lo.id});
                } else if(iAttrs.extendedSearchresultData === "valmentava" || 
                    iAttrs.extendedSearchresultData === "erityisopetus" ||
                    iAttrs.extendedSearchresultData === "valmistava" ) {
                    $scope.extendedLO = SpecialLOService.query({id: $scope.lo.id});
                } else if(iAttrs.extendedSearchresultData === "lukio") {
                    $scope.extendedLO = UpperSecondaryLOService.query({id: $scope.lo.id});
                } else if(iAttrs.extendedSearchresultData === "korkeakoulu") {
                    $scope.extendedLO = HigherEducationLOService.query({id: $scope.lo.id});
                }

                $scope.loType = iAttrs.extendedSearchresultData;
                    
                $scope.extendedLO.then(function(result) {
                    if ($scope.lo.prerequisiteCode) {
                        for(var i = 0; i < result.lo.lois.length; i++) {
                            // filter out unnecessary lois by prerequisite
                            var loi = result.lo.lois[i];
                            if ($scope.lo.prerequisiteCode != loi.prerequisite.value) {
                                result.lo.lois.splice(i, 1);
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

            // vocational education needs prerequisite checking...
            $scope.addToBasket = function(applicationoptionId) {
                var addVocationalEdToBasket = function(aoId) {
                    var basketType = ApplicationBasketService.getType();
                    if (!basketType || $scope.lo.prerequisite.value == basketType) {
                        ApplicationBasketService.addItem(applicationoptionId, $scope.lo.prerequisite.value);
                    } else {
                        $scope.popoverTitle = TranslationService.getTranslation('popover-title-error');
                        $scope.popoverContent = "<div>" + TranslationService.getTranslation('popover-content-error') + "</div><a href='#/muistilista'>" + TranslationService.getTranslation('popover-content-link-to-application-basket') + "</a>";
                    }
                }

                var addHighEdToBasket = function(aoId) {
                    ApplicationBasketService.addItem(aoId);
                }

                if ($scope.loType == 'korkeakoulu') {
                    addHighEdToBasket(applicationoptionId);
                } else {
                    addVocationalEdToBasket(applicationoptionId);
                }
            }

            $scope.isApplicationOpen = function(as, ao) {
                if (ao.specificApplicationDates) {
                    return ao.canBeApplied;    
                } else {
                    return as.asOngoing;
                }
            }
        }
    };
}]).

directive('srHakukohteet', [function () {
    return {
        restrict: 'A',
        transclude: true,
        templateUrl: function(element, attrs) {
            if (attrs.templateType) {
                return 'templates/' + attrs.templateType + '/searchResultHakukohteet.html';
            } else {
                return 'templates/searchResultHakukohteet.html';
            }
        }
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
                hakupaattynyt: TranslationService.getTranslation('application-system-active-past'),
                externalApplicationForm: TranslationService.getTranslation('tooltip:external-application-form')
            } 
        }
    };
}]).

directive('srNotApplicable', [function() {
    return {
        restrict: 'A',
        require: '^extendedSearchresultData',
        template:
            '<p class="small">' +
            '<span data-ki-i18n="application-period" data-show-colon="true" class="margin-right-1"></span>' +
            '<span data-ki-timestamp="{{applicationoption.applicationStartDate}}"></span>&ndash;<span data-ki-timestamp="{{applicationoption.applicationEndDate}}"></span>' +
            '</p>'
    }
}]).

directive('srExtendedKoulutustarjonta', [function () {
    return {
        restrict: 'A',
        require: '^extendedSearchresultData',
        templateUrl: function(element, attrs) {
            if (attrs.templateType) {
                return 'templates/' + attrs.templateType + '/searchResultExtendedKoulutustarjonta.html';
            } else {
                return 'templates/searchResultExtendedKoulutustarjonta.html';
            }
        }
    };
}]).

directive('srBasicInformation', [function () {
    return {
        restrict: 'A',
        require: '^extendedSearchresultData',
        templateUrl: function(element, attrs) {
            if (attrs.templateType) {
                return 'templates/' + attrs.templateType + '/searchResultBasicInformation.html';
            } else {
                return 'templates/searchResultBasicInformation.html';
            }
        }
    };
}]);