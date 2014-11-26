"use strict";

describe('AppBasketCtrl', function() {

    var ctrl,
        scope, 
        httpBackend,
        controller,
        appBasketService;

    beforeEach(function() {
        module('kiApp');

        inject(function($controller, $httpBackend, $rootScope, ApplicationBasketService) {
            scope = $rootScope.$new();
            controller = $controller;
            httpBackend = $httpBackend;
            appBasketService = ApplicationBasketService;

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

    });

    describe('basket with items in it', function() {

        beforeEach(function() {
            httpBackend.when('GET', '../basket/items?uiLang=fi&aoId=aoId1').respond('["aoId1"]');
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
        })

    })

    

})