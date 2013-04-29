/* Controllers */

/**
 *  Controller for index view
 */
 function IndexCtrl($scope, TitleService) {
    TitleService.setTitle('Etusivu');

    // launch navigation script
    $scope.initNavigation = function() {
        OPH.Common.initDropdownMenu();
    }
};

/**
 *  Controller for search functionality 
 */
 function SearchCtrl($scope, $routeParams, SearchLearningOpportunity, SearchService, TitleService, $location) {
    $scope.queryString = SearchService.getTerm();
    TitleService.setTitle('Hakutulokset');

    if ($routeParams.queryString) {
        $scope.loResult = SearchLearningOpportunity.query({queryString: $routeParams.queryString});
        $scope.queryString = $routeParams.queryString;
        $scope.showFilters = $scope.queryString ? true : false;
        SearchService.setTerm($routeParams.queryString);
    }

    // Perform search using LearningOpportunity service
    $scope.search = function() {
        if ($scope.queryString) {
            SearchService.setTerm($scope.queryString);
            $location.path('/haku/' + $scope.queryString);
        }
    };

    // Forward to parent learning opportunity info page
    $scope.selectLO = function(parentLOId, LOId) {
        var path = parentLOId ? parentLOId + '/' + LOId : LOId;
        $location.path('/info/' + path);
    };

    // launch navigation script
    $scope.initNavigation = function() {
        OPH.Common.initDropdownMenu();
    };
};

/**
 *  Controller for info views (parent and child)
 */
 function InfoCtrl($scope, $routeParams, ParentLearningOpportunity, SearchService, LODataService, TitleService) {
    $scope.queryString = SearchService.getTerm();

    var setTitle = function(parent, child) {
        if (child) {
            TitleService.setTitle(child.degreeTitle);
        } else {
            TitleService.setTitle(parent.name);
        }
    };

    // fetch data for parent and its children LOs
    if ($routeParams) {
        $scope.parentId = $routeParams.parentId;
        if (!LODataService.dataExists($scope.parentId)) {
            $scope.parentLO = ParentLearningOpportunity.query({parentId: $routeParams.parentId}, function(data) {
                LODataService.setLOData(data);
                $scope.childLO = LODataService.getChildData($routeParams.childId);
                setTitle(data, $scope.childLO);
            });
        } else {
            $scope.parentLO = LODataService.getLOData();
            $scope.childLO = LODataService.getChildData($routeParams.childId);
            setTitle($scope.parentLO, $scope.childLO);
        }
    }

    $scope.scrollToAnchor = function(id) {
        $('body').scrollTop($('#' + id).offset().top);
    };

    $scope.initTabs = tabsMenu.build;

    // TODO: remove these after we get some real data (references in templates as well)
    $scope.lorem = 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla posuere, nisl eu gravida elementum, risus risus varius quam, eu rutrum lectus purus quis arcu. Donec euismod porta mi, sed imperdiet ligula sagittis et. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Sed ut felis sit amet ipsum eleifend rhoncus. Donec sed viverra velit. Morbi mollis pellentesque mollis.';
    $scope.loremshort = 'Etiam sit amet urna justo, vitae luctus eros. In hac habitasse platea dictumst. Suspendisse ut ultricies enim. Etiam quis ante massa, sit amet interdum nulla. Donec ultrices velit nec turpis ullamcorper pharetra.';

    // trigger once content is loaded
    $scope.$on('$viewContentLoaded', tabsMenu.build);
};