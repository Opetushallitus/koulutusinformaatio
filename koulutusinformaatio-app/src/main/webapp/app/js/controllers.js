/* Controllers */

/**
 *  Controls the selected user interface language
 */
function LanguageCtrl($scope, $location, LanguageService) {
    $scope.changeLanguage = function(code) {
       LanguageService.setLanguage(code);
       i18n.setLng(code);
       document.location.reload(true);
   }
};

/**
 *  Controls header actions
 */
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
};


/**
 *  Controller for application basket
 */
function ApplicationBasketCtrl($scope, $routeParams, $location, TitleService, ApplicationBasketService, SearchService, kiAppConstants) {
    var title = i18n.t('title-application-basket');
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

    $scope.addToBasket = function(aoId) {
        var basketType = ApplicationBasketService.getType();
        if (!basketType || $scope.selectedLOI.prerequisite.value == basketType) {
            ApplicationBasketService.addItem(aoId, $scope.selectedLOI.prerequisite.value);
            $scope.popoverTitle = i18n.t('popover-title-success');
            $scope.popoverContent = "<a href='#/muistilista'>" + i18n.t('popover-content-link-to-application-basket') + "</a>";
        } else {
            $scope.popoverTitle = i18n.t('popover-title-error');
            $scope.popoverContent = "<div>" + i18n.t('popover-content-error') + "</div><a href='#/muistilista'>" + i18n.t('popover-content-link-to-application-basket') + "</a>";
        }
    };

    $scope.applicationSystemIsActive = function(as) {
        for (var i in as.applicationDates) {
            if (as.applicationDates.hasOwnProperty(i)) {
                var start = as.applicationDates[i].startDate;
                var end = as.applicationDates[i].endDate;
                var current = new Date().getTime();

                if (current >= start && current <= end) {
                    return true;
                }
            }
        }

        return false;
    };

    $scope.popoverTitle = i18n.t('popover-title');
    $scope.popoverContent = "<a href='#/muistilista'>" + i18n.t('popover-content') + "</a>";
};

/**
 *  Controller for search field in header
 */
function SearchFieldCtrl($scope, $routeParams, $location, SearchService) {
    $scope.queryString = SearchService.getTerm();

    // Perform search using LearningOpportunity service
    $scope.search = function() {
        if ($scope.queryString) {
            SearchService.setTerm($scope.queryString);
            $location.path('/haku/' + $scope.queryString);
        }
    };
};

/**
 *  Controller for search filters
 */
function SearchFilterCtrl($scope, $routeParams, SearchLearningOpportunityService, kiAppConstants, FilterService) {
    $scope.individualizedActive = $scope.prerequisite != 'PK';
    var resultsPerPage = kiAppConstants.searchResultsPerPage;
    var filters = FilterService.get();
    $scope.prerequisite = filters.prerequisite;
    $scope.locations = filters.locations;

    $scope.change = function() {
        $scope.individualizedActive = $scope.prerequisite!= 'PK';
        FilterService.set($scope.prerequisite, $scope.locations);

        SearchLearningOpportunityService.query({
            queryString: $scope.queryString,
            prerequisite: $scope.prerequisite,
            start: 0,
            rows: resultsPerPage,
            locations: $scope.locations
        }).then(function(result) {
            $scope.$parent.loResult = result;
            $scope.$parent.maxPages = Math.ceil(result.totalCount / resultsPerPage);
            $scope.$parent.showPagination = $scope.$parent.maxPages > 1;
            $scope.$parent.currentPage = 1;
        });
    }
};

