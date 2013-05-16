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

    // fetch data for parent and/or its child LO
    if ($routeParams) {
        $scope.parentId = $routeParams.parentId;
        if (!ParentLODataService.dataExists($scope.parentId)) {
            ParentLearningOpportunityService.query({parentId: $routeParams.parentId, language: $scope.descriptionLanguage}).then(function(result) {
                //var translationLanguageIndex = result.availableTranslationLanguages.indexOf(result.translationLanguage);
                //result.availableTranslationLanguages.splice(translationLanguageIndex, 1);
                $scope.parentLO = result;
                ParentLODataService.setParentLOData(result);
                setTitle($scope.parentLO, $scope.childLO);
            });
        } else {
            $scope.parentLO = ParentLODataService.getParentLOData();
            setTitle($scope.parentLO, $scope.childLO);
        }

        if ($routeParams.closId && $routeParams.cloiId) {
            ChildLearningOpportunityService.query({parentId: $routeParams.parentId, closId: $routeParams.closId, cloiId: $routeParams.cloiId, language: $scope.descriptionLanguage}).then(function(result) {
                //var translationLanguageIndex = result.availableTranslationLanguages.indexOf(result.translationLanguage);
                //result.availableTranslationLanguages.splice(translationLanguageIndex, 1);
                $scope.childLO = result;
                //var startDate = new Date(result.startDate);
                //$scope.childLO.startDate = startDate.getDate() + '.' + (startDate.getMonth() + 1) + '.' + startDate.getFullYear();
                //$scope.childLO.teachingLanguage = result.teachingLanguages[0] ? result.teachingLanguages[0] : '';
                //$scope.childLO.formOfEducation = result.formOfEducation[0] ? result.formOfEducation[0] : '';

                setTitle($scope.parentLO, $scope.childLO);
            }); 
        }
    }

    $scope.changeDescriptionLanguage = function(languageCode) {
        $scope.descriptionLanguage = languageCode;

        ParentLearningOpportunityService.query({parentId: $scope.parentId, language: languageCode}).then(function(result) {
            var translationLanguageIndex = result.availableTranslationLanguages.indexOf(result.translationLanguage);
            result.availableTranslationLanguages.splice(translationLanguageIndex, 1);
            $scope.parentLO = result;
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