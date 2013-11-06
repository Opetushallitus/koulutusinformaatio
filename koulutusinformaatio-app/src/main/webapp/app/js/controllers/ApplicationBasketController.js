/**
 *  Controller for application basket
 */
function ApplicationBasketCtrl($scope, $rootScope, $routeParams, ApplicationBasketService, SearchService, FilterService, kiAppConstants, Config) {
    $rootScope.title = i18n.t('title-application-basket') + ' - ' + i18n.t('sitename');
    $scope.hakuAppUrl = Config.get('hakulomakeUrl');
    var basketLimit = kiAppConstants.applicationBasketLimit; // TODO: get this from application data?

    $scope.queryString = SearchService.getTerm() + '?' + FilterService.getParams();
    $scope.notificationText = i18n.t('application-basket-fill-form-notification', {count: basketLimit});
    $scope.basketIsEmpty = ApplicationBasketService.isEmpty();

    if (!$scope.basketIsEmpty) {
        ApplicationBasketService.query().then(function(result) {
            $scope.applicationItems = result;
        });
    }

    $scope.title = i18n.t('title-application-basket-content');
    $scope.itemCount = ApplicationBasketService.getItemCount();

    
    var applicationSystemIsActive = function(asId) {
        var items = $scope.applicationItems;

        for (var i = 0; i < items.length; i++) {
            var item = items[i];

            if (item.applicationSystemId == asId && item.applicationDates) {
                return item.asOngoing ? true : false;
            }
        }

        return false;
    };

    $scope.removeItem = function(aoId) {
        ApplicationBasketService.removeItem(aoId);

        var items = $scope.applicationItems;

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

        $scope.itemCount = ApplicationBasketService.getItemCount();
        $scope.basketIsEmpty = ApplicationBasketService.isEmpty();
    };

    $scope.emptyApplicationBasket = function() {
        var areyousure = confirm(i18n.t('application-basket-empty-confirm'));
        if (areyousure) {
            ApplicationBasketService.empty();
            $scope.applicationItems = [];
            $scope.basketIsEmpty = true;
            $scope.itemCount = ApplicationBasketService.getItemCount();
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
        var items = $scope.applicationItems;
        var itemsInBasket = 0;

        for (var i in items) {
            if (items.hasOwnProperty(i)) {
                var item = items[i];
                if (item && item.applicationSystemId == asId && item.applicationOptions) {
                    itemsInBasket = item.applicationOptions.length;
                    break;
                }
            }
        }
  
        if (itemsInBasket > basketLimit) {
            return true;
        } else {
            return false;
        }
    }

    $scope.$on('$viewContentLoaded', function() {
        OPH.Common.initHeader();
    });
};