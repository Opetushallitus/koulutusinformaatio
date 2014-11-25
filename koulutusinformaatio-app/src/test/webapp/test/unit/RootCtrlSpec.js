"use strict";

describe('RootCtrl', function() {

    var ctrl,
        scope,
        rs,
        httpBackend,
        controller;

    beforeEach(function() {
        module('kiApp');

        inject(function($controller, $httpBackend, $rootScope) {
            rs = $rootScope;
            scope = $rootScope.$new();
            controller = $controller;
            httpBackend = $httpBackend;
            ctrl = controller('RootCtrl', { $scope: scope });
            
        });
    });

    it('remove error status when route changes', function() {
        rs.error = true;
        rs.$broadcast('$locationChangeStart', {});
        expect(rs.error).toBeUndefined();
    });
});