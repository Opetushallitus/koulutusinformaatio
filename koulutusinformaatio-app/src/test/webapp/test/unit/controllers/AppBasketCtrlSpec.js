"use strict";

describe('AppBasketCtrl', function() {

    var ctrl,
        scope, 
        httpBackend,
        controller,
        appBasketService,
        recaptcha;

    beforeEach(function() {
        module('kiApp');

        inject(function($controller, $httpBackend, $rootScope, ApplicationBasketService, vcRecaptchaService) {
            scope = $rootScope.$new();
            controller = $controller;
            httpBackend = $httpBackend;
            appBasketService = ApplicationBasketService;
            recaptcha = vcRecaptchaService;

            ctrl = controller('AppBasketCtrl', { $scope: scope });
        });
    });

    afterEach(function() {
        httpBackend.verifyNoOutstandingExpectation();
        httpBackend.verifyNoOutstandingRequest();
    });

    describe('empty basket', function() {

        it('should be empty', function() {
            scope.$digest();
            expect(scope.basketIsEmpty).toBeTruthy();
            expect(scope.itemCount).toEqual(0);
        })

        it('sending should not be enabled', function() {
            scope.$digest();
            expect(scope.emailSendingEnabled).toBeFalsy();
        })

    });

    describe('basket with items in it', function() {

        beforeEach(function() {
            httpBackend.when('GET', '/basket/items?uiLang=fi&aoId=aoId1').respond('["aoId1"]');
            appBasketService.addItem('aoId1');
            ctrl = controller('AppBasketCtrl', { $scope: scope });
            httpBackend.flush();
        });

        afterEach(function() {
            appBasketService.empty();
        });

        it('should not be empty', function() {
            scope.$digest();
            expect(scope.basketIsEmpty).toBeFalsy();
            expect(scope.itemCount).toEqual(1);
        });

        it('sending should be enabled', function() {
            scope.$digest();
            expect(scope.emailSendingEnabled).toBeTruthy();
        });

        it('recaptcha should be defined', function() {
            expect(recaptcha).toBeDefined();
        });

        it('should have error flag set after failed sending', function() {
            httpBackend.when('POST', '/omatsivut/muistilista').respond(403, '');
            scope.sendMuistilista();
            httpBackend.flush()
            waitsFor(function() {
                return scope.emailStatus.error == true;
            }, "error flag should be set", 500);
        })

        it('should have flag set after succesafull sending', function() {
            httpBackend.when('POST', '/omatsivut/muistilista').respond(200, '');
            scope.sendMuistilista();
            httpBackend.flush()
            waitsFor(function() {
                return scope.emailStatus.ok == true;
            }, "ok flag should be set", 500);
        })
    })

    

})