describe('SearchWizard', function() {
    var utility;

    beforeEach(function() {
        module('kiApp', 'SearchWizard');

        inject(function(SearchWizardSelectionsService) {
            utility = SearchWizardSelectionsService;
        });
    });

    describe('SearchWizardSelectionsService', function() {

        it('should not add empty items', function() {
            utility.addSelection();
            utility.addSelection('item1');
            expect(utility.getSelections().length).toEqual(0);
        });

        it('should add selections correctly', function() {
            utility.addSelection('item1', 'value1');
            utility.addSelection('item2', 'value2');
            expect(utility.getSelections().length).toEqual(2);
        });

        it('should override overlapping selections correctly', function() {
            utility.addSelection('item1', 'value1');
            utility.addSelection('item1', 'value2');
            expect(utility.getSelections().length).toEqual(1);
            expect(utility.getSelectionValueByKey('item1')).toEqual('value2');
        });

    });
});