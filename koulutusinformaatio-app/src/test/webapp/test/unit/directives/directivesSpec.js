"use strict";

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
            expect(elem.hasClass('label-success')).toBeTruthy();
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
            expect(elem.hasClass('label-default')).toBeTruthy();
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
                            hakutapa: '01',
                            hakutyyppi: '03',
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
            expect(elem.hasClass('label-success')).toBeTruthy();
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
            expect(elem.hasClass('label-default')).toBeTruthy();
        });

        it('should contain correct text', function() {
            expect(elem.text()).toMatch('label-as-not-ongoing');
        });

    });

});


