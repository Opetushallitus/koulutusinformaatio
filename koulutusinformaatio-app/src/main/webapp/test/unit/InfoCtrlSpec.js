'use strict';

describe('InfoController', function() {
    var ctrl, scope;

    beforeEach(module('kiApp'));

    describe('when LearningOpportunity is selected', function() {
        var $httpBackend_;
        var $controller_;
        var ParentLearningOpportunityService_;
        var ChildLearningOpportunityService_;
        var SearchService_;
        var ParentLODataService_;
        var TitleService_;
        var $location_;

        beforeEach(inject(function($httpBackend, $rootScope, $controller, ParentLearningOpportunityService, 
                ChildLearningOpportunityService, SearchService, ParentLODataService, TitleService) {
            $httpBackend_ = $httpBackend;
            $controller_ = $controller;
            ParentLearningOpportunityService_ = ParentLearningOpportunityService;
            ChildLearningOpportunityService_ = ChildLearningOpportunityService;
            SearchService_ = SearchService;
            ParentLODataService_ = ParentLODataService;
            TitleService_ = TitleService;
            scope = $rootScope.$new();

        }));

        afterEach(function() {
            $httpBackend_.verifyNoOutstandingExpectation();
            $httpBackend_.verifyNoOutstandingRequest();
        });

        it('should fetch parent data', function() {
            var parentId = '123456';

            $httpBackend_.when('GET', '../lo/123456?lang=fi').respond(200, {"id": parentId, "name": "parent name", "availableTranslationLanguages": ["fi"]});
            ctrl = $controller_(InfoCtrl, {
                $scope: scope, 
                $routeParams: {parentId: '123456'}, 
                ParentLearningOpportunityService: ParentLearningOpportunityService_,
                ChildLearningOpportunityService: ChildLearningOpportunityService_,
                SearchService: SearchService_,
                ParentLODataService: ParentLODataService_,
                TitleService: TitleService_
            });
            $httpBackend_.flush();

            expect(scope.parentLO).not.toBeUndefined();
            expect(scope.childLO).toBeUndefined();
            expect(scope.parentLO.id).toMatch(parentId);
        });

        it('should fetch parent and child data', function() {
            var parentId = '123456';
            var closId = parentId + '_3';
            var cloiId = parentId + '_4';

            $httpBackend_.when('GET', '../lo/123456?lang=fi').respond(200, {"id": parentId, "name": "parent name", "availableTranslationLanguages": ["fi"], "children": [{"closId": "123456_1", "cloiId": "123456_2"}, {"closId": "123456_3", "cloiId": "123456_3"}]});
            $httpBackend_.when('GET', '../lo/123456/123456_3/123456_4?lang=fi').respond(200, {"id": parentId, "closId": closId, "cloiId": cloiId, "name": "parent name", "availableTranslationLanguages": ["fi"]});
            ctrl = $controller_(InfoCtrl, {
                $scope: scope, 
                $routeParams: {parentId: '123456', closId: '123456_3', cloiId: '123456_4'}, 
                ParentLearningOpportunityService: ParentLearningOpportunityService_,
                ChildLearningOpportunityService: ChildLearningOpportunityService_,
                SearchService: SearchService_,
                ParentLODataService: ParentLODataService_,
                TitleService: TitleService_
            });
            $httpBackend_.flush();

            expect(scope.parentLO).not.toBeUndefined();
            expect(scope.childLO).not.toBeUndefined();
            expect(scope.parentLO.id).toMatch(parentId);
            expect(scope.childLO.closId).toMatch(closId);
            expect(scope.childLO.cloiId).toMatch(cloiId);
        });
    });
});