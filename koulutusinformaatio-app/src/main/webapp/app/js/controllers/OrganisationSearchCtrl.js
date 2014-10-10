function OrganisationSearchCtrl($scope, $rootScope, $location, $routeParams, FilterService, SearchLearningOpportunityService, LanguageService, TranslationService, kiAppConstants) {

    // filter selector collapse state
    $scope.filterSelectorIsCollapsed = false;

    $scope.change = function() {
        FilterService.set({
            locations: $scope.locations,
            page: kiAppConstants.searchResultsStartPage,
            articlePage: kiAppConstants.searchResultsStartPage,
            organisationPage: kiAppConstants.searchResultsStartPage,
            itemsPerPage: $scope.itemsPerPage,
            sortCriteria: $scope.sortCriteria,
            excludes: $scope.excludes,
            organisationFacetFilters: $scope.organisationFacetFilters
        });

        // append filters to url and reload
        $scope.refreshOrganisationView();
    }

    $scope.refreshOrganisationView = function() {
        $location.search(FilterService.get()).replace();
        $scope.initSearch();
    }

    $scope.initSearch = function() {
        var queryParams = $location.search();
        FilterService.query(queryParams)
            .then(function() {
                $scope.locations = FilterService.getLocations();
                $scope.itemsPerPage = FilterService.getItemsPerPage();
                $scope.sortCriteria = FilterService.getSortCriteria();
                $scope.model = {
                    currentOrganisationPage: FilterService.getOrganisationPage()
                };
                $scope.excludes = FilterService.getExcludes();
                $scope.organisationFacetFilters = FilterService.getOrganisationFacetFilters();

                $scope.doOrganisationSearching();
            });
    }

    $scope.doOrganisationSearching = function() {
        var qParams = FilterService.get();
        qParams.tab = 'organisations';
        $location.search(qParams).replace();
        
        SearchLearningOpportunityService.query({
            queryString: $routeParams.queryString,
            start: (FilterService.getOrganisationPage()-1) * $scope.itemsPerPage,
            rows: $scope.itemsPerPage,
            locations: FilterService.getLocationNames(),
            sortCriteria: FilterService.getSortCriteria(),
            lang: LanguageService.getLanguage(),
            excludes : FilterService.getExcludes(),
            organisationFacetFilters: FilterService.getOrganisationFacetFilters(),
            searchType : 'PROVIDER'
        }).then(function(result) {
            /*
            if (result.articleCount == 0 && result.loCount > 0 && !$rootScope.tabChangeable) {
                qParams.tab = 'los';
                $location.search(qParams).replace();
                $route.reload();
                $rootScope.tabChangeable = true;
            } else {
                */
                $scope.loResult = result;
                $scope.totalItems = result.totalCount;
                $scope.loCount = result.loCount;
                $scope.articleCount = result.articleCount;
                $scope.orgCount = result.orgCount;
                $scope.maxPages = Math.ceil(result.articleCount / $scope.itemsPerPage);
                $scope.showPagination = $scope.maxPages > 1;
                $scope.pageMin = ($scope.model.currentOrganisationPage - 1) * $scope.itemsPerPage + 1;
                $scope.pageMax = $scope.model.currentOrganisationPage * $scope.itemsPerPage < $scope.orgCount
                    ? $scope.model.currentOrganisationPage * $scope.itemsPerPage
                            : $scope.orgCount;
                $scope.queryString = $routeParams.queryString;
                $scope.showPagination = $scope.orgCount > $scope.itemsPerPage;
                $scope.tabTitles.learningOpportunities = TranslationService.getTranslation('search-tab-lo') + ' (' + $scope.loCount + ')';
                $scope.tabTitles.articles = TranslationService.getTranslation('search-tab-article') + ' (' + $scope.articleCount + ')';
                $scope.tabTitles.organisations = TranslationService.getTranslation('search-tab-organisation') + ' (' + $scope.orgCount + ')';
                $scope.tabTitles.queryString = $routeParams.queryString;
                $scope.tabTitles.totalCount = $scope.loResult.totalCount;
                $rootScope.tabChangeable = true;
                $scope.populateOrganisationFacetSelections();
            //}
        });
        
    }

    $scope.populateOrganisationFacetSelections = function() {
        $scope.organisationFacetSelections = [];
        $scope.organisationFacetFilters = FilterService.getOrganisationFacetFilters();
        angular.forEach($scope.organisationFacetFilters, function(fFilter, key) {
            var curSelection = {
                                facetField: fFilter.split(':')[0], 
                                valueId: fFilter.split(':')[1],
                                valueName: fFilter.split(':')[1]
                                };
            $scope.organisationFacetSelections.push(curSelection);
        });
        
    };

    $scope.isOrganisationFacetSelected = function(fv) {
        var isSelected = false;
        for (var i = 0; i < $scope.organisationFacetSelections.length; i++) {
            if (($scope.organisationFacetSelections[i].facetField == fv.facetField)
                    && ($scope.organisationFacetSelections[i].valueId == fv.valueId)) {
                isSelected = true;
            }
        }
        return isSelected;
    }
    
    $scope.selectOrganisationFacetFilter = function(selection, facetField) {
        if ($scope.organisationFacetFilters != undefined) {
            $scope.organisationFacetFilters.push(facetField +':'+ selection);
        } else {
            $scope.organisationFacetFilters = [];
            $scope.organisationFacetFilters.push(facetField +':'+selection);
        }

        $scope.change();
    }

    $scope.removeOrganisationFacetSelection = function(facetSelection) {

        var tempFilters = [];
        angular.forEach($scope.organisationFacetFilters, function(value, index) {
            var curVal = value.split(':')[1];
            var curField = value.split(':')[0];
            if ((curField != facetSelection.facetField) 
                    || (curVal != facetSelection.valueId)) {
                tempFilters.push(value);
            }
        });

        $scope.organisationFacetFilters = tempFilters;
        //console.log($scope.organisationFacetFilters);
        $scope.change();
    }

    $scope.changePage = function(page) {
        FilterService.setOrganisationPage(page);
        $scope.doOrganisationSearching();
        $('html, body').scrollTop($('#organisation-results').offset().top); // scroll to top of list
    }

    $scope.toggleCollapsed = function(index) {
        if (!$scope.collapsed) {
            $scope.collapsed = [];
        }

        $scope.collapsed[index] = !$scope.collapsed[index];
    };
   
}