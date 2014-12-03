"use strict";

describe('SearchBySubjectCtrl', function() {

    var ctrl,
        scope,
        rs,
        httpBackend,
        controller,
        location,
        themes;

    beforeEach(function() {
        module('kiApp', 'kiMock');

        inject(function($controller, $httpBackend, $rootScope, $location, themeFacet) {
            rs = $rootScope;
            scope = $rootScope.$new();
            controller = $controller;
            httpBackend = $httpBackend;
            location = $location;
            themes = themeFacet;

            httpBackend.when('GET', '../lo/search?start=0&rows=0&lang=fi&searchType=LO&text=*').respond(themes);
            httpBackend.when('GET', 'partials/search/search.html').respond(''); // for some unknown reason this gets requested
            ctrl = controller('SearchBySubjectCtrl', { $scope: scope });
            httpBackend.flush();
        });
    });

    afterEach(function() {
        httpBackend.verifyNoOutstandingExpectation();
        httpBackend.verifyNoOutstandingRequest();
    });

    
    it('should initialize themes', function() {
        expect(scope.themes).toBeDefined();
    });

    it('should select theme', function() {
        scope.selectTheme(scope.themes[0]);
        expect(scope.selectedTheme.valueName).toEqual('themeName');
    });

    it('should select topic', function() {
        scope.selectTheme(scope.themes[0]);
        var topic = scope.selectedTheme.childValues[0]
        scope.selectTopic(scope.selectedTheme.childValues[0]);
        expect(topic.valueName).toEqual("topicName");
        expect(location.url()).toEqual('/haku/*?facetFilters=topic_ffm:' + topic.valueId);
    })

});