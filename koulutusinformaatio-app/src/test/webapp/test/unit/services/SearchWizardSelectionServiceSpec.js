describe('SearchWizard', function() {
    var utility;

    beforeEach(function() {
        module('kiApp', 'kiApp.SearchWizard');

        inject(function(SearchWizardSelectionsService) {
            utility = SearchWizardSelectionsService;
        });
    });

    describe('SearchWizardSelectionsService', function() {

        it('should add selections correctly', function() {
            utility.addSelection({name: 'item1', code: 'value1'});
            utility.addSelection({name: 'item2', code: 'value2'});
            expect(utility.getSelections().length).toEqual(2);
        });

    });
});