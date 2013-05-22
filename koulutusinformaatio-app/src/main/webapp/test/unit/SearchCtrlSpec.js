'use strict';

describe('SearchController', function() {
    var ctrl, scope;

    var searchterms = {
        kasityo: 'Kasityo',
        musiikki: 'Musiikki*',
        empty: ''
    };

    beforeEach(module('kiApp'));

    describe('when initial query string is given in path', function() {

        beforeEach(inject(function($httpBackend, $rootScope, $controller, SearchLearningOpportunityService, 
            SearchService, TitleService, $location) {

            this.httpBackend = $httpBackend;
            this.controller = $controller;
            this.SearchLearningOpportunityService = SearchLearningOpportunityService;
            this.SearchService = SearchService;
            this.TitleService = TitleService;
            this.location = $location;
            this.scope = $rootScope.$new();

        }));


        afterEach(function() {
            this.httpBackend.verifyNoOutstandingExpectation();
            this.httpBackend.verifyNoOutstandingRequest();
        });

        it('should have scope variables set when an empty query string is given', function() {
            spyOn(this.SearchLearningOpportunityService, 'query').andCallThrough();
            
            ctrl = this.controller(SearchCtrl, {
                $scope: this.scope, 
                $routeParams: {}, 
                SearchLearningOpportunityService: this.SearchLearningOpportunityService,
                SearchService: this.SearchService,
                TitleService: this.TitleService,
                $location: this.location
            });

            expect(this.SearchLearningOpportunityService.query).not.toHaveBeenCalled();
            expect(this.scope.queryString).toBe(null);
            expect(this.scope.loResult).toBeUndefined();
        });

        it('should have scope variables set when a proper query string is given', function() {
            spyOn(this.SearchLearningOpportunityService, 'query').andCallThrough();

            this.httpBackend.when('GET', '../lo/search/' + searchterms.kasityo).respond(200, '[]');
            ctrl = this.controller(SearchCtrl, {
                $scope: this.scope, 
                $routeParams: {queryString: searchterms.kasityo}, 
                SearchLearningOpportunityService: this.SearchLearningOpportunityService,
                SearchService: this.SearchService,
                TitleService: this.TitleService,
                $location: this.location
            });
            this.httpBackend.flush();

            expect(this.SearchLearningOpportunityService.query).toHaveBeenCalled();
            expect(this.SearchLearningOpportunityService.query.calls.length).toEqual(1);
            expect(this.scope.queryString).toEqual(searchterms.kasityo);
            expect(this.scope.loResult.length).toEqual(0);
        });

        it('should return correct result for manual search with valid query string', function() {
            ctrl = this.controller(SearchCtrl, {
                $scope: this.scope, 
                $routeParams: {}, 
                SearchLearningOpportunityService: this.SearchLearningOpportunityService,
                TitleService: this.TitleService,
                SearchService: this.SearchService,
                $location: this.location
            });

            this.scope.queryString = searchterms.musiikki;
            this.scope.search();

            expect(this.SearchService.getTerm()).toMatch(searchterms.musiikki);
            expect(this.location.path()).toMatch('/haku/' + searchterms.musiikki);
        });

        it('should return correct result for manual search with an empty query string', function() {
            ctrl = this.controller(SearchCtrl, {
                $scope: this.scope, 
                $routeParams: {}, 
                SearchLearningOpportunityService: this.SearchLearningOpportunityService,
                TitleService: this.TitleService,
                SearchService: this.SearchService,
                $location: this.location
            });

            var locationPrev = this.location.path();
            var searchTermPrev = this.SearchService.getTerm();

            this.scope.queryString = searchterms.empty;
            this.scope.search();
            
            // location and search term should not be updated when searching with an empty string
            expect(this.location.path()).toMatch(locationPrev);
            expect(this.SearchService.getTerm()).toMatch(searchTermPrev);
        });
    });
});