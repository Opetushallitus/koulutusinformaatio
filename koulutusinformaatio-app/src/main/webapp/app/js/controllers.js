/* Controllers */

function LanguageCtrl($scope, $location, LanguageService) {
    $scope.changeLanguage = function(code) {
       LanguageService.setLanguage(code);
       i18n.setLng(code);
       document.location.reload(true);
   }
};

function HeaderCtrl($scope, $location, ApplicationBasketService) {
    $scope.appBasketItemCount = function() {
        return ApplicationBasketService.getItemCount();
    }
};

/**
 *  Controller for index view
 */
 function IndexCtrl($scope, TitleService) {
    var title = i18n.t('title-front-page');
    TitleService.setTitle(title);

    // launch navigation script
    /*
    $scope.initNavigation = function() {
        OPH.Common.initDropdownMenu();
    }
    */

    /*
    $scope.$on('$viewContentLoaded', function() {
        OPH.Common.initHeader();
    });
    */
};

/**
 *  Controller for search filters
 */
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

/**
 *  Controller for application basket
 */
function ApplicationBasketCtrl($scope, $routeParams, $location, TitleService, ApplicationBasketService, SearchService, kiAppConstants) {
    var title = i18n.t('title-application-basket');
    //TitleService.setTitle(title);

    var basketLimit = kiAppConstants.applicationBasketLimit; // TODO: get this from application data?

    $scope.queryString = SearchService.getTerm();
    $scope.notificationText = i18n.t('application-basket-fill-form-notification', {count: basketLimit});
    $scope.basketIsEmpty = ApplicationBasketService.isEmpty();

    if (!$scope.basketIsEmpty) {
        ApplicationBasketService.query().then(function(result) {
            $scope.applicationItems = result;
            TitleService.setTitle(title);
        });
    }

    $scope.title = i18n.t('title-application-basket-content');
    $scope.itemCount = ApplicationBasketService.getItemCount();

    
    var applicationSystemIsActive = function(asId) {
        var items = $scope.applicationItems;

        for (var i = 0; i < items.length; i++) {
            var item = items[i];

            if (item.applicationSystemId == asId && item.applicationDates) {
                var start = item.applicationDates.startDate;
                var end = item.applicationDates.endDate;
                var current = new Date().getTime();

                return (current >= start && current <= end);
            }
        }

        return false;
    };

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

        $scope.itemCount = ApplicationBasketService.getItemCount();
        $scope.basketIsEmpty = ApplicationBasketService.isEmpty();
    };

    $scope.emptyApplicationBasket = function() {
        var areyousure = confirm(i18n.t('application-basket-empty-confirm'));
        if (areyousure) {
            ApplicationBasketService.empty();
            $scope.applicationItems = [];
            $scope.basketIsEmpty = true;
            $scope.itemCount = ApplicationBasketService.getItemCount();
        }
    };

    $scope.applyButtonIsDisabled = function(asId) {
        var itemsInBasket = ApplicationBasketService.getItemCount();
        if (itemsInBasket > basketLimit || !applicationSystemIsActive(asId)) {
            return true;
        } else {
            return false;
        }
    }

    $scope.rowClass = function(isLast) {
        return isLast ? 'last' : '';
    }

    $scope.$on('$viewContentLoaded', function() {
        OPH.Common.initHeader();
    });
};

/**
 *  Controller for adding applications to application basket
 */
function ApplicationCtrl($scope, $routeParams, ApplicationBasketService, UtilityService) {
    //$scope.buttonsAreDisabled = $scope.applicationOptionId && $scope.applicationOptionName ? false : true;

    $scope.addToBasket = function() {
        var basketType = ApplicationBasketService.getType();
        if (!basketType || $scope.selectedAo.prerequisite == basketType) {
            ApplicationBasketService.addItem($scope.selectedAo.id, $scope.selectedAo.prerequisite);
            $scope.popoverTitle = i18n.t('popover-title-success');
            $scope.popoverContent = "<a href='#/muistilista'>" + i18n.t('popover-content-link-to-application-basket') + "</a>";
        } else {
            $scope.popoverTitle = i18n.t('popover-title-error');
            $scope.popoverContent = "<div>" + i18n.t('popover-content-error') + "</div><a href='#/muistilista'>" + i18n.t('popover-content-link-to-application-basket') + "</a>";
        }
    };

    $scope.changeValue = function(aoId, aoName, aoSora, aoTeachLang) {
        var ao = UtilityService.getApplicationOptionById(aoId, $scope.parentLO.applicationOptions);
        $scope.selectedAo = {
            name: ao.name,
            id: ao.id,
            sora: ao.sora,
            teachLang: ao.teachLang,
            prerequisite: ao.prerequisite
        };
    };

    $scope.subtabClass = function(isFirst) {
        return isFirst ? 'tab current' : 'tab';
    };

    $scope.subtabContentStyle = function(isFirst) {
        return isFirst ? {'display': 'block'} : {}; 
    };

    $scope.applicationSystemIsActive = function() {
        if ($scope.parentLO && $scope.parentLO.applicationSystem && $scope.parentLO.applicationSystem.applicationDates) {
            var start = $scope.parentLO.applicationSystem.applicationDates.startDate;
            var end = $scope.parentLO.applicationSystem.applicationDates.endDate;
            var current = new Date().getTime();

            return (current >= start && current <= end);
        }

        return false;
    };

    $scope.popoverTitle = i18n.t('popover-title');
    $scope.popoverContent = "<a href='#/muistilista'>" + i18n.t('popover-content') + "</a>";
};

