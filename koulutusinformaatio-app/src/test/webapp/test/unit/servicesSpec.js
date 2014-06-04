describe('UtilityService', function() {
    var utility;

    beforeEach(function() {
        module('kiApp', 'kiApp.services');

        inject(function(KiSorter) {
            utility = KiSorter;
        });
    });

    describe('sortApplicationSystems', function() {

        var hakutapa = {
            yhteishaku: '01',
            erillishaku: '02',
            jatkuva: '03'
        };

        var hakutyyppi = {
            varsinainen: '01',
            taydennys: '02',
            lisa: '03'
        };

        it('should work for empty input', function() {
            utility.sortApplicationSystems();
        });

        it('should sort application systems by their status', function() {
            var data = [
                {
                    id: 'a',
                    applicationOptions: [
                        {
                            canBeApplied: false
                        }
                    ]
                },
                {
                    id: 'b',
                    applicationOptions: [
                        {
                            canBeApplied: true
                        }
                    ]
                }
            ];

            utility.sortApplicationSystems(data);
            expect(data[0].id).toEqual('b');
            expect(data[1].id).toEqual('a');
        });

        it('should sort "käynnissä oleva varsinainen yhteishaku" first', function() {
            var data = [
                {
                    id: 'a',
                    hakutapa: hakutapa.yhteishaku,
                    hakutyyppi: hakutyyppi.lisa,
                    applicationOptions: [
                        {
                            canBeApplied: true
                        }
                    ]
                },
                {
                    id: 'b',
                    hakutapa: hakutapa.yhteishaku,
                    hakutyyppi: hakutyyppi.varsinainen,
                    applicationOptions: [
                        {
                            canBeApplied: true
                        }
                    ]
                }
            ];

            utility.sortApplicationSystems(data);
            expect(data[0].id).toEqual('b');
            expect(data[1].id).toEqual('a');
        });

        it('should sort "käynnissä oleva yhteishaun lisähaku" first', function() {
            var data = [
                {
                    id: 'a',
                    hakutapa: hakutapa.yhteishaku,
                    hakutyyppi: hakutyyppi.varsinainen,
                    applicationOptions: [
                        {
                            canBeApplied: false
                        }
                    ]
                },
                {
                    id: 'b',
                    hakutapa: hakutapa.yhteishaku,
                    hakutyyppi: hakutyyppi.lisa,
                    applicationOptions: [
                        {
                            canBeApplied: true
                        }
                    ]
                }
            ];

            utility.sortApplicationSystems(data);
            expect(data[0].id).toEqual('b');
            expect(data[1].id).toEqual('a');
        });

        it('should sort "käynnissä oleva yhteishaun lisähaku" before upcoming "varsinainen yhteishaku"', function() {
            var ts = new Date().getTime();
            var data = [
                {
                    id: 'a',
                    hakutapa: hakutapa.yhteishaku,
                    hakutyyppi: hakutyyppi.varsinainen,
                    applicationOptions: [
                        {
                            canBeApplied: false,
                            applicationStartDate: ts + 15 * 24 * 60 * 60 * 1000 
                        }
                    ]
                },
                {
                    id: 'b',
                    hakutapa: hakutapa.yhteishaku,
                    hakutyyppi: hakutyyppi.lisa,
                    applicationOptions: [
                        {
                            canBeApplied: true
                        }
                    ]
                }
            ];

            utility.sortApplicationSystems(data);
            expect(data[0].id).toEqual('b');
            expect(data[1].id).toEqual('a');
        });

        it('should sort upcoming "varsinainen yhteishaku" before "käynnissä oleva erillishaku"', function() {
            var ts = new Date().getTime();
            var data = [
                {
                    id: 'a',
                    hakutapa: hakutapa.erillishaku,
                    hakutyyppi: hakutyyppi.varsinainen,
                    applicationOptions: [
                        {
                            canBeApplied: true,
                            
                        }
                    ]
                },
                {
                    id: 'b',
                    hakutapa: hakutapa.yhteishaku,
                    hakutyyppi: hakutyyppi.varsinainen,
                    applicationOptions: [
                        {
                            canBeApplied: false,
                            applicationStartDate: ts + 15 * 24 * 60 * 60 * 1000 
                        }
                    ]
                }
            ];

            utility.sortApplicationSystems(data);
            expect(data[0].id).toEqual('b');
            expect(data[1].id).toEqual('a');
        });

        it('should sort applications systems by start date', function() {
            var ts = new Date().getTime();
            var data = [
                {
                    id: 'a',
                    hakutapa: hakutapa.yhteishaku,
                    hakutyyppi: hakutyyppi.varsinainen,
                    applicationOptions: [
                        {
                            canBeApplied: false,
                            applicationStartDate: ts + 15 * 24 * 60 * 60 * 1000
                        }
                    ]
                },
                {
                    id: 'b',
                    hakutapa: hakutapa.yhteishaku,
                    hakutyyppi: hakutyyppi.varsinainen,
                    applicationOptions: [
                        {
                            canBeApplied: false,
                            applicationStartDate: ts + 5 * 24 * 60 * 60 * 1000 
                        }
                    ]
                }
            ];

            utility.sortApplicationSystems(data);
            expect(data[0].id).toEqual('b');
            expect(data[1].id).toEqual('a');
        });

        it('should sort applications systems by name', function() {
            var ts = new Date().getTime();
            var data = [
                {
                    id: 'a',
                    name: 'baa',
                    hakutapa: hakutapa.yhteishaku,
                    hakutyyppi: hakutyyppi.varsinainen,
                    applicationOptions: [
                        {
                            canBeApplied: false,
                            applicationStartDate: ts + 15 * 24 * 60 * 60 * 1000
                        }
                    ]
                },
                {
                    id: 'b',
                    name: 'aab',
                    hakutapa: hakutapa.yhteishaku,
                    hakutyyppi: hakutyyppi.varsinainen,
                    applicationOptions: [
                        {
                            canBeApplied: false,
                            applicationStartDate: ts + 15 * 24 * 60 * 60 * 1000 
                        }
                    ]
                }
            ];

            utility.sortApplicationSystems(data);
            expect(data[0].id).toEqual('b');
            expect(data[1].id).toEqual('a');
        });

        it('should sort applications systems correctly', function() {
            var ts = new Date().getTime();
            var data = [
                {
                    id: 'a',
                    name: 'a',
                    hakutapa: hakutapa.yhteishaku,
                    hakutyyppi: hakutyyppi.varsinainen,
                    applicationOptions: [
                        {
                            canBeApplied: false,
                            applicationStartDate: ts + 15 * 24 * 60 * 60 * 1000
                        }
                    ]
                },
                {
                    id: 'b',
                    name: 'b',
                    hakutapa: hakutapa.yhteishaku,
                    hakutyyppi: hakutyyppi.varsinainen,
                    applicationOptions: [
                        {
                            canBeApplied: false,
                            applicationStartDate: ts + 5 * 24 * 60 * 60 * 1000 
                        }
                    ]
                },
                {
                    id: 'c',
                    name: 'c',
                    hakutapa: hakutapa.yhteishaku,
                    hakutyyppi: hakutyyppi.varsinainen,
                    applicationOptions: [
                        {
                            canBeApplied: false,
                            applicationStartDate: ts + 5 * 24 * 60 * 60 * 1000 
                        }
                    ]
                },
                {
                    id: 'd',
                    name: 'd',
                    hakutapa: hakutapa.yhteishaku,
                    hakutyyppi: hakutyyppi.lisa,
                    applicationOptions: [
                        {
                            canBeApplied: true
                        }
                    ]
                },
                {
                    id: 'e',
                    name: 'e',
                    hakutapa: hakutapa.yhteishaku,
                    hakutyyppi: hakutyyppi.varsinainen,
                    applicationOptions: [
                        {
                            canBeApplied: true
                        }
                    ]
                }
            ];

            utility.sortApplicationSystems(data);
            expect(data[0].id).toEqual('e');
            expect(data[1].id).toEqual('d');
            expect(data[2].id).toEqual('b');
            expect(data[3].id).toEqual('c');
            expect(data[4].id).toEqual('a');
        });

        it('should sort application options correctly', function() {
            var data = [
                {
                    id: 'a',
                    hakutapa: hakutapa.yhteishaku,
                    hakutyyppi: hakutyyppi.lisa,
                    applicationOptions: [
                        {
                            name: 'a',
                            applicationStartDate: 15,
                            canBeApplied: false
                        },
                        {
                            name: 'b',
                            canBeApplied: true
                        },
                        {
                            name: 'c',
                            applicationStartDate: 10,
                            canBeApplied: false
                        },
                        {
                            name: 'd',
                            applicationStartDate: 10,
                            canBeApplied: false
                        },
                        {
                            name: 'e',
                            applicationStartDate: 15,
                            canBeApplied: false
                        }
                    ]
                }
            ];

            utility.sortApplicationSystems(data);
            expect(data[0].applicationOptions[0].name).toEqual('b');
            expect(data[0].applicationOptions[1].name).toEqual('c');
            expect(data[0].applicationOptions[2].name).toEqual('d');
            expect(data[0].applicationOptions[3].name).toEqual('a');
            expect(data[0].applicationOptions[4].name).toEqual('e');
        });
    });
});


