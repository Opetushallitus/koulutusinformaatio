"use strict";

describe('ApplicationCtrl', function() {

    var ctrl,
        scope,
        rs,
        httpBackend,
        controller,
        lotypes;

    beforeEach(function() {
        module('kiApp', 'kiMock');

        inject(function($controller, $httpBackend, $rootScope, LOTypes) {
            rs = $rootScope;
            scope = $rootScope.$new();
            controller = $controller;
            httpBackend = $httpBackend;
            lotypes = LOTypes;


            ctrl = controller('ApplicationCtrl', { $scope: scope });
            //httpBackend.flush();
        });
    });

    afterEach(function() {
        httpBackend.verifyNoOutstandingExpectation();
        httpBackend.verifyNoOutstandingRequest();
    });

    it('should add non-vocational education to basket', function() {
        scope.loType = lotypes.KORKEAKOULU;
        scope.addToBasket('kk_ao_id');
        expect(scope.isItemAddedToBasket('kk_ao_id')).toBeTruthy();
    });

    it('should add vocational education to basket', function() {
        scope.loType = lotypes.KOULUTUSOHJELMA;
        scope.selectedLOI = createLOIWithPrerequisite('PK')
        scope.addToBasket('ko_ao_id');
        expect(scope.isItemAddedToBasket('ko_ao_id')).toBeTruthy();
    });

    it('should not add vocational education with different prerequisite to basket', function() {
        scope.loType = lotypes.KOULUTUSOHJELMA;
        scope.prerequisite = 'PK';
        scope.selectedLOI = createLOIWithPrerequisite('PK')
        scope.addToBasket('ko_ao_pk_id');
        expect(scope.isItemAddedToBasket('ko_ao_pk_id')).toBeTruthy();

        scope.prerequisite = 'YO';
        scope.selectedLOI = createLOIWithPrerequisite('YO');
        scope.addToBasket('ko_ao_yo_id');
        expect(scope.isItemAddedToBasket('ko_ao_yo_id')).toBeFalsy();
    });

    var createLOIWithPrerequisite = function(prerequisite) {
        return {
            prerequisite: {
                value: prerequisite
            }
        };
    }

    /*
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
*/
});