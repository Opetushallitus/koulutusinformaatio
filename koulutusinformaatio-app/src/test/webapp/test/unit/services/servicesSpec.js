describe('UtilityService', function() {
    var utility;

    beforeEach(function() {
        module('kiApp');

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

        it('should sort upcoming "varsinainen yhteishaku" after "käynnissä oleva erillishaku"', function() {
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
                    hakutapa: hakutapa.erillishaku,
                    hakutyyppi: hakutyyppi.varsinainen,
                    applicationOptions: [
                        {
                            canBeApplied: true,
                            
                        }
                    ]
                }
            ];

            utility.sortApplicationSystems(data);
            expect(data[0].id).toEqual('b');
            expect(data[1].id).toEqual('a');
        });

        it('should sort upcoming as before ended as', function() {
            var ts = new Date().getTime();
            var data = [
                {
                    id: 'ended',
                    hakutapa: hakutapa.yhteishaku,
                    hakutyyppi: hakutyyppi.lisahaku,
                    applicationOptions: [
                        {
                            canBeApplied: false,
                            applicationStartDate: ts - 15 * 24 * 60 * 60 * 1000 
                        }
                    ]
                },
                {
                    id: 'upcoming',
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
            expect(data[0].id).toEqual('upcoming');
            expect(data[1].id).toEqual('ended');
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
                            canBeApplied: true,
                            applicationEndDate: ts + 3 * 24 * 60 * 60 * 1000
                        }
                    ]
                },
                ,
                {
                    id: 'b',
                    hakutapa: hakutapa.yhteishaku,
                    hakutyyppi: hakutyyppi.varsinainen,
                    applicationOptions: [
                        {
                            canBeApplied: true,
                            applicationEndDate: ts + 1 * 24 * 60 * 60 * 1000
                        }
                    ]
                },
                {
                    id: 'c',
                    hakutapa: hakutapa.yhteishaku,
                    hakutyyppi: hakutyyppi.varsinainen,
                    applicationOptions: [
                        {
                            canBeApplied: true,
                            applicationEndDate: ts + 2 * 24 * 60 * 60 * 1000
                        }
                    ]
                }
            ];

            utility.sortApplicationSystems(data);
            expect(data[0].id).toEqual('b');
            expect(data[1].id).toEqual('c');
            expect(data[2].id).toEqual('a');
        });

        it('should sort applications systems correctly', function() {
            var ts = new Date().getTime();
            var data = [
                {
                    id: 'a',
                    name: 'Varsinainen yhteishaku päättyy yli kahden viikon päästä, johon ei voi hakea',
                    hakutapa: hakutapa.yhteishaku,
                    hakutyyppi: hakutyyppi.varsinainen,
                    applicationOptions: [
                        {
                            canBeApplied: false,
                            applicationStartDate: ts + 15 * 24 * 60 * 60 * 1000,
                            nextApplicationPeriodStarts: ts + 15 * 24 * 60 * 60 * 1000
                        }
                    ]
                },
                {
                    id: 'b',
                    name: 'Varsinainen yhteishaku päättyy päivän päässä',
                    hakutapa: hakutapa.yhteishaku,
                    hakutyyppi: hakutyyppi.varsinainen,
                    applicationOptions: [
                        {
                            canBeApplied: true,
                            applicationEndDate: ts + 1 * 24 * 60 * 60 * 1000 
                        }
                    ]
                },
                {
                    id: 'c',
                    name: 'Varsinainen yhteishaku päättyy kahden päivän päässä',
                    hakutapa: hakutapa.yhteishaku,
                    hakutyyppi: hakutyyppi.varsinainen,
                    applicationOptions: [
                        {
                            canBeApplied: true,
                            applicationEndDate: ts + 2 * 24 * 60 * 60 * 1000 
                        }
                    ]
                },
                {
                    id: 'd',
                    name: 'Lisäyhteishaku päättyy päivän päässä',
                    hakutapa: hakutapa.yhteishaku,
                    hakutyyppi: hakutyyppi.lisa,
                    applicationOptions: [
                        {
                            canBeApplied: true,
                            applicationEndDate: ts + 1 * 24 * 60 * 60 * 1000
                        }
                    ]
                },
                {
                    id: 'e',
                    name: 'Varsinainen yhteishaku päättyy kolmen päivän päässä',
                    hakutapa: hakutapa.yhteishaku,
                    hakutyyppi: hakutyyppi.varsinainen,
                    applicationOptions: [
                        {
                            canBeApplied: true,
                            applicationEndDate: ts + 3 * 24 * 60 * 60 * 1000 
                        }
                    ]
                },
                {
                    id: 'f',
                    name: 'Varsinainen erillishaku päättyy päivän päässä',
                    hakutapa: hakutapa.erillishaku,
                    hakutyyppi: hakutyyppi.varsinainen,
                    applicationOptions: [
                        {
                            canBeApplied: true,
                            applicationEndDate: ts + 1 * 24 * 60 * 60 * 1000 
                        }
                    ]
                },
                {
                    id: 'g',
                    name: 'Varsinainen erillishaku alkanut 20 päivää sitten, johon ei voi hakea',
                    hakutapa: hakutapa.erillishaku,
                    hakutyyppi: hakutyyppi.varsinainen,
                    applicationOptions: [
                        {
                            canBeApplied: false,
                            applicationStartDate: ts - 20 * 24 * 60 * 60 * 1000
                        }
                    ]
                },
                {
                    id: 'h',
                    name: 'Lisäyhteishaku alkanut 30 päivää sitten, johon ei voi hakea',
                    hakutapa: hakutapa.yhteishaku,
                    hakutyyppi: hakutyyppi.lisa,
                    applicationOptions: [
                        {
                            canBeApplied: false,
                            applicationStartDate: ts - 30 * 24 * 60 * 60 * 1000
                        }
                    ]
                },
                {
                    id: 'i',
                    name: 'Varsinainen yhteishaku, jossa hakukausi alkaa 20 päivän päästä',
                    hakutapa: hakutapa.yhteishaku,
                    hakutyyppi: hakutyyppi.varsinainen,
                    applicationOptions: [
                        {
                            canBeApplied: false,
                            applicationStartDate: ts + 20 * 24 * 60 * 60 * 1000,
                            nextApplicationPeriodStarts: ts + 20 * 24 * 60 * 60 * 1000
                        }
                    ]
                },
                {
                    id: 'j',
                    name: 'Varsinainen yhteishaku, jossa hakukausi alkaa 10 päivän päästä',
                    hakutapa: hakutapa.yhteishaku,
                    hakutyyppi: hakutyyppi.varsinainen,
                    applicationOptions: [
                        {
                            canBeApplied: false,
                            applicationStartDate: ts + 10 * 24 * 60 * 60 * 1000,
                            nextApplicationPeriodStarts: ts + 10 * 24 * 60 * 60 * 1000
                        }
                    ]
                },
                {
                    id: 'k',
                    name: 'Varsinainen erillishaku, jossa haku on päättynyt 10 päivä sitten',
                    hakutapa: hakutapa.erillishaku,
                    hakutyyppi: hakutyyppi.varsinainen,
                    applicationOptions: [
                        {
                            canBeApplied: false,
                            applicationEndDate: ts - 10 * 24 * 60 * 60 * 1000
                        }
                    ]
                }
            ];

            utility.sortApplicationSystems(data);
            jasmine.log(data);
            expect(data[0].id).toEqual('b');  // Varsinainen yhteishaku päättyy päivän päässä
            expect(data[1].id).toEqual('c');  // Varsinainen yhteishaku päättyy kahden päivän päässä
            expect(data[2].id).toEqual('e');  // Varsinainen yhteishaku päättyy kolmen päivän päässä
            expect(data[3].id).toEqual('j');  // Varsinainen yhteishaku, jossa hakukausi alkaa 10 päivän päästä
            expect(data[4].id).toEqual('d');  // Lisäyhteishaku päättyy päivän päässä
            expect(data[5].id).toEqual('f');  // Varsinainen erillishaku päättyy päivän päässä
            expect(data[6].id).toEqual('a');  // Varsinainen yhteishaku päättyy yli kahden viikon päästä, johon ei voi hakea
            expect(data[7].id).toEqual('i');  // Varsinainen yhteishaku, jossa hakukausi alkaa 20 päivän päästä
            expect(data[8].id).toEqual('h');  // Lisäyhteishaku alkanut 30 päivää sitten, johon ei voi hakea
            expect(data[9].id).toEqual('g');  // Varsinainen erillishaku alkanut 20 päivää sitten, johon ei voi hakea
            expect(data[10].id).toEqual('k'); // Varsinainen erillishaku, jossa haku on päättynyt 10 päivä sitten
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

    afterEach(function() {
        service.empty();
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
    });
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
