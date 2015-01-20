"use strict";

/**
 *  Controller for application basket
 */

angular.module('ApplicationBasket', []).

controller('AppBasketCtrl', 
    [
        '$scope',
        '$rootScope',
        '$routeParams',
        "$timeout",
        'ApplicationBasketService',
        'SearchService',
        'FilterService',
        'TranslationService',
        'AlertService',
        'AuthService',
        'Config', 
        'LanguageService',
    function($scope, $rootScope, $routeParams, $timeout, ApplicationBasketService, SearchService, FilterService, TranslationService, AlertService, AuthService, Config, LanguageService) {
        $rootScope.title = TranslationService.getTranslation('title-application-basket') + ' - ' + TranslationService.getTranslation('sitename');
        $rootScope.description = $rootScope.title;
        $scope.hakuAppUrl = Config.get('hakulomakeUrl');
        $scope.loginUrl = Config.get('loginUrl');

        $scope.queryString = SearchService.getTerm() + '?' + FilterService.getParams();

        $scope.$watch(function() { return ApplicationBasketService.isEmpty(); }, function(value) {
            $scope.emailSendingEnabled = ApplicationBasketService.isEmpty() == false && $routeParams.emailSendingEnabled == true;
        });
        $scope.email = {
            "subject": "Muistilista opintopolusta",
            "from": "",
            "to": ""
        };
        $scope.emailStatus = {
            "sending": false,
            "error": false,
            "ok": false
        };

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

        $scope.showAlert = function() {
            return !$scope.hideAlert() && !$scope.isAuthenticated;
        };

        $scope.closeAlert = function() {
            AlertService.setAlert('appbasket');
        };

        $scope.hideAlert = function() {
            return AlertService.getAlert('appbasket');
        };
        $scope.lang = LanguageService.getLanguage();
        $scope.images = {
            logo: 'img/opintopolku_large-' + $scope.lang + '.png'
        };

        $scope.sendMuistilista = function() {
            $scope.emailStatus.sending = true
            ApplicationBasketService.sendByEmail($scope.email.subject, $scope.email.to, $scope.email.from).then(function(result) {
                $scope.emailStatus.ok = true;
                $scope.emailStatus.error = false;
                $scope.email.to = "";
                $timeout( function() {
                    $scope.emailStatus.sending = false
                    $scope.emailStatus.ok = false;
                }, 5000);
            },
            function(error) {
                $scope.emailStatus.ok = false;
                $scope.emailStatus.error = true;
                $timeout( function() {
                    $scope.emailStatus.sending = false
                    $scope.emailStatus.error = false;
                }, 5000);
            });
        }

}]);