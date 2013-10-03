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
            $scope.queryString = '';
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

    FilterService.set(queryParams);
    var filters = FilterService.get();
    $scope.prerequisite = filters.prerequisite;
    $scope.locations = filters.locations;
    $scope.ongoing = filters.ongoing;

    $scope.change = function() {
        FilterService.set({
            prerequisite: $scope.prerequisite,
            locations: $scope.locations,
            ongoing: $scope.ongoing
        });
        FilterService.setPage(kiAppConstants.searchResultsStartPage);

        // append filters to url
        $location.search( FilterService.get() );
    }
};

/**
 *  Controller for search functionality 
 */
 function SearchCtrl($scope, $rootScope, $location, $routeParams, SearchLearningOpportunityService, SearchService, kiAppConstants, FilterService) {
    var resultsPerPage = kiAppConstants.searchResultsPerPage;
    FilterService.setPage($location.search().page);
    
    $rootScope.title = i18n.t('title-search-results') + ' - ' + i18n.t('sitename');

    $scope.changePage = function(page) {
        $scope.currentPage = page;
        $('html, body').scrollTop($('body').offset().top); // scroll to top of list
    };

    $scope.$watch('currentPage', function(value) {
        FilterService.setPage(value);
        $location.search(FilterService.get());
        
    });

    if ($routeParams.queryString) {
            var filters = FilterService.get();
            $scope.currentPage = filters.page;

            SearchLearningOpportunityService.query({
                queryString: $routeParams.queryString,
                start: (filters.page-1) * resultsPerPage,
                rows: resultsPerPage,
                prerequisite: filters.prerequisite,
                locations: filters.locations,
                ongoing: filters.ongoing
            }).then(function(result) {
                $scope.loResult = result;
                $scope.maxPages = Math.ceil(result.totalCount / resultsPerPage);
                $scope.showPagination = $scope.maxPages > 1;
            });

            //$scope.queryString = $routeParams.queryString;
            //$scope.showFilters = $scope.queryString ? true : false;
            SearchService.setTerm($routeParams.queryString);
        } else {
            $scope.loResult = {totalCount : 0};
        }

    $scope.$on('$viewContentLoaded', function() {
        OPH.Common.initHeader();
    });
};