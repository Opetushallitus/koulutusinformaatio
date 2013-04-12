'use strict';

describe('SearchCtrl', function(){
  var ctrl, scope;

  beforeEach(module('kiApp'));

  beforeEach(inject(function($httpBackend, $rootScope, $controller, LearningOpportunity){
  	$httpBackend.expectGET('../lo/search?queryString=xyz').respond([]);
  	scope = $rootScope.$new();
  	ctrl = $controller(SearchCtrl, {$scope: scope, $routeParams: {queryString: 'xyz'}, LearningOpportunity: LearningOpportunity});
    //searchCtrl = new SearchCtrl();
  }));


  it('should do something', function() {
    expect(scope.queryString).toEqual('xyz');
  });
});
