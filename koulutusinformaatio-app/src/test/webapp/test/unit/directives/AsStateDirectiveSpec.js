"use strict";

describe('kiAsState directive', function() {
    var scope,
    elem,
    directive,
    compiled,
    html,
    setup;

    //set our view html.
    html = '<div data-ki-as-state></div>';

    setup = function($compile, scope) {
        elem = angular.element(html);
        compiled = $compile(elem);
        compiled(scope);
        scope.$digest();
    }

    beforeEach(function () {    
        module('kiApp', 'kiTemplates');
    });

    describe('with one upcoming application system', function() {
        
        beforeEach(function () {    
            inject(function($compile, $rootScope) {
                scope = $rootScope;
                scope.lo = {
                    asOngoing: false,
                    nextApplicationPeriodStarts: [
                        355406400000
                    ]
                }
                    
                setup($compile, scope);
            });
        });

        it('should have only one timestamp', function() {
            expect(elem.find('ul').hasClass('list-unstyled')).toBeTruthy();
            expect($(elem).find('ul li').length).toEqual(1);
        });

        it('should contain the correct timestamp', function() {
            expect($(elem).find('ul li').eq(0).text().trim()).toEqual('6.4.1981 time-abbreviation 15:00');
        });
    });

    describe('with multiple upcoming application systems', function() {
        
        beforeEach(function () {    
            inject(function($compile, $rootScope) {
                scope = $rootScope;
                scope.lo = {
                    asOngoing: false,
                    nextApplicationPeriodStarts: [
                        355406400000,
                        355410000000
                    ]
                }
                    
                setup($compile, scope);
            });
        });

        it('should have two timestamps', function() {
            expect(elem.find('ul').hasClass('inside')).toBeTruthy();
            expect($(elem).find('ul li').length).toEqual(2);
        });

        it('should contain the correct timestamp', function() {
            expect($(elem).find('ul li').eq(0).text().trim()).toEqual('6.4.1981 time-abbreviation 15:00');
            expect($(elem).find('ul li').eq(1).text().trim()).toEqual('6.4.1981 time-abbreviation 16:00');
        })

    });
});