"use strict";

describe('GeneralLOService', function() {
    var service,
        transformer,
        spy,
        httpBackend,
        rs;

    beforeEach(function() {
        module('kiApp');

        inject(function($httpBackend, $rootScope, GeneralLOService, ParentLOTransformer) {
            service = GeneralLOService;
            transformer = ParentLOTransformer;
            httpBackend = $httpBackend;
            rs = $rootScope;
            spy = spyOn(transformer, 'transform').andReturn({});
        });
    });

    afterEach(function() {
        httpBackend.verifyNoOutstandingExpectation();
        httpBackend.verifyNoOutstandingRequest();
    });

    it('should transform the response', function() {
        httpBackend.when('GET', '../lo/parent/123?uiLang=fi').respond(200, {});
        service.query({id: '123'}, '../lo/parent/', transformer).then(function(result) {
            expect(spy).toHaveBeenCalled();
            expect(result).toBeDefined();
        });
        httpBackend.flush();
    });

    it('should set the error flag in rootscope when request fails', function() {
        httpBackend.when('GET', '../lo/parent/123?uiLang=fi').respond(404, {});
        service.query({id: '123'}, '../lo/parent/', transformer).then(function(result) {},
        function(error) {
            expect(rs.error).toBeTruthy();
        });
        httpBackend.flush();
    });
})