/**
 *  Controller for search functionality 
 */
 function SearchCtrl($scope, $routeParams, $location, SearchLearningOpportunityService, SearchService, TitleService, kiAppConstants) {
    $scope.queryString = SearchService.getTerm();
    var resultsPerPage = kiAppConstants.searchResultsPerPage;
    $scope.currentPage = kiAppConstants.searchResultsStartPage;

    var title = i18n.t('title-search-results');
    TitleService.setTitle(title);

    // Perform search using LearningOpportunity service
    $scope.search = function() {
        if ($scope.queryString) {
            SearchService.setTerm($scope.queryString);
            $location.path('/haku/' + $scope.queryString);
        }
    };

    $scope.changePage = function(page) {
        $scope.currentPage = page;
        $('html, body').scrollTop($('#search-results').offset().top); // scroll to top of list
    };

    $scope.$watch('currentPage', function(value) {
        if ($routeParams.queryString) {
            SearchLearningOpportunityService.query({
                queryString: $routeParams.queryString,
                start: (value-1) * resultsPerPage,
                rows: resultsPerPage}).then(function(result) {
                    $scope.loResult = result;
                    $scope.maxPages = Math.ceil(result.totalCount / resultsPerPage);
                    $scope.showPagination = $scope.maxPages > 1;
            });

            $scope.queryString = $routeParams.queryString;
            $scope.showFilters = $scope.queryString ? true : false;
            SearchService.setTerm($routeParams.queryString);
        }
    });

    $scope.$on('$viewContentLoaded', function() {
        OPH.Common.initHeader();
    });
};

