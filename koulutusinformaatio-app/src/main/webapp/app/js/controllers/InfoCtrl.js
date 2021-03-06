/**
 *  Controller for info views (parent and child)
 */
 function InfoCtrl($scope, $rootScope, $routeParams, $location, SearchService, LearningOpportunityPictureService, LearningOpportunityProviderPictureService, UtilityService, TranslationService, Config, loResource, ChildLOService, LanguageService, VirkailijaLanguageService, _) {
    $scope.loType = $routeParams.loType;

    // redirect to koulutus
    if("erityisopetus" == $scope.loType){
        var koulutusOid = $routeParams.id.split('_')[2];
        return $location.path("/koulutus/"+koulutusOid).replace;
    }

    var getUrl = function(key) {
      return function(id) { return window.url(key, id) };
    };

    $scope.queryString = SearchService.getTerm();
    //$scope.descriptionLanguage = 'fi';
    $scope.hakuAppUrl = getUrl('haku-app.lomake');
    $scope.ataruAppUrl = getUrl('ataru-app.hakemus');
    $scope.uiLang = LanguageService.getLanguage();
    $scope.virkailijaLang = VirkailijaLanguageService.getLanguage();

    // set tab titles based on lo and education type
    $scope.$watch('lo.applicationSystems', function(applicationSystems) {
        var getValintaperusteetTitle = function() {
            var usesSystemApplicationForm = _.findWhere(applicationSystems, {
                useSystemApplicationForm: true
            }) !== undefined;
            if (usesSystemApplicationForm) {
                return TranslationService.getTranslation('lo-application');
            }
            return TranslationService.getTranslation('lo-application-er');
        };

        $scope.tabtitle =  {
            koulutus: TranslationService.getTranslation('lo-description'),
            valintaperusteet: getValintaperusteetTitle()
        }

        // initialisoidaan näppäimistö navigointi sivun välilehdille
        var loTablist = null;
        loTablist =  new tabpanel('lo-tablist', false);
    });

    // sets site title content
    var setTitle = function(parent, child) {
        var sitename = TranslationService.getTranslation('sitename');
        var provider = '';

        if (child && child.provider) {
            provider = child.provider.name;
        } else if (parent && parent.provider) {
            provider = parent.provider.name;
        } 

        if (child) {
            $rootScope.title = child.name + ' - ' + provider + ' - ' + sitename;
        } else {
            $rootScope.title = parent.name + ' - ' + provider + ' - ' + sitename;
        }
    };

    // sets meta description tag content
    var setMetaDescription = function(parent, child) {
        var lo = child || parent;
        var description;
        var maxLength = 160;

        // use goals (Tavoitteet) to generate description content if exists
        if (lo.goals) {

            var goalsAsText;
            try {
                goalsAsText = $(lo.goals).contents().filter(function () {
                    return this.nodeType === 3; // Node.TEXT_NODE;
                }).text();
            } catch (ignoreExceptionNonHtmlGoalsText) {
                goalsAsText = lo.goals;
            }

            if (goalsAsText) {
                description = _.first(goalsAsText.split('.'));
                description = description.length > maxLength ? description.substr(0, maxLength) : description
            }

            $rootScope.description = description;
        } else { // otherwise use learning opportunity name and provider name
            description = lo.name;
            if (lo.provider) {
                description += ' - ' + lo.provider.name;
            }
            $rootScope.description = description;
        }
    }

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
    
    var setRecommendationFields = function() {
    	if ($scope.uiLang == 'fi') {
    		$scope.lopExclField = '-lopName_fi_str_display';
    	} else if ($scope.uiLang == 'sv') {
    		$scope.lopExclField = '-lopName_sv_str_display';
    	} else if ($scope.uiLang == 'en') {
    		$scope.lopExclField = '-lopName_en_str_display';
    	} else {
    		$scope.lopExclField = '-lopName';
    	}
    }

    var getPrerequisite = function () {
        // use hash if present
        return $location.hash() ? $location.hash() : $location.search().prerequisite
    }

    var objPropNotIn = function (prop, excluded) {
      return function (obj) {
        return obj && obj[prop] ? !_.contains(excluded, obj[prop]) : false;
      }
    };

    $scope.shouldShowEducationDomain = objPropNotIn('toteutustyyppi', ['PELASTUSALAN_KOULUTUS']);
    $scope.shouldShowQualifications = objPropNotIn('toteutustyyppi', ['PELASTUSALAN_KOULUTUS']);

    var initializeLO = function() {
        setTitle($scope.parent, $scope.lo);
        setMetaDescription($scope.parent, $scope.lo);
        $scope.showApplicationRadioSelection = showApplicationRadioSelection() ? '' : 'hidden';

        setRecommendationFields();
        
        var loi = getLOIByPrerequisite(getPrerequisite());
        
        if (loi) {
            changeLOISelection(loi);
        } else {
            loi = getFirstLOI();
            changeLOISelection(loi);
        }
        
        if ($scope.lo.containsPseudoChildLOS) {
            ChildLOService.query({
                id : $scope.selectedAs.children[0].losId,
                lang : $scope.lo.translationLanguage
            }).then(function(loChildResult) {
                $scope.lois = loChildResult.lo.lois;
                $scope.pseudoChild = getLOIByPrerequisite(getPrerequisite());
            });
        }
    };

    var changeLOISelection = function(loi) {

        var getFirstApplicationSystem = function(loi) {
            if (loi.applicationSystems && loi.applicationSystems.length > 0) {
                return loi.applicationSystems[0];
            }
        };

        for (var loiIndex in $scope.lois) {
            if ($scope.lois.hasOwnProperty(loiIndex)) {
                if ($scope.lois[loiIndex].prerequisite.value == loi.prerequisite.value) {
                    $scope.selectedLOI = angular.copy($scope.lois[loiIndex]);
                    $scope.prerequisite = angular.copy($scope.selectedLOI.prerequisite);
                    var as = getFirstApplicationSystem($scope.selectedLOI);
                    $scope.selectedAs = as;

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
        
        // force to load content with queried language (used for SEO snapshots)
        if (languageCode) {
            $location.search('descriptionLang', languageCode);
        } else if ($location.search().descriptionLang) {
            languageCode = $location.search().descriptionLang;
        }
        if($routeParams.id.indexOf("_UUSI") > 0){
            $routeParams.prerequisite = 'UUSI';
        }
        $routeParams.prerequisite = $routeParams.prerequisite || 'PK';
        $scope.prerequisite = $routeParams.prerequisite;

        loResource.query({
            id: $routeParams.id,
            lang: languageCode,
            loType: $routeParams.loType,
            prerequisite: $routeParams.prerequisite
        }).then(function(loResult) {
            $scope.lo = loResult.lo;
            $scope.tarjontaViewUrl = window.url("tarjonta-app.koulutus", $scope.lo.id);
            $scope.parent = loResult.parent;
            $scope.provider = loResult.provider;
            $scope.lois = loResult.lo.lois;
            initializeLO();
        });
    }

    $scope.changePrerequisiteSelection = function(prerequisite) {
        $location.hash(null).replace(); // override hash if used
        $location.search({prerequisite: prerequisite}).replace();
        loadLo();
    }

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
                $scope.provider.providerImage = result;
            });
        }
    });

    $scope.isToinenaste = function(lo){
        if(lo) {
            return (['et01.03.01', 'et01.03.02', 'et01.03.03', 'et01.03.04'].indexOf(lo.educationType) !== -1);
        } else {
            return false;
        }
    }

    $scope.$watch('lo', function(data) {
        if (data && data.structureImageId) {
        	if (data.structureImage && data.structureImage != null) {
        		$scope.structureImage = data.structureImage;
        	} else {
        		LearningOpportunityPictureService.query({pictureId: data.structureImageId}).then(function(result) {
        			$scope.structureImage = result;
        		});
        	}
        }
        if (data && data.additionalProviders && data.additionalProviders.length > 0) {
        	for (var curProvIndex in data.additionalProviders) {
				if (data.additionalProviders.hasOwnProperty(curProvIndex)) {
					var curProvider = data.additionalProviders[curProvIndex];
					if (curProvider && curProvider.pictureFound) {
						LearningOpportunityProviderPictureService.query({providerId: curProvider.id}).then(function(result) {
			                curProvider.providerImage = result;
			            });
					}
				}
			}
        }
    });

    $scope.selectLabel = function(label){
        $location.path("/haku/" + label);
    }
    
    // initialize view model
    loadLo();
};

