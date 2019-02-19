angular.module('kiApp.directives.AppBasket', []).

directive('kiAppBasketApplicationsystem', function() {
    return {
        restrict: 'A',
        transclude: true,
        scope: {
            items: '='
        },
        template: '<div data-ng-repeat="as in items" data-ng-transclude></div>'
    }
}).

directive('kiAppBasketApplicationsystemTable', function() {
    return {
        restrict: 'A',
        require: '^kiAppBasketApplicationsystem',
        templateUrl: 'js/directives/AppBasket/appBasketApplicationsystemTable.html',
        controller: function($scope, ApplicationBasketService, TranslationService, Config) {

            $scope.hakuAppUrl = function(id){
                return window.url("haku-app.lomake", id)
            };
            $scope.ataruHakuAppUrl = function(as) {
                var id = as.applicationSystemId
                var hakukohteet = ""
                for (var i = 0; i < as.applicationOptions.length; i++) {
                    if(i !== 0) {
                        hakukohteet = hakukohteet + ",";
                    }
                    hakukohteet = hakukohteet + as.applicationOptions[i].id;
                }
                return window.url('ataru-app.haku', id, hakukohteet);
            };
            $scope.tooltips = {
                externalApplicationForm: TranslationService.getTranslation('tooltip:external-application-form'),
                removeApplicationOption: TranslationService.getTranslation('tooltip:application-basket-remove')
            };

            // remove ao item form basket, also removes as item if it contains 0 ao items after removal
            $scope.removeItem = function(aoId) {
                ApplicationBasketService.removeItem(aoId);

                var items = $scope.items;

                for (var i = 0; i < items.length; i++) {
                    var item = items[i];
                    for (var j = 0; j < item.applicationOptions.length; j++) {
                        var ao = item.applicationOptions[j];
                        if (ao.id == aoId) {
                            item.applicationOptions.splice(j, 1);
                            break;
                        }
                    }

                    if (item.applicationOptions.length <= 0) {
                        items.splice(i, 1);
                    }
                }
            };

            $scope.isErikseenHaettava = function(as) {
                var id = 'erikseenHaettavatHakukohteet';

                return as.applicationSystemId == id ? true : false;
            }
        }
    }
}).

directive('kiAppBasketApplicationoption', function() {
    return {
        restrict: 'A',
        replace: true,
        require: '^kiAppBasketApplicationsystemTable',
        template: '<tr data-ng-include="getTemplate()"></tr>',
        controller: function($scope) {
            $scope.getTemplate = function() {
                return 'js/directives/AppBasket/' + $scope.item.type + '/ao.html';
            };
        }
    }   
}).

directive('kiAppBasketApplicationsystemTableInfo', function() {
    return {
        restrict: 'A',
        require: '^kiAppBasketApplicationsystemTable',
        templateUrl: 'js/directives/AppBasket/appBasketApplicationsystemTableInfo.html',
        controller: function($scope, ApplicationBasketService, TranslationService) {

            $scope.getOverflowWarning = function(itemcount) {
                return TranslationService.getTranslation('application-basket-overflow-warning', {itemcount: itemcount});
            };

            $scope.getNotificationText = function(itemcount) {
                return TranslationService.getTranslation('application-basket-fill-form-notification', {count: itemcount});
            };

            var applicationSystemIsActive = function(as) {
                if (as && as.applicationDates) {
                    return as.asOngoing ? true : false;
                }

                return false;
            };

            var removeApplicationSystem = function(as) {
                if (as) {
                    var aos = angular.copy(as.applicationOptions);
                    angular.forEach(aos, function(ao, aokey) {
                        $scope.removeItem(ao.id);
                    });
                }
            };

            $scope.removeApplicationSystemFromBasket = function(as) {
                var areyousure = confirm(TranslationService.getTranslation('application-basket-empty-confirm'));
                if (areyousure) {
                    removeApplicationSystem(as);
                }
            };

            $scope.isAtaruApplicationSystem = function(as) {
                var ataruForm = false;
                for (var i = 0, len = (as.applicationOptions || []).length; i < len; i++) {
                    if(as.applicationOptions[i].ataruFormKey) {
                        ataruForm = true;
                    }
                }
                return ataruForm;
            }

            $scope.isApplicationSystemDisabled = function(as) {
                var isOverflowing = $scope.applicationBasketIsOverflowing(as);
                if (isOverflowing || !applicationSystemIsActive(as)) {
                    return true;
                } else {
                    return false;
                }
            };

            $scope.isApplicationOptionDisabled = function(ao) {
                return !ao.canBeApplied;
            };

            $scope.ataruAppUrl = function(id) {
                return window.url('ataru-app.hakemus', id);
            };

            $scope.applicationBasketIsOverflowing = function(as) {
                var itemsInBasket = 0;
                var basketLimit;

                if (as && as.applicationOptions) {
                    basketLimit = as.maxApplicationOptions;
                    itemsInBasket = as.applicationOptions.length;
                }
          
                if (basketLimit > 1 && itemsInBasket > basketLimit) {
                    return true;
                } else {
                    return false;
                }
            };
        }
    }
}).

/**
 *  Render application status label
 */
directive('kiAppBasketApplicationStatus', function() {
    return {
        restrict: 'A',
        template:   '<span data-ng-if="isApplicableInFuture()"><span data-ki-i18n="application-system-active-future" data-lang="{{lang}}"></span> <span data-ki-timestamp="{{timestamp}}"></span></span>' +
                    '<span data-ng-if="isInThePast()" data-ki-i18n="application-system-active-past" data-lang="{{lang}}"></span>' +
                    '<span data-ng-if="isCurrentlyActive()" data-ki-i18n="application-system-active-present" data-lang="{{lang}}"></span>',
        scope: {
            applicationSystem: '=as',
            applicationOption: '=ao',
            lang: '@lang'
        },
        controller: function($scope) {
            $scope.isApplicableInFuture = function() {
                return $scope.state == "future";
            };

            $scope.isCurrentlyActive = function() {
                return $scope.state == "present";
            };

            $scope.isInThePast = function() {
                return $scope.state == "past";
            }
        },
        link: function(scope, element, attrs) {
            var as = scope.applicationSystem;
            var ao = scope.applicationOption;
            if (ao) {
                if (ao.canBeApplied) {
                    scope.state = "present";
                } else if (ao.nextApplicationPeriodStarts) {
                    scope.state = "future";
                    scope.timestamp = ao.nextApplicationPeriodStarts;
                } else {
                    scope.state = "past";
                }
            } else if (as) {
                if (as.asOngoing) {
                    scope.state = "present";
                } else if (as.nextApplicationPeriodStarts) {
                    scope.state = "future";
                    scope.timestamp = as.nextApplicationPeriodStarts;
                } else {
                    scope.state = "past";
                }
            }
        }
    }
}).

/**
 *  Render application time
 */
directive('kiApplicationTime', function() {
    return {
        restrict: 'A',
        templateUrl: 'js/directives/AppBasket/applicationTime.html',
        scope : {
            dates: '=',
            hakutapa: '=',
            label: '='
        },
        controller: function($scope) {
            $scope.isJatkuva = function() {
                // code for jatkuva haku is 03
                return $scope.hakutapa == '03';
            }
        },
        link: function($scope, element, attrs) {
            element.addClass('italic');
        }
    }
});
