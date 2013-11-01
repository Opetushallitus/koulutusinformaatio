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
    });

    $scope.change = function() {
        FilterService.set({
            prerequisite: $scope.prerequisite,
            locations: $scope.locations,
            ongoing: $scope.ongoing,
            page: kiAppConstants.searchResultsStartPage
        });

        // append filters to url and reload
        $location.search(FilterService.get());
    }
};

/**
 *  Controller for search functionality 
 */
 function SearchCtrl($scope, $rootScope, $location, $routeParams, SearchLearningOpportunityService, SearchService, kiAppConstants, FilterService, Config) {
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
        SearchLearningOpportunityService.query({
            queryString: $routeParams.queryString,
            start: (FilterService.getPage()-1) * resultsPerPage,
            rows: resultsPerPage,
            prerequisite: FilterService.getPrerequisite(),
            locations: FilterService.getLocationNames(),
            ongoing: FilterService.isOngoing()
        }).then(function(result) {
            $scope.loResult = result;
            $scope.maxPages = Math.ceil(result.totalCount / resultsPerPage);
            $scope.showPagination = $scope.maxPages > 1;
        });

        $scope.queryString = $routeParams.queryString;
        //$scope.showFilters = $scope.queryString ? true : false;
        SearchService.setTerm($routeParams.queryString);
    }

    $scope.$on('$viewContentLoaded', function() {
        OPH.Common.initHeader();
    });
};