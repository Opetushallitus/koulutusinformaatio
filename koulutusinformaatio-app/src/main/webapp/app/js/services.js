/*  Services */

angular.module('kiApp.services', 
[
    'ngResource', 
    'kiApp.HostResolver', 
    'kiApp.NavigationService',
    'kiApp.ArticleContentSearchService',
    'kiApp.TranslationService'
]).

service('SearchLearningOpportunityService', ['$http', '$timeout', '$q', '$analytics', '$rootScope', 'FilterService', 'LearningOpportunitySearchResultTransformer', function($http, $timeout, $q, $analytics, $rootScope, FilterService, LearningOpportunitySearchResultTransformer) {
    
    // gather information for analytics
    var parseFilterValues = function(params) {
        var getTilaValue = function(params) {
            var result = 0;
            result = params.ongoing ? result + 1 : result;
            result = params.upcoming ? result + 1 : result;

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
            qParams += (params.lang != undefined) ? ('&lang=' + params.lang) : '';
            qParams += (params.lopFilter != undefined) ? ('&lopFilter=' + params.lopFilter) : '';
            qParams += (params.educationCodeFilter != undefined) ? ('&educationCodeFilter=' + params.educationCodeFilter) : '';
            
            if (params.facetFilters != undefined) {
            	 angular.forEach(params.facetFilters, function(facetFilter, key) {
            		 qParams += '&facetFilters=' + facetFilter;
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

            $http.get('../lo/search/' + encodeURI(params.queryString) + qParams, {}).
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
                    parent: {},
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
                    parent: {},
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
                lang: LanguageService.getLanguage()
            }

            if (options.lang) {
                queryParams.lang = options.lang
            }

            var url = '../lo/preview/';

            $http.get(url + options.id, {
                params: queryParams
            }).
            
            //$http.get('mocks/amk.json', {}).
            success(function(result) {
            	HigherEducationTransformer.transform(result);
            	result.preview = true;
            	result.tarjontaEditUrl =  Config.get('tarjontaUrl') + '/koulutus/' + result.id + '/edit';
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
            					ao.editUrl =  Config.get('tarjontaUrl') + '/hakukohde/' + ao.id + '/edit';
            				}
            			}
            		} 
            	}
                var loResult = {
                    lo: result,
                    parent: {},
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
service('ParentLOTransformer', ['UtilityService', '$filter', '$rootScope', function(UtilityService, $filter, $rootScope) {
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
                    var translationLanguageIndex = loi.availableTranslationLanguages.indexOf(result.translationLanguage);
                    loi.availableTranslationLanguages.splice(translationLanguageIndex, 1);
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
                UtilityService.sortApplicationSystems(asByPrerequisite);
                
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
service('HigherEducationTransformer', ['UtilityService', '$rootScope', '$filter', 'LanguageService', function(UtilityService, $rootScope, $filter, LanguageService) {

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
				var translationLanguageIndex = result.availableTranslationLanguages.indexOf(result.translationLanguage);
				result.availableTranslationLanguages.splice(translationLanguageIndex, 1);
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
			result.formOfTeaching = getFirstItemInList(result.formOfTeaching);

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
				UtilityService.sortApplicationSystems(asByPrerequisite);

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
service('ChildLOTransformer', ['UtilityService', '$rootScope', function(UtilityService, $rootScope) {

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
                    var translationLanguageIndex = loi.availableTranslationLanguages.indexOf(result.translationLanguage);
                    loi.availableTranslationLanguages.splice(translationLanguageIndex, 1);
                }
            } 
            

            for (var loiIndex in result.lois) {
                if (result.lois.hasOwnProperty(loiIndex)) {
                    var loi = result.lois[loiIndex];

                    var startDate = new Date(loi.startDate);
                    loi.startDate = startDate.getDate() + '.' + (startDate.getMonth() + 1) + '.' + startDate.getFullYear();
                    loi.teachingLanguage = getFirstItemInList(loi.teachingLanguages);
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
                UtilityService.sortApplicationSystems(asByPrerequisite);
                
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
service('LearningOpportunitySearchResultTransformer', ['UtilityService', '$filter', '$rootScope', function(UtilityService, $filter, $rootScope) {
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
 *  Service taking care of search term saving
 */
 service('SearchService', function() {
    var key = 'searchTerm';
    return {
        getTerm: function() {
            var term = $.cookie(key);
            if (term) {
                return term;
            } else {
                return '';
            }
        },

        setTerm: function(newTerm) {
            if (newTerm) {
                $.cookie(key, newTerm, {useLocalStorage: false, path: '/'});
            }
        }
    };
}).

/**
 *  Service keeping track of the current language selection
 */
service('LanguageService', function() {
    var defaultLanguage = 'fi';
    var key = 'i18next';

    return {
        getLanguage: function() {
            return $.cookie(key) || defaultLanguage;
        },

        setLanguage: function(language) {
            $.cookie(key, language, {useLocalStorage: false, path: '/'});
        },

        getDefaultLanguage: function() {
            return defaultLanguage;
        }
    };
}).

/**
 *  Service for "caching" current parent selection
 */
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

/**
 *  Service for "caching" current child selection
 */
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

/**
 *  Service for maintaining application basket state
 */
service('ApplicationBasketService', ['$http', '$q', '$rootScope', 'LanguageService', 'UtilityService', function($http, $q, $rootScope, LanguageService, UtilityService) {
    var key = 'basket';
    var typekey = 'baskettype';
    var cookieConfig = {useLocalStorage: false, maxChunkSize: 2000, maxNumberOfCookies: 20, path: '/'};

    // used to update item count in basket
    var updateBasket = function(count) {
        var event = $.Event('basketupdate');
        event.count = count;
        $('#appbasket-link').trigger(event);
    };

    // TODO: could we automate data transformation somehow?
    var transformData = function(result) {
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
                            result[asIndex].applicationOptions[i].prerequisite = applicationOptions[i].children[0].prerequisite;
                        }

                        // set teaching languge as the first language in array
                        var ao = applicationOptions[i];
                        if (ao.teachingLanguages && ao.teachingLanguages.length > 0) {
                            ao.teachLang = ao.teachingLanguages[0];
                        }

                        // set LOS id for lukio
                        // check if ao is of type lukio
                        ao.isLukio = UtilityService.isLukio(ao);
                        ao.losId = (ao.children && ao.children.length > 0) ? ao.children[0].losId : '';
                    }
                }
            }
        }

        return result;
    };

    return {
        addItem: function(aoId, itemType) {

            var current = $.cookie(key);

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
                $.cookie(typekey, itemType, cookieConfig);
            }

            $.cookie(key, JSON.stringify(current), cookieConfig);
            
            updateBasket(this.getItemCount());
        },

        removeItem: function(aoId) {
            if (this.getItemCount() > 1) {
                var value = $.cookie(key);
                value = JSON.parse(value);

                var index = value.indexOf(aoId);
                value.splice(index, 1);

                $.cookie(key, JSON.stringify(value), cookieConfig);
            } else {
                this.empty();
            }

            updateBasket(this.getItemCount());
        },

        empty: function() {
            $.cookie(key, null, cookieConfig);
            $.cookie(typekey, null, cookieConfig);
            updateBasket(this.getItemCount());
        },

        getItems: function() {
            return JSON.parse($.cookie(key));
        },

        getItemCount: function() {
            return $.cookie(key) ? JSON.parse($.cookie(key)).length : 0;
        },

        isEmpty: function() {
            return this.getItemCount() <= 0;
        },

        getType: function() {
            if (!this.isEmpty()) {
                return $.cookie(typekey);
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
service('FilterService', ['$q', '$http', 'UtilityService', 'LanguageService', 'kiAppConstants', function($q, $http, UtilityService, LanguageService, kiAppConstants) {
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
            var locationCodes = (queryParams.locations && typeof queryParams.locations == 'string') ? UtilityService.getStringAsArray(queryParams.locations) : getLocationCodes();

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
                page: filters.page,
                articlePage: filters.articlePage,
                facetFilters: filters.facetFilters,
                langCleared: filters.langCleared,
                itemsPerPage: filters.itemsPerPage,
                sortCriteria: filters.sortCriteria,
                lopFilter: filters.lopFilter,
                educationCodeFilter: filters.educationCodeFilter
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
            params += filters.page ? '&page=' + filters.page : '';
            params += (filters.facetFilters && filters.facetFilters.length > 0) ? '&facetFilters=' + filters.facetFilters.join(',') : '';
            params += filters.langCleared ? '&langCleared=' + filters.langCleared : '';
            params += filters.itemsPerPage ? '&itemsPerPage=' + filters.itemsPerPage : '';
            params += filters.sortCriteria ? '&sortCriteria=' + filters.sortCriteria : '';
            params += filters.lopFilter ? '&lopFilter=' + filters.lopFilter : '';
            params += filters.educationCodeFilter ? '&educationCodeFilter=' + filters.educationCodeFilter : '';
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
        
        getLopFilter: function() {
        	return filters.lopFilter;
        },
        
        getEducationCodeFilter: function() {
        	return filters.educationCodeFilter;
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
 *  Service for retrieving translated values for text
 */
service('UtilityService', function() {
    var isLisahaku = function(as) {
        return as.aoSpecificApplicationDates;
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
        isLisahaku: isLisahaku,
        sortApplicationSystems: function(applicationSystems) {
            if (applicationSystems) {
                applicationSystems.sort(function(a, b) {

                    var getEarliestStartDate = function(as) {
                        var earliest = -1;

                        if (isLisahaku(as)) {
                            earliest = getEarliestStartDateForLisahaku(as);
                        } else {
                            angular.forEach(as.applicationDates, function(value, key){
                                if (earliest < 0 || value.startDate < earliest) {
                                    earliest = value.startDate;
                                }
                            });
                        }

                        return earliest;
                    }

                    var getEarliestStartDateForLisahaku = function(as) {
                        var earliest = -1;
                        angular.forEach(as.applicationOptions, function(ao, key) {
                            if (earliest < 0 || ao.applicationStartDate < earliest) {
                                earliest = ao.applicationStartDate;
                            }
                        });

                        return earliest;
                    }

                    var getLatestsEndDateForLisahaku = function(as) {
                        var latest = -1;
                        angular.forEach(as.applicationOptions, function(ao, key) {
                            if (latest < 0 || ao.applicationEndDate > latest) {
                                latest = ao.applicationEndDate;
                            }
                        });

                        return latest;
                    }



                    var isLisahakuOngoing = function(as) {
                        var result = false;
                        angular.forEach(as.applicationOptions, function(ao, key){
                            if (ao.canBeApplied) {
                                result = true;
                            }
                        });

                        return result;
                    }

                    if ( isLisahaku(a) ) {
                        a.asOngoing = isLisahakuOngoing(a) ? true : false;
                    }

                    if ( isLisahaku(b) ) {
                        b.asOngoing = isLisahakuOngoing(b) ? true : false;
                    }

                    var comp = 0;
                    if (a.asOngoing == b.asOngoing) {
                        if (a.nextApplicationPeriodStarts && b.nextApplicationPeriodStarts) {
                            comp = a.nextApplicationPeriodStarts - b.nextApplicationPeriodStarts;
                        } else if (a.nextApplicationPeriodStarts) {
                            comp = -1;
                        } else if (b.nextApplicationPeriodStarts) {
                            comp = 1;
                        } else {
                            var earliestA = getEarliestStartDate(a);
                            var earliestB = getEarliestStartDate(b);
                            comp = earliestA > earliestB ? 1 : -1;
                        }
                    } else if (a.asOngoing) {
                        comp = -1;
                    } else {
                        comp = 1;
                    }

                    return comp;
                });
            }
        },
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