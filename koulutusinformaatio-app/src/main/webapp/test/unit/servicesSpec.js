'use strict';

describe('SearchService', function() {

    beforeEach(module('kiApp'));

    var SearchService_;

    beforeEach(inject(function(SearchService) {
        SearchService_ = SearchService;
    }));

    it('should save the search term', function() {
        var searchterm = 'musiikki';
        SearchService_.setTerm(searchterm);

        expect(SearchService_.getTerm()).toMatch(searchterm);
    });
});

describe('TitleService', function() {

    beforeEach(module('kiApp'));

    var TitleService_;

    beforeEach(inject(function(TitleService) {
        TitleService_ = TitleService;
    }));

    it('should save the given title set', function() {
        var title = 'Etusivu';
        TitleService_.setTitle(title);

        expect(TitleService_.getTitle()).toMatch(title + ' - Opintopolku.fi');
    });
});

describe('ParentLODataService', function() {
    var parentId = '123456';
    var childId = parentId + '_2';

    beforeEach(module('kiApp'));

    var LODataService_;

    beforeEach(inject(function(ParentLODataService) {
        LODataService_ = ParentLODataService;
    }));

    it('should have no data set', function() {
        expect(LODataService_.dataExists(parentId)).toBeFalsy();
    });

    it('should have parent data set', function() {
        LODataService_.setParentLOData({"id": parentId, "name": "parent name"});
        expect(LODataService_.dataExists(parentId)).toBeTruthy();
        expect(LODataService_.getParentLOData(parentId).id).toEqual(parentId);
    });

    it('should have parent and child data set', function() {
        LODataService_.setParentLOData({"id": parentId, "name": "parent name", children: [{"id": "123456_1"}, {"id": childId}]});
        expect(LODataService_.dataExists(parentId)).toBeTruthy();
        expect(LODataService_.getParentLOData(parentId).id).toEqual(parentId);
    });


});