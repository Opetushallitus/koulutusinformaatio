'use strict';

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