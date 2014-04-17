/**
 *  Controller for search field in header
 */
function SearchFieldCtrl($scope, $location, $route, $rootScope, SearchService, kiAppConstants, FilterService, AutocompleteService, TreeService, TranslationService) {
    $scope.searchFieldPlaceholder = TranslationService.getTranslation('search-field-placeholder'); 
    $scope.suggestions = [];
    
    $scope.locales = {
        'search': TranslationService.getTranslation('tooltip:search')
    }


    $scope.$watch('queryString', function() {
    	if ($scope.queryString != undefined && $scope.queryString.length > 0) {
    	AutocompleteService.query($scope.queryString).then(function(result) {
    		$scope.suggestions.length = 0;
    		if (result.keywords != undefined && result.keywords.length > 0) {
                $scope.suggestions.push({value: TranslationService.getTranslation('autocomplete-keyword'), group: true});
    			for (var i = 0; i < result.keywords.length; i++) {
    				$scope.suggestions.push({value: result.keywords[i]});
    			}
    		} 
    		
    		if (result.loNames != undefined && result.loNames.length > 0) {
                $scope.suggestions.push({value: TranslationService.getTranslation('autocomplete-loname'), group: true});
    			for (var i = 0; i < result.loNames.length; i++) {
    				$scope.suggestions.push({value: result.loNames[i]});
    			}
    		}
    	});
    	}
    }, true);
    
    // Perform search using LearningOpportunity service
    $scope.search = function() {
        if ($scope.queryString) {
            var activeTab = $location.search().tab;
            FilterService.clear(); // clear all filters for new search
            TreeService.clear(); // clear tree selections
            FilterService.setPage(kiAppConstants.searchResultsStartPage);
            FilterService.setArticlePage(kiAppConstants.searchResultsStartPage);
            SearchService.setTerm($scope.queryString);
            var queryString = $scope.queryString;
            
            // empty query string
            $scope.queryString = '';
            
            $rootScope.tabChangeable = false;

            // update location
            var filters = FilterService.get();
            filters.tab = activeTab;
            $location.hash(null);
            $location.path('/haku/' + queryString);
            $location.search(filters);
        }
    };
};

/**
 *  Controller for search filters
 */