/**
 *  Controller for search functionality 
 */
 function SearchCtrl($scope, $routeParams, $location, SearchLearningOpportunityService, SearchService, TitleService, kiAppConstants, FilterService) {
    var resultsPerPage = kiAppConstants.searchResultsPerPage;
    $scope.currentPage = kiAppConstants.searchResultsStartPage;

    var title = i18n.t('title-search-results');
    TitleService.setTitle(title);

    $scope.changePage = function(page) {
        $scope.currentPage = page;
        $('html, body').scrollTop($('body').offset().top); // scroll to top of list
    };

    $scope.$watch('currentPage', function(value) {
        if ($routeParams.queryString) {
            var filters = FilterService.get();
            SearchLearningOpportunityService.query({
                queryString: $routeParams.queryString,
                start: (value-1) * resultsPerPage,
                rows: resultsPerPage,
                prerequisite: filters.prerequisite,
                locations: filters.locations }).then(function(result) {
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


    $scope.changeLOISelection = function(loiId) {
        var aggregateChildren = function(loi) {
            var children = [];
            if (loi.applicationSystems && loi.applicationSystems.length > 0) {
                var as = loi.applicationSystems[0];
                for (var i in as.applicationOptions) {
                    if (as.applicationOptions.hasOwnProperty(i)) {
                        var ao = as.applicationOptions[i];
                        if (ao.childRefs) {
                            children = children.concat(ao.childRefs);
                        }
                    }
                }
            }

            return children;
        };

        var getFirstApplicationSystem = function(loi) {
            if (loi.applicationSystems && loi.applicationSystems.length > 0) {
                return loi.applicationSystems[0];
            }
        };

        var getPrerequisite = function(loi) {
            var as = getFirstApplicationSystem(loi);
            if (as && as.applicationOptions && as.applicationOptions.length > 0) {
                return as.applicationOptions[0].prerequisite;
            }
        }

        /*
        if (isChild()) {
            $scope.selectedLOI = {};
            $scope.selectedLOI.applicationSystems = $scope.childLO.applicationSystems;
            $scope.selectedLOI.prerequisite = getPrerequisite($scope.selectedLOI);
            $scope.selectedAs = $scope.childLO.applicationSystems[0];
        } else {
        */
            for (var loi in $scope.lois) {
                if ($scope.lois.hasOwnProperty(loi)) {
                    if ($scope.lois[loi].id == loiId) {
                        $scope.selectedLOI = angular.copy($scope.lois[loi]);
                        var children = aggregateChildren($scope.selectedLOI);
                        var as = getFirstApplicationSystem($scope.selectedLOI);
                        $scope.selectedAs = as;

                        if ($scope.selectedAs) {
                            $scope.selectedAs.children = children;
                        }
                    }
                }
            }
        //}
    }

    $scope.loiClass = function(prerequisite) {
        if ($scope.selectedLOI && $scope.selectedLOI.prerequisite) {
            return ($scope.selectedLOI.prerequisite.value == prerequisite.value) ? 'disabled': '';
        } else {
            return '';
        }
    }

    var setTitle = function(parent, child) {
        if (child) {
            TitleService.setTitle(child.name);
        } else {
            TitleService.setTitle(parent.name);
        }
    };

    var isChild = function() {
        return $routeParams.childId ? true : false;
    };

    var getFirstParentLOI = function() {
        if (hasLOIs()) {
            return $scope.lois[0];
        }
    }

    var hasLOIs = function() {
        if ($scope.lois) {
            return $scope.lois.length > 0;
        } else {
            return false;
        } 
    }

    var showApplicationRadioSelection = function() {
        if (hasLOIs()) {
            return $scope.lois.length == 1 ? false : true;
        }

        return true;
    }

    $scope.hasChildren = function() {
        if ($scope.selectedAs && $scope.selectedAs.children) {
            return $scope.selectedAs.children.length > 0;
        } else {
            return false;
        }
    };

    var initializeParent = function() {
        setTitle($scope.parentLO, $scope.childLO);
        $scope.showApplicationRadioSelection = showApplicationRadioSelection() ? '' : 'hidden';

        var firstParentLOIInList = getFirstParentLOI();
        if (firstParentLOIInList) {
            $scope.changeLOISelection(firstParentLOIInList.id);
        }
    };

    $scope.$watch('parentLO.provider', function(data) {
        if (data && data.pictureFound) {
            LearningOpportunityProviderPictureService.query({providerId: data.id}).then(function(result) {
                $scope.providerImage = result;
            });
        }
    });

    $scope.isChild = isChild();

    // fetch data for parent and/or its child LO
    // TODO: could this logic be hidden in service?
    if (isChild()) {
        ChildLearningOpportunityService.query({
            childId: $routeParams.childId,
            language: $scope.descriptionLanguage}).then(function(childResult) {
                $scope.childLO = childResult;
                $scope.lois = childResult.lois;

                if (!ParentLODataService.dataExists(childResult.parent.id)) {
                    ParentLearningOpportunityService.query({
                        parentId: childResult.parent.id, 
                        language: $scope.descriptionLanguage}).then(function(parentResult) {
                            $scope.parentLO = parentResult;
                            ParentLODataService.setParentLOData(parentResult);
                            initializeParent();
                        });
                } else {
                    $scope.parentLO = ParentLODataService.getParentLOData();
                    initializeParent();
                }
            }); 
    } else {
        if (!ParentLODataService.dataExists($routeParams.parentId)) {
            ParentLearningOpportunityService.query({
                parentId: $routeParams.parentId, 
                language: $scope.descriptionLanguage}).then(function(result) {
                    $scope.parentLO = result;
                    $scope.lois = result.lois;
                    ParentLODataService.setParentLOData(result);
                    initializeParent();
                });
        } else {
            $scope.parentLO = ParentLODataService.getParentLOData();
            $scope.lois = $scope.parentLO.lois;
            initializeParent();
        }
    }

    // change description language and re-load LO data with the specified language
    $scope.changeDescriptionLanguage = function(languageCode) {
        $scope.descriptionLanguage = languageCode;

        var parentId = isChild() ? $scope.childLO.parent.id : $routeParams.parentId;

        // parent data has to be updated every time since child views contain parent data too
        ParentLearningOpportunityService.query({
            parentId: parentId, 
            language: languageCode}).then(function(result) {
                $scope.parentLO = result;
                if (isChild()) {
                    ChildLearningOpportunityService.query({
                        childId: $routeParams.childId,
                        language: $scope.descriptionLanguage}).then(function(result) {
                            $scope.childLO = result;
                            setTitle($scope.parentLO, $scope.childLO);
                            $scope.changeLOISelection();
                        });
                } else {
                    setTitle($scope.parentLO, $scope.childLO);
                    $scope.changeLOISelection($scope.selectedLOI.id);
                }
        });
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

    // trigger once content is loaded
    $scope.$on('$viewContentLoaded', function() {
        //tabsMenu.build();
        OPH.Common.initHeader();
    });
};