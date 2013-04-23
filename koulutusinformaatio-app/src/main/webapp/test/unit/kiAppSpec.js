'use strict';

describe('SearchController', function() {
    var ctrl, scope;

    var searchterms = {
        kasityo: 'Kasityo',
        musiikki: 'Musiikki*',
        empty: ''
    };

    beforeEach(module('kiApp'));

    describe('when initial query string is given', function() {
        beforeEach(inject(function($httpBackend, $rootScope, $controller, LearningOpportunity, ParentLearningOpportunity, SearchService, $location){
            $httpBackend.when('GET', '../lo/search/' + searchterms.kasityo).respond([]);
            $httpBackend.when('GET', '../lo/search/' + searchterms.musiikki).respond([]);
            $httpBackend.when('GET', '../lo/search' + searchterms.empty).respond([]);
            scope = $rootScope.$new();
            ctrl = $controller(SearchCtrl, {$scope: scope, 
                $routeParams: {queryString: searchterms.kasityo}, 
                LearningOpportunity: LearningOpportunity,
                ParentLearningOpportunity: ParentLearningOpportunity,
                SearchService: SearchService,
                $location: $location
            });
        }));

        it('should have scope variables set when a proper query string is given', function() {
            expect(scope.queryString).toEqual(searchterms.kasityo);
            expect(scope.loResult.length).toEqual(0);
            //expect(scope.showFilters).toBeTruthy();
        });

        it('should return correct result for manual search with valid query string', function() {
            scope.queryString = searchterms.musiikki;
            scope.search();
            expect(scope.loResult.length).toEqual(0);
            //expect(scope.showFilters).toBeTruthy();
        });

        it('should return correct result for manual search with an empty query string', function() {
            scope.queryString = searchterms.empty;
            scope.search();
            expect(scope.loResult.length).toEqual(0);
            //expect(scope.showFilters).toBeFalsy();
        });
    });

describe('when initial query string is not given', function() {
    beforeEach(inject(function($httpBackend, $rootScope, $controller, LearningOpportunity){
        $httpBackend.when('GET', '../lo/search/' + searchterms.kasityo).respond([]);
        $httpBackend.when('GET', '../lo/search/' + searchterms.musiikki).respond([]);
        $httpBackend.when('GET', '../lo/search' + searchterms.empty).respond([]);
        scope = $rootScope.$new();
        ctrl = $controller(SearchCtrl, {$scope: scope, $routeParams: {}, LearningOpportunity: LearningOpportunity});
    }));

    it('should have scope varables set when a proper query string is given', function() {
        expect(scope.queryString).toBeUndefined();
        expect(scope.loResult).toBeUndefined();
        expect(scope.showFilters).toBeUndefined();
    });
});

});

describe('InfoController', function() {
    var ctrl, scope;

    beforeEach(module('kiApp'));

    describe('when parent LO is selected', function() {
        beforeEach(inject(function($httpBackend, $rootScope, $controller, ParentLearningOpportunity, SearchService, $location){
            $httpBackend.when('GET', '../lo/123456').respond({});
            scope = $rootScope.$new();
            ctrl = $controller(IndexCtrl, {
                $scope: scope, 
                $routeParams: {parentId: '123456'}, 
                ParentLearningOpportunity: ParentLearningOpportunity,
                SearchService: SearchService,
                $location: $location
            });
        }));

        it('should fetch parent data', function() {
            //expect(scope.parentLO.length).toEqual(0);
            //expect(scope.showFilters).toBeTruthy();
        });
    });
});
