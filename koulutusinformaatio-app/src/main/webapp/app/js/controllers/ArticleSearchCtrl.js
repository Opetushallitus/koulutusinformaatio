function ArticleSearchCtrl($scope, $rootScope, $route, $location, $routeParams, FilterService, SearchLearningOpportunityService, LanguageService, kiAppConstants, TranslationService) {
    
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
            excludes: $scope.excludes,
            articleFacetFilters: $scope.articleFacetFilters
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
        $scope.refreshArticleView();
    }

    $scope.showPagination = false;
    
    // filter selector collapse state
    $scope.filterSelectorIsCollapsed = false;

    $scope.changePage = function(page) {
        FilterService.setArticlePage(page);
        $scope.doArticleSearching();
        $('html, body').scrollTop($('#article-results').offset().top); // scroll to top of list
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
                $scope.upcomingLater = FilterService.isUpcomingLater(),
                $scope.langCleared = FilterService.getLangCleared();
                $scope.itemsPerPage = FilterService.getItemsPerPage();
                $scope.sortCriteria = FilterService.getSortCriteria();
                $scope.model = {
                    currentArticlePage: FilterService.getArticlePage()
                };
                $scope.facetFilters = FilterService.getFacetFilters();
                $scope.lopFilter = FilterService.getLopFilter();
                $scope.educationCodeFilter = FilterService.getEducationCodeFilter();
                $scope.excludes = FilterService.getExcludes();
                $scope.articleFacetFilters = FilterService.getArticleFacetFilters();

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
            upcomingLater: FilterService.isUpcomingLater(),
            facetFilters: $scope.resolveFacetFilters(),
            sortCriteria: FilterService.getSortCriteria(),
            lang: LanguageService.getLanguage(),
            lopFilter: FilterService.getLopFilter(),
            educationCodeFilter: FilterService.getEducationCodeFilter(),
            excludes : FilterService.getExcludes(),
            articleFacetFilters : FilterService.getArticleFacetFilters(),
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
                $scope.orgCount = result.orgCount;
                $scope.maxPages = Math.ceil(result.articleCount / $scope.itemsPerPage);
                $scope.showPagination = $scope.maxPages > 1;
                $scope.pageMin = ($scope.model.currentArticlePage - 1) * $scope.itemsPerPage + 1;
                $scope.pageMax = $scope.model.currentArticlePage * $scope.itemsPerPage < $scope.articleCount
                    ? $scope.model.currentArticlePage * $scope.itemsPerPage
                            : $scope.articleCount;
                $scope.queryString = $routeParams.queryString;
                $scope.showPagination = $scope.articleCount > $scope.itemsPerPage;
                $scope.tabTitles.learningOpportunities = TranslationService.getTranslation('search-tab-lo') + ' (' + $scope.loCount + ')';
                $scope.tabTitles.articles = TranslationService.getTranslation('search-tab-article') + ' (' + $scope.articleCount + ')';
                $scope.tabTitles.organisations = TranslationService.getTranslation('search-tab-organisation') + ' (' + $scope.orgCount + ')';
                $scope.tabTitles.queryString = $routeParams.queryString;
                $scope.tabTitles.totalCount = $scope.loResult.totalCount;
                $rootScope.tabChangeable = true;
                $scope.populateArticleFacetSelections();
            }
        });
        
    }
    
    $scope.refreshArticleView = function() {
        $location.search(FilterService.get()).replace();
        $scope.initSearch();
    }
    
    $scope.isArticleFacetSelected = function(fv) {
        var isSelected = false;
        for (var i = 0; i < $scope.articleFacetSelections.length; i++) {
            if (($scope.articleFacetSelections[i].facetField == fv.facetField)
                    && ($scope.articleFacetSelections[i].valueId == fv.valueId)) {
                isSelected = true;
            }
        }
        return isSelected;
    }
    
    $scope.selectArticleFacetFilter = function(selection, facetField) {
        if ($scope.articleFacetFilters != undefined) {
            $scope.articleFacetFilters.push(facetField +':'+ selection);
        } else {
            $scope.articleFacetFilters = [];
            $scope.articleFacetFilters.push(facetField +':'+selection);
        }

        $scope.change();
    }

    $scope.removeArticleFacetSelection = function(facetSelection) {

        var tempFilters = [];
        angular.forEach($scope.articleFacetFilters, function(value, index) {
            var curVal = value.split(':')[1];
            var curField = value.split(':')[0];
            if ((curField != facetSelection.facetField) 
                    || (curVal != facetSelection.valueId)) {
                tempFilters.push(value);
            }
        });

        $scope.articleFacetFilters = tempFilters;
        $scope.change();
    }
    
    $scope.areThereArticleFacetSelections = function() {
        return (($scope.articleFacetSelections != undefined) && ($scope.articleFacetSelections.length > 0));
    }
    
    $scope.populateArticleFacetSelections = function() {
        $scope.articleFacetSelections = [];
        $scope.articleFacetFilters = FilterService.getArticleFacetFilters();
        angular.forEach($scope.articleFacetFilters, function(fFilter, key) {
            var curSelection = {
                                facetField: fFilter.split(':')[0], 
                                valueId: fFilter.split(':')[1],
                                valueName: fFilter.split(':')[1]
                                };
            $scope.articleFacetSelections.push(curSelection);
        });
        
    };

    $scope.toggleCollapsed = function(index) {
        if (!$scope.collapsed) {
            $scope.collapsed = [];
        }

        $scope.collapsed[index] = !$scope.collapsed[index];
    };
};
