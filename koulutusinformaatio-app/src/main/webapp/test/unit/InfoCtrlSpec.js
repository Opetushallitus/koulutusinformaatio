'use strict';

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