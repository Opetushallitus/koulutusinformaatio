/* Controllers */

function LanguageCtrl($scope, $location, LanguageService) {
    $scope.changeLanguage = function(code) {
       LanguageService.setLanguage(code);
       i18n.setLng(code);
       document.location.reload(true);
   }
};

/**
 *  Controller for index view
 */
 function IndexCtrl($scope, TitleService) {
    var title = i18n.t('title-front-page');
    TitleService.setTitle(title);

    // launch navigation script
    $scope.initNavigation = function() {
        OPH.Common.initDropdownMenu();
    }
};

function SearchFilterCtrl($scope, $routeParams, SearchLearningOpportunityService) {
    $scope.individualizedActive = $scope.pohjakoulutus != 1;

    $scope.change = function() {
        $scope.individualizedActive = $scope.baseeducation != 1;

        SearchLearningOpportunityService.query({
            queryString: $scope.queryString,
            locations: $scope.locations,
            baseEducation: $scope.baseeducation,
            individualized: $scope.individualized
        }).then(function(result) {
            $scope.loResult = result;
        });
    }
};

function ApplicationBasketCtrl($scope, $routeParams, $location, TitleService, ApplicationBasketService) {
    var title = i18n.t('title-application-basket');
    TitleService.setTitle(title);

    ApplicationBasketService.query().then(function(result) {
        $scope.applicationItems = result;
    });

    $scope.title = i18n.t('title-application-basket-content', {count: 3});

    $scope.removeItem = function(aoId) {
        ApplicationBasketService.removeItem(aoId);

        var items = $scope.applicationItems;

        for (var i = 0; i < items.length; i++) {
            var item = items[i];
            for (var j = 0; j < item.applicationOptions.length; j++) {
                var ao = item.applicationOptions[j];
                if (ao.id == aoId) {
                    item.applicationOptions.splice(j, 1);
                    break;
                }
            }

            if (item.applicationOptions.length <= 0) {
                items.splice(i, 1);
            }
        }
    };

    $scope.gotoParent = function(id) {
        $location.path('/info/' + id);
    };

    $scope.gotoChild = function(parentId, losId, loiId) {
        $location.path('/info/' + parentId + '/' + losId + '/' + loiId);
    }
};

function ApplicationCtrl($scope, $routeParams, ApplicationBasketService) {
    $scope.addToBasket = function(aoId) {
        ApplicationBasketService.addItem(aoId);
    }
};

/**
 *  Controller for search functionality 
 */
 function SearchCtrl($scope, $routeParams, $location, SearchLearningOpportunityService, SearchService, TitleService) {
    $scope.queryString = SearchService.getTerm();

    var title = i18n.t('title-search-results');
    TitleService.setTitle(title);

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
    $scope.selectLO = function(lo) {
        var path;
        if (lo.parentId) {
            path = lo.parentId + '/' + lo.losId + '/' + lo.id;
        } else {
            path = lo.id;
        }
        //var path = parentLOId ? parentLOId + '/' + LOId : LOId;
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
 function InfoCtrl($scope, $routeParams, $location, ParentLearningOpportunityService, ChildLearningOpportunityService, SearchService, ParentLODataService, TitleService) {
    $scope.queryString = SearchService.getTerm();
    $scope.descriptionLanguage = 'fi';

    var setTitle = function(parent, child) {
        if (child) {
            TitleService.setTitle(child.name);
        } else {
            TitleService.setTitle(parent.name);
        }
    };

    var isChild = function() {
        return ($routeParams.closId && $routeParams.cloiId);
    }

    // fetch data for parent and/or its child LO
    if ($routeParams) {
        $scope.parentId = $routeParams.parentId;
        if (!ParentLODataService.dataExists($scope.parentId)) {
            ParentLearningOpportunityService.query({parentId: $routeParams.parentId, language: $scope.descriptionLanguage}).then(function(result) {
                $scope.parentLO = result;
                ParentLODataService.setParentLOData(result);
                setTitle($scope.parentLO, $scope.childLO);
            });
        } else {
            $scope.parentLO = ParentLODataService.getParentLOData();
            setTitle($scope.parentLO, $scope.childLO);
        }

        if (isChild()) {
            ChildLearningOpportunityService.query({parentId: $routeParams.parentId, closId: $routeParams.closId, cloiId: $routeParams.cloiId, language: $scope.descriptionLanguage}).then(function(result) {
                $scope.childLO = result;
                setTitle($scope.parentLO, $scope.childLO);
            }); 
        }
    }

    // change description language and re-load LO data with the specified language
    $scope.changeDescriptionLanguage = function(languageCode) {
        $scope.descriptionLanguage = languageCode;

        // parent data has to be updated every time since child views contain parent data too
        ParentLearningOpportunityService.query({parentId: $routeParams.parentId, language: languageCode}).then(function(result) {
            $scope.parentLO = result;
            if (isChild()) {
                ChildLearningOpportunityService.query({parentId: $routeParams.parentId, closId: $routeParams.closId, cloiId: $routeParams.cloiId, language: $scope.descriptionLanguage}).then(function(result) {
                    $scope.childLO = result;
                    setTitle($scope.parentLO, $scope.childLO);
                });
            } else {
                setTitle($scope.parentLO, $scope.childLO);
            }
        });
    };

    $scope.hasChildren = function() {
        if ($scope.parentLO && $scope.parentLO.children) {
            return $scope.parentLO.children.length > 0;
        } else {
            return false;
        }
    }

    // redirect to child page
    $scope.gotoChild = function(child) {
        $location.path('/info/' + $scope.parentLO.id + '/' + child.losId + '/' + child.loiId);
    }

    // redirect to parent page
    $scope.gotoParent = function() {
        $location.path('/info/' + $scope.parentLO.id);
    }

    // scrolls to an anchor on page
    $scope.scrollToAnchor = function(id) {
        $('html, body').scrollTop($('#' + id).offset().top);
    };

    $scope.initTabs = tabsMenu.build;

    // TODO: remove these after we get some real data (references in templates as well)
    $scope.lorem;// = 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla posuere, nisl eu gravida elementum, risus risus varius quam, eu rutrum lectus purus quis arcu. Donec euismod porta mi, sed imperdiet ligula sagittis et. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Sed ut felis sit amet ipsum eleifend rhoncus. Donec sed viverra velit. Morbi mollis pellentesque mollis.';
    $scope.loremshort;// = 'Etiam sit amet urna justo, vitae luctus eros. In hac habitasse platea dictumst. Suspendisse ut ultricies enim. Etiam quis ante massa, sit amet interdum nulla. Donec ultrices velit nec turpis ullamcorper pharetra.';

    
    /* TODO: tabs accessible vie url?
    var tabIdFromRoute = $routeParams.tabId;
    var initkitabs = function() {
        tabsMenu.build(tabIdFromRoute);
    }
    */

    // trigger once content is loaded
    $scope.$on('$viewContentLoaded', tabsMenu.build);
};