angular.module('kiApp.directives.AppBasket', []).

directive('kiAppBasketApplicationsystem', function() {
    return {
        restrict: 'A',
        transclude: true,
        scope: {
            items: '='
        },
        template: '<div class="application-basket margin-bottom-6" data-ng-repeat="as in items" data-ng-transclude></div>'
    }
}).

directive('kiAppBasketApplicationsystemTable', function() {
    return {
        restrict: 'A',
        require: '^kiAppBasketApplicationsystem',
        templateUrl: 'templates/AppBasket/appBasketApplicationsystemTable.html',
        controller: function($scope, ApplicationBasketService) {

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
        }
    }
}).

directive('kiAppBasketApplicationoption', function() {
    return {
        restrict: 'A',
        require: '^kiAppBasketApplicationsystemTable',
        templateUrl: 'templates/AppBasket/appBasketApplicationoptions.html'
    }
}).

directive('kiAppBasketApplicationsystemTableInfo', function() {
    return {
        restrict: 'A',
        require: '^kiAppBasketApplicationsystemTable',
        templateUrl: 'templates/AppBasket/appBasketApplicationsystemTableInfo.html',
        controller: function($scope, ApplicationBasketService, TranslationService) {

            $scope.getOverflowWarning = function(itemcount) {
                return TranslationService.getTranslation('application-basket-overflow-warning', {itemcount: itemcount});
            }

            $scope.getNotificationText = function(itemcount) {
                return TranslationService.getTranslation('application-basket-fill-form-notification', {count: itemcount});
            }

            var applicationSystemIsActive = function(asId) {
                var items = $scope.items;

                for (var i = 0; i < items.length; i++) {
                    var item = items[i];

                    if (item.applicationSystemId == asId && item.applicationDates) {
                        return item.asOngoing ? true : false;
                    }
                }

                return false;
            };

            var getApplicationSystem = function(asId) {
            var result;
            angular.forEach($scope.items, function(as, askey){
                if (asId == as.applicationSystemId) {
                    result = as;
                }
            });

            return result;
            }

            var removeApplicationSystem = function(asId) {
                var as = getApplicationSystem(asId);
                if (as) {
                    var aos = angular.copy(as.applicationOptions);
                    angular.forEach(aos, function(ao, aokey) {
                        $scope.removeItem(ao.id);
                    });
                }
            }

            $scope.removeApplicationSystemFromBasket = function(asId) {
                var areyousure = confirm(TranslationService.getTranslation('application-basket-empty-confirm'));
                if (areyousure) {
                    removeApplicationSystem(asId);
                }
            };

            $scope.applyButtonIsDisabled = function(asId) {
                var isOverflowing = $scope.applicationBasketIsOverflowing(asId);
                if (isOverflowing || !applicationSystemIsActive(asId)) {
                    return true;
                } else {
                    return false;
                }
            }

            $scope.applicationBasketIsOverflowing = function(asId) {
                var items = $scope.items;
                var itemsInBasket = 0;
                var basketLimit;

                for (var i in items) {
                    if (items.hasOwnProperty(i)) {
                        var item = items[i];
                        if (item && item.applicationSystemId == asId && item.applicationOptions) {
                            basketLimit = item.maxApplicationOptions;
                            itemsInBasket = item.applicationOptions.length;
                            break;
                        }
                    }
                }
          
                if (basketLimit > 1 && itemsInBasket > basketLimit) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }
});