describe('UtilityService', function() {
    var utility;

    beforeEach(function() {
        module('kiApp', 'kiApp.services');

        inject(function(UtilityService) {
            utility = UtilityService;
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

    describe('groupByApplicationSystems', function() {
        var data = [
            {
                id: '1a',
                applicationOptions: [
                    {id: 'ao_id_1a1'}, 
                    {id: 'ao_id_1a2'}
                ]
            },
            {
                id: '2b',
                applicationOptions: [
                    {id: 'ao_id_2b1'}, 
                    {id: 'ao_id_2b2'}
                ]
            },
            {
                id: '1a',
                applicationOptions: [
                    {id: 'ao_id_1a3'}, 
                    {id: 'ao_id_1a4'}
                ]
            }
        ];

        it('should return empty array for undefined input', function() {
            var input;
            var result = utility.groupByApplicationSystem(input);
            expect(result.length).toEqual(0);
        });

        it('should return empty array for empty input', function() {
            var result = utility.groupByApplicationSystem([]);
            expect(result.length).toEqual(0);
        });

        it('should return the application system array grouped', function() {
            var result = utility.groupByApplicationSystem(data);
            expect(result.length).toEqual(2);
            expect(result[0].id).toEqual('1a');
            expect(result[0].applicationOptions.length).toEqual(4);
        });
    });
});



describe('ApplicationBasketService', function() {
    var service;

    beforeEach(function() {
        module('kiApp', 'kiApp.services');

        inject(function(ApplicationBasketService) {
            service = ApplicationBasketService;
        });
    });

    describe('isEmpty', function() {

        beforeEach(function() {
            service.empty();
        });
        

        it('should return true value for uninitialized basket', function() {
            var result = service.isEmpty();
            expect(result).toBeTruthy();
        });

        it('should successfully add an item with type to an empty basket', function() {
            service.addItem('ao_id_1', 'basket_type_1');
            expect(service.getItemCount()).toEqual(1);
            expect(service.getType()).toEqual('basket_type_1');
        });

        it('should be able to add an item with the same type', function() {
            service.addItem('ao_id_1', 'basket_type_1');
            service.addItem('ao_id_2', 'basket_type_1');
            expect(service.getItemCount()).toEqual(2);
        });

        /*
        it('should not be able to add an item with a different type', function() {
            service.addItem('ao_id_1', 'basket_type_1');
            service.addItem('ao_id_2', 'basket_type_2');
            expect(service.getItemCount()).toEqual(1);
        });
        */

        it('should be able to remove an existing item', function() {
            service.addItem('ao_id_1', 'basket_type_1');
            service.addItem('ao_id_2', 'basket_type_1');
            service.removeItem('ao_id_1');
            expect(service.getItemCount()).toEqual(1);
            expect(service.getItems()[0]).toEqual('ao_id_2');
        });
    });

    describe('itemExists', function() {

        var addData = function() {
            service.addItem('ao_id_1', 'basket_type_1');
            service.addItem('ao_id_2', 'basket_type_1');
            service.addItem('ao_id_3', 'basket_type_1');
            service.addItem('ao_id_4', 'basket_type_1');
        }

        beforeEach(function() {
            service.empty();
        });

        it('should return false for empty basket', function() {
            var result = service.itemExists('ao_id_1');
            expect(result).toBeFalsy();
        });

        it('should return true for existing id', function() {
            addData();
            var result = service.itemExists('ao_id_3');
            expect(result).toBeTruthy();
        });

        it('should return false for non-existing id', function() {
            addData();
            var result = service.itemExists('ao_id_x');
            expect(result).toBeFalsy();
        });

    });
});


describe('HostResolver', function() {
    var service;

    beforeEach(function() {
        module('kiApp', 'kiApp.HostResolver');

        inject(function(HostResolver) {
            service = HostResolver;
        });
    });

    describe('resolve', function() {

        it('should return undefined value for undefined host', function() {
            var host;
            var result = service.resolve(host);
            expect(result).toBeUndefined();
        });

        it('should return undefined value for non-existing host', function() {
            var result = service.resolve('non-existing-host');
            expect(result).toBeUndefined();
        });

        it('should return correct value for existing host', function() {
            var result = service.resolve('testi');
            expect(result).toEqual('qa');
        });
    });

    describe('mapHostToConf', function() {

        it('should return default value (prod) for undefined host', function() {
            var host;
            var result = service.mapHostToConf(host);
            expect(result).toEqual('prod');
        });

        it('should return default value (prod) for non-existing host', function() {
            var result = service.mapHostToConf('non-existing-host');
            expect(result).toEqual('prod');
        });

        it('should return correct value for existing host', function() {
            var result = service.mapHostToConf('localhost');
            expect(result).toEqual('dev');
        });

        it('should return correct value for host "reppu"', function() {
            var result = service.mapHostToConf('reppu');
            expect(result).toEqual('reppu');
        });

    });

    describe('getCookiePrefixByDomain', function() {

        it('should return prefix value for non prod domains', function() {
            var prefix = 'test';

            var result_qa = service.getCookiePrefixByDomain('testi.opintopolku.fi');
            expect(result_qa).toEqual(prefix);

            var result_reppu = service.getCookiePrefixByDomain('test-oph.ware.fi');
            expect(result_reppu).toEqual(prefix);

            var result_koulutus = service.getCookiePrefixByDomain('koulutus.opintopolku.fi');
            expect(result_koulutus).toEqual(prefix);

            var result_sth = service.getCookiePrefixByDomain('opintopolku.fi ');
            expect(result_sth).toEqual(prefix);
        });

        it('should return empty prefix value for prod domains', function() {
            var prefix = '';

            var result_fi = service.getCookiePrefixByDomain('opintopolku.fi');
            expect(result_fi).toEqual(prefix);

            var result_sv = service.getCookiePrefixByDomain('studieinfo.fi');
            expect(result_sv).toEqual(prefix);

            var result_en = service.getCookiePrefixByDomain('studyinfo.fi');
            expect(result_en).toEqual(prefix);
        });

        it('should return empty value for undefined params', function() {
            var result = service.getCookiePrefixByDomain();
            expect(result).toEqual('');

            var result2 = service.getCookiePrefixByDomain(null);
            expect(result2).toEqual('');
        });

        it('should be case insensitive', function() {
            var result = service.getCookiePrefixByDomain('OPINTOPOLKU.FI');
            expect(result).toEqual('');

            var result2 = service.getCookiePrefixByDomain('StudyInfo.fi');
            expect(result2).toEqual('');
        });
    })
});

describe('CookieService', function() {
    var service;

    beforeEach(function() {
        module('kiApp', 'kiApp.CookieService');

        inject(function(CookieService) {
            service = CookieService;
        });
    });

    describe('CookieService set and get', function() {

        it('should set cookie correctly', function() {
            service.set('testcookiekey', 'testcookievalue');
            var result = service.get('testcookiekey');
            expect(result).toEqual('testcookievalue');
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