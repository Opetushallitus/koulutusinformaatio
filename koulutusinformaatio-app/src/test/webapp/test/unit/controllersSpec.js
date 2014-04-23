/*
describe('ApplicationBasket', function() {
    var controller;
    var scope;

    beforeEach(function() {
        module('kiApp', 'ApplicationBasket', 'ngMock');
    });

    describe('Controller', function() {

        var response = [
            {
                applicationSystemId: 'as_id_1',
                applicationOptions: [
                    {
                        id: 'ao_id_1_1'
                    },
                    {
                        id: 'ao_id_1_2'
                    }
                ] 
            },
            {
                applicationSystemId: 'as_id_2',
                applicationOptions: [
                    {
                        id: 'ao_id_2_1'
                    },
                    {
                        id: 'ao_id_2_2'
                    }
                ] 
            }
        ]

        
        beforeEach(inject(function($controller, $httpBackend, $rootScope, ApplicationBasketService, SearchService, FilterService, kiAppConstants, Config) {
            scope = $rootScope.$new();
            ApplicationBasketService.addItem('test');

            // answer yes to confirm dialogs
            spyOn(window, 'confirm').andReturn(true);

            // static response for http query
            $httpBackend.when('GET', /\.\.\/basket\/items(.*)/).respond(200, angular.copy(response));

            controller = $controller('ApplicationBasketCtrl', {
                $scope: scope,
                $rootScope: $rootScope,
                ApplicationBasketService: ApplicationBasketService,
                SearchService: SearchService,
                FilterService: FilterService,
                kiAppConstants: kiAppConstants,
                Config: Config
            });

            $httpBackend.flush();

        }));

        it('should remove given ao item', function() {
            scope.removeItem('ao_id_1_2');
            expect(scope.applicationItems.length).toEqual(2);
            expect(scope.applicationItems[0].applicationOptions.length).toEqual(1);
            expect(scope.applicationItems[0].applicationOptions[0].id).toEqual('ao_id_1_1');
        });

        it('should not change basket content if removing non-existing ao item', function() {
            scope.removeItem('ao_id_3_1');
            expect(scope.applicationItems.length).toEqual(2);
            expect(scope.applicationItems[0].applicationOptions.length).toEqual(2);
            expect(scope.applicationItems[1].applicationOptions.length).toEqual(2);
        });

        it('should remove given as item', function() {
            scope.removeApplicationSystemFromBasket('as_id_2');
            expect(scope.applicationItems.length).toEqual(1);
            expect(scope.applicationItems[0].applicationOptions.length).toEqual(2);
        });

        it('should not change basket content if removing non-existing as item', function() {
            scope.removeApplicationSystemFromBasket('as_id_3');
            expect(scope.applicationItems.length).toEqual(2);
        });
    });
});
*/