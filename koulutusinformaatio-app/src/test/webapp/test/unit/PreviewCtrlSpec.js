"use strict";

describe('PreviewCtrl', function() {

    var ctrl,
        scope, 
        httpBackend,
        controller,
        langService;

    beforeEach(function() {
        module('kiApp');

        inject(function($controller, $httpBackend, $rootScope, LanguageService) {
            scope = $rootScope.$new();
            controller = $controller;
            httpBackend = $httpBackend;
            langService = LanguageService;
        });
    });

    describe('LanguageCtrl', function() {

        beforeEach(function() {
            ctrl = controller('LanguageCtrl', { $scope: scope, $window: mockWindow });
        });

        it('should change language', function() {
            scope.changeLanguage('fi');
            expect(langService.getLanguage()).toEqual('fi');
        });
    });

    describe('HeaderCtrl', function() {

        beforeEach(function() {
            ctrl = controller('HeaderCtrl', { $scope: scope });
        });

        it('should populate model', function() {
            expect(scope.lang).toBeDefined();
            expect(scope.links).toBeDefined();
            expect(scope.locales).toBeDefined();
            expect(scope.images).toBeDefined();
        });

    });
});