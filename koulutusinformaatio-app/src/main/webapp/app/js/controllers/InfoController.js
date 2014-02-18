/**
 *  Controller for info views (parent and child)
 */
 function InfoCtrl($scope, $rootScope, $routeParams, $location, SearchService, LearningOpportunityProviderPictureService, UtilityService, TranslationService, Config, loResource) {
    $scope.loType = $routeParams.loType;

    $scope.queryString = SearchService.getTerm();
    $scope.descriptionLanguage = 'fi';
    $scope.hakuAppUrl = Config.get('hakulomakeUrl');
    

    $scope.tabtitle = (function() {
        var getValintaperusteetTitle = function() {
            if ($scope.loType == 'erityisopetus' ||
                $scope.loType == 'valmentava') {
                return TranslationService.getTranslation('lo-application-er');
            } else {
                return TranslationService.getTranslation('lo-application');
            }
        };

        return {
            koulutus: TranslationService.getTranslation('lo-description'),
            valintaperusteet: getValintaperusteetTitle()
        }
    }());

    

    var setTitle = function(parent, child) {
        var sitename = TranslationService.getTranslation('sitename');
        if (child) {
            $rootScope.title = child.name + ' - ' + sitename;
        } else {
            $rootScope.title = parent.name + ' - ' + sitename;
        }
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

    var showAoAnchorLinks = function() {
        var length = 0;
        angular.forEach($scope.lo.applicationSystems, function(as, askey){
            length += as.applicationOptions.length;
        });

        return length > 1 ? true : false;
    }

    var initializeLO = function() {
        setTitle($scope.parent, $scope.lo);
        $scope.showApplicationRadioSelection = showApplicationRadioSelection() ? '' : 'hidden';
        $scope.showAoAnchorLinks = showAoAnchorLinks();
        var loi = getLOIByPrerequisite($location.hash());
        //var loi = getLOIByPrerequisite($location.search().prerequisite);
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

    var loError = function(result) {
    };

    // fetch data for LO
    var loadLo = function(languageCode) {
        loResource.query({
            id: $routeParams.id,
            lang: languageCode
        }).then(function(loResult) {
            $scope.lo = loResult.lo;
            $scope.tarjontaViewUrl = Config.get('tarjontaUrl') + '/koulutus/' + $scope.lo.id;
            $scope.parent = loResult.parent;
            $scope.provider = loResult.provider;
            $scope.lois = loResult.lo.lois;
            initializeLO();
        });
    }

    $scope.changePrerequisiteSelection = function(prerequisite) {
        $location.hash(prerequisite).replace();
        loadLo();
        //$location.search({prerequisite: prerequisite}).replace();
    }

    $scope.hasChildren = function() {
        if ($scope.selectedAs && $scope.selectedAs.children) {
            return $scope.selectedAs.children.length > 0;
        } else {
            return false;
        }
    };

    // change description language and re-load LO data with the specified language
    $scope.changeDescriptionLanguage = function(languageCode) {
        loadLo(languageCode);
        return false;
    };

    // scrolls to an anchor on page
    $scope.scrollToAnchor = function(id) {
        id = id.replace(/\./g,"\\.");
        $('html, body').scrollTop($('#' + id).offset().top);
        return false;
    };

    $scope.$watch('provider', function(data) {
        if (data && data.pictureFound) {
            LearningOpportunityProviderPictureService.query({providerId: data.id}).then(function(result) {
                $scope.providerImage = result;
            });
        }
    });

    // initialize view model
    loadLo();

    // trigger once content is loaded
    $scope.$on('$viewContentLoaded', function() {
        OPH.Common.initHeader();
    });
};


/**
 *  Controller for adding applications to application basket
 */
function ApplicationCtrl($scope, ApplicationBasketService, UtilityService, TranslationService) {

    $scope.getApplicationFormUrl = function() {
        console.log('gaah');
    }

    // vocational education needs prerequisite checking...
    $scope.addToBasket = function(aoId) {
        var basketType = ApplicationBasketService.getType();
        if (!basketType || $scope.selectedLOI.prerequisite.value == basketType) {
            ApplicationBasketService.addItem(aoId, $scope.selectedLOI.prerequisite.value);
        } else {
            $scope.popoverTitle = TranslationService.getTranslation('popover-title-error');
            $scope.popoverContent = "<div>" + TranslationService.getTranslation('popover-content-error') + "</div><a href='#!/muistilista'>" + TranslationService.getTranslation('popover-content-link-to-application-basket') + "</a>";
        }
    };

    // ...but high education does not need prerequisite checking
    $scope.addHighEdToBasket = function(aoId) {
        ApplicationBasketService.addItem(aoId);
    }

    $scope.applicationSystemIsActive = function(as) {
        for (var i in as.applicationDates) {
            if (as.applicationDates.hasOwnProperty(i)) {
                return as.asOngoing ? true : false;
            }
        }

        return false;
    };

    $scope.isItemAddedToBasket = function(aoId) {
        return ApplicationBasketService.itemExists(aoId);
    }
};