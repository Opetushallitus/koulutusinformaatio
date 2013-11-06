describe('UtilityService', function() {
    var utility;

    beforeEach(function() {
        module('kiApp', 'kiApp.services');

        inject(function(UtilityService) {
            utility = UtilityService;
        });
    });

    describe('sortApplicationSystems', function() {

        it('should work for empty input', function() {
            utility.sortApplicationSystems();
        });

        it('should sort application systems by ongoing attribute', function() {
            var data = [
                {
                    asOngoing: false,
                },
                {
                    asOngoing: true
                }
            ];

            utility.sortApplicationSystems(data);
            expect(data[0].asOngoing).toBeTruthy();
            expect(data[1].asOngoing).toBeFalsy();
        });

        it('should sort application systems by nextApplicationPeriodStarts attribute', function() {
            var data = [
                {
                    asOngoing: false,
                    id: 'a',
                    nextApplicationPeriodStarts: 2
                },
                {
                    asOngoing: false,
                    id: 'b',
                    nextApplicationPeriodStarts: 1
                }
            ];

            utility.sortApplicationSystems(data);
            expect(data[0].id).toEqual('b');
            expect(data[1].id).toEqual('a');
        });

        it('should sort application systems by earliest start date', function() {
            var data = [
                {
                    asOngoing: false,
                    id: 'a',
                    applicationDates: [
                        {
                            startDate: 5,
                            endDate: 10
                        },
                        {
                            startDate: 20,
                            endDate: 30
                        }
                    ]
                },
                {
                    asOngoing: false,
                    id: 'b',
                    applicationDates: [
                        {
                            startDate: 4,
                            endDate: 8
                        }
                    ]
                }
            ];

            utility.sortApplicationSystems(data);
            expect(data[0].id).toEqual('b');
            expect(data[1].id).toEqual('a');
        });

        it('should sort ended applications systems to the bottom', function() {
            var data = [
                {
                    asOngoing: false,
                    id: 'a'
                },
                {
                    asOngoing: false,
                    id: 'b',
                    nextApplicationPeriodStarts: 1
                },
                {
                    asOngoing: false,
                    id: 'c',
                    nextApplicationPeriodStarts: 2
                }
            ];

            utility.sortApplicationSystems(data);
            expect(data[0].id).toEqual('b');
            expect(data[1].id).toEqual('c');
            expect(data[2].id).toEqual('a');
        });
    });

    describe('sortApplicationSystems', function() {
        var data = [
            {id: 1}, {id: 2}
        ];

        it('should return the correct item if it exists', function() {
            var result = utility.getApplicationOptionById(1, data);
            expect(result.id).toEqual(1);
        });

        it('should return undefined if item does not exists', function() {
            var result = utility.getApplicationOptionById(3, data);
            expect(result).toBeUndefined();
        });

        it('should return undefined if item does not exists', function() {
            var aoId;

            var result = utility.getApplicationOptionById(aoId, data);
            expect(result).toBeUndefined();
        });
    });

    describe('getStringAsArray', function() {

        it('should return the comma-separated string as an array', function() {
            var result = utility.getStringAsArray('abc,def,gef');
            expect(result.length).toEqual(3);
        });

        it('should return single item array for strings without separator', function() {
            var result = utility.getStringAsArray('abc');
            expect(result.length).toEqual(1);
        });

        it('should return undefined if param not given', function() {
            var result = utility.getStringAsArray();
            expect(result).toBeUndefined();
        });

        it('should return undefined if param not of type string', function() {
            var result = utility.getStringAsArray(2);
            expect(result).toBeUndefined();
        });
    });
});