function SearchFilterCtrl($scope, $location, SearchLearningOpportunityService, kiAppConstants, FilterService, LanguageService, DistrictService, ChildLocationsService, UtilityService, TranslationService, $modal, _) {

    $scope.change = function() {
        FilterService.set({
            prerequisite: $scope.prerequisite,
            locations: $scope.locations,
            ongoing: $scope.ongoing,
            upcoming: $scope.upcoming,
            page: kiAppConstants.searchResultsStartPage,
            articlePage: kiAppConstants.searchResultsStartPage,
            facetFilters: $scope.facetFilters,
            langCleared: $scope.langCleared,
            itemsPerPage: $scope.itemsPerPage,
            sortCriteria: $scope.sortCriteria,
            lopFilter: $scope.lopFilter,
            educationCodeFilter: $scope.educationCodeFilter,
            excludes: $scope.excludes
        });
        
        if ($scope.lopFilter != undefined) {
        	$scope.lopRecommendation = true;
        } else {
        	$scope.lopRecommendation = false;
        }
        
        if ($scope.educationCodeFilter != undefined) {
        	$scope.educationCodeRecommendation = true;
        } else {
        	$scope.educationCodeRecommendation = false;
        }

        // append filters to url and reload
        $scope.refreshView();
    }

    /*
     * localizations
     */
    $scope.locales = {
        'closeFacet': TranslationService.getTranslation('tooltip:remove-search-result-facet'),
        'openCloseFacet': TranslationService.getTranslation('tooltip:close-facet'),
        'locationDialog': TranslationService.getTranslation('tooltip:location-dialog'),
        'removeFacet': TranslationService.getTranslation('tooltip:remove-facet'),
        'resultsToShow': TranslationService.getTranslation('tooltip:choose-results-to-show'),
        'resultsCriteria': TranslationService.getTranslation('tooltip:choose-result-criteria'),
        'tip': TranslationService.getTranslation('tooltip:tip'),
        'searchResultFacetInfo': TranslationService.getTranslation('tooltip:search-result-facet-info')
    }

    $scope.tipPopoverContent = "<p style='width:400px'>" + $scope.locales.searchResultFacetInfo + "</p>";
    
    /*
     * Selecting a facet value for filtering results
     */
    $scope.selectFacetFilter = function(selection, facetField) {
    	var facetSelection = {facetField: facetField, selection: selection};
    	if ($scope.facetFilters != undefined) {
    		
    		var tempFilters = [];
    		
    		angular.forEach($scope.facetFilters, function(value, index) {
    			var curField = value.split(':')[0];
    			if ((facetField == 'theme_ffm' || facetField == 'topic_ffm')
    					&& curField != 'theme_ffm' && curField != 'topic_ffm') {
    				tempFilters.push(value);
    			}
    			else if (!(facetField == 'theme_ffm' || facetField == 'topic_ffm') && curField != facetField) {
    				tempFilters.push(value);
    			}
    		});
    		
    		$scope.facetFilters = tempFilters;
    		
    		$scope.facetFilters.push(facetField +':'+selection);
    	} else {
    		$scope.facetFilters = [];
    		$scope.facetFilters.push(facetField +':'+selection);
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
    
    $scope.removeLopRecommendation = function() {
    	$scope.lopFilter = undefined;
    	$scope.change();
    }
    
    $scope.removeEducationCodeRecommendation = function() {
    	$scope.educationCodeFilter = undefined;
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
    	 var locations = FilterService.getLocations();
    	 return (($scope.facetSelections != undefined) && ($scope.facetSelections.length > 0))
    	 		|| ((locations != undefined) &&  (locations.length > 0))
    	 		|| $scope.ongoing
    	 		|| $scope.upcoming
    	 		|| $scope.lopRecommendation
    	 		|| $scope.educationCodeRecommendation;
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
    		$scope.distResult.unshift({name: TranslationService.getTranslation('koko') + ' ' + TranslationService.getTranslation('suomi'), code: '-1'});
    	});
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

    $scope.setFilteredLocations = function(value) {
        _.each(value, function(location) {
            if (!$scope.locations) {
                $scope.locations = [location];
            } else if (_.where($scope.locations, {code: location.code}).length <= 0) {
                $scope.locations.push(location);
            }
        });
    }
};

/**
 *  Controller for search functionality 
 */
function SearchCtrl($scope, $rootScope, $location, $window, $routeParams, $route, SearchLearningOpportunityService, SearchService, kiAppConstants, FilterService, Config, LanguageService, TranslationService, $timeout, SearchResultFacetTransformer) {
    var queryParams;
    $scope.selectAreaVisible = false;
    $rootScope.title = TranslationService.getTranslation('title-search-results') + ' - ' + TranslationService.getTranslation('sitename');
    $rootScope.description = $rootScope.title;

    $scope.pageSizes = [25, 50, 100];

    $scope.sortCriterias = [
        {value: TranslationService.getTranslation('sort-criteria-default')}, 
        {value: TranslationService.getTranslation('sort-criteria-alphabetical-desc')}, 
        {value: TranslationService.getTranslation('sort-criteria-alphabetical-asc')}
    ];

    $scope.tabTitles = {
        learningOpportunities: TranslationService.getTranslation('search-tab-lo'),
        learningOpportunitiesTooltip: TranslationService.getTranslation('tooltip:search-tab-lo-tooltip'),
        articles: TranslationService.getTranslation('search-tab-article'),
        articlesTooltip: TranslationService.getTranslation('tooltip:search-tab-article-tooltip')
    };

    /*
    $scope.titleLocales = {
        close: TranslationService.getTranslation('tooltip:close'),
        removeFacet: TranslationService.getTranslation('tooltip:remove-facet')
    }
    */

    $scope.tabs = [
        {active: false},
        {active: false}
    ];

    $scope.paginationNext = TranslationService.getTranslation('pagination-next');
    $scope.paginationPrevious = TranslationService.getTranslation('pagination-previous');
    $scope.valitseAlueTitle = TranslationService.getTranslation('valitse-alue');
    $scope.noSearchResults = TranslationService.getTranslation('no-search-results-info', {searchterm: $routeParams.queryString ? $routeParams.queryString: ''});




    $scope.changePage = function(page) {
        $scope.currentPage = page;
        FilterService.setPage(page);
        $scope.refreshView();
        $('html, body').scrollTop($('body').offset().top); // scroll to top of list
    };

    $scope.refreshView = function() {
        $location.search(FilterService.get()).replace();
        $scope.initSearch();
    }

    $scope.initTabs = function() {
        var qParams = $location.search();
        if (qParams.tab && qParams.tab == 'articles') {
            $scope.tabs[1].active = true;
        } else {
            $scope.tabs[0].active = true;
        }
        $timeout(function(){
            var searcTablist = null;
            searcTablist = new tabpanel('search-tablist', false);
        },0);

    }
    
    //Getting the query params from the url
    //after which searching is done.
    $scope.initSearch = function() {
        var queryParams = $location.search();
    	FilterService.query(queryParams)
            .then(function() {
                $scope.prerequisite = FilterService.getPrerequisite();
                $scope.locations = FilterService.getLocations();
                $scope.ongoing = FilterService.isOngoing();
                $scope.upcoming = FilterService.isUpcoming();
                $scope.facetFilters = FilterService.getFacetFilters();
                $scope.langCleared = FilterService.getLangCleared();
                $scope.itemsPerPage = FilterService.getItemsPerPage();
                $scope.sortCriteria = FilterService.getSortCriteria();
                $scope.currentPage = FilterService.getPage();
                $scope.lopFilter = FilterService.getLopFilter();
                $scope.educationCodeFilter = FilterService.getEducationCodeFilter();
                $scope.excludes = FilterService.getExcludes();
                
                if ($scope.lopFilter != undefined) {
                	$scope.lopRecommendation = true;
                } else {
                	$scope.lopRecommendation = false;
                }
                
                if ($scope.educationCodeFilter != undefined) {
                	$scope.educationCodeRecommendation = true;
                } else {
                	$scope.educationCodeRecommendation = false;
                }
                
                $scope.doSearching();
            });
    }
    $scope.initTabs();

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
        var qParams = FilterService.get();
        qParams.tab = 'los';
        $location.search(qParams).replace();
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
    			lang: LanguageService.getLanguage(),
    			lopFilter: FilterService.getLopFilter(),
    		    educationCodeFilter: FilterService.getEducationCodeFilter(),
    		    excludes : FilterService.getExcludes(),
    		    searchType : 'LO'
    		}).then(function(result) {
    			
    			if (result.loCount == 0 && result.articleCount > 0 && !$rootScope.tabChangeable) {
    				qParams.tab = 'articles';
    				$location.search(qParams).replace();
    				$route.reload();
    				$rootScope.tabChangeable = true;
    			} else {
    			
    				$scope.loResult = result;
    				$scope.loResult.queryString = $routeParams.queryString;
    				$scope.totalItems = result.totalCount;
    				$scope.loCount = result.loCount;
    				$scope.articleCount = result.articleCount;
    				$scope.maxPages = Math.ceil(result.loCount / $scope.itemsPerPage);
    				$scope.showPagination = $scope.maxPages > 1;
    				$scope.pageMin = ($scope.currentPage - 1) * $scope.itemsPerPage + 1;
    				$scope.pageMax = $scope.currentPage * $scope.itemsPerPage < $scope.loCount
                    	? $scope.currentPage * $scope.itemsPerPage
                    			: $scope.loCount;
    				$scope.populateFacetSelections();
    				
    				$scope.tabTitles.learningOpportunities = TranslationService.getTranslation('search-tab-lo') + ' (' + $scope.loCount + ')';
    	            $scope.tabTitles.articles = TranslationService.getTranslation('search-tab-article') + ' (' + $scope.articleCount + ')';
    	            if (FilterService.getLopFilter() != undefined && FilterService.getLopFilter() != null) {
    	            	$scope.tabTitles.queryString = FilterService.getLopFilter();
    	            } else {
    	            	$scope.tabTitles.queryString = $routeParams.queryString;
    	            }
    	            $scope.tabTitles.totalCount = $scope.loResult.totalCount;
    	            $rootScope.tabChangeable = true;
    	            $scope.loResult = SearchResultFacetTransformer.transform($scope.loResult, $scope.facetFilters);//$scope.convertLoResult($scope.loResult);
    	            
    	            
    			}
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
    			facetFilters: facetFiltersArr.join()
    		});

    		$scope.refreshView();
    	} else if (!$routeParams.queryString || $routeParams.queryString == '') {
            $scope.loResult = {};
            $scope.loResult.totalCount = 0;
            $scope.loResult.loCount = 0;
            $scope.loResult.articleCount = 0;
        }
    }

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
    	
    }
    

    $scope.resolveDefLang = function() {
    	if (LanguageService.getLanguage() == 'sv' || LanguageService.getLanguage() == 'SV') {
    		return 'SV';
    	}
    	return 'FI';
    }
};

