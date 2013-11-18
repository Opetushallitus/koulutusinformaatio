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
function SearchFilterCtrl($scope, $location, SearchLearningOpportunityService, kiAppConstants, FilterService, LanguageService) {
    var queryParams = $location.search();

    FilterService.query(queryParams).then(function() {
        $scope.prerequisite = FilterService.getPrerequisite();
        $scope.locations = FilterService.getLocations();
        $scope.ongoing = FilterService.isOngoing();
        $scope.upcoming = FilterService.isUpcoming();
        $scope.$parent.currentPage = FilterService.getPage();
        $scope.facetFilters = FilterService.getFacetFilters();
        $scope.langCleared=FilterService.getLangCleared();
    });

    $scope.change = function() {
        FilterService.set({
            prerequisite: $scope.prerequisite,
            locations: $scope.locations,
            ongoing: $scope.ongoing,
            upcoming: $scope.upcoming,
            page: kiAppConstants.searchResultsStartPage,
            facetFilters: $scope.facetFilters,
            langCleared: $scope.langCleared
        });

        // append filters to url and reload
        $location.search(FilterService.get());
    }
    
    /*
     * Selecting a facet value for filtering results
     */
    $scope.selectFacetFilter = function(selection, facetField) {
    	var facetSelection = {facetField: facetField, selection: selection};
    	if ($scope.facetFilters != undefined) {
    		$scope.facetFilters.push(facetField +':'+selection);
    		$scope.facetSelections.push(facetSelection);
    	} else {
    		$scope.facetFilters = [];
    		$scope.facetFilters.push(facetField +':'+selection);
    		$scope.facetSelections = [];
    		$scope.facetSelections.push(facetSelection);
    	}
    	$scope.change();
    }
    
    /*
     * Removing a facet selection to broaden search.
     */
    $scope.removeSelection = function(facetSelection) {
    	if ($scope.isDefaultTeachLang(facetSelection)) {
    		$scope.langCleared = true;
    	} 
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
    
    //Is the facet selection a selection of finish teaching language
    $scope.isDefaultTeachLang = function(facetSelection) {
    	return (facetSelection.facetField == 'teachingLangCode_ffm') 
    			&& (facetSelection.valueId == $scope.resolveDefLang());
    }
    
    /*
     * Is a given facet value selected
     */
    $scope.isSelected = function(facetValue) {
    	var isSelected = false;
    	for (var i = 0; i < $scope.facetSelections.length; i++) {
    		if (($scope.facetSelections[i].facetField == facetValue.facetField)
    				&& ($scope.facetSelections[i].valueId == facetValue.valueId)) {
    			isSelected = true;
    		}
    	}
    	return isSelected;
    }
    
    //Are there selections to show in the facet selections area
    $scope.areThereSelections = function() {
    	 $scope.locations = FilterService.getLocations();
    	 return (($scope.facetSelections != undefined) && ($scope.facetSelections.length > 0))
    	 		|| (($scope.locations != undefined) &&  ($scope.locations.length > 0))
    	 		|| $scope.ongoing
    	 		|| $scope.upcoming;
    }
   
    //Removing a location from the facet selections area
    $scope.removeLocation = function(loc) {
    	$scope.locations.splice($scope.locations.indexOf(loc), 1);
        $scope.change();
    }
    
    $scope.setOngoing = function() {
    	$scope.ongoing = true;
    	$scope.change();
    }
    
    $scope.removeOngoing = function() {
    	$scope.ongoing = false;
    	$scope.change();
    }
    
    $scope.setUpcoming = function() {
    	$scope.upcoming = true;
    	$scope.change();
    }
    
    $scope.removeUpcoming = function() {
    	$scope.upcoming = false;
    	$scope.change();
    }
};

/**
 *  Controller for search functionality 
 */
 function SearchCtrl($scope, $rootScope, $location, $routeParams, SearchLearningOpportunityService, SearchService, kiAppConstants, FilterService, Config, LanguageService) {
	 var queryParams = $location.search();

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
    

    
    //Getting the query params from the url
    //after which searching is done.
	FilterService.query(queryParams).then(function() {
	     $scope.prerequisite = FilterService.getPrerequisite();
	     $scope.locations = FilterService.getLocations();
	     $scope.ongoing = FilterService.isOngoing();
	     $scope.upcoming = FilterService.isUpcoming();
	     $scope.facetFilters = FilterService.getFacetFilters();
	     $scope.langCleared=FilterService.getLangCleared();
	     $scope.doSearching();
	 });
    
	//Returns true if the language filter is set
	//i.e. either a teaching language filter or langCleared (language is explicitely cleared by the user)
    $scope.isLangFilterSet = function() {
    	
    	if ($scope.langCleared) {
    		return true;
    	}

    	if ($scope.facetFilters != undefined) {
    		for (var i = 0; i < $scope.facetFilters.length; ++i) {
    			if ($scope.facetFilters[i].indexOf("teachingLangCode_ffm") > -1) {
    				return true;
    			}
    		}
    	}
    	
    	return false;
    }

    //Searching solr
    $scope.doSearching = function() {

    	//If the language filter is set, the search query is made 
    	if ($routeParams.queryString && $scope.isLangFilterSet()) {
    		SearchLearningOpportunityService.query({
    			queryString: $routeParams.queryString,
    			start: (FilterService.getPage()-1) * resultsPerPage,
    			rows: resultsPerPage,
    			prerequisite: FilterService.getPrerequisite(),
    			locations: FilterService.getLocationNames(),
    			ongoing: FilterService.isOngoing(),
    			upcoming: FilterService.isUpcoming(),
    			facetFilters: FilterService.getFacetFilters(),
    			lang: LanguageService.getLanguage()
    		}).then(function(result) {
    			$scope.loResult = result;
                $scope.totalItems = result.totalCount;
    			$scope.maxPages = Math.ceil(result.totalCount / resultsPerPage);
                $scope.itemsPerPage = resultsPerPage;
    			$scope.showPagination = $scope.maxPages > 1;
    			$scope.populateFacetSelections();
    		});

    		$scope.queryString = $routeParams.queryString;
    		SearchService.setTerm($routeParams.queryString);
    		
    		//If the language filter is not set, it is added to the url, and then page is refreshed
    		//which will result in the search being made
    	} else if ($routeParams.queryString && !$scope.isLangFilterSet()) {
    		var queryParams = $location.search();
    		var facetFiltersArr = [];
    		//The existing facet filters are preserved
    		if ((queryParams.facetFilters != undefined) && ((typeof queryParams.facetFilters == 'string') 
    				|| (queryParams.facetFilters instanceof String))) {
    			var newFilters = [];
    			newFilters.push(queryParams.facetFilters);
    			newFilters.push('teachingLangCode_ffm:' + $scope.resolveDefLang());
    			facetFiltersArr = newFilters;
    		} else if (queryParams.facetFilters != undefined) {
    			queryParams.facetFilters.push('teachingLangCode_ffm:' + $scope.resolveDefLang());
    			facetFiltersArr = queryParams.facetFilters;
    		} else {
    			facetFiltersArr.push('teachingLangCode_ffm:' + $scope.resolveDefLang());
    		}

    		FilterService.set({
    			prerequisite: $scope.prerequisite,
    			locations: $scope.locations,
    			ongoing: $scope.ongoing,
    			upcoming: $scope.upcoming,
    			page: kiAppConstants.searchResultsStartPage,
    			facetFilters: facetFiltersArr
    		});

    		$location.search(FilterService.get());
    	}
    }

    
    $scope.$on('$viewContentLoaded', function() {
        OPH.Common.initHeader();
    });
    
    /*
     * Populating the facet selections (shown in the UI). Based on
     * facet filters in the url.
     */
    $scope.populateFacetSelections = function () {
    	$scope.facetSelections = [];
    	$scope.facetFilters = FilterService.getFacetFilters();
    	angular.forEach($scope.facetFilters, function(fFilter, key) {
    		var curVal = fFilter.split(':')[1];
    		var selLength = $scope.facetSelections.length;
    		angular.forEach($scope.loResult.teachingLangFacet.facetValues, function(fVal, key) {
    			if (this == fVal.valueId) {
    				$scope.facetSelections.push(fVal);
    			}
    		}, curVal);
    		if (selLength == $scope.facetSelections.length) {
    			angular.forEach($scope.loResult.filterFacet.facetValues, function(fVal, key) {
        			if (this == fVal.valueId) {
        				$scope.facetSelections.push(fVal);
        			}
        		}, curVal);
    		} 
    	});
    	
    	angular.forEach($scope.loResult.appStatusFacet.facetValues, function(fVal, key) {
    		if (fVal.valueId == 'ongoing') {
    			$scope.loResult.ongoingFacet = fVal;
    		} else if (fVal.valueId == 'upcoming') {
    			$scope.loResult.upcomingFacet = fVal;
    		}
    	});
    	
    }
    

    $scope.resolveDefLang = function() {
    	if (LanguageService.getLanguage() == 'sv' || LanguageService.getLanguage() == 'SV') {
    		return 'SV';
    	}
    	return 'FI';
    }
};