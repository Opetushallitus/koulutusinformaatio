"use strict";

describe('Location filter directive', function() {
    var scope,
    elem,
    directive,
    compiled,
    html;
  
    beforeEach(function () {

        //load the module
        module('kiApp', 'kiApp.directives', 'templates/locationFilter.html');
        
        //set our view html.
        html = '<div data-ki-location-filter></div>';
        
        inject(function($compile, $rootScope, $templateCache) {
            //create a scope (you could just use $rootScope, I suppose)
            scope = $rootScope;
            scope.locations = ['Helsinki', 'Turku'];
            scope.change = function() {};

            //get the jqLite or jQuery element
            elem = angular.element(html);

            //compile the element into a function to 
            // process the view.
            compiled = $compile(elem);

            //run the compiled view.
            compiled(scope);

            //call digest on the scope!
            scope.$digest();
        });
    });

    it('should contain exactly one input field', function() {
        expect(elem.find('input').length).toEqual(1);
    });

    it('should contain the locations passed with scope', function() {
        expect(elem.find('li').length).toEqual(2);
        expect($(elem).find('li').get(0).innerText).toContain('Helsinki');
        expect($(elem).find('li').get(1).innerText).toContain('Turku');
    });

    it('should add input field current value as new location', function() {
        scope.location = 'Espoo';
        scope.add();
        expect(scope.locations.length).toEqual(3);
        expect(scope.locations[2]).toEqual('Espoo');
    });

    it('should remove the clicked location', function() {
        $(elem).find('li a').get(0).click();
        expect(elem.find('li').length).toEqual(1);
    });
});