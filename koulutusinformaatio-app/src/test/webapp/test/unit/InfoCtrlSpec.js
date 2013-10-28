/*
'use strict';

/*
describe('InfoController', function() {
    var ctrl, scope;

    beforeEach(module('kiApp'));

    describe('when LearningOpportunity is selected', function() {

        beforeEach(inject(function($httpBackend, $rootScope, $controller, ParentLearningOpportunityService, 
                ChildLearningOpportunityService, SearchService, ParentLODataService, TitleService) {
            this.httpBackend = $httpBackend;
            this.controller = $controller;
            this.ParentLearningOpportunityService = ParentLearningOpportunityService;
            this.ChildLearningOpportunityService = ChildLearningOpportunityService;
            this.SearchService = SearchService;
            this.ParentLODataService = ParentLODataService;
            this.TitleService = TitleService;
            this.scope = $rootScope.$new();

        }));

        afterEach(function() {
            this.httpBackend.verifyNoOutstandingExpectation();
            this.httpBackend.verifyNoOutstandingRequest();
        });

        it('should fetch parent data', function() {
            var parentId = '123456';

            this.httpBackend.when('GET', '../lo/123456?lang=fi').respond(200, {"id": parentId, "name": "parent name", "availableTranslationLanguages": ["fi"]});
            ctrl = this.controller(InfoCtrl, {
                $scope: this.scope, 
                $routeParams: {parentId: '123456'}, 
                ParentLearningOpportunityService: this.ParentLearningOpportunityService,
                ChildLearningOpportunityService: this.ChildLearningOpportunityService,
                SearchService: this.SearchService,
                ParentLODataService: this.ParentLODataService,
                TitleService: this.TitleService
            });
            this.httpBackend.flush();

            expect(this.scope.parentLO).not.toBeUndefined();
            expect(this.scope.childLO).toBeUndefined();
            expect(this.scope.parentLO.id).toMatch(parentId);
        });

        it('should fetch parent and child data', function() {
            var parentId = '123456';
            var closId = parentId + '_3';
            var cloiId = parentId + '_4';

            this.httpBackend.when('GET', '../lo/123456?lang=fi').respond(200, {"id": parentId, "name": "parent name", "availableTranslationLanguages": ["fi"], "children": [{"closId": "123456_1", "cloiId": "123456_2"}, {"closId": "123456_3", "cloiId": "123456_3"}]});
            this.httpBackend.when('GET', '../lo/123456/123456_3/123456_4?lang=fi').respond(200, {"id": parentId, "closId": closId, "cloiId": cloiId, "name": "parent name", "availableTranslationLanguages": ["fi"]});
            ctrl = this.controller(InfoCtrl, {
                $scope: this.scope, 
                $routeParams: {parentId: '123456', closId: '123456_3', cloiId: '123456_4'}, 
                ParentLearningOpportunityService: this.ParentLearningOpportunityService,
                ChildLearningOpportunityService: this.ChildLearningOpportunityService,
                SearchService: this.SearchService,
                ParentLODataService: this.ParentLODataService,
                TitleService: this.TitleService
            });
            this.httpBackend.flush();

            expect(this.scope.parentLO).not.toBeUndefined();
            expect(this.scope.childLO).not.toBeUndefined();
            expect(this.scope.parentLO.id).toMatch(parentId);
            expect(this.scope.childLO.closId).toMatch(closId);
            expect(this.scope.childLO.cloiId).toMatch(cloiId);
        });
    });
});
*/