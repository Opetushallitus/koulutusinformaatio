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
function SearchFilterCtrl($scope, $location, SearchLearningOpportunityService, kiAppConstants, FilterService, LanguageService, DistrictService, ChildLocationsService, UtilityService) {

    $scope.change = function() {
        FilterService.set({
            prerequisite: $scope.prerequisite,
            locations: $scope.locations,
            ongoing: $scope.ongoing,
            upcoming: $scope.upcoming,
            page: kiAppConstants.searchResultsStartPage,
            facetFilters: $scope.facetFilters,
            langCleared: $scope.langCleared,
            itemsPerPage: $scope.itemsPerPage,
            sortCriteria: $scope.sortCriteria
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
    
    $scope.openAreaDialog = function() {
    	DistrictService.query().then(function(result) {
    		$scope.distResult = result;
    		$scope.distResult.unshift({name: i18n.t('koko') + ' ' + i18n.t('suomi'), code: '-1'});
    	});
    }
    
    $scope.doMunicipalitySearch = function() {
    	var queryDistricts = [];
    	if ($scope.muniResult != undefined) {
    		$scope.muniResult.length = 0;
    	} else {
    		$scope.muniResult = [];
    	}
    	if ($scope.isWholeAreaSelected($scope.selectedDistricts)) {
    		queryDistricts = $scope.distResult;
    	} else {
    		queryDistricts = $scope.selectedDistricts;
    	}
    	ChildLocationsService.query(queryDistricts).then(function(result) {
    		
    		if (!$scope.isWholeAreaSelected($scope.selectedDistricts)) {
                UtilityService.sortLocationsByName(result);
    			$scope.muniResult.push.apply($scope.muniResult, queryDistricts);
    			$scope.muniResult.push.apply($scope.muniResult, result);
    		} else {
    			$scope.muniResult.push.apply($scope.muniResult, result);
    		}
    		
    	});
    }
    
    $scope.filterBySelLocations = function() {
        if (!$scope.locations) {
            $scope.locations = [];
        }

        angular.forEach($scope.selectedMunicipalities, function(location, locationkey) {
            if (location && $scope.locations.indexOf(location) < 0) {
                $scope.locations.push(location);
            }
        });
        
    	$scope.change();
    }
    
    $scope.isWholeAreaSelected = function(areaArray) {
    	for (var i = 0; i < areaArray.length; i++) {
    		if (areaArray[i].code == '-1') {
    			return true;
    		}
    	}
    	return false;
    }

    $scope.toggleCollapsed = function(index) {
        if (!$scope.collapsed) {
            $scope.collapsed = [];
        }

        $scope.collapsed[index] = !$scope.collapsed[index];
    }
    
    $scope.isEdTypeSelected = function(facetValue) {
    	if ($scope.facetSelections == undefined || facetValue == undefined) {
    		return false;
    	}
    	var isSelected = false;
    	for (var i = 0; i < $scope.facetSelections.length; i++) {
    		if (($scope.facetSelections[i].facetField == facetValue.facetField)
    				&& ($scope.facetSelections[i].valueId == facetValue.valueId)) {
    			isSelected = true;
    		}
    	}
    	return isSelected;
    }

    $scope.selectMunicipality = function() {
        if (!$scope.selectedMunicipalities) {
            $scope.selectedMunicipalities = [];
        }

        angular.forEach($scope.selectedMunicipality, function(mun, munkey){
            
            var found = false;
            angular.forEach($scope.selectedMunicipalities, function(value, key){
                if (value.code == mun.code) {
                    found = true;
                }
            });

            if (!found) {
                $scope.selectedMunicipalities.push(mun);
            }

        });
    }

    $scope.removeMunicipality = function(code) {
        angular.forEach($scope.selectedMunicipalities, function(mun, key) {
            if (code == mun.code) {
                $scope.selectedMunicipalities.splice(key, 1);
            }
        });
    }
};

/**
 *  Controller for search functionality 
 */
 function SearchCtrl($scope, $rootScope, $location, $routeParams, SearchLearningOpportunityService, SearchService, kiAppConstants, FilterService, Config, LanguageService) {
	var queryParams = $location.search();
	$scope.selectAreaVisible = false;
    $rootScope.title = i18n.t('title-search-results') + ' - ' + i18n.t('sitename');

    $scope.pageSizes = [25, 50, 100];

    $scope.sortCriterias = [
        {value: i18n.t('sort-criteria-default')}, 
        {value: i18n.t('sort-criteria-alphabetical-desc')}, 
        {value: i18n.t('sort-criteria-alphabetical-asc')},
        {value: i18n.t('sort-criteria-duration-asc'), group: i18n.t('sort-criteria-duration-group')},
        {value: i18n.t('sort-criteria-duration-desc'), group: i18n.t('sort-criteria-duration-group')}
        
    ];

    $scope.paginationNext = i18n.t('pagination-next');
    $scope.paginationPrevious = i18n.t('pagination-previous');
    $scope.valitseAlueTitle = i18n.t('valitse-alue');
    $scope.noSearchResults = i18n.t('no-search-results-info', {searchterm: SearchService.getTerm()});

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
	     $scope.langCleared = FilterService.getLangCleared();
         $scope.itemsPerPage = FilterService.getItemsPerPage();
         $scope.sortCriteria = FilterService.getSortCriteria();
         $scope.currentPage = FilterService.getPage();
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
    			start: (FilterService.getPage()-1) * $scope.itemsPerPage,
    			rows: $scope.itemsPerPage,
    			prerequisite: FilterService.getPrerequisite(),
    			locations: FilterService.getLocationNames(),
    			ongoing: FilterService.isOngoing(),
    			upcoming: FilterService.isUpcoming(),
    			facetFilters: FilterService.getFacetFilters(),
                sortCriteria: FilterService.getSortCriteria(),
    			lang: LanguageService.getLanguage()
    		}).then(function(result) {
    			console.log(result);
    			$scope.loResult = result;
                $scope.totalItems = result.totalCount;
    			$scope.maxPages = Math.ceil(result.totalCount / $scope.itemsPerPage);
    			$scope.showPagination = $scope.maxPages > 1;
                $scope.pageMin = ($scope.currentPage - 1) * $scope.itemsPerPage + 1;
                $scope.pageMax = $scope.currentPage * $scope.itemsPerPage < $scope.totalItems
                    ? $scope.currentPage * $scope.itemsPerPage
                    : $scope.totalItems;
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
    			angular.forEach($scope.loResult.prerequisiteFacet.facetValues, function(fVal, key) {
        			if (this == fVal.valueId) {
        				$scope.facetSelections.push(fVal);
        			}
        		}, curVal);
    		}
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
    	
    	angular.forEach($scope.loResult.edTypeFacet.facetValues, function(fVal, key) {
    		$scope.loResult.edTypeFacet[fVal.valueId] = fVal;
    	});
    	
    }
    

    $scope.resolveDefLang = function() {
    	if (LanguageService.getLanguage() == 'sv' || LanguageService.getLanguage() == 'SV') {
    		return 'SV';
    	}
    	return 'FI';
    }
};

function SortCtrl($scope, $location, FilterService) {
    $scope.updateItemsPerPage = function() {
        FilterService.setItemsPerPage($scope.itemsPerPage);
        $location.search(FilterService.get());
    }

    $scope.updateSortCriteria = function() {
        FilterService.setSortCriteria($scope.sortCriteria);
        $location.search(FilterService.get());
    }
};