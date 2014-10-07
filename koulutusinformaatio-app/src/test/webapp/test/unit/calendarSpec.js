"use strict";

describe('ki', function() {

    beforeEach(function() {
        ApplicationSystemCalendar.calendar('selector', 'fi');
    })

    describe('Utils', function() {

        describe('isInThePast', function() {

            it('should return true for dates in the past', function() {
                var ts = new Date().getTime() - 100000;
                expect(ki.Utils.isInThePast({endDate: ts})).toBeTruthy();
            });

            it('should return false for dates in the future', function() {
                var ts = new Date().getTime() + 100000;
                expect(ki.Utils.isInThePast({endDate: ts})).toBeFalsy();
            });

            it('should return false for undifed dates', function() {
                expect(ki.Utils.isInThePast({})).toBeFalsy();
            });

        });

        describe('getAsStartDate', function() {

            var as = {
                applicationPeriods: [
                    {
                        dateRange: {
                            startDate: 1,
                            endDate: 0
                        }
                    }
                ]
            };

            it('should return correct start timestamp', function() {
                var result = ki.Utils.getAsStartDate(as);
                expect(result).toEqual(1);
            });

            it('should return undefined for undefined value', function() {
                var result = ki.Utils.getAsStartDate();
                expect(result).toBeUndefined();
            });

            it('should return undefined for as with no date range', function() {
                var result = ki.Utils.getAsStartDate({applicationPeriods: [{}]});
                expect(result).toBeUndefined();
            });

        });

        describe('getApplicationSystemName', function() {

            var as = {
                name: 'as1',
                applicationPeriods: [
                    {
                        dateRange: {
                            startDate: 1,
                            endDate: 0
                        },    
                        name: 'ap1'
                    }
                ]
            },

            as2 = {
                name: 'as2',
                applicationPeriods: [
                    {
                        dateRange: {
                            startDate: 1,
                            endDate: 0
                        }
                    }
                ]
            };

            it('should return correct name', function() {
                var result = ki.Utils.getApplicationSystemName(as);
                expect(result).toEqual('as1, ap1');
            });

            it('should return correct name if application period name does not exist', function() {
                var result = ki.Utils.getApplicationSystemName(as2);
                expect(result).toEqual('as2');
            });

            it('should return undefined for undefined value', function() {
                var result = ki.Utils.getApplicationSystemName();
                expect(result).toBeUndefined();
            });

        });

        describe('getTimestamp', function() {

            it('should return correctly formatted date', function() {
                var result = ki.Utils.getTimestamp(new Date(0));
                expect(result).toEqual('1.1.1970 klo 02:00');
            });

            it('should return undefined for undefined date', function() {
                var result = ki.Utils.getTimestamp();
                expect(result).toBeUndefined();
            });

        });

    });

});