function LoTabCtrl($scope, $location) {
    
    var qParams = $location.search();
    if (qParams.tab) {
        $scope.tabs = [false, false, false];
        $scope.tabs[qParams.tab] = true;
    } else {
        $scope.tabs = [true, false, false];
    }
};


/**
 *  Controller for adding applications to application basket
 */
function ApplicationCtrl($scope, ApplicationBasketService, UtilityService, TranslationService, LOTypes) {

    $scope.tooltips = {
        externalApplicationForm: TranslationService.getTranslation('tooltip:external-application-form')
    };
    
    $scope.addToBasket = function(aoId) {
        // vocational education needs prerequisite checking...
        var addVocationalEdToBasket = function(aoId) {
            var basketType = ApplicationBasketService.getType();
            if (!basketType || $scope.prerequisite == basketType) {
                ApplicationBasketService.addItem(aoId, $scope.prerequisite);
            } else {
                $scope.popoverTitle = TranslationService.getTranslation('popover-title-error');
                $scope.popoverContent = "<div>" + TranslationService.getTranslation('popover-content-error') + "</div><a href='#!/muistilista'>" + TranslationService.getTranslation('popover-content-link-to-application-basket') + "</a>";
            }
        }

        // ...but other types of education do not require prerequisite checking
        var addEducationToBasket = function(aoId) {
            ApplicationBasketService.addItem(aoId);
        }

        if ($scope.loType == LOTypes.TUTKINTO) {
            addVocationalEdToBasket(aoId);
        } else {
            addEducationToBasket(aoId);
        }
    };

    $scope.isItemAddedToBasket = function(aoId) {
        return ApplicationBasketService.itemExists(aoId);
    }
};