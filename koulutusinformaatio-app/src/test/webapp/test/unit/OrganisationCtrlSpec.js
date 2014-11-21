"use strict";

describe('OrganisationCtrl', function() {

    var ctrl,
        scope, 
        httpBackend,
        controller;

    beforeEach(function() {
        module('kiApp');

        inject(function($controller, $httpBackend, $rootScope) {
            scope = $rootScope.$new();
            controller = $controller;
            httpBackend = $httpBackend;
            httpBackend.when('GET', '../lop/123?lang=fi').respond('{"id": "123", "name": "organisation name", "pictureFound": "true", "applicationSystemIds": ["abc"]}');
            httpBackend.when('GET', '../lop/123/picture').respond('{}');

            ctrl = controller('OrganisationCtrl', { $scope: scope, $routeParams: {id: 123} });
            httpBackend.flush();
        });
    });

    it('should fetch organisation data correctly', function() {
        expect(scope.provider.id).toEqual("123");
        expect(scope.provider.name).toEqual("organisation name");
    });

    it('should fetch organisation image', function() {
        expect(scope.providerImage).toBeDefined();
    });

    it('should be an organisation which has published learning opportunities', function() {
        expect(scope.hasLOs).toBeTruthy();
    })

});