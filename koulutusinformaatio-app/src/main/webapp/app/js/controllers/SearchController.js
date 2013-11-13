/**
 *  Controller for search field in header
 */
function SearchFieldCtrl($scope, $location, SearchService, kiAppConstants, FilterService) {
    $scope.searchFieldPlaceholder = i18n.t('search-field-placeholder'); 

    // Perform search using LearningOpportunity service
    $scope.search = function() {
        if ($scope.queryString) {
            FilterService.setPage(kiAppConstants.searchResultsStartPage);
            SearchService.setTerm($scope.queryString);
            var queryString = $scope.queryString;
            
            // empty query string
            $scope.queryString = '';

            // update location
            $location.hash(null);
            $location.path('/haku/' + queryString);
            $location.search(FilterService.get());
        }
    };
};

/**
 *  Controller for search filters
 */
function SearchFilterCtrl($scope, $location, SearchLearningOpportunityService, kiAppConstants, FilterService) {
    var queryParams = $location.search();

    FilterService.query(queryParams).then(function() {
        $scope.prerequisite = FilterService.getPrerequisite();
        $scope.locations = FilterService.getLocations();
        $scope.ongoing = FilterService.isOngoing();
        $scope.$parent.currentPage = FilterService.getPage();
        $scope.facetFilters = FilterService.getFacetFilters();
    });

    $scope.change = function() {
    	console.log("Change called");
        FilterService.set({
            prerequisite: $scope.prerequisite,
            locations: $scope.locations,
            ongoing: $scope.ongoing,
            page: kiAppConstants.searchResultsStartPage,
            facetFilters: $scope.facetFilters
        });

        console.log("Filters set");
        // append filters to url and reload
        $location.search(FilterService.get());
    }
    
    $scope.selectFacetFilter = function(selection, facetField) {
    	console.log("Selected: " + facetField +':'+selection);
    	var facetSelection = {facetField: facetField, selection: selection};
    	if ($scope.facetFilters != undefined) {
    		$scope.facetFilters.push(facetField +':'+selection);
    		$scope.facetSelections.push(facetSelection);
    		console.log($scope.facetFilters);
    	} else {
    		$scope.facetFilters = [];
    		$scope.facetFilters.push(facetField +':'+selection);
    		$scope.facetSelections = [];
    		$scope.facetSelections.push(facetSelection);
    	}
    	$scope.change();
    }
    
    $scope.removeSelection = function(facetSelection) {
    	var tempSels = [];
    	angular.forEach($scope.facetSelections, function(value, index) {
    		if ((value.facetField != facetSelection.facetField) 
    				|| (value.valueId != facetSelection.valueId)) {
    			tempSels.push(value);
    		}
    	});
    	$scope.facetSelections = tempSels;
    	
    	var tempFilters = [];
    	angular.forEach($scope.facetFilters, function(value, index) {
    		var curVal = value.split(':')[1];
    		var curField = value.split(':')[0];
    		if ((curField != facetSelection.facetField) 
    				|| (curVal != facetSelection.valueId)) {
    			tempFilters.push(value);
    		}
    	});
    	$scope.facetFilters = tempFilters;
    	$scope.change();
    }
    
    $scope.isSelected = function(facetValue) {
    	console.log("Checking");
    	var isSelected = false;
    	
    	for (var i = 0; i < $scope.facetSelections.length; i++) {
    		if (($scope.facetSelections[i].facetField == facetValue.facetField)
    				&& ($scope.facetSelections[i].valueId == facetValue.valueId)) {
    			console.log("match");
    			isSelected = true;
    		}
    	}
    	
    	console.log("Returning: " + isSelected);
    	return isSelected;
    }
};

/**
 *  Controller for search functionality 
 */
 function SearchCtrl($scope, $rootScope, $location, $routeParams, SearchLearningOpportunityService, SearchService, kiAppConstants, FilterService, Config, LanguageService) {
    var resultsPerPage = kiAppConstants.searchResultsPerPage;
    
    $rootScope.title = i18n.t('title-search-results') + ' - ' + i18n.t('sitename');

    $scope.changePage = function(page) {
        $scope.currentPage = page;
        $('html, body').scrollTop($('body').offset().top); // scroll to top of list
    };

    $scope.$watch('currentPage', function(value) {
        if (value) {
            FilterService.setPage(value);
            $location.search(FilterService.get());
        }    
    });

    if ($routeParams.queryString) {
    	console.log("Now searching");
        SearchLearningOpportunityService.query({
            queryString: $routeParams.queryString,
            start: (FilterService.getPage()-1) * resultsPerPage,
            rows: resultsPerPage,
            prerequisite: FilterService.getPrerequisite(),
            locations: FilterService.getLocationNames(),
            ongoing: FilterService.isOngoing(),
            facetFilters: FilterService.getFacetFilters(),
            lang: LanguageService.getLanguage()
        }).then(function(result) {
        	console.log(result);
            $scope.loResult = result;
            $scope.maxPages = Math.ceil(result.totalCount / resultsPerPage);
            $scope.showPagination = $scope.maxPages > 1;
            $scope.populateFacetSelections();
        });

        $scope.queryString = $routeParams.queryString;
        //$scope.showFilters = $scope.queryString ? true : false;
        SearchService.setTerm($routeParams.queryString);
        console.log("Searching called");
    }

    $scope.$on('$viewContentLoaded', function() {
        OPH.Common.initHeader();
    });
    
    $scope.populateFacetSelections = function () {
    	$scope.facetSelections = [];
    	$scope.facetFilters = FilterService.getFacetFilters();
    	angular.forEach($scope.facetFilters, function(fFilter, key) {
    		var curVal = fFilter.split(':')[1];
    		console.log("Splitted cur val: " + curVal);
    		angular.forEach($scope.loResult.teachingLangFacet.facetValues, function(fVal, key) {
    			console.log(fVal);
    			console.log("fVal: " + fVal.valueId + ", cur val: " + this);
    			if (this == fVal.valueId) {
    				console.log("Adding fVal: " + fVal.valueId);
    				$scope.facetSelections.push(fVal);
    			}
    		}, curVal);
    	});
    }
};