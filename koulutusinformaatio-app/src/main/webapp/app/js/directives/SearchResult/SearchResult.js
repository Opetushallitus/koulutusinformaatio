angular.module('SearchResult', []).

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

/**
 *  Updates the title element of the page.
 */
directive('searchResult', ['FilterService', 'TranslationService', 'LOTypes', 'UtilityService', function(FilterService, TranslationService, LOTypes, UtilityService) {
    return {
        restrict: 'A',
        template: '<div data-ng-include="getTemplate()" class="search-result"></div>',
        link: function(scope, element, attrs) {

            scope.locales = {
                openEducation: TranslationService.getTranslation('tooltip:open-education-view')
            }

            scope.getTemplate = function() {
                return 'js/directives/SearchResult/' + UtilityService.getTemplateByLoType(scope.lo.type) + '/searchResult.html';
            }

            // remove prerequisite hash from lo id (it's there for solr indexing purposes)
            var hashIndex = scope.lo.id.indexOf('#');
            if ( hashIndex > -1) {
                scope.lo.id = scope.lo.id.substring(0, hashIndex);
            }

            scope.lo.type = scope.lo.type.toLowerCase();
            scope.lo.linkHref = '#!/' + scope.lo.type + '/' + scope.lo.id;

            var prerequisite = scope.lo.prerequisiteCode || FilterService.getPrerequisite();
            if (prerequisite && scope.lo.type === LOTypes.TUTKINTO && scope.lo.id.indexOf('#') === -1) {
                scope.lo.linkHref += '?prerequisite=' + prerequisite;
            }
        }
    }
}]).

directive('toggleCollapse', ['$timeout', function ($timeout) {
    return {
        restrict: 'A',
        transclude: true,
        controller: function($scope) {
            $scope.toggleExtendedView = function() {
                if($scope.showExtension == 'closed') {
                    if(!$scope.extendedLO) {
                        // do not open until data is loaded
                        $scope.fetchLOData().then(function() {
                            $scope.showExtension = 'opened';
                        });
                    } else {
                        $scope.showExtension = 'opened';
                    }
                } else {
                    $scope.showExtension = 'closed';
                }
            }
        },
        link: function (scope, iElement, iAttrs) {
            scope.showExtension = "closed";
        },
        template:
            '<div class="clear"></div>' +
            //'<div data-collapse="showExtension == \'closed\'">' + // dynamic content in collapse does not work with IE
            '<div data-ng-if="showExtension == \'opened\'">' +
                '<div class="search-result-extended" data-ng-transclude></div>' +
            '</div>'

    };
}]).

directive('extendedSearchresultData', 
    [
        '$q',
        'ParentLOService',
        'SpecialLOService',
        'UpperSecondaryLOService',
        'KoulutusLOService',
        'HigherEducationLOService',
        'AdultVocationalLOService',
        'LOTypes',
        function ($q, ParentLOService, SpecialLOService, UpperSecondaryLOService, KoulutusLOService, HigherEducationLOService, AdultVocationalLOService, LOTypes) {
    return {    
        restrict: 'A',
        link: function($scope, ielement, iAttrs) {
            $scope.fetchLOData = function() {
                var deferred = $q.defer(),
                    LOService;
                $scope.extendedLO = undefined;

                if(iAttrs.extendedSearchresultData === LOTypes.TUTKINTO) {
                    LOService = ParentLOService;
                } else if(iAttrs.extendedSearchresultData === LOTypes.VALMENTAVA || 
                    iAttrs.extendedSearchresultData === LOTypes.ERITYISOPETUS ||
                    iAttrs.extendedSearchresultData === LOTypes.VALMISTAVA ) {
                    LOService = SpecialLOService;
                } else if(iAttrs.extendedSearchresultData === LOTypes.LUKIO) {
                    LOService = UpperSecondaryLOService;
                } else if(iAttrs.extendedSearchresultData === LOTypes.KOULUTUS) {
                    LOService = KoulutusLOService;
                } else if(iAttrs.extendedSearchresultData === LOTypes.KORKEAKOULU) {
                    LOService = HigherEducationLOService;
                } else if (iAttrs.extendedSearchresultData === LOTypes.AIKUISLUKIO) {
                    LOService = KoulutusLOService;
                } else if (iAttrs.extendedSearchresultData === LOTypes.AMMATILLINENAIKUISKOULUTUS) {
                    LOService = AdultVocationalLOService;
                } else if (iAttrs.extendedSearchresultData === LOTypes.AIKUISTENPERUSOPETUS) {
                    LOService = KoulutusLOService;
                }

                LOService.query({id: $scope.lo.id}).then(function(result) {
                    if ($scope.lo.prerequisiteCode && result.lo.children) {
                        for(var i = 0; i < result.lo.children.length; i++) {
                            // filter out unnecessary lois by prerequisite
                            var loi = result.lo.children[i];
                            if ($scope.lo.prerequisiteCode != loi.prerequisite.value) {
                                result.lo.children.splice(i, 1);
                            }
                        }
                    }
                    $scope.extendedLO = result;

                    deferred.resolve(result);
                }, function(error) {
                    deferred.reject(error);
                });

                $scope.loType = iAttrs.extendedSearchresultData;

                return deferred.promise;
            };
        }
    };
}]).

