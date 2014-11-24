"use strict";

describe('InfoCtrl', function() {

    var ctrl,
        scope,
        rs,
        httpBackend,
        controller,
        location;

    beforeEach(function() {
        module('kiApp');

        inject(function($controller, $httpBackend, $rootScope, $location, ParentLOService) {
            rs = $rootScope;
            scope = $rootScope.$new();
            controller = $controller;
            httpBackend = $httpBackend;
            location = $location;


            httpBackend.when('GET', '../lo/parent/123?uiLang=fi').respond('{}');
            ctrl = controller('InfoCtrl', { $scope: scope, $routeParams: {id: '123'}, loResource: ParentLOService });
            httpBackend.flush();
        });
    });

    it('should...', function() {

    });

    /*
    describe('starting from first phase by default', function() {

        beforeEach(function() {
            httpBackend.when('GET', '../lo/search?start=0&rows=0&lang=fi&searchType=LO&text=*').respond('{}');
            httpBackend.when('GET', 'partials/search/search.html').respond(''); // for some unknown reason this gets requested
            ctrl = controller('SearchWizardCtrl', { $scope: scope });
            httpBackend.flush();
        });

        it('should be in the first phase', function() {
            expect(scope.isFirstPhase()).toBeTruthy();
        });

        it('should go to the following phase after selection', function() {
            scope.makeSelection(builder.buildPhaseOneSelection('educationcode'), 'label');
            httpBackend.flush();
            expect(scope.currentPhase).toEqual(constants.phases.EDTYPE);
        });

        it('should revert to the previous phase', function() {
            // go to next phase
            scope.makeSelection(builder.buildPhaseOneSelection('educationcode'), 'label');
            httpBackend.flush();
            expect(scope.currentPhase).toEqual(constants.phases.EDTYPE);

            // then revert
            scope.gotoPreviousPhase();
            httpBackend.flush();
            expect(scope.currentPhase).toEqual(constants.phases.PHASEONE);
        });
    });

    describe('starting from phase defined by query parameters', function() {

        beforeEach(function() {
            location.search({qParam1: 'qValue1', qParam2: 'qValue2', phaseone: 'phaseoneValue', phase: constants.keys.PHASEONE});
            httpBackend.when('GET', '../lo/search?start=0&rows=0&lang=fi&searchType=LO&text=*').respond('{}');
            httpBackend.when('GET', 'partials/search/search.html').respond(''); // for some unknown reason this gets requested
            ctrl = controller('SearchWizardCtrl', { $scope: scope });
            httpBackend.flush();
        });

        it('should initalize wizard to phase defined by query parameters', function() {
            expect(scope.currentPhase).toEqual(constants.phases.EDTYPE);
        });

        it('should create correct search url from selections', function() {
            scope.showResults();
            expect(location.url()).toEqual('/haku/*?tab=los');
        })

    })
*/

    
    
});