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


describe('Application system state label', function() {
    var scope,
    elem,
    directive,
    compiled,
    html,
    setup;

    //set our view html.
    html = '<span data-ki-as-state-label></span>';

    setup = function($compile, scope) {
        elem = angular.element(html);
        compiled = $compile(elem);
        compiled(scope);
        scope.$digest();
    }

    beforeEach(function () {    
        module('kiApp');
    });


    describe('for ongoing application system', function() {
  
        beforeEach(function () {            
            inject(function($compile, $rootScope, $templateCache) {
                scope = $rootScope;
                scope.loi = {
                    applicationSystems: 
                    [
                        {
                            asOngoing: true,
                            aoSpecificApplicationDates: false,
                            applicationOptions: 
                            [
                                {
                                    canBeApplied: true
                                }
                            ]
                        }
                    ]
                }

                setup($compile, scope);
            });
        });

        it('should have class label and vih', function() {
            expect(elem.hasClass('label')).toBeTruthy();
            expect(elem.hasClass('vih')).toBeTruthy();
        });

        it('should contain correct text', function() {
            expect(elem.text()).toMatch('label-as-ongoing');
        });

    });

    describe('for inaccessible application system', function() {
  
        beforeEach(function () {            
            inject(function($compile, $rootScope, $templateCache) {
                scope = $rootScope;
                scope.loi = {
                    applicationSystems: 
                    [
                        {
                            asOngoing: false,
                            aoSpecificApplicationDates: false,
                            applicationOptions: 
                            [
                                {
                                    canBeApplied: false
                                }
                            ]
                        }
                    ]
                }

                setup($compile, scope);
            });
        });

        it('should have class label and har', function() {
            expect(elem.hasClass('label')).toBeTruthy();
            expect(elem.hasClass('har')).toBeTruthy();
        });

        it('should contain correct text', function() {
            expect(elem.text()).toMatch('label-as-not-ongoing');
        });

    });

    describe('for ongoing lisähaku application system', function() {
  
        beforeEach(function () {            
            inject(function($compile, $rootScope, $templateCache) {
                scope = $rootScope;
                scope.loi = {
                    applicationSystems: 
                    [
                        {
                            asOngoing: false,
                            aoSpecificApplicationDates: true,
                            applicationOptions: 
                            [
                                {
                                    canBeApplied: true
                                }
                            ]
                        }
                    ]
                }

                setup($compile, scope);
            });
        });

        it('should have class label and vih', function() {
            expect(elem.hasClass('label')).toBeTruthy();
            expect(elem.hasClass('vih')).toBeTruthy();
        });

        it('should contain correct text', function() {
            expect(elem.text()).toMatch('label-as-ongoing');
        });

    });

    describe('for inaccessible lisähaku application system', function() {
  
        beforeEach(function () {            
            inject(function($compile, $rootScope, $templateCache) {
                scope = $rootScope;
                scope.loi = {
                    applicationSystems: 
                    [
                        {
                            asOngoing: false,
                            aoSpecificApplicationDates: true,
                            applicationOptions: 
                            [
                                {
                                    canBeApplied: false
                                }
                            ]
                        }
                    ]
                }

                setup($compile, scope);
            });
        });

        it('should have class label and har', function() {
            expect(elem.hasClass('label')).toBeTruthy();
            expect(elem.hasClass('har')).toBeTruthy();
        });

        it('should contain correct text', function() {
            expect(elem.text()).toMatch('label-as-not-ongoing');
        });

    });

});