directive('srApplicationBasket', [
    'ApplicationBasketService',
    'TranslationService',
    'LOTypes',
    function (ApplicationBasketService, TranslationService, LOTypes) {
    return {
        restrict: 'A',
        controller: function($scope) {
            $scope.isItemAddedToBasket = function(applicationoptionId) {
                return ApplicationBasketService.itemExists(applicationoptionId);
            }

            $scope.addToBasket = function(applicationoptionId) {
                // vocational education needs prerequisite checking...
                var addVocationalEdToBasket = function(aoId) {
                    var basketType = ApplicationBasketService.getType();
                    if (!basketType || $scope.lo.prerequisite.value == basketType) {
                        ApplicationBasketService.addItem(applicationoptionId, $scope.lo.prerequisite.value);
                    } else {
                        $scope.popoverTitle = TranslationService.getTranslation('popover-title-error');
                        $scope.popoverContent = "<div>" + TranslationService.getTranslation('popover-content-error') + "</div><a href='#/muistilista'>" + TranslationService.getTranslation('popover-content-link-to-application-basket') + "</a>";
                    }
                }

                // ...but other types of education do not require prerequisite checking
                var addEducationToBasket = function(aoId) {
                    ApplicationBasketService.addItem(aoId);
                }

                if ($scope.loType == LOTypes.TUTKINTO || $scope.loType == LOTypes.LUKIO) {
                    addVocationalEdToBasket(applicationoptionId);
                } else {
                    addEducationToBasket(applicationoptionId);
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
                return 'js/directives/SearchResult/' + attrs.templateType + '/searchResultHakukohteet.html';
            } else {
                return 'js/directives/SearchResult/searchResultHakukohteet.html';
            }
        }
    };
}]).

directive('srExtendedOptions', ['TranslationService','Config', function (TranslationService, Config) {
    return {
        restrict: 'A',
        require: '^extendedSearchresultData',
        templateUrl: 'js/directives/SearchResult/searchResultOptions.html',
        controller: function($scope) {
            $scope.hakuAppUrl = Config.get('hakulomakeUrl');
            $scope.locales = {
                hakuaika: TranslationService.getTranslation('application-period'),
                hakukaynnissa: TranslationService.getTranslation('application-system-active-present'),
                hakupaattynyt: TranslationService.getTranslation('application-system-active-past'),
                externalApplicationForm: TranslationService.getTranslation('tooltip:external-application-form')
            } 
            $scope.date = new Date().getTime();
        }
    };
}]).

directive('srNotApplicable', [function() {
    return {
        restrict: 'A',
        require: '^extendedSearchresultData',
        template:
            '<p class="text-muted">' +
            '<span data-ki-i18n="application-period" data-show-colon="true"></span>' +
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
                return 'js/directives/SearchResult/' + attrs.templateType + '/searchResultExtendedKoulutustarjonta.html';
            } else {
                return 'js/directives/SearchResult/searchResultExtendedKoulutustarjonta.html';
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
                return 'js/directives/SearchResult/' + attrs.templateType + '/searchResultBasicInformation.html';
            } else {
                return 'js/directives/SearchResult/searchResultBasicInformation.html';
            }
        }
    };
}])

.directive('srThemeIcons', [function () {
    return {
        restrict: 'A',
        require: '^extendedSearchresultData',
        template: '<div data-ng-repeat="theme in extendedLO.lo.themes" class="{{themes[theme.uri]}}-icon" title="{{theme.description}}"></div>',
        controller: function($scope, SearchResultConstants) {
            $scope.themes = SearchResultConstants.themes;
        }
    };
}]);

;