function ArticleSearchCtrl($scope, $rootScope, $route, $location, $routeParams, ArticleContentSearchService, FilterService, SearchLearningOpportunityService, LanguageService, TranslationService) {
    $scope.currentPage = 1;
    $scope.showPagination = false;

    $scope.changePage = function(page) {
        $scope.currentArticlePage = page;
        $scope.currentPage = page;
        FilterService.setArticlePage(page);
        $scope.doArticleSearching();
        $('html, body').scrollTop($('body').offset().top); // scroll to top of list
    }
    
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
    
    $scope.resolveFacetFilters = function() {
    	var filters = FilterService.getFacetFilters();
    	if (filters == undefined) {
    		filters = [];
    	}
    	if (!$scope.isLangFilterSet()) {
    		filters.push('teachingLangCode_ffm:' + LanguageService.getLanguage().toUpperCase());
    	}
    	return filters;
    }

    //Getting the query params from the url
    //after which searching is done.
    $scope.initSearch = function() {
        var queryParams = $location.search();
        FilterService.query(queryParams)
            .then(function() {
                $scope.prerequisite = FilterService.getPrerequisite();
                $scope.locations = FilterService.getLocations();
                $scope.ongoing = FilterService.isOngoing();
                $scope.upcoming = FilterService.isUpcoming(),
                $scope.langCleared = FilterService.getLangCleared();
                $scope.itemsPerPage = FilterService.getItemsPerPage();
                $scope.sortCriteria = FilterService.getSortCriteria();
                $scope.currentArticlePage = FilterService.getArticlePage();
                $scope.facetFilters = FilterService.getFacetFilters();
                $scope.lopFilter = FilterService.getLopFilter();
                $scope.educationCodeFilter = FilterService.getEducationCodeFilter();
                $scope.excludes = FilterService.getExcludes();

                $scope.doArticleSearching();
            });
    }
 
    $scope.doArticleSearching = function() {
        var qParams = FilterService.get();
        qParams.tab = 'articles';
        $location.search(qParams).replace();
        
        SearchLearningOpportunityService.query({
			queryString: $routeParams.queryString,
			start: (FilterService.getArticlePage()-1) * $scope.itemsPerPage,
			rows: $scope.itemsPerPage,
			prerequisite: FilterService.getPrerequisite(),
			locations: FilterService.getLocationNames(),
			ongoing: FilterService.isOngoing(),
			upcoming: FilterService.isUpcoming(),
			facetFilters: $scope.resolveFacetFilters(),
            sortCriteria: FilterService.getSortCriteria(),
			lang: LanguageService.getLanguage(),
			lopFilter: FilterService.getLopFilter(),
		    educationCodeFilter: FilterService.getEducationCodeFilter(),
		    excludes : FilterService.getExcludes(),
		    searchType : 'ARTICLE'
		}).then(function(result) {
			
			if (result.articleCount == 0 && result.loCount > 0 && !$rootScope.tabChangeable) {
				qParams.tab = 'los';
				$location.search(qParams).replace();
				$route.reload();
				$rootScope.tabChangeable = true;
			} else {
				$scope.loResult = result;
				$scope.totalItems = result.totalCount;
				$scope.loCount = result.loCount;
				$scope.articleCount = result.articleCount;
				$scope.maxPages = Math.ceil(result.articleCount / $scope.itemsPerPage);
				$scope.showPagination = $scope.maxPages > 1;
				$scope.pageMin = ($scope.currentPage - 1) * $scope.itemsPerPage + 1;
				$scope.pageMax = $scope.currentPage * $scope.itemsPerPage < $scope.articleCount
                	? $scope.currentPage * $scope.itemsPerPage
                			: $scope.articleCount;
				$scope.queryString = $routeParams.queryString;
				$scope.showPagination = $scope.articleCount > $scope.itemsPerPage;
				$scope.tabTitles.learningOpportunities = TranslationService.getTranslation('search-tab-lo') + ' (' + $scope.loCount + ')';
				$scope.tabTitles.articles = TranslationService.getTranslation('search-tab-article') + ' (' + $scope.articleCount + ')';
				$scope.tabTitles.queryString = $routeParams.queryString;
				$scope.tabTitles.totalCount = $scope.loResult.totalCount;
				$rootScope.tabChangeable = true;
			}
		});
        
    }
    
    $scope.refreshArticleView = function() {
        $location.search(FilterService.get()).replace();
        $scope.initSearch();
    }

};


function SortCtrl($scope, $location, FilterService) {
    $scope.updateItemsPerPage = function(tab) {
        FilterService.setItemsPerPage($scope.itemsPerPage);
        if (tab == 'los') {
        	$scope.refreshView();
        } else if (tab == 'article') {
        	$scope.refreshArticleView();
        }
    }

    $scope.updateSortCriteria = function(tab) {
        FilterService.setSortCriteria($scope.sortCriteria);
        if (tab == 'los') {
        	$scope.refreshView();
        } else if (tab == 'article') {
        	$scope.refreshArticleView();
        }
    }
};

