"use strict";

describe('ChildLOService', function() {
    var service,
        parentService,
        spy,
        httpBackend,
        rs;

    beforeEach(function() {
        module('kiApp');

        inject(function($httpBackend, $rootScope, ChildLOService, ChildLOTransformer, ParentLOService) {
            service = ChildLOService;
            parentService = ParentLOService;
            httpBackend = $httpBackend;
            rs = $rootScope;
            spy = spyOn(ChildLOTransformer, 'transform').andReturn();
            spyOn(parentService, 'query').andCallThrough();
        });
    });

    afterEach(function() {
        httpBackend.verifyNoOutstandingExpectation();
        httpBackend.verifyNoOutstandingRequest();
    });

    it('should transform the response', function() {
        httpBackend.when('GET', '/lo/child/123?uiLang=fi').respond(200, { parent: { id: 'parent_123' } });
        httpBackend.when('GET', '/lo/tutkinto/parent_123?uiLang=fi').respond(200, {});
        service.query({id: '123'}).then(function(result) {
            expect(spy).toHaveBeenCalled();
            expect(parentService.query).toHaveBeenCalled();
            expect(result).toBeDefined();
        });
        httpBackend.flush();
    });

    it('should set the error flag in rootscope when request fails', function() {
        httpBackend.when('GET', '/lo/child/123?uiLang=fi').respond(404, {});
        service.query({id: '123'}).then(function(result) {},
        function(error) {
            expect(rs.error).toBeTruthy();
        });
        httpBackend.flush();
    });
})