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
        'vcRecaptchaService',
    function($scope, $rootScope, $routeParams, $timeout, ApplicationBasketService, SearchService, FilterService, TranslationService, AlertService, AuthService, Config, LanguageService, recaptcha) {
        $rootScope.title = TranslationService.getTranslation('title-application-basket') + ' - ' + TranslationService.getTranslation('sitename');
        $rootScope.description = $rootScope.title;
        $scope.hakuAppUrl = Config.get('hakulomakeUrl');
        $scope.loginUrl = Config.get('loginUrl');

        $scope.queryString = SearchService.getTerm() + '?' + FilterService.getParams();

        $scope.$watch(function() { return ApplicationBasketService.isEmpty(); }, function(value) {
            $scope.emailSendingEnabled = ApplicationBasketService.isEmpty() == false && $routeParams.emailSendingEnabled == true;
        });
        $scope.email = {
            "subject": "",
            "to": [],
            "captcha": ""
        };
        $scope.emailStatus = {
            "sending": false,
            "error": false,
            "ok": false
        };
        $scope.captchaNotSet = function() {
            return !$scope.email.captcha || $scope.email.captcha.length == 0;
        }

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
        $scope.lang = LanguageService.getLanguage();
        $scope.images = {
            logo: 'img/opintopolku_large-' + $scope.lang + '.png'
        };

        $scope.sendMuistilista = function() {
            $scope.emailStatus.sending = true;
            var subject = TranslationService.getTranslation('appbasket:email-subject-value') + ($scope.email.subject.length > 0 ? ": " + $scope.email.subject : "");
            ApplicationBasketService.sendByEmail(subject, $scope.email.to, $scope.email.captcha).then(function(result) {
                $scope.emailStatus.ok = true;
                $scope.emailStatus.error = false;
                $scope.email.to = "";
                $timeout(reinitEmailStatus, 5000);
            },
            function(error) {
                $scope.emailStatus.ok = false;
                $scope.emailStatus.error = true;
                $timeout(reinitEmailStatus, 5000);
            });
        };

        function reinitEmailStatus() {
            $scope.emailStatus.sending = false;
            $scope.emailStatus.error = false;
            $scope.email.captcha = "";
            recaptcha.reload();
        }

}]);