"use strict";

xdescribe('AppBasket directive', function() {
    var scope,
    elem,
    directive,
    compiled,
    html,
    setup;

    //set our view html.
    html = '<div data-ki-app-basket-applicationsystem data-items="applicationItems">' +
                '<div data-ki-app-basket-applicationsystem-table></div>' +
            '</div>';

    setup = function($compile, scope) {
        elem = angular.element(html);
        compiled = $compile(elem);
        compiled(scope);
        scope.$apply();
    }

    beforeEach(function () {    
        module('kiApp', 'kiTemplates', 'kiMock');

        inject(function($compile, $rootScope, appbasketData) {
            scope = $rootScope;
            /*
            scope.applicationItems = [
                {
                    applicationSystemId: 'as_id',
                    applicationOptions: [
                        {
                            id: 'ao_id',
                            type: 'ao_type'
                        }
                    ]
                }
            ];
            */
            scope.applicationItems = appbasketData;
            setup($compile, scope);
        });

    });

    it('should..', function() {

    });

    /*
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
*/
});