/*  Services */

angular.module('kiApp.services', 
[
    'ngResource', 
    'kiApp.HostResolver', 
    'kiApp.NavigationService',
    'kiApp.ArticleContentSearchService',
    'kiApp.TranslationService',
    'kiApp.CookieService'
]).

service('SearchLearningOpportunityService', ['$http', '$timeout', '$q', '$analytics', '$rootScope', 'FilterService', 'LearningOpportunitySearchResultTransformer', function($http, $timeout, $q, $analytics, $rootScope, FilterService, LearningOpportunitySearchResultTransformer) {
    
    // gather information for analytics
    var parseFilterValues = function(params) {
        var getTilaValue = function(params) {
            var result = 0;
            result = params.ongoing ? result + 1 : result;
            result = params.upcoming ? result + 1 : result;
            result = params.upcomingLater ? result + 1 : result;

            return result;
        }

        var facetitemIsOfType = function(item, filterKey) {
            if (item && (typeof item == 'string' || item instanceof String)) {
                var temp = item.split(':');
                if (temp && temp[0] && temp[0].indexOf(filterKey) >= 0) {
                    return true;
                }
            }

            return false;
        }

        var getTyyppiValue = function(params) {
            var result = 0;
            if (params.facetFilters) {
                angular.forEach(params.facetFilters, function(item, key) {
                    result = facetitemIsOfType(item, 'educationType') ? result + 1: result;
                });
            }

            return result;
        }

        var getPohjakoulutusValue = function(params) {
            var result = 0;
            if (params.facetFilters) {
                angular.forEach(params.facetFilters, function(item, key) {
                    result = facetitemIsOfType(item, 'prerequisites') ? result + 1: result;
                });
            }

            return result;
        }

        var getPaikkakuntaValue = function(params) {
            return params.locations ? params.locations.length : 0;
        }

        var getOpetuskieliValue = function(params) {
            var result = 0;
            if (params.facetFilters) {
                angular.forEach(params.facetFilters, function(item, key) {
                    result = facetitemIsOfType(item, 'teachingLangCode') ? result + 1: result;
                });
            }

            return result;
        }

        return {
            page: [
                {name: 'Haun tila', value: getTilaValue(params)},
                {name: 'Koulutuksen tyyppi', value: getTyyppiValue(params)},
                {name: 'Pohjakoulutus', value: getPohjakoulutusValue(params)},
                {name: 'Paikkakunta', value: getPaikkakuntaValue(params)},
                {name: 'Opetuskieli', value: getOpetuskieliValue(params)}
            ]
        };

    };

    return {
        query: function(params) {
            var deferred = $q.defer();
            var cities = '';
            
            if (params.locations) {
                for (var index = 0; index < params.locations.length; index++) {
                    if (params.locations.hasOwnProperty(index)) {
                        cities += '&city=' + params.locations[index];
                    }
                }

                cities = cities.substring(1, cities.length);
            }

            var qParams = '?';

            qParams += (params.start != undefined) ? ('start=' + params.start) : '';
            qParams += (params.rows != undefined) ? ('&rows=' + params.rows) : '';
            qParams += (params.prerequisite != undefined) ? ('&prerequisite=' + params.prerequisite) : '';
            qParams += (params.locations != undefined && params.locations.length > 0) ? ('&' + cities) : '';
            qParams += (params.ongoing != undefined) ? ('&ongoing=' + params.ongoing) : '';
            qParams += (params.upcoming != undefined) ? ('&upcoming=' + params.upcoming) : '';
            qParams += (params.upcomingLater != undefined) ? ('&upcomingLater=' + params.upcomingLater) : '';
            qParams += (params.lang != undefined) ? ('&lang=' + params.lang) : '';
            qParams += (params.lopFilter != undefined) ? ('&lopFilter=' + params.lopFilter) : '';
            qParams += (params.educationCodeFilter != undefined) ? ('&educationCodeFilter=' + params.educationCodeFilter) : '';
            qParams += (params.searchType != undefined) ? ('&searchType=' + params.searchType) : '&searchType=LO';
            
            if (params.facetFilters != undefined) {
            	 angular.forEach(params.facetFilters, function(facetFilter, key) {
            		 qParams += '&facetFilters=' + facetFilter;
                 });
            }
            
            if (params.articleFacetFilters != undefined) {
           	 angular.forEach(params.articleFacetFilters, function(facetFilter, key) {
           		 qParams += '&articleFacetFilters=' + facetFilter;
                });
           }
            
            if (params.excludes != undefined) {
            	angular.forEach(params.excludes, function(exclude, key) {
           		 	qParams += '&excludes=' + exclude;
                });
            }
            
            var sortField = '';
            if (params.sortCriteria != undefined) {
            	if (params.sortCriteria == 1 || params.sortCriteria == 2) {
            		sortField = 'name_ssort';
            	} else if (params.sortCriteria == 3 || params.sortCriteria == 4) {
            		sortField = 'duration_isort';
            	}
            } 
            
            qParams += (sortField.length > 0) ? ('&sort=' +sortField) : '';
            qParams += ((params.sortCriteria != undefined) && ((params.sortCriteria == 2) || (params.sortCriteria == 4))) ? ('&order=desc') : '';

            $http.get('../lo/search/' + encodeURI(params.queryString).replace("#", "%23").replace(";", "%3B") + qParams, {}).
            success(function(result) {
                LearningOpportunitySearchResultTransformer.transform(result);
                var variables = parseFilterValues(params);
                var category;
                if (params.locations && params.locations.length > 0) {
                    category = params.locations[0];
                } else {
                    category = false;
                }

                $analytics.siteSearchTrack(params.queryString, category, result.totalCount, variables);
                deferred.resolve(result);
            }).
            error(function(result) {
                $rootScope.error = true;
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]).

service('SearchLocationService', ['$http', '$timeout', '$q', 'LanguageService', function($http, $timeout, $q, LanguageService) {

    return {
        query: function(queryParam) {
            var deferred = $q.defer();

            $http.get('../location/search/' + queryParam, {
                params: {
                    lang: LanguageService.getLanguage()
                }
            }).
            success(function(result) {
                deferred.resolve(result);
            }).
            error(function(result) {
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]).

service('AutocompleteService', ['$http', '$timeout', '$q', 'LanguageService', function($http, $timeout, $q, LanguageService) {

    return {
        query: function(queryParam) {
            var deferred = $q.defer();

            $http.get('../lo/autocomplete/' + queryParam, {
                params: {
                    lang: LanguageService.getLanguage()
                }
            }).
            success(function(result) {
                deferred.resolve(result);
            }).
            error(function(result) {
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]).

/**
 * Service for retrieving districts (maakunnat). Used in faceted search
 */
service('DistrictService', ['$http', '$timeout', '$q', 'LanguageService', function($http, $timeout, $q, LanguageService) {

    return {
        query: function() {
            var deferred = $q.defer();

         
            
            $http.get('../location/districts', {
                params: {
                    lang: LanguageService.getLanguage()
                }
            }).
            success(function(result) {
                deferred.resolve(result);
            }).
            error(function(result) {
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]).

/**
 * Service for retrieving municipalities belonging to a district. Used in faceted search
 */
service('ChildLocationsService', ['$http', '$timeout', '$q', 'LanguageService', function($http, $timeout, $q, LanguageService) {

    return {
        query: function(districtVal) {
            var deferred = $q.defer();

            var params = '?lang=' + LanguageService.getLanguage();
            for (var i = 0; i < districtVal.length; i++) {
            	params += '&districts=' + districtVal[i].code;
            }
            
            $http.get('../location/child-locations' + params, {}).
            success(function(result) {
                deferred.resolve(result);
            }).
            error(function(result) {
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]).

/**
 *  Resource for requesting parent LO data
 */
service('ParentLOService', ['$http', '$timeout', '$q', '$rootScope', 'LanguageService', 'ParentLOTransformer', function($http, $timeout, $q, $rootScope, LanguageService, ParentLOTransformer) {
    
    return {
        query: function(options) {
            var deferred = $q.defer();
            var queryParams = {
                uiLang: LanguageService.getLanguage()
            }

            if (options.lang) {
                queryParams.lang = options.lang
            }
            
            $http.get('../lo/parent/' + options.id, {
                params: queryParams
            }).

            success(function(result) {
                ParentLOTransformer.transform(result);
                var loResult = {
                    lo: result,
                    provider: result.provider
                }

                deferred.resolve(loResult);
            }).
            error(function(result) {
                $rootScope.error = true;
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]).

/**
 *  Resource for requesting child LO data
 */
service('ChildLOService', ['$http', '$timeout', '$q', '$rootScope', 'LanguageService', 'ChildLOTransformer', 'ParentLOService', function($http, $timeout, $q, $rootScope, LanguageService, ChildLOTransformer, ParentLOService) {
    return {
        query: function(options) {
            var deferred = $q.defer();
            var queryParams = {
                uiLang: LanguageService.getLanguage()
            }

            if (options.lang) {
                queryParams.lang = options.lang
            }

            var url = '../lo/child/';

            $http.get(url + options.id, {
                params: queryParams
            }).
            success(function(result) {
                ChildLOTransformer.transform(result);
                ParentLOService.query({
                    id: result.parent.id,
                    lang: options.lang
                }).then(function(presult) {
                    result.educationDegree = presult.lo.educationDegree;
                    var loResult = {
                        lo: result,
                        parent: presult.lo,
                        provider: presult.provider
                    }
                    deferred.resolve(loResult);    
                }, function(reason) {
                    $rootScope.error = true;
                    deferred.reject(reason);
                });
            }).
            error(function(result) {
                $rootScope.error = true;
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]).

service('SpecialLOService', ['$http', '$timeout', '$q', '$rootScope', 'LanguageService', 'ChildLOTransformer', function($http, $timeout, $q, $rootScope, LanguageService, ChildLOTransformer) {
    return {
        query: function(options) {
            var deferred = $q.defer();
            var queryParams = {
                uiLang: LanguageService.getLanguage()
            }

            if (options.lang) {
                queryParams.lang = options.lang
            }

            $http.get('../lo/special/' + options.id, {
                params: queryParams
            }).
            success(function(result) {
                ChildLOTransformer.transform(result);
                var loResult = {
                    lo: result,
                    provider: result.provider
                }
                deferred.resolve(loResult);
            }).
            error(function(result) {
                $rootScope.error = true;
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]).

/**
 * Resource for requesting Upper Secondary LO data
 */
service('UpperSecondaryLOService', ['$http', '$timeout', '$q', '$rootScope', 'LanguageService', 'ChildLOTransformer', function($http, $timeout, $q, $rootScope, LanguageService, ChildLOTransformer) {
    return {
        query: function(options) {
            var deferred = $q.defer();
            var queryParams = {
                uiLang: LanguageService.getLanguage()
            }

            if (options.lang) {
                queryParams.lang = options.lang
            }

            var url = '../lo/upsec/';

            $http.get(url + options.id, {
                params: queryParams
            }).
            success(function(result) {
                ChildLOTransformer.transform(result);
                var loResult = {
                    lo: result,
                    //parent: {},
                    provider: result.provider
                }
                deferred.resolve(loResult);
            }).
            error(function(result) {
                $rootScope.error = true;
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]).

/**
 * Resource for requesting University of Applied Sciences LO data
 */
service('HigherEducationLOService', ['$http', '$timeout', '$q', 'LanguageService', 'HigherEducationTransformer', function($http, $timeout, $q, LanguageService, HigherEducationTransformer) {
    return {
        query: function(options) {
            var deferred = $q.defer();
            var queryParams = {
                uiLang: LanguageService.getLanguage()
            }

            if (options.lang) {
                queryParams.lang = options.lang
            }

            var url = '../lo/highered/';

            $http.get(url + options.id, {
                params: queryParams
            }).
            
            //$http.get('mocks/amk.json', {}).
            success(function(result) {
            	HigherEducationTransformer.transform(result);
                var loResult = {
                    lo: result,
                    //parent: {},
                    provider: result.provider
                }
                
                deferred.resolve(loResult);
            }).
            error(function(result) {
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]).

/**
 * Resource for requesting University of Applied Sciences LO data
 */
service('HigherEducationPreviewLOService', ['$http', '$timeout', '$q', 'LanguageService', 'HigherEducationTransformer', 'Config', function($http, $timeout, $q, LanguageService, HigherEducationTransformer, Config) {
    return {
        query: function(options) {
            var deferred = $q.defer();
            var queryParams = {
                uiLang: LanguageService.getLanguage(),
                lang: LanguageService.getLanguage(),
                timestamp: Date.now()
            }

            if (options.lang) {
                queryParams.lang = options.lang
            }

            var url = '../lo/preview/';
            //var url = 'mocks/kk.json';
            //$http.get(url, {}).
            
            $http.get(url + options.id, {
                params: queryParams
            }).
            
            
            //$http.get('mocks/amk.json', {}).
            success(function(result) {
            	HigherEducationTransformer.transform(result);
            	result.preview = true;
            	result.tarjontaEditUrl =  Config.get('tarjontaUrl') + '/koulutus/' + result.id + '/edit?' + Date.now();
            	if (result.children) {
            		for (var i = 0; i < result.children.length; ++i) {
            			result.children[i].preview = true;
            		} 
            	}
            	if (result.applicationSystems) {
            		for (var i = 0; i < result.applicationSystems.length; ++i) {
            			var as = result.applicationSystems[i];
            			as.preview = true;
            			if (as.applicationOptions) {
            				for (var j = 0; j < as.applicationOptions.length; ++j) {
            					var ao = as.applicationOptions[j];
            					ao.preview = true;
            					ao.editUrl =  Config.get('tarjontaUrl') + '/hakukohde/' + ao.id + '/edit?' + Date.now();
            				}
            			}
            		} 
            	}
                var loResult = {
                    lo: result,
                    //parent: {},
                    provider: result.provider
                }
                deferred.resolve(loResult);
            }).
            error(function(result) {
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]).

/**
 * Transformer for parent LO data
 */
service('ParentLOTransformer', ['KiSorter', '$filter', '$rootScope', function(KiSorter, $filter, $rootScope) {
    return {
        transform: function(result) {

            // se LO translation language
            if (result && result.translationLanguage) {
                $rootScope.translationLanguage = result.translationLanguage;
            }

            if (result && result.provider && result.provider.name) {
                result.provider.encodedName = $filter('encodeURIComponent')('"' + result.provider.name + '"');
            }

            for (var loiIndex in result.lois) {
                if (result.lois.hasOwnProperty(loiIndex)) {
                    var loi = result.lois[loiIndex];
                    loi.availableTranslationLanguages = _.filter(loi.availableTranslationLanguages, function(item) { return item.value.toLowerCase() != result.translationLanguage});
                }
            } 


            // set teaching languge as the first language in array
            for (var index in result.lois) {
                if (result.lois.hasOwnProperty(index)) {
                    var loi = result.lois[index];
                    for (var asIndex in loi.applicationSystems) {
                        if (loi.applicationSystems.hasOwnProperty(asIndex)) {
                            var as = loi.applicationSystems[asIndex];
                            for (var aoIndex in as.applicationOptions) {
                                if (as.applicationOptions.hasOwnProperty(aoIndex)) {
                                    var ao = as.applicationOptions[aoIndex];

                                    if (ao.teachingLanguages && ao.teachingLanguages.length > 0) {
                                        ao.teachLang = ao.teachingLanguages[0];

                                        $rootScope.teachingLang = ao.teachLang.toLowerCase();
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // check if application system is of type Lisähaku
            for (var loiIndex in result.lois) {
                if (result.lois.hasOwnProperty(loiIndex)) {
                    var loi = result.lois[loiIndex];
                    for (var asIndex in loi.applicationSystems) {
                        if (loi.applicationSystems.hasOwnProperty(asIndex)) {
                            var as = loi.applicationSystems[asIndex];
                            if (as.applicationOptions && as.applicationOptions.length > 0) {
                                var firstAo = as.applicationOptions[0];
                                as.aoSpecificApplicationDates = firstAo.specificApplicationDates;
                            }
                        }
                    }
                }
            }

            // sort exams based on start time
            for (var index in result.lois) {
                if (result.lois.hasOwnProperty(index)) {
                    var loi = result.lois[index];
                    for (var asIndex in loi.applicationSystems) {
                        if (loi.applicationSystems.hasOwnProperty(asIndex)) {
                            var as = loi.applicationSystems[asIndex];
                            for (var aoIndex in as.applicationOptions) {
                                if (as.applicationOptions.hasOwnProperty(aoIndex)) {
                                    var ao = as.applicationOptions[aoIndex];
                                    for (var exam in ao.exams) {
                                        if (ao.exams.hasOwnProperty(exam)) {
                                            if (ao.exams[exam].examEvents) {
                                                ao.exams[exam].examEvents.sort(function(a, b) {
                                                    return a.start - b.start;
                                                });
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // group application systems by prerequisite
            var applicationSystemsByPrerequisite = {};
            angular.forEach(result.lois, function(loi, loikey) {
                angular.forEach(loi.applicationSystems, function(as, askey) {
                    as.loiId = loi.id;
                    if (applicationSystemsByPrerequisite[loi.prerequisite.value]) {
                        applicationSystemsByPrerequisite[loi.prerequisite.value].push(as);
                    } else {
                        applicationSystemsByPrerequisite[loi.prerequisite.value] = [];
                        applicationSystemsByPrerequisite[loi.prerequisite.value].push(as);
                    }

                });
            });

            // sort application systems and select active LOI
            var lois = [];
            angular.forEach(applicationSystemsByPrerequisite, function(asByPrerequisite, key){
                KiSorter.sortApplicationSystems(asByPrerequisite);
                
                if (asByPrerequisite.length > 0) {
                    var loiId = asByPrerequisite[0].loiId;
                }

                
                angular.forEach(result.lois, function(loi, loikey){
                    if (loi.id === loiId) {
                        loi.applicationSystems = asByPrerequisite;
                        lois.push(loi);
                    }
                });
            });
            result.lois = lois;

            // sort LOIs based on prerequisite
            if (result.lois) {
                result.lois.sort(function(a, b) {
                    if (a.prerequisite.description > b.prerequisite.description) return 1;
                    else if (a.prerequisite.description < b.prerequisite.description) return -1;
                    else return a.id > b.id ? 1 : -1;
                });
            }

            // aggregate childrefs from application options to application systems
            angular.forEach(result.lois, function(loi, loikey) {
                angular.forEach(loi.applicationSystems, function(as, askey) {
                    var children = [];
                    angular.forEach(as.applicationOptions, function(ao, aokey) {
                        angular.forEach(ao.childRefs, function(childref, childrefkey) {
                            var childFound = false;
                            angular.forEach(children, function(child) {
                                if (child.losId == childref.losId) {
                                    childFound = true;
                                }
                            });

                            if (!childFound) {
                                children.push(childref);
                            }
                        });
                    });

                    as.children = children;
                });
            });
        }
    }
}]).

/**
 * Transformer for child LO data
 */
service('HigherEducationTransformer', ['KiSorter', '$rootScope', '$filter', 'LanguageService', '_', function(KiSorter, $rootScope, $filter, LanguageService, _) {

	var getFirstItemInList = function(list) {
		if (list && list[0]) {
			return list[0];
		} else {
			return '';
		}
	};

	return {
		transform: function(result) {

			if (result && result.translationLanguage) {
				$rootScope.translationLanguage = result.translationLanguage;
			}

			if (result && result.availableTranslationLanguages) {
                result.availableTranslationLanguages = _.filter(result.availableTranslationLanguages, function(item) { return item.value.toLowerCase() != result.translationLanguage});
			}

			if (result && result.provider && result.provider.name) {
				result.provider.encodedName = $filter('encodeURIComponent')('"' + result.provider.name + '"');
			}
			if (result.startDate) {
				var startDate = new Date(result.startDate);
				result.startDate = startDate.getDate() + '.' + (startDate.getMonth() + 1) + '.' + startDate.getFullYear();
			}
			if (result.educationDegree && (result.educationDegree == 'koulutusasteoph2002_62' || result.educationDegree == 'koulutusasteoph2002_71')) {
				result.polytechnic = true;
			}
			result.teachingLanguage = getFirstItemInList(result.teachingLanguages);
			//result.formOfTeaching = getFirstItemInList(result.formOfTeaching);
			

			if (result.themes != undefined && result.themes != null) {
				var distinctMap = {};
				var distinctArray = [];
				for (var i=0; i < result.themes.length;i++) {
					var theme = result.themes[i];
					if (distinctMap[theme.uri] == undefined) {
						distinctMap[theme.uri] = theme;
						distinctArray.push(theme);
					}
				}
				result.themes = distinctArray;
			}
			
			

			for (var asIndex in result.applicationSystems) {
				if (result.applicationSystems.hasOwnProperty(asIndex)) {
					var as = result.applicationSystems[asIndex];
					for (var aoIndex in as.applicationOptions) {
						if (as.applicationOptions.hasOwnProperty(aoIndex)) {
							var ao = as.applicationOptions[aoIndex];

							if (ao.teachingLanguages && ao.teachingLanguages.length > 0) {
								ao.teachLang = ao.teachingLanguages[0];

								$rootScope.teachingLang = LanguageService.getLanguage();//ao.teachLang.toLowerCase();
							}
						}
					}
				}
			}

			for (var asIndex in result.applicationSystems) {
				if (result.applicationSystems.hasOwnProperty(asIndex)) {
					var as = result.applicationSystems[asIndex];
					for (var aoIndex in as.applicationOptions) {
						if (as.applicationOptions.hasOwnProperty(aoIndex)) {
							var ao = as.applicationOptions[aoIndex];
							for (var exam in ao.exams) {
								if (ao.exams.hasOwnProperty(exam)) {
									if (ao.exams[exam].examEvents) {
										ao.exams[exam].examEvents.sort(function(a, b) {
											return a.start - b.start;
										});
									}
								}
							}
						}
					}
				}
			}

			// group application systems by prerequisite
			var applicationSystemsByPrerequisite = {};

			angular.forEach(result.applicationSystems, function(as, askey) {


				angular.forEach(as.applicationOptions, function(ao, aokey) { 
					angular.forEach(ao.requiredBaseEducations, function(prerequisite, prereqKey) {
						if (applicationSystemsByPrerequisite[prerequisite]) {
							applicationSystemsByPrerequisite[prerequisite].push(as);
						} else {
							applicationSystemsByPrerequisite[prerequisite] = [];
							applicationSystemsByPrerequisite[prerequisite].push(as);
						}
					});
				});

			});


			// sort application systems and select active LOI

			angular.forEach(applicationSystemsByPrerequisite, function(asByPrerequisite, key){
				KiSorter.sortApplicationSystems(asByPrerequisite);

			});


			// check if application system is of type Lisähaku

			for (var asIndex in result.applicationSystems) {
				if (result.applicationSystems.hasOwnProperty(asIndex)) {
					var as = result.applicationSystems[asIndex];
					if (as.applicationOptions && as.applicationOptions.length > 0) {
						var firstAo = as.applicationOptions[0];
						as.aoSpecificApplicationDates = firstAo.specificApplicationDates;
					}
				}
			}
		}
	}
}]).

/**
 * Transformer for child LO data
 */
service('ChildLOTransformer', ['UtilityService', 'KiSorter', '$rootScope', function(UtilityService, KiSorter, $rootScope) {

    var getFirstItemInList = function(list) {
        if (list && list[0]) {
            return list[0];
        } else {
            return '';
        }
    };

    return {
        transform: function(result) {
            var studyplanKey = "KOULUTUSOHJELMA";

            if (result && result.translationLanguage) {
                $rootScope.translationLanguage = result.translationLanguage;
            }

            for (var loiIndex in result.lois) {
                if (result.lois.hasOwnProperty(loiIndex)) {
                    var loi = result.lois[loiIndex];
                    loi.availableTranslationLanguages = _.filter(loi.availableTranslationLanguages, function(item) { return item.value.toLowerCase() != result.translationLanguage});
                }
            } 
            

            for (var loiIndex in result.lois) {
                if (result.lois.hasOwnProperty(loiIndex)) {
                    var loi = result.lois[loiIndex];

                    var startDate = new Date(loi.startDate);
                    loi.startDate = startDate.getDate() + '.' + (startDate.getMonth() + 1) + '.' + startDate.getFullYear();
                    loi.teachingLanguage = getFirstItemInList(loi.teachingLanguages);
                    loi.formsOfTeaching = loi.formOfTeaching;
                    loi.formOfTeaching = getFirstItemInList(loi.formOfTeaching);
                    

                    if (loi.webLinks) {
                        loi.studyPlan = loi.webLinks[studyplanKey];
                    }
                }
            }

            // set teaching languge as the first language in array
            for (var index in result.lois) {
                if (result.lois.hasOwnProperty(index)) {
                    var loi = result.lois[index];
                    for (var asIndex in loi.applicationSystems) {
                        if (loi.applicationSystems.hasOwnProperty(asIndex)) {
                            var as = loi.applicationSystems[asIndex];
                            for (var aoIndex in as.applicationOptions) {
                                if (as.applicationOptions.hasOwnProperty(aoIndex)) {
                                    var ao = as.applicationOptions[aoIndex];

                                    if (ao.teachingLanguages && ao.teachingLanguages.length > 0) {
                                        ao.teachLang = ao.teachingLanguages[0];
                                        
                                        $rootScope.teachingLang = ao.teachLang.toLowerCase();
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // sort exams based on start time
            for (var loiIndex in result.lois) {
                if (result.lois.hasOwnProperty(loiIndex)) {
                    var loi = result.lois[loiIndex];
                    for (var asIndex in loi.applicationSystems) {
                        if (loi.applicationSystems.hasOwnProperty(asIndex)) {
                            var as = loi.applicationSystems[asIndex];
                            for (var aoIndex in as.applicationOptions) {
                                if (as.applicationOptions.hasOwnProperty(aoIndex)) {
                                    var ao = as.applicationOptions[aoIndex];
                                    for (var exam in ao.exams) {
                                        if (ao.exams.hasOwnProperty(exam)) {
                                            if (ao.exams[exam].examEvents) {
                                                ao.exams[exam].examEvents.sort(function(a, b) {
                                                    return a.start - b.start;
                                                });
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // check if application system is of type Lisähaku
            for (var loiIndex in result.lois) {
                if (result.lois.hasOwnProperty(loiIndex)) {
                    var loi = result.lois[loiIndex];
                    for (var asIndex in loi.applicationSystems) {
                        if (loi.applicationSystems.hasOwnProperty(asIndex)) {
                            var as = loi.applicationSystems[asIndex];
                            if (as.applicationOptions && as.applicationOptions.length > 0) {
                                var firstAo = as.applicationOptions[0];
                                as.aoSpecificApplicationDates = firstAo.specificApplicationDates;
                            }
                        }
                    }
                }
            }

            // group application systems by prerequisite
            var applicationSystemsByPrerequisite = {};
            angular.forEach(result.lois, function(loi, loikey) {
                angular.forEach(loi.applicationSystems, function(as, askey) {
                    as.loiId = loi.id;
                    if (applicationSystemsByPrerequisite[loi.prerequisite.value]) {
                        applicationSystemsByPrerequisite[loi.prerequisite.value].push(as);
                    } else {
                        applicationSystemsByPrerequisite[loi.prerequisite.value] = [];
                        applicationSystemsByPrerequisite[loi.prerequisite.value].push(as);
                    }

                });
            });

            // sort application systems and select active LOI
            var lois = [];
            angular.forEach(applicationSystemsByPrerequisite, function(asByPrerequisite, key){
                KiSorter.sortApplicationSystems(asByPrerequisite);
                
                if (asByPrerequisite.length > 0) {
                    var loiId = asByPrerequisite[0].loiId;
                }

                
                angular.forEach(result.lois, function(loi, loikey){
                    if (loi.id === loiId) {
                        loi.applicationSystems = asByPrerequisite;
                        loi.applicationSystems = UtilityService.groupByApplicationSystem(loi.applicationSystems);
                        lois.push(loi);
                    }
                });
            });

            result.lois = lois;

            // sort language selection alphabetically
            angular.forEach(result.lois, function(loi, loikey){
                UtilityService.sortLanguageSelection(loi.languageSelection);
            });

            

            // sort LOIs based on prerequisite
            if (result.lois) {
                result.lois.sort(function(a, b) {
                    if (a.prerequisite.description > b.prerequisite.description) return 1;
                    else if (a.prerequisite.description < b.prerequisite.description) return -1;
                    else return a.id > b.id ? 1 : -1;
                });
            }

            // aggregate childrefs from application options to application systems
            angular.forEach(result.lois, function(loi, loikey) {
                angular.forEach(loi.applicationSystems, function(as, askey) {
                    var children = [];
                    angular.forEach(as.applicationOptions, function(ao, aokey) {
                        angular.forEach(ao.childRefs, function(childref, childrefkey) {
                            var childFound = false;
                            angular.forEach(children, function(child) {
                                if (child.losId == childref.losId) {
                                    childFound = true;
                                }
                            });

                            if (!childFound) {
                                children.push(childref);
                            }
                        });
                    });

                    as.children = children;
                });
            });
        }
    }
}]).

/**
 *  Transform search result data
 */
service('LearningOpportunitySearchResultTransformer', ['$filter', '$rootScope', function($filter, $rootScope) {
    return {
        transform: function(result) {

            // order themes alphabetically (theme Yleisisivistävä is always first)
            if (result && result.topicFacet && result.topicFacet.facetValues) {
                result.topicFacet.facetValues.sort(function(a, b) {
                    var regexp = /^teemat_1$/;
                    if (regexp.test(a.valueId)) {
                        return -1;
                    } else if (regexp.test(b.valueId)) {
                        return 1;
                    } else {
                        return b.valueName > a.valueName ? -1 : 1;
                    }
                });

                // order theme subjects alphabetically
                angular.forEach(result.topicFacet.facetValues, function(facet, key) {
                    if (facet.childValues) {
                        facet.childValues.sort(function(a, b) {
                            return b.valueName > a.valueName ? -1 : 1;
                        });
                    }
                });
            }

            // order teaching languages in order: FI, SV, EN, other languages in alphabetical order
            if (result && result.teachingLangFacet && result.teachingLangFacet.facetValues ) {
                result.teachingLangFacet.facetValues.sort( function(a, b) {

                    if (a.valueId == "FI" && b.valueId == "SV") return -1;
                    else if (a.valueId == "SV" && b.valueId == "FI") return 1;
                    else if (a.valueId == "FI" && b.valueId == "EN") return -1;
                    else if (a.valueId == "EN" && b.valueId == "FI") return 1;
                    else if (a.valueId == "SV" && b.valueId == "EN") return -1;
                    else if (a.valueId == "EN" && b.valueId == "SV") return 1;
                    else return 1

                });
            }
        }
    }
}]).

service('SearchResultFacetTransformer', ['UtilityService', '$filter', function(UtilityService, $filter) {
	
	var getFacetValById = function(valueId, givenVals) {
    	
    	var selectedEdTypeFacetVal = undefined;
    	var edTypeFacetVals = [];
    	edTypeFacetVals = edTypeFacetVals.concat(givenVals);
    	
    	while (edTypeFacetVals != null && edTypeFacetVals.length > 0) {
			
			var currentValue = edTypeFacetVals.shift();
			
			if (currentValue != null && currentValue.valueId == valueId) {
				selectedEdTypeFacetVal = currentValue;
			}
			
			if (currentValue != null && selectedEdTypeFacetVal == undefined) {
				edTypeFacetVals = edTypeFacetVals.concat(currentValue.childValues);
			}
		}
    	return selectedEdTypeFacetVal;
    };
    
	return {
    	
        transform: function(result, facetFilters) {

        	var loResult = result;
        	
        	var wasEducationType = false;
        	var educationtypeSelection = undefined;
        	var wasTheme = false;
        	var wasTopic = false;
        	var themeTopicSelection = undefined;
        	
        	angular.forEach(facetFilters, function(value, index) {
        		var curVal = value.split(':')[1];
        		var curField = value.split(':')[0];
        		if ((curField == 'educationType_ffm')) {
        			
        			wasEducationType = true;
        			
        			educationtypeSelection = curVal;
        		} else if (curField == 'theme_ffm') {
        			wasTheme = true;
        			themeTopicSelection = curVal;
        		} else if (curField == 'topic_ffm') {
        			wasTopic = true;
        			themeTopicSelection = curVal;
        		}
        	});
        	
        	
        	if (wasTheme) {
        		var selectedThemeFacetVal = getFacetValById(themeTopicSelection, loResult.topicFacet.facetValues);
        		topicFacetValues = [];
    			topicFacetValues.push(selectedThemeFacetVal);
    			
    			loResult.topicFacetValues = topicFacetValues;
        	} else if (wasTopic) {
        		var selectedTopicFacetVal = getFacetValById(themeTopicSelection, loResult.topicFacet.facetValues);
        		var parentThemeVal = getFacetValById(selectedTopicFacetVal.parentId, loResult.topicFacet.facetValues);
        		parentThemeVal.childValues = [];
        		parentThemeVal.childValues.push(selectedTopicFacetVal);
        		
        		topicFacetValues = [];
    			topicFacetValues.push(parentThemeVal);
    			
    			loResult.topicFacetValues = topicFacetValues;
        	} else {
        		angular.forEach(loResult.topicFacet.facetValues, function(value, index) {
            		value.childValues = [];
            	});
        		
        		loResult.topicFacetValues = loResult.topicFacet.facetValues;
        	}
        	
        	if (wasEducationType) {
        		
        		var selectedEdTypeFacetVal = getFacetValById(educationtypeSelection, loResult.edTypeFacet.facetValues);  
        		if (selectedEdTypeFacetVal != undefined) {
        			
        			angular.forEach(selectedEdTypeFacetVal.childValues, function(value, index) {
        				value.childValues = [];
        			});
        			
        			var parent = getFacetValById(selectedEdTypeFacetVal.parentId, loResult.edTypeFacet.facetValues);
        			
        			while (parent != undefined && parent != null) {
        				parent.childValues = [];
    					parent.childValues.push(selectedEdTypeFacetVal);
        				selectedEdTypeFacetVal = parent;
        				parent = getFacetValById(selectedEdTypeFacetVal.parentId, loResult.edTypeFacet.facetValues);
        			}
        			
        			edTypeFacetValues = [];
        			edTypeFacetValues.push(selectedEdTypeFacetVal);
        			
        			loResult.edTypeFacetValues = edTypeFacetValues;
        		} else {
        			angular.forEach(loResult.edTypeFacet.facetValues, function(value, index) {
                		value.childValues = [];
                	});
            		
            		loResult.edTypeFacetValues = loResult.edTypeFacet.facetValues;
        		}
        	} else {
        		angular.forEach(loResult.edTypeFacet.facetValues, function(value, index) {
            		value.childValues = [];
            	});
        		
        		loResult.edTypeFacetValues = loResult.edTypeFacet.facetValues;
        	}
        	
        	return loResult;
        	
        }
    }
}]).

/**
 *  Resource for requesting LO provider picture
 */
service('LearningOpportunityProviderPictureService', ['$http', '$timeout', '$q', function($http, $timeout, $q) {
    return  {
        query: function(options) {
            var deferred = $q.defer();

            $http.get('../lop/' + options.providerId + '/picture').
            success(function(result) {
                deferred.resolve(result);
            }).
            error(function(result) {
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]).

/**
 *  Resource for requesting LO picture
 */
service('LearningOpportunityPictureService', ['$http', '$timeout', '$q', function($http, $timeout, $q) {
    return  {
        query: function(options) {
            var deferred = $q.defer();
            
            $http.get('../lo/picture/' + options.pictureId).
            success(function(result) {
                deferred.resolve(result);
            }).
            error(function(result) {
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]).


/**
 *  Service taking care of search term saving
 */
 service('SearchService', ['CookieService', function(CookieService) {
    var key = 'searchTerm';
    return {
        getTerm: function() {
            return CookieService.get(key) || '';
        },

        setTerm: function(newTerm) {
            if (newTerm) {
                CookieService.set(key, newTerm);
            }
        }
    };
}]).

/**
 *  Service keeping track of the current language selection
 */
service('LanguageService', ['CookieService', function(CookieService) {
    var defaultLanguage = 'fi';
    var key = 'i18next';

    return {
        getLanguage: function() {
            return CookieService.get(key) || defaultLanguage;
        },

        setLanguage: function(language) {
            CookieService.set(key, language);
        },

        getDefaultLanguage: function() {
            return defaultLanguage;
        }
    };
}]).

/**
 *  Service keeping track of virkalija language selection
 */
service('VirkailijaLanguageService', ['CookieService', function(CookieService) {
    var defaultLanguage = 'fi';
    var key = 'virkailijaLang';

    return {
        getLanguage: function() {
            return CookieService.get(key) || defaultLanguage;
        },

        setLanguage: function(language) {
            CookieService.set(key, language);
        },

        getDefaultLanguage: function() {
            return defaultLanguage;
        }
    };
}]).

/**
 *  Service for "caching" current parent selection
 */
 /*
 service('ParentLODataService', function() {
    var data;

    return {
        getParentLOData: function() {
            return data;
        },

        setParentLOData: function(newData) {
            data = newData;
        },

        dataExists: function(id) {
            return data && data.id == id; 
        }
    };
}).
*/

/**
 *  Service for "caching" current child selection
 */
 /*
 service('ChildLODataService', function() {
    var data;

    return {
        getChildLOData: function() {
            return data;
        },

        setChildLOData: function(newData) {
            data = newData;
        },

        dataExists: function(id) {
            return data && data.id == id; 
        }
    };
}).
*/

/**
 *  Service for maintaining application basket state
 */
service('ApplicationBasketService', ['$http', '$q', '$rootScope', 'LanguageService', 'UtilityService', 'CookieService', function($http, $q, $rootScope, LanguageService, UtilityService, CookieService) {
    var key = 'basket';
    var typekey = 'baskettype';
    var cookieConfig = {useLocalStorage: false, maxChunkSize: 2000, maxNumberOfCookies: 20, path: '/'};

    var transformData = function(result) {

        var createLinkToLo = function(ao) {
            var loRef = ao.type + '/';

            switch(ao.type) {
                case 'korkeakoulu':
                    loRef += ao.higherEducations && ao.higherEducations.length > 0 ? ao.higherEducations[0].id : '';
                    break;
                case 'lukio':
                    loRef += ao.losId;
                    break;
                case 'tutkinto':
                    loRef += ao.parent.id;
                    break;
                case 'valmistava':
                    loRef += ao.parent.id;
                    break;
            }

            loRef += '?';
            loRef += ao.prerequisite ? 'prerequisite=' + ao.prerequisite.value + '&' : '';
            loRef += 'tab=1';

            return loRef;
        };

        for (var asIndex in result) {
            if (result.hasOwnProperty(asIndex)) {
                var applicationDates = result[asIndex].applicationDates;
                if (applicationDates && applicationDates.length > 0) {
                    result[asIndex].applicationDates = applicationDates[0];
                }

                var applicationOptions = result[asIndex].applicationOptions;
                for (var i in applicationOptions) {
                    if (applicationOptions.hasOwnProperty(i)) {
                        if (applicationOptions[i].children && applicationOptions[i].children.length > 0) {
                            result[asIndex].applicationOptions[i].qualification = applicationOptions[i].children[0].qualification;
                        }

                        // set teaching languge as the first language in array
                        var ao = applicationOptions[i];
                        if (ao.teachingLanguages && ao.teachingLanguages.length > 0) {
                            ao.teachLang = ao.teachingLanguages[0];
                        }

                        // set LOS id for lukio
                        // check if ao is of type lukio
                        ao.losId = (ao.children && ao.children.length > 0) ? ao.children[0].losId : '';

                        // transform type to lower case
                        ao.type = ao.type ? ao.type.toLowerCase() : '';

                        // set link to lo
                        ao.loRef = createLinkToLo(ao);
                    }
                }
            }
        }

        return result;
    };

    return {
        addItem: function(aoId, itemType) {

            var current = CookieService.get(key);

            if (current) {
                current = JSON.parse(current);

                // do not add same ao twice
                if (current.indexOf(aoId) < 0) {
                        current.push(aoId);
                }
            } else {
                current = [];
                current.push(aoId);
            }

            // save type if defined
            if (itemType) {
                CookieService.set(typekey, itemType, cookieConfig);
            }

            CookieService.set(key, JSON.stringify(current), cookieConfig);
        },

        removeItem: function(aoId) {
            if (this.getItemCount() > 1) {
                var value = CookieService.get(key);
                value = JSON.parse(value);

                var index = value.indexOf(aoId);
                value.splice(index, 1);

                CookieService.set(key, JSON.stringify(value), cookieConfig);
            } else {
                this.empty();
            }
        },

        empty: function() {
            CookieService.set(key, null, cookieConfig);
            CookieService.set(typekey, null, cookieConfig);
        },

        getItems: function() {
            return JSON.parse( CookieService.get(key) );
        },

        getItemCount: function() {
            return CookieService.get(key) ? JSON.parse( CookieService.get(key) ).length : 0;
        },

        isEmpty: function() {
            return this.getItemCount() <= 0;
        },

        getType: function() {
            if (!this.isEmpty()) {
                return CookieService.get(typekey);
            }
        },

        itemExists: function(aoId) {
            var result = false;
            angular.forEach(this.getItems(), function(item, key) {
                if (aoId == item) {
                    result = true;
                }
            });

            return result;
        },

        query: function(params) {
            var deferred = $q.defer();
            var basketItems = this.getItems();

            var qParams = 'uiLang=' + LanguageService.getLanguage();

            
            for (var index = 0; index < basketItems.length; index++) {
                if (basketItems.hasOwnProperty(index)) {
                    qParams += '&aoId=' + basketItems[index];
                }
            }

            $http.get('../basket/items?' + qParams).
            success(function(result) {
                result = transformData(result);
                deferred.resolve(result);
            }).
            error(function(result) {
                $rootScope.error = true;
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]).

/**
 *  Service for maintaining search filter state
 */
service('FilterService', ['$q', '$http', 'UtilityService', 'LanguageService', 'kiAppConstants', '_', function($q, $http, UtilityService, LanguageService, kiAppConstants, _) {
    var filters = {};

    var filterIsEmpty = function(filter) {
        if (filter == undefined || filter == null) return true;
        else if (typeof filter == 'boolean' && !filter) return true;
        else if (filter instanceof Array && filter.length <= 0 ) return true;
        else return false;
    }

    var getLocationCodes = function() {
        var codes = [];
        angular.forEach(filters.locations, function(value, key) {
            codes.push(value.code);
        });

        return codes;
    }

    var set = function(newFilters) {
        filters = {};
        for (var i in newFilters) {
            if (newFilters.hasOwnProperty(i)) {
                var filter = newFilters[i];

                if (!filterIsEmpty(filter)) {
                    filters[i] = filter;
                }
            }
        }
    }

    /*
    var setLocations = function(locations) {
        filters.locations = locations;
    }
    */

    return {
        query: function(queryParams) {
            var deferred = $q.defer();

            var codes = ''
            var locationCodes = (queryParams.locations && typeof queryParams.locations == 'string') ? UtilityService.getStringAsArray(queryParams.locations) : queryParams.locations || [];

            angular.forEach(locationCodes, function(value, key){
                codes += '&code=' + value;
            });

            var uiLang = LanguageService.getLanguage();

            if (locationCodes.length > 0) {
                $http.get('../location?lang=' + uiLang + codes, {
                }).
                success(function(result) {
                    queryParams.locations = result;
                    set(queryParams);
                    deferred.resolve();
                }).
                error(function(result) {
                    deferred.reject();
                });
            } else {
                queryParams.locations = [];
                set(queryParams);
                deferred.resolve();
            }

            return deferred.promise;
        },

        set: function(newFilters) {
            set(newFilters);
        },

        get: function() {
            var result =  {
                prerequisite: filters.prerequisite,
                locations: getLocationCodes(),
                ongoing: filters.ongoing,
                upcoming: filters.upcoming,
                upcomingLater: filters.upcomingLater,
                page: filters.page,
                articlePage: filters.articlePage,
                facetFilters: filters.facetFilters,
                langCleared: filters.langCleared,
                itemsPerPage: filters.itemsPerPage,
                sortCriteria: filters.sortCriteria,
                lopFilter: filters.lopFilter,
                educationCodeFilter: filters.educationCodeFilter,
                excludes: filters.excludes,
                articleFacetFilters : filters.articleFacetFilters
            };

            angular.forEach(result, function(value, key) {
                if (value instanceof Array && value.length <= 0 || !value) {
                    delete result[key];
                }
            });


            return result;
        },

        clear: function() {
            filters = {};
        },

        getPrerequisite: function() {
            if (filters.prerequisite) {
                return filters.prerequisite;
            }
        },

        isOngoing: function() {
            return filters.ongoing;
        },
        
        isUpcoming: function() {
            return filters.upcoming;
        },
        
        isUpcomingLater: function() {
            return filters.upcomingLater;
        },

        getLocations: function() {
            return filters.locations;
        },

        getLocationNames: function() {
            var locations = [];
            angular.forEach(filters.locations, function(value, key) {
                locations.push(value.name);
            });

            return locations;
        },

        setPage: function(value) {
            if (value && !isNaN(value)) {
                filters.page = parseInt(value);
            } else {
                filters.page = 1;
            }
        },

        getPage: function() {
            if (filters.page) {
                return typeof filters.page === 'string' ? parseInt(filters.page) : filters.page;
            } else {
                return 1;
            }
        },

        setArticlePage: function(value) {
            if (value && !isNaN(value)) {
                filters.articlePage = parseInt(value);
            } else {
                filters.articlePage = 1;
            }
        },

        getArticlePage: function() {
            if (filters.articlePage) {
                return typeof filters.articlePage === 'string' ? parseInt(filters.articlePage) : filters.articlePage;
            } else {
                return 1;
            }
        },

        getLocationCodes: getLocationCodes,

        getParams: function() {
            var params = '';
            params += filters.prerequisite ? '&prerequisite=' + filters.prerequisite : '';
            params += (filters.locations && filters.locations.length > 0) ? '&locations=' + getLocationCodes().join(',') : '';
            params += filters.ongoing ? '&ongoing' : '';
            params += filters.upcoming ? '&upcoming' : '';
            params += filters.upcomingLater ? '&upcomingLater' : '';
            params += filters.page ? '&page=' + filters.page : '';
            params += (filters.facetFilters && filters.facetFilters.length > 0) ? '&facetFilters=' + filters.facetFilters.join(',') : '';
            params += filters.langCleared ? '&langCleared=' + filters.langCleared : '';
            params += filters.itemsPerPage ? '&itemsPerPage=' + filters.itemsPerPage : '';
            params += filters.sortCriteria ? '&sortCriteria=' + filters.sortCriteria : '';
            params += filters.lopFilter ? '&lopFilter=' + filters.lopFilter : '';
            params += filters.educationCodeFilter ? '&educationCodeFilter=' + filters.educationCodeFilter : '';
            params += (filters.excludes && filters.excludes.length > 0) ? '&excludes=' + filters.excludes.join('|') : '';
            params += (filters.articleFacetFilters && filters.articleFacetFilters.length > 0) ? '&articleFacetFilters=' + filters.articleFacetFilters.join(',') : '';
            params = params.length > 0 ? params.substring(1, params.length) : '';
            return params;
        },
        
        getFacetFilters: function() {
        	if (filters.facetFilters != undefined && (typeof filters.facetFilters == 'string' || filters.facetFilters instanceof String)) {
        		filters.facetFilters = filters.facetFilters.split(',');
        		return filters.facetFilters;
        	}
        	return filters.facetFilters;
        },
        
        getArticleFacetFilters: function() {
        	if (filters.articleFacetFilters != undefined && (typeof filters.articleFacetFilters == 'string' || filters.articleFacetFilters instanceof String)) {
        		filters.articleFacetFilters = filters.articleFacetFilters.split(',');
        		return filters.articleFacetFilters;
        	}
        	return filters.articleFacetFilters;
        },
        
        getLopFilter: function() {
        	return filters.lopFilter;
        },
        
        getEducationCodeFilter: function() {
        	return filters.educationCodeFilter;
        },
        
        getExcludes: function() {
        	if (filters.excludes != undefined && (typeof filters.excludes == 'string' || filters.excludes instanceof String)) {
        		filters.excludes = filters.excludes.split('|');
        	}
        	return filters.excludes;
        },
        
        getLangCleared: function() {
        	return filters.langCleared;
        },

        getItemsPerPage: function() {
            if (filters.itemsPerPage) {
                return typeof filters.itemsPerPage === 'string' ? parseInt(filters.itemsPerPage) : filters.itemsPerPage;
            } else {
                return kiAppConstants.searchResultsPerPage;
            }
        },

        setItemsPerPage: function(value) {
            if (value && !isNaN(value)) {
                filters.itemsPerPage = parseInt(value);
            } else {
                filters.itemsPerPage = kiAppConstants.searchResultsPerPage;
            }
        },

        getSortCriteria: function() {
            if (filters.sortCriteria) {
                return filters.sortCriteria;
            } else {
                return kiAppConstants.defaultSortCriteria;
            }
        },

        setSortCriteria: function(value) {
            if (value) {
                filters.sortCriteria = value;
            } else {
                filters.sortCriteria = kiAppConstants.defaultSortCriteria;
            }
        }
    };
}]).

/**
 * Keeps up information about collapse block hide/show status
 */
service('CollapseBlockService', function() {
    var blocks = {};

    return {
        setBlock: function(id, value) {
            if (value) {
                blocks[id] = value;
            } else {
                blocks[id] = false;
            }
        },

        getBlock: function(id) {
            if (blocks[id] === undefined) {
                return true;
            } else {
                return blocks[id];
            }
        }
    }
}).

/**
 *  Sort application systems and application options by complex rules
 */
service('KiSorter', ['UtilityService', function(UtilityService) {
    var sortApplicationSystems = function(applicationSystems) {

        var isHakuKaynnissa = function(as) {
            var isOngoing = false;
            angular.forEach(as.applicationOptions, function(item) {
                if (item.canBeApplied) {
                    isOngoing = true;
                }
            });

            return isOngoing;
        }

        var isVarsinainenYhteishakuKaynnissa = function(as) {
            return UtilityService.isYhteishaku(as) && UtilityService.isVarsinainenHaku(as) && isHakuKaynnissa(as);
        }

        var isLisaYhteishakuKaynnissa = function(as) {
            return UtilityService.isYhteishaku(as) && UtilityService.isLisahaku(as) && isHakuKaynnissa(as);
        }

        var isVarsinainenYhteishakuTulossaHakuun = function(as) {
            var earliest = -1;
            var limit = 30 * 24 * 60 * 60 * 1000;
            var ts = new Date().getTime();
            if (UtilityService.isYhteishaku(as) && UtilityService.isVarsinainenHaku(as)) {
                angular.forEach(as.applicationOptions, function(ao) {
                    if (earliest < 0 || ao.applicationStartDate < earliest) {
                        earliest = ao.applicationStartDate;
                    }
                });

                var delta = earliest - ts
                return delta >= 0 && delta < limit;
            }

            return false;
        }

        var getEarliestStartDate = function(as) {
            var earliest = -1;
            angular.forEach(as.applicationOptions, function(ao) {
                if (earliest < 0 || ao.applicationStartDate < earliest) {
                    earliest = ao.applicationStartDate;
                }
            });

            return earliest;
        }


        if (applicationSystems) {
            applicationSystems.sort(function(a, b) {

                /*
                Hakujen järjestys:
                1. Käynnissä oleva varsinainen yhteishaku
                2. Käynnissä oleva yhteishaun lisähaku
                3. 30 päivän sisällä hakuun tuleva varsinainen yhteishaku
                4. Mikä tahansa käynnissä oleva haku
                5. Haku, joka alkaa ensimmäisenä
                6. Haku, jonka nimi on aakkosissa ensimmäisenä
                */
                if (isVarsinainenYhteishakuKaynnissa(a) != isVarsinainenYhteishakuKaynnissa(b)) {
                    return isVarsinainenYhteishakuKaynnissa(a) ? -1 : 1
                } else if (isLisaYhteishakuKaynnissa(a) != isLisaYhteishakuKaynnissa(b)) {
                    return isLisaYhteishakuKaynnissa(a) ? -1 : 1;
                } else if (isVarsinainenYhteishakuTulossaHakuun(a) != isVarsinainenYhteishakuTulossaHakuun(b)) {
                    return isVarsinainenYhteishakuTulossaHakuun(a) ? -1 : 1;
                } else if (isHakuKaynnissa(a) != isHakuKaynnissa(b)) {
                    return isHakuKaynnissa(a) ? -1 : 1;
                } else if (getEarliestStartDate(a) != getEarliestStartDate(b)) {
                    return getEarliestStartDate(a) - getEarliestStartDate(b);
                } else {
                    if (a.name < b.name) return -1;
                    else if (a.name > b.name) return 1;
                    else return 0;
                }
            });
        }
    };

    var sortApplicationOptions = function(applicationOptions) {
        if (applicationOptions) {
            applicationOptions.sort(function(a, b) {

                /*
                Hakukohteiden järjestys:
                1. Käynnissä oleva hakukohde
                2. Hakukohde, jonka haku alkaa ensimmäisenä
                3. Hakukohde, jonka nimi on aakkosissa ensimmäisenä
                */
                if (a.canBeApplied != b.canBeApplied) {
                    return a.canBeApplied ? -1 : 1;
                } else if (a.applicationStartDate != b.applicationStartDate) {
                    return a.applicationStartDate - b.applicationStartDate;
                } else {
                    if (a.name < b.name) return -1;
                    else if (a.name > b.name) return 1;
                    else return 0;
                }
            });
        }
    };

    return {
        sortApplicationSystems: function(applicationSystems) {
            sortApplicationSystems(applicationSystems);
            angular.forEach(applicationSystems, function(as) {
                sortApplicationOptions(as.applicationOptions);
            });
        }
    }
}]).

/**
 *  Service for retrieving translated values for text
 */
service('UtilityService', function() {
    var hakutapa = {
        yhteishaku: '01',
        erillishaku: '02',
        jatkuva: '03'
    };

    var hakutyyppi = {
        varsinainen: '01',
        taydennys: '02',
        lisa: '03'
    };

    var isLisahaku = function(as) {
        return as.hakutyyppi == hakutyyppi.lisa;
    }

    var isYhteishaku = function(as) {
        return as.hakutapa == hakutapa.yhteishaku;
    }

    var isVarsinainenHaku = function(as) {
        return as.hakutyyppi == hakutyyppi.varsinainen;
    }

    return {
        getApplicationOptionById: function(aoId, aos) {
            if (aos && aos.length > 0) {
                for (var index in aos) {
                    if (aos.hasOwnProperty(index)) {
                        if (aos[index].id == aoId) {
                            return aos[index];
                        }
                    }
                }
            }
        },
        getStringAsArray: function(stringToArray) {
            var delimiter = ',';
            if (stringToArray && typeof stringToArray == 'string') {
                return stringToArray.split(delimiter);
            }
        },
        isLukio: function(lo) {
            return lo.educationDegree == 31 ? true : false;
        },
        isYhteishaku: isYhteishaku,
        isVarsinainenHaku: isVarsinainenHaku,
        isLisahaku: isLisahaku,
        groupByApplicationSystem: function(applicationSystems) {
            result = [];
            angular.forEach(applicationSystems, function(as, askey){
                var found;
                angular.forEach(result, function(item, itemkey){
                    if (item.id == as.id) {
                        found = item;
                    }
                });

                if (found) {
                    // add application options to found item
                    angular.forEach(as.applicationOptions, function(ao, aokey) {
                        var aoFound = false;
                        angular.forEach(found.applicationOptions, function(aoitem, aoitemkey){
                            if (ao.id == aoitem.id) {
                                aoFound = true;
                            }
                        });

                        if (!aoFound) {
                            found.applicationOptions.push(ao);
                        }
                    });
                } else {
                    result.push(as);
                }
            });

            return result;
        },
        sortLanguageSelection: function(languageSelection) {
            if (languageSelection) {
                languageSelection.sort(function(a, b) {
                    if(a.subjectCode < b.subjectCode) return -1;
                    if(a.subjectCode > b.subjectCode) return 1;
                    return 0;
                });
            }
        },
        sortLocationsByName: function(locations) {
            locations.sort(function(a, b) {
                if (a.name > b.name) {
                    return 1;
                } else if (a.name < b.name) {
                    return -1;
                } else {
                    return 0;
                }
            });
        },
        padWithZero: function(number) {
            number = number.toString();
            if (number.length <= 1) {
                return "0" + number;
            } else {
                return number;
            }
        }
    };
});