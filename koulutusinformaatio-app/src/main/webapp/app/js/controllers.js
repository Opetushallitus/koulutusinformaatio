/* Controllers */

function LanguageCtrl($scope, $location, LanguageService) {
    $scope.changeLanguage = function(code) {
       LanguageService.setLanguage(code);
       //console.log(i18n);
       i18n.setLng(code);
       document.location.reload(true);
    }
};

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
 function SearchCtrl($scope, $routeParams, SearchLearningOpportunityService, SearchService, TitleService, $location) {
    $scope.queryString = SearchService.getTerm();
    TitleService.setTitle('Hakutulokset');

    if ($routeParams.queryString) {
        SearchLearningOpportunityService.query({queryString: $routeParams.queryString}).then(function(result) {
            $scope.loResult = result;
        });
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
 function InfoCtrl($scope, $routeParams, ParentLearningOpportunityService, ChildLearningOpportunityService, SearchService, ParentLODataService, TitleService) {
    $scope.queryString = SearchService.getTerm();

    var setTitle = function(parent, child) {
        if (child) {
            TitleService.setTitle(child.degreeTitle);
        } else {
            TitleService.setTitle(parent.name);
        }
    };

    // fetch data for parent and/or its child LO
    if ($routeParams) {
        $scope.parentId = $routeParams.parentId;
        if (!ParentLODataService.dataExists($scope.parentId)) {
            ParentLearningOpportunityService.query({parentId: $routeParams.parentId}).then(function(result) {
                $scope.parentLO = result;
                ParentLODataService.setParentLOData(result);
            });
        } else {
            $scope.parentLO = ParentLODataService.getParentLOData();
        }
                
        if ($routeParams.closId && $routeParams.cloiId) {
            ChildLearningOpportunityService.query({parentId: $routeParams.parentId, closId: $routeParams.closId, cloiId: $routeParams.cloiId}).then(function(result) {
                $scope.childLO = result;
                setTitle($scope.parentLO, $scope.childLO);
            }); 
        }
    }

    $scope.scrollToAnchor = function(id) {
        $('html, body').scrollTop($('#' + id).offset().top);
    };

    $scope.initTabs = tabsMenu.build;

    // TODO: remove these after we get some real data (references in templates as well)
    $scope.lorem = 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla posuere, nisl eu gravida elementum, risus risus varius quam, eu rutrum lectus purus quis arcu. Donec euismod porta mi, sed imperdiet ligula sagittis et. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Sed ut felis sit amet ipsum eleifend rhoncus. Donec sed viverra velit. Morbi mollis pellentesque mollis.';
    $scope.loremshort = 'Etiam sit amet urna justo, vitae luctus eros. In hac habitasse platea dictumst. Suspendisse ut ultricies enim. Etiam quis ante massa, sit amet interdum nulla. Donec ultrices velit nec turpis ullamcorper pharetra.';

    // trigger once content is loaded
    $scope.$on('$viewContentLoaded', tabsMenu.build);
};