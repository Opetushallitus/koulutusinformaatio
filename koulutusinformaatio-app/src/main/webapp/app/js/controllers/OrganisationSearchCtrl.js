function OrganisationSearchCtrl($scope, $rootScope, $location, $location, $route, $routeParams, FilterService, SearchLearningOpportunityService, LanguageService, TranslationService, kiAppConstants) {

    // filter selector collapse state
    $scope.filterSelectorIsCollapsed = false;

    $scope.change = function() {
        FilterService.set({
            prerequisite: $scope.prerequisite,
            locations: $scope.locations,
            ongoing: $scope.ongoing,
            upcoming: $scope.upcoming,
            upcomingLater: $scope.upcomingLater,
            page: kiAppConstants.searchResultsStartPage,
            articlePage: kiAppConstants.searchResultsStartPage,
            organisationPage: kiAppConstants.searchResultsStartPage,
            facetFilters: $scope.facetFilters,
            langCleared: $scope.langCleared,
            itemsPerPage: $scope.itemsPerPage,
            sortCriteria: $scope.sortCriteria,
            lopFilter: $scope.lopFilter,
            educationCodeFilter: $scope.educationCodeFilter,
            excludes: $scope.excludes,
            organisationFacetFilters: $scope.organisationFacetFilters,
            articleFacetFilters: $scope.articleFacetFilters
        });

        // append filters to url and reload
        $scope.refreshOrganisationView();
    }

    $scope.refreshOrganisationView = function() {
        $location.search(FilterService.get()).replace();
        $scope.initSearch();
    }

    $scope.resolveFacetFilters = function() {
        var filters = FilterService.getFacetFilters();
        if (filters == undefined) {
            filters = [];
        }
        return filters;
    }

    $scope.initSearch = function() {
        var queryParams = $location.search();
        FilterService.query(queryParams)
            .then(function() {
                $scope.prerequisite = FilterService.getPrerequisite();
                $scope.locations = FilterService.getLocations();
                $scope.ongoing = FilterService.isOngoing();
                $scope.upcoming = FilterService.isUpcoming(),
                $scope.upcomingLater = FilterService.isUpcomingLater(),
                $scope.facetFilters = FilterService.getFacetFilters();
                $scope.langCleared = FilterService.getLangCleared();
                $scope.itemsPerPage = FilterService.getItemsPerPage();
                $scope.sortCriteria = FilterService.getSortCriteria();
                $scope.model = {
                    currentOrganisationPage: FilterService.getOrganisationPage()
                };
                $scope.lopFilter = FilterService.getLopFilter();
                $scope.educationCodeFilter = FilterService.getEducationCodeFilter();
                $scope.excludes = FilterService.getExcludes();
                $scope.articleFacetFilters = FilterService.getArticleFacetFilters();
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
            organisationFacetFilters: FilterService.getOrganisationFacetFilters(),
            searchType : 'PROVIDER'
        }).then(function(result) {
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
        });
        
    }

    $scope.populateOrganisationFacetSelections = function() {
        
        $scope.organisationFacetSelections = [];
        $scope.organisationFacetFilters = FilterService.getOrganisationFacetFilters();
        angular.forEach($scope.organisationFacetFilters, function(fFilter, key) {
            var curVal = fFilter.split(':')[1];

            angular.forEach($scope.loResult.providerTypeFacet.facetValues, function(facet) {
                if (facet.valueId === curVal) {
                    $scope.organisationFacetSelections.push(facet);
                }
            })
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

    //Are there selections to show in the facet selections area
    $scope.areThereSelections = function() {
         var locations = FilterService.getLocations();
         return (($scope.organisationFacetSelections != undefined) && ($scope.organisationFacetSelections.length > 0))
                || ((locations != undefined) &&  (locations.length > 0));
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

    //Removing a location from the facet selections area
    $scope.removeLocation = function(loc) {
        $scope.locations.splice($scope.locations.indexOf(loc), 1);
        $scope.change();
    }

    $scope.showLosForProvider = function(providerName) {
        $location.url('/haku/*?page=1&lopFilter=' + providerName + '&itemsPerPage=25&sortCriteria=0&tab=los');
        $route.reload();
    }
   
}