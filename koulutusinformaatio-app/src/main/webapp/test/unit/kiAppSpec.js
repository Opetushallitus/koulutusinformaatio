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
        var $httpBackend_;
        var $controller_ ;
        var SearchLearningOpportunity_;
        var ParentLearningOpportunity_;
        var SearchService_;
        var $location_;

        beforeEach(inject(function($httpBackend, $rootScope, $controller, SearchLearningOpportunity, 
                ParentLearningOpportunity, SearchService, $location) {
            $httpBackend_ = $httpBackend;
            $controller_ = $controller;
            SearchLearningOpportunity_ = SearchLearningOpportunity;
            ParentLearningOpportunity_ = ParentLearningOpportunity;
            SearchService_ = SearchService;
            $location_ = $location;
            scope = $rootScope.$new();
        }));

        afterEach(function() {
            $httpBackend_.verifyNoOutstandingExpectation();
            $httpBackend_.verifyNoOutstandingRequest();
        });

        it('should have scope variables set when an empty query string is given', function() {
            spyOn(SearchLearningOpportunity_, 'query').andCallThrough();
            
            ctrl = $controller_(SearchCtrl, {
                $scope: scope, 
                $routeParams: {}, 
                SearchLearningOpportunity: SearchLearningOpportunity_,
                ParentLearningOpportunity: ParentLearningOpportunity_,
                SearchService: SearchService_,
                $location: $location_
            });

            expect(SearchLearningOpportunity_.query).not.toHaveBeenCalled();
            expect(scope.queryString).toBeUndefined();
            expect(scope.loResult).toBeUndefined();
        });

        it('should have scope variables set when a proper query string is given', function() {
            spyOn(SearchLearningOpportunity_, 'query').andCallThrough();

            $httpBackend_.when('GET', '../lo/search/' + searchterms.kasityo).respond(200, '[]');
            ctrl = $controller_(SearchCtrl, {
                $scope: scope, 
                $routeParams: {queryString: searchterms.kasityo}, 
                SearchLearningOpportunity: SearchLearningOpportunity_,
                ParentLearningOpportunity: ParentLearningOpportunity_,
                SearchService: SearchService_,
                $location: $location_
            });
            $httpBackend_.flush();

            expect(SearchLearningOpportunity_.query).toHaveBeenCalled();
            expect(SearchLearningOpportunity_.query.calls.length).toEqual(1);
            expect(scope.queryString).toEqual(searchterms.kasityo);
            expect(scope.loResult.length).toEqual(0);
        });

        it('should return correct result for manual search with valid query string', function() {
            ctrl = $controller_(SearchCtrl, {
                $scope: scope, 
                $routeParams: {}, 
                SearchLearningOpportunity: SearchLearningOpportunity_,
                ParentLearningOpportunity: ParentLearningOpportunity_,
                SearchService: SearchService_,
                $location: $location_
            });

            scope.queryString = searchterms.musiikki;
            scope.search();

            expect(SearchService_.getTerm()).toMatch(searchterms.musiikki);
            expect($location_.path()).toMatch('/haku/' + searchterms.musiikki);
        });

        it('should return correct result for manual search with an empty query string', function() {
            ctrl = $controller_(SearchCtrl, {
                $scope: scope, 
                $routeParams: {}, 
                SearchLearningOpportunity: SearchLearningOpportunity_,
                ParentLearningOpportunity: ParentLearningOpportunity_,
                SearchService: SearchService_,
                $location: $location_
            });

            var locationPrev = $location_.path();
            var searchTermPrev = SearchService_.getTerm();

            scope.queryString = searchterms.empty;
            scope.search();
            
            // location and serach term should not be updated when searching with an empty string
            expect($location_.path()).toMatch(locationPrev);
            expect(SearchService_.getTerm).toMatch(searchTermPrev);
        });
    });
});

describe('InfoController', function() {
    var ctrl, scope;

    beforeEach(module('kiApp'));

    describe('when LearningOpportunity is selected', function() {
        var $httpBackend_;
        var $controller_;
        var ParentLearningOpportunity_;
        var SearchService_;
        var LODataService_;
        var TitleService_;
        var $location_;

        beforeEach(inject(function($httpBackend, $rootScope, $controller, ParentLearningOpportunity, 
                SearchService, LODataService, TitleService) {
            $httpBackend_ = $httpBackend;
            $controller_ = $controller;
            ParentLearningOpportunity_ = ParentLearningOpportunity;
            SearchService_ = SearchService;
            LODataService_ = LODataService;
            TitleService_ = TitleService;
            scope = $rootScope.$new();

        }));

        afterEach(function() {
            $httpBackend_.verifyNoOutstandingExpectation();
            $httpBackend_.verifyNoOutstandingRequest();
        });

        it('should fetch parent data', function() {
            var parentId = '123456';

            $httpBackend_.when('GET', '../lo/123456').respond(200, {"id": parentId, "name": "parent name"});
            ctrl = $controller_(InfoCtrl, {
                $scope: scope, 
                $routeParams: {parentId: '123456'}, 
                ParentLearningOpportunity: ParentLearningOpportunity_,
                SearchService: SearchService_,
                LODataService: LODataService_,
                TitleService: TitleService_
            });
            $httpBackend_.flush();

            expect(scope.parentLO).not.toBeUndefined();
            expect(scope.childLO).toBeUndefined();
            expect(scope.parentLO.id).toMatch(parentId);
        });

        it('should fetch parent and child data', function() {
            var parentId = '123456';
            var childId = parentId + '_2';

            $httpBackend_.when('GET', '../lo/123456').respond(200, {"id": parentId, "name": "parent name", "children": [{"id": "123456_1"}, {"id": childId}]});
            ctrl = $controller_(InfoCtrl, {
                $scope: scope, 
                $routeParams: {parentId: '123456', childId: '123456_2'}, 
                ParentLearningOpportunity: ParentLearningOpportunity_,
                SearchService: SearchService_,
                LODataService: LODataService_,
                TitleService: TitleService_
            });
            $httpBackend_.flush();

            expect(scope.parentLO).not.toBeUndefined();
            expect(scope.childLO).not.toBeUndefined();
            expect(scope.parentLO.id).toMatch(parentId);
            expect(scope.childLO.id).toMatch(childId);
        });
    });
});

describe('IndexController', function() {
    var ctrl, scope;

    beforeEach(module('kiApp'));

    describe('when index page is viewed', function() {
        var $controller_;
        var TitleService_;

        beforeEach(inject(function($rootScope, $controller, TitleService) {
            $controller_ = $controller;
            TitleService_ = TitleService;
            scope = $rootScope.$new();

        }));

        it('should have the correct title', function() {
            ctrl = $controller_(IndexCtrl, {
                $scope: scope, 
                TitleService: TitleService_
            });

            expect(TitleService_.getTitle()).toContain('Etusivu');
        });
    });
});
