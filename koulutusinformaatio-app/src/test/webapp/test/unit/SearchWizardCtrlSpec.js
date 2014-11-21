"use strict";

describe('SearchWizardCtrl', function() {

    var ctrl, $scope, $httpBackend;

    beforeEach(function() {
        module('kiApp');

        inject(function($controller, $httpBackend) {
            $scope = {};
            $httpBackend = $httpBackend;
            ctrl = $controller('SearchWizardCtrl', { $scope: $scope });

            $httpBackend.when('GET', '/lo/search').respond('{test: "test"}');
        });
    });

    it('should test', function() {
        //console.log($scope.searchResult);
    })

})