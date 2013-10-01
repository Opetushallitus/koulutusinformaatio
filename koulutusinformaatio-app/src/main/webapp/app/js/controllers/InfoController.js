/**
 *  Controller for info views (parent and child)
 */
 function InfoCtrl($scope, $routeParams, $location, ParentLearningOpportunityService, ChildLearningOpportunityService, SearchService, ParentLODataService, ChildLODataService, TitleService, LearningOpportunityProviderPictureService, UtilityService, TabService) {
    $scope.queryString = SearchService.getTerm();
    $scope.descriptionLanguage = 'fi';

    $scope.tabtitle = {
        koulutus: i18n.t('lo-description'),
        valintaperusteet: i18n.t('lo-application')
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
    var initView = function() {
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
    }

    $scope.changePrerequisiteSelection = function(prerequisite) {
        $location.hash(prerequisite).replace();
        initView();
        //$location.search({prerequisite: prerequisite}).replace();
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

    // initilize view model
    initView();

    // trigger once content is loaded
    $scope.$on('$viewContentLoaded', function() {
        OPH.Common.initHeader();
    });
};