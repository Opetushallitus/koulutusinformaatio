"use strict";

/**
 *  Controller for application basket
 */

angular.module('ApplicationBasket', []).

controller('AppBasketCtrl', 
    [
        '$scope',
        '$rootScope',
        'ApplicationBasketService',
        'SearchService',
        'FilterService',
        'TranslationService',
        'AlertService',
        'AuthService',
        'Config', 
    function($scope, $rootScope, ApplicationBasketService, SearchService, FilterService, TranslationService, AlertService, AuthService, Config) {
        $rootScope.title = TranslationService.getTranslation('title-application-basket') + ' - ' + TranslationService.getTranslation('sitename');
        $rootScope.description = $rootScope.title;
        $scope.hakuAppUrl = Config.get('hakulomakeUrl');
        $scope.loginUrl = Config.get('loginUrl');

        $scope.queryString = SearchService.getTerm() + '?' + FilterService.getParams();

        // load app basket content only if it contains items
        if (!ApplicationBasketService.isEmpty()) {
            ApplicationBasketService.query().then(function(result) {
                $scope.applicationItems = result;
            });
        }

        $scope.title = TranslationService.getTranslation('title-application-basket');
        $scope.isAuthenticated = AuthService.isAuthenticated();


        $scope.$watch(function() { return ApplicationBasketService.getItemCount(); }, function(value) {
            $scope.itemCount = value;
        });

        $scope.$watch(function() { return ApplicationBasketService.isEmpty(); }, function(value) {
            $scope.basketIsEmpty = value;
        });

        $scope.closeAlert = function() {
            AlertService.setAlert('appbasket');
        };

        $scope.hideAlert = function() {
            return AlertService.getAlert('appbasket');
        };
        
}]);