/**
 *  Controller for info views (parent and child)
 */
 function InfoCtrl($scope, $routeParams, $location, ParentLearningOpportunityService, ChildLearningOpportunityService, SearchService, ParentLODataService, TitleService, LearningOpportunityProviderPictureService, UtilityService) {
    $scope.queryString = SearchService.getTerm();
    $scope.descriptionLanguage = 'fi';

    // how to avoid this?
    $scope.selectedTab = 'kuvaus';
    $scope.providerAsideClass = 'hidden';
    $scope.applyFormClass = '';

    var setTitle = function(parent, child) {
        if (child) {
            TitleService.setTitle(child.name);
        } else {
            TitleService.setTitle(parent.name);
        }
    };

    var isChild = function() {
        return ($routeParams.childId);
    };

    var getApplicationSystemId = function(aos) {
        if (hasApplicationOptions()) {
            var ao = $scope.parentLO.applicationOptions[0];
            if (ao && ao.applicationSystem) {
                return ao.applicationSystem.id;
            }
        }
    };

    var getFirstApplicationOption = function() {
        if (hasApplicationOptions()) {
            return $scope.parentLO.applicationOptions[0];
        }
    };

    var getFirstParentLOI = function() {
        if (hasParentLOIs()) {
            return $scope.parentLO.lois[0];
        }
    }

    var hasApplicationOptions = function() {
        if ($scope.parentLO && $scope.parentLO.applicationOptions) {
            return $scope.parentLO.applicationOptions.length > 0;
        } else {
            return false;
        } 
    };

    var hasParentLOIs = function() {
        if ($scope.parentLO && $scope.parentLO.lois) {
            return $scope.parentLO.lois.length > 0;
        } else {
            return false;
        }
    }

    var showApplicationRadioSelection = function() {
        if (hasApplicationOptions()) {
            return $scope.parentLO.applicationOptions.length == 1 ? false : true;
        }

        return true;
    }

    var initializeParent = function() {
        setTitle($scope.parentLO, $scope.childLO);
        $scope.hasApplicationOptions = hasApplicationOptions();
        $scope.showApplicationRadioSelection = showApplicationRadioSelection() ? '' : 'hidden';

        $scope.asId = getApplicationSystemId();

        // select first ao in list
        var firstAoInList = getFirstApplicationOption();
        if (firstAoInList) {
            $scope.selectedAo = {
                name: firstAoInList.name,
                id: firstAoInList.id,
                sora: firstAoInList.sora,
                teachLang: firstAoInList.teachLang,
                prerequisite: firstAoInList.prerequisite
            };
        }

        var firstParentLOIInList = getFirstParentLOI();
        if (firstParentLOIInList) {
            $scope.selectedParentLOI = firstParentLOIInList;
        }
    };

    $scope.$watch('parentLO.provider', function(data) {
        if (data) {
            LearningOpportunityProviderPictureService.query({providerId: data.id}).then(function(result) {
                $scope.providerImage = result;
            });
        }
    });

    // fetch data for parent and/or its child LO
    /*
    if ($routeParams) {
        $scope.parentId = $routeParams.parentId;
        if (!ParentLODataService.dataExists($scope.parentId)) {
            ParentLearningOpportunityService.query({
                parentId: $routeParams.parentId, 
                language: $scope.descriptionLanguage}).then(function(result) {
                    $scope.parentLO = result;
                    ParentLODataService.setParentLOData(result);
                    initializeParent();
                });
        } else {
            $scope.parentLO = ParentLODataService.getParentLOData();
            initializeParent();
        }

        if (isChild()) {
            ChildLearningOpportunityService.query({
                parentId: $routeParams.parentId, 
                childId: $routeParams.childId,
                //cloiId: $routeParams.cloiId, 
                language: $scope.descriptionLanguage}).then(function(result) {
                    $scope.childLO = result;
                    setTitle($scope.parentLO, $scope.childLO);
                }); 
        }
    }
    */

    if (isChild()) {
        ChildLearningOpportunityService.query({
            //parentId: $routeParams.parentId, 
            childId: $routeParams.childId,
            language: $scope.descriptionLanguage}).then(function(childResult) {
                $scope.childLO = childResult;

                if (!ParentLODataService.dataExists(childResult.parent.id)) {
                    ParentLearningOpportunityService.query({
                        parentId: childResult.parent.id, 
                        language: $scope.descriptionLanguage}).then(function(parentResult) {
                            $scope.parentLO = parentResult;
                            ParentLODataService.setParentLOData(parentResult);
                            initializeParent();
                            setTitle($scope.parentLO, $scope.childLO);
                        });
                } else {
                    $scope.parentLO = ParentLODataService.getParentLOData();
                    initializeParent();
                    setTitle($scope.parentLO, $scope.childLO);
                }
            }); 
    } else {
        if (!ParentLODataService.dataExists($routeParams.parentId)) {
            ParentLearningOpportunityService.query({
                parentId: $routeParams.parentId, 
                language: $scope.descriptionLanguage}).then(function(result) {
                    $scope.parentLO = result;
                    ParentLODataService.setParentLOData(result);
                    initializeParent();
                });
        } else {
            $scope.parentLO = ParentLODataService.getParentLOData();
            initializeParent();
        }
    }

    // change description language and re-load LO data with the specified language
    $scope.changeDescriptionLanguage = function(languageCode) {
        $scope.descriptionLanguage = languageCode;

        // parent data has to be updated every time since child views contain parent data too
        ParentLearningOpportunityService.query({
            parentId: $routeParams.parentId, 
            language: languageCode}).then(function(result) {
                $scope.parentLO = result;
                if (isChild()) {
                    ChildLearningOpportunityService.query({
                        parentId: $routeParams.parentId, 
                        childId: $routeParams.childId,
                        language: $scope.descriptionLanguage}).then(function(result) {
                            $scope.childLO = result;
                            setTitle($scope.parentLO, $scope.childLO);
                        });
                } else {
                    setTitle($scope.parentLO, $scope.childLO);
                }
        });
    };

    $scope.hasChildren = function() {
        if ($scope.selectedParentLOI ) {
            return $scope.selectedParentLOI.children.length > 0;
        } else {
            return false;
        }
    };

    // scrolls to an anchor on page
    $scope.scrollToAnchor = function(id) {
        $('html, body').scrollTop($('#' + id).offset().top);
    };

    $scope.changeMainTab = function(tabName) {
        $scope.selectedTab = tabName;

        if (tabName == 'kuvaus' || tabName == 'hakeutuminen') {
            $scope.providerAsideClass = 'hidden';
            $scope.applyFormClass = '';
        } else {
            $scope.providerAsideClass = '';
            $scope.applyFormClass = 'hidden';
        }
    }

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
    $scope.$on('$viewContentLoaded', function() {
        //tabsMenu.build();
        OPH.Common.initHeader();
    });
};