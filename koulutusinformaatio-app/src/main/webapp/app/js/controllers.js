/* Controllers */

/**
 *  Controls the selected user interface language
 */
function LanguageCtrl($scope, LanguageService) {
    $scope.changeLanguage = function(code) {
       LanguageService.setLanguage(code);
       i18n.setLng(code);
       document.location.reload(true);
   }
};

/**
 *  Controls header actions
 */
function HeaderCtrl($scope, ApplicationBasketService, LanguageService) {
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
 *  Controller for application basket
 */
function ApplicationBasketCtrl($scope, $routeParams, TitleService, ApplicationBasketService, SearchService, FilterService, kiAppConstants) {
    var title = i18n.t('title-application-basket');
    var basketLimit = kiAppConstants.applicationBasketLimit; // TODO: get this from application data?
    TitleService.setTitle(title);

    $scope.queryString = SearchService.getTerm() + '?' + FilterService.getParams();
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
function ApplicationCtrl($scope, ApplicationBasketService, UtilityService) {

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
function SearchFieldCtrl($scope, $location, SearchService, FilterService) {
    $scope.searchFieldPlaceholder = i18n.t('search-field-placeholder'); 

    // Perform search using LearningOpportunity service
    $scope.search = function() {
        if ($scope.queryString) {
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
    var resultsPerPage = kiAppConstants.searchResultsPerPage;

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

        // append filters to url
        $location.search( FilterService.get() );
    }
};

/**
 *  Controller for search functionality 
 */
 function SearchCtrl($scope, $routeParams, SearchLearningOpportunityService, SearchService, TitleService, kiAppConstants, FilterService) {
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
                locations: filters.locations,
                ongoing: filters.ongoing
            }).then(function(result) {
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

