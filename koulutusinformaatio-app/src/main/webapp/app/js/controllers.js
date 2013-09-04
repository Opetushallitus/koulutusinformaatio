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
function HeaderCtrl($scope, $location, ApplicationBasketService, LanguageService) {
    $scope.appBasketItemCount = function() {
        return ApplicationBasketService.getItemCount();
    }

    $scope.lang = LanguageService.getLanguage();
};

/**
 *  Controls footer actions
 */
function FooterCtrl($scope, LanguageService, kiAppConstants) {
    $scope.locales = {
        opetushallitus: i18n.t('opetushallitus-address-line-1'),
        opetusministerio: i18n.t('opetusministerio-address-line-1')
    };
    
    if (LanguageService.getLanguage() == LanguageService.getDefaultLanguage()) {
        $scope.images = {
            opetushallitus: 'img/OPH_logo.png',
            opetusministerio: 'img/OKM_logo.png'
        }

        $scope.links = {
            opetushallitus: 'http://www.oph.fi/etusivu',
            opetusministerio: 'http://www.minedu.fi/OPM/',
            rekisteriseloste: kiAppConstants.contextRoot + 'rekisteriseloste.html'
        }
    } else {
        $scope.images = {
            opetushallitus: 'img/OPH_logo-sv.png',
            opetusministerio: 'img/OKM_logo-sv.png'
        }

        $scope.links = {
            opetushallitus: 'http://www.oph.fi/startsidan',
            opetusministerio: 'http://www.minedu.fi/OPM/?lang=sv',
            rekisteriseloste: kiAppConstants.contextRoot + 'sv/rekisteriseloste.html'
        }
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
    TitleService.setTitle(title);

    $scope.queryString = SearchService.getTerm();
    $scope.notificationText = i18n.t('application-basket-fill-form-notification', {count: basketLimit});
    $scope.basketIsEmpty = ApplicationBasketService.isEmpty();

    if (!$scope.basketIsEmpty) {
        ApplicationBasketService.query().then(function(result) {
            $scope.applicationItems = result;
        });
    }

    $scope.title = i18n.t('title-application-basket-content');
    $scope.itemCount = ApplicationBasketService.getItemCount();

    
    var applicationSystemIsActive = function(asId) {
        var items = $scope.applicationItems;

        for (var i = 0; i < items.length; i++) {
            var item = items[i];

            if (item.applicationSystemId == asId && item.applicationDates) {
                return item.asOngoing ? true : false;
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
        var isOverflowing = $scope.applicationBasketIsOverflowing(asId);
        if (isOverflowing || !applicationSystemIsActive(asId)) {
            return true;
        } else {
            return false;
        }
    }

    $scope.applicationBasketIsOverflowing = function(asId) {
        var items = $scope.applicationItems;
        var itemsInBasket = 0;

        for (var i in items) {
            if (items.hasOwnProperty(i)) {
                var item = items[i];
                if (item && item.applicationSystemId == asId && item.applicationOptions) {
                    itemsInBasket = item.applicationOptions.length;
                    break;
                }
            }
        }
  
        if (itemsInBasket > basketLimit) {
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
                return as.asOngoing ? true : false;
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
function SearchFieldCtrl($scope, $routeParams, $location, SearchService, $route) {
    $scope.searchFieldPlaceholder = i18n.t('search-field-placeholder'); 

    // Perform search using LearningOpportunity service
    $scope.search = function() {
        if ($scope.queryString) {
            SearchService.setTerm($scope.queryString);
            var queryString = $scope.queryString;
            $scope.queryString = '';
            $location.path('/haku/' + queryString);
        }
    };
};

/**
 *  Controller for search filters
 */
function SearchFilterCtrl($scope, $routeParams, SearchLearningOpportunityService, kiAppConstants, FilterService) {
    var resultsPerPage = kiAppConstants.searchResultsPerPage;
    var filters = FilterService.get();
    $scope.prerequisite = filters.prerequisite;
    $scope.individualized = filters.individualized;
    $scope.locations = filters.locations;
    $scope.individualizedDisabled = $scope.prerequisite != 'PK';

    $scope.change = function() {
        $scope.individualizedDisabled = $scope.prerequisite != 'PK';
        FilterService.set($scope.prerequisite, $scope.individualized, $scope.locations);

        SearchLearningOpportunityService.query({
            queryString: $scope.queryString,
            prerequisite: $scope.prerequisite,
            individualized: $scope.individualized,
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
                individualized: filters.individualized,
                locations: filters.locations }).then(function(result) {
                    $scope.loResult = result;
                    $scope.maxPages = Math.ceil(result.totalCount / resultsPerPage);
                    $scope.showPagination = $scope.maxPages > 1;
                });

            $scope.queryString = $routeParams.queryString;
            $scope.showFilters = $scope.queryString ? true : false;
            SearchService.setTerm($routeParams.queryString);
        } else {
            $scope.loResult = {totalCount : 0};
        }
    });

    $scope.$on('$viewContentLoaded', function() {
        OPH.Common.initHeader();
    });
};

/**
 *  Controller for info views (parent and child)
 */
 function InfoCtrl($scope, $routeParams, $location, ParentLearningOpportunityService, ChildLearningOpportunityService, SearchService, ParentLODataService, ChildLODataService, TitleService, LearningOpportunityProviderPictureService, UtilityService, TabService) {
    $scope.queryString = SearchService.getTerm();
    $scope.descriptionLanguage = 'fi';

    // how to avoid this?
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
        return $routeParams.childId ? true : false;
    };

    var getFirstLOI = function() {
        if (hasLOIs()) {
            return $scope.lois[0];
        }
    };

    var getLOIByPrerequisite = function(prerequisite) {
        for (var loiIndex in $scope.lois) {
            if ($scope.lois.hasOwnProperty(loiIndex)) {
                var loi = $scope.lois[loiIndex];

                if (loi.prerequisite.value == prerequisite) {
                    return loi;
                }
            }
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

    var initializeParent = function() {
        setTitle($scope.parentLO, $scope.childLO);
        $scope.showApplicationRadioSelection = showApplicationRadioSelection() ? '' : 'hidden';
        var loi = getLOIByPrerequisite($location.hash());
        if (loi) {
            changeLOISelection(loi);
        } else {
            loi = getFirstLOI();
            changeLOISelection(loi);
        }
    };

    var changeLOISelection = function(loi) {

        var aggregateChildren = function(loi) {
            var children = [];
            if (loi.applicationSystems && loi.applicationSystems.length > 0) {
                var as = loi.applicationSystems[0];
                for (var i in as.applicationOptions) {
                    if (as.applicationOptions.hasOwnProperty(i)) {
                        var ao = as.applicationOptions[i];
                        if (ao.childRefs) {
                            //children = children.concat(ao.childRefs);
                            for (var childIndex in ao.childRefs) {
                                if (ao.childRefs.hasOwnProperty(childIndex)) {
                                    var child = ao.childRefs[childIndex];

                                    var childFound = false;
                                    for (var j in children) {
                                        if (children.hasOwnProperty(j)) {
                                            if (child.losId == children[j].losId) {
                                                childFound = true;
                                            }
                                        }
                                    }

                                    if (!childFound) {
                                        children.push(child);
                                    }
                                }
                            }
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

        for (var loiIndex in $scope.lois) {
            if ($scope.lois.hasOwnProperty(loiIndex)) {
                if ($scope.lois[loiIndex].prerequisite.value == loi.prerequisite.value) {
                    $scope.selectedLOI = angular.copy($scope.lois[loiIndex]);
                    $scope.prerequisite = angular.copy($scope.selectedLOI.prerequisite);
                    var children = aggregateChildren($scope.selectedLOI);
                    var as = getFirstApplicationSystem($scope.selectedLOI);
                    $scope.selectedAs = as;

                    if ($scope.selectedAs) {
                        $scope.selectedAs.children = children;
                    }

                    if ($scope.selectedAs && $scope.selectedAs.applicationOptions && $scope.selectedAs.applicationOptions.length > 0) {
                        $scope.applicationOption = $scope.selectedAs.applicationOptions[0];
                    }
                }
            }
        }
    };

    $scope.changePrerequisiteSelection = function(prerequisite) {
        $location.hash(prerequisite).replace();
    }

    $scope.loiClass = function(prerequisite) {
        if ($scope.selectedLOI && $scope.selectedLOI.prerequisite) {
            return ($scope.selectedLOI.prerequisite.value == prerequisite.value) ? 'disabled': '';
        } else {
            return '';
        }
    };

    $scope.hasChildren = function() {
        if ($scope.selectedAs && $scope.selectedAs.children) {
            return $scope.selectedAs.children.length > 0;
        } else {
            return false;
        }
    };

    $scope.$watch('parentLO.provider', function(data) {
        if (data && data.pictureFound) {
            LearningOpportunityProviderPictureService.query({providerId: data.id}).then(function(result) {
                $scope.providerImage = result;
            });
        }
    });

    var childLOSuccess = function(childResult) {
        $scope.childLO = childResult;
        $scope.lois = childResult.lois;
        ChildLODataService.setChildLOData(childResult);

        if (!ParentLODataService.dataExists(childResult.parent.id)) {
            ParentLearningOpportunityService.query({
                parentId: childResult.parent.id
            }).then(function(parentResult) {
                $scope.parentLO = parentResult;
                ParentLODataService.setParentLOData(parentResult);
                initializeParent();
                initializeTranslationLanguage(childResult);
            });
        } else {
            $scope.parentLO = ParentLODataService.getParentLOData();
            initializeParent();
            initializeTranslationLanguage(childResult);
        }
    };

    var parentLOSuccess = function(result) {
        $scope.parentLO = result;
        $scope.lois = result.lois;
        ParentLODataService.setParentLOData(result);
        initializeParent();
    };

    var initializeTranslationLanguage = function(result) {
        if (result && result.availableTranslationLanguages && result.availableTranslationLanguages.length > 1) {
            var translationLanguageIndex = result.availableTranslationLanguages.indexOf($scope.selectedLOI.translationLanguage);
            result.availableTranslationLanguages.splice(translationLanguageIndex, 1);
        }
    }

    var loError = function(result) {
    };

    // fetch data for parent and/or its child LO
    // TODO: could this logic be hidden in service?
    if (isChild()) {
        if (!ChildLODataService.dataExists($routeParams.childId)) {
            ChildLearningOpportunityService.query({
                childId: $routeParams.childId
            }).then(childLOSuccess, loError);
        } else {
            $scope.childLO = ChildLODataService.getChildLOData();
            $scope.parentLO = ParentLODataService.getParentLOData();
            $scope.lois = $scope.childLO.lois;
            initializeParent();
        }
    } else {
        if (!ParentLODataService.dataExists($routeParams.parentId)) {
            ParentLearningOpportunityService.query({
                parentId: $routeParams.parentId
            }).then(parentLOSuccess, loError);
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
                            $scope.lois = result.lois;
                            setTitle($scope.parentLO, $scope.childLO);
                            initializeParent();
                            initializeTranslationLanguage(result);
                        });
                } else {
                    setTitle($scope.parentLO, $scope.childLO);
                    $scope.lois = result.lois;
                    initializeParent();
                }
        });

            return false;
    };

    // scrolls to an anchor on page
    $scope.scrollToAnchor = function(id) {
        $('html, body').scrollTop($('#' + id).offset().top);
        return false;
    };

    $scope.changeMainTab = function(tabName) {
        TabService.setCurrentTab(tabName);

        if (tabName == 'kuvaus' || tabName == 'hakeutuminen') {
            $scope.providerAsideClass = 'hidden';
            $scope.applyFormClass = '';
        } else {
            $scope.providerAsideClass = '';
            $scope.applyFormClass = 'hidden';
        }
    }

    $scope.initTabs = tabsMenu.build( TabService.getCurrentTab() );

    // trigger once content is loaded
    $scope.$on('$viewContentLoaded', function() {
        //tabsMenu.build();
        OPH.Common.initHeader();
    });
};