/*
'use strict';
describe('FilterService', function() {
    beforeEach(module('kiApp'));

    beforeEach(inject(function(FilterService) {
        this.FilterService = FilterService;
    }));

    it('should set empty filter as empty', function() {
        this.FilterService.set({});

        expect(this.FilterService.get()).toEqual({});
    });

    it('should set the filters correctly', function() {
        var myFilter = {myFilter: 'myFilterValue'};
        this.FilterService.set(myFilter);

        expect(this.FilterService.get()).toEqual(myFilter);
    });

    it('should transform locations filter to array', function() {
        this.FilterService.set({
            locations: 'Helsinki'
        });

        expect(this.FilterService.get().locations instanceof Array).toBeTruthy();
        expect(this.FilterService.get().locations.length).toEqual(1);
        expect(this.FilterService.get().locations[0]).toMatch('Helsinki');
    });

    it('should set boolean value only when it is true', function() {
        this.FilterService.set({
            myTrueFilter: true,
            myFalseFilter: false
        });

        expect(this.FilterService.get().myTrueFilter).toEqual(true);
        expect(this.FilterService.get().myFalseFilter).toBeUndefined();
    });

    it('should return empty filter as empty query param string', function() {
        this.FilterService.set({});

        expect(this.FilterService.getParams()).toMatch('');
    });

    it('should return array filter as comma separated list', function() {
        this.FilterService.set({
            locations: 'Helsinki,Turku,Espoo'
        });

        expect(this.FilterService.getParams()).toEqual('locations=Helsinki,Turku,Espoo');
    });

    it('should return boolean filter only when value is true', function() {
        this.FilterService.set({
            booleanFilterTrue: true,
            booleanFilterFalse: false
        });

        expect(this.FilterService.getParams()).toEqual('booleanFilterTrue');
    });

    it('should return combination of filters', function() {
        this.FilterService.set({
            locations: 'Helsinki,Turku',
            booleanFilter: true,
            stringFilter: 'filterstring'
        });

        expect(this.FilterService.getParams()).toEqual('locations=Helsinki,Turku&booleanFilter&stringFilter=filterstring');
    });

})
/*
describe('SearchService', function() {

    beforeEach(module('kiApp'));

    beforeEach(inject(function(SearchService) {
        this.SearchService = SearchService;
    }));

    it('should save the search term', function() {
        var searchterm = 'musiikki';
        this.SearchService.setTerm(searchterm);

        expect(this.SearchService.getTerm()).toMatch(searchterm);
    });
});

describe('TitleService', function() {

    beforeEach(module('kiApp'));

    beforeEach(inject(function(TitleService) {
        this.TitleService = TitleService;
    }));

    it('should save the given title set', function() {
        var title = 'Etusivu';
        this.TitleService.setTitle(title);

        expect(this.TitleService.getTitle()).toMatch(title + ' - Opintopolku.fi');
    });
});

describe('ParentLODataService', function() {
    var parentId = '123456';
    var childId = parentId + '_2';

    beforeEach(module('kiApp'));

    beforeEach(inject(function(ParentLODataService) {
        this.ParentLODataService = ParentLODataService;
    }));

    it('should have no data set', function() {
        expect(this.ParentLODataService.dataExists(parentId)).toBeFalsy();
    });

    it('should have parent data set', function() {
        this.ParentLODataService.setParentLOData({"id": parentId, "name": "parent name"});
        expect(this.ParentLODataService.dataExists(parentId)).toBeTruthy();
        expect(this.ParentLODataService.getParentLOData(parentId).id).toEqual(parentId);
    });

    it('should have parent and child data set', function() {
        this.ParentLODataService.setParentLOData({"id": parentId, "name": "parent name", children: [{"id": "123456_1"}, {"id": childId}]});
        expect(this.ParentLODataService.dataExists(parentId)).toBeTruthy();
        expect(this.ParentLODataService.getParentLOData(parentId).id).toEqual(parentId);
    });


});

describe('TranslationService', function() {
    
    beforeEach(module('kiApp'));

    beforeEach(inject(function(TranslationService) {
        this.TranslationService = TranslationService;
    }));

    it('should return a value for existing key', function() {
        var key = 'existing-key';
        var value = this.TranslationService.getTranslation(key);
        expect(value).toMatch('translation value');
    });

    it('should return key for non-existing key', function() {
        var key = 'non-existing-key';
        var value = this.TranslationService.getTranslation(key);
        expect(value).toMatch(key);
    });

})
*/