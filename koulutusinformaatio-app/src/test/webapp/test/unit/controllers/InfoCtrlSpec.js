"use strict";

describe('InfoCtrl', function() {

    var ctrl,
        scope,
        rs,
        httpBackend,
        controller,
        location;

    beforeEach(function() {
        module('kiApp', 'kiMock');

        inject(function($controller, $httpBackend, $rootScope, $location, ParentLOService, parentLO) {
            rs = $rootScope;
            scope = $rootScope.$new();
            controller = $controller;
            httpBackend = $httpBackend;
            location = $location;

            httpBackend.when('GET', '/lo/tutkinto/123_PKYO?uiLang=fi&prerequisite=PK').respond(parentLO);
            httpBackend.when('GET', '/lop/123_provider/picture').respond('imagedata');
            httpBackend.when('GET', '/lo/picture/structure_image_id').respond('structureimagedata');
            httpBackend.when('GET', '/lop/additional_provider_id/picture').respond('additional_provider_image_data');

            ctrl = controller('InfoCtrl', { $scope: scope, $routeParams: {id: '123'}, loResource: ParentLOService });
            httpBackend.flush();
        });
    });

    afterEach(function() {
        httpBackend.verifyNoOutstandingExpectation();
        httpBackend.verifyNoOutstandingRequest();
    });

    it('should change prerequisite value', function() {
        expect(scope.selectedLOI.prerequisite.value).toEqual('YO');
        scope.changePrerequisiteSelection('PK');
        httpBackend.flush();
        expect(scope.selectedLOI.prerequisite.value).toEqual('PK');
    });

    it('should load the structure image if image id is present', function() {
        expect(scope.structureImage).toBeDefined();
    });

    it('should load images for additional providers', function() {
        expect(scope.lo.additionalProviders[0].providerImage).toBeDefined();
    })

    it('should set tab-title to hakeminen', function() {
        scope.lo.applicationSystems = [{
            useSystemApplicationForm: false
        }];
        scope.$digest();
        expect(scope.tabtitle.valintaperusteet).toEqual('lo-application-er');
    });

    it('should set tab-title to valintaperusteet ja pääsykokeet', function() {
        scope.lo.applicationSystems = [
            {
                useSystemApplicationForm: false
            },
            {
                useSystemApplicationForm: true
            }
        ];
        scope.$digest();
        expect(scope.tabtitle.valintaperusteet).toEqual('lo-application');
    });

});
