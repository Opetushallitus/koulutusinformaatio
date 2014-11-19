angular.module('kiApp.SearchLearningOpportunityService', ['ngResource'])

.service('SearchLearningOpportunityService', 
    [
        '$http',
        '$timeout',
        '$q',
        '$analytics',
        '$rootScope',
        '$location',
        '$route',
        '$filter',
        'FilterService',
        'LearningOpportunitySearchResultTransformer',
        function($http, $timeout, $q, $analytics, $rootScope, $location, $route, $filter, FilterService, LearningOpportunitySearchResultTransformer) {
    
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
            $rootScope.isLoading = true;

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
            qParams += (params.lopFilter != undefined) ? ('&lopFilter=' + $filter('encodeURIComponent')(params.lopFilter)) : '';
            qParams += (params.educationCodeFilter != undefined) ? ('&educationCodeFilter=' + params.educationCodeFilter) : '';
            qParams += (params.searchType != undefined) ? ('&searchType=' + params.searchType) : '&searchType=LO';
            qParams += (params.queryString != undefined) ? ('&text=' + $filter('encodeURIComponent')(params.queryString)) : '&text= ';
            
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

            if (params.organisationFacetFilters != undefined) {
                angular.forEach(params.organisationFacetFilters, function(facetFilter, key) {
                    qParams += '&providerFacetFilters=' + facetFilter;
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

            $http.get('../lo/search' + qParams, {}).
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
                $rootScope.isLoading = false;

                deferred.resolve(result);
            }).
            error(function(result) {
                $rootScope.error = true;
                $rootScope.isLoading = false;
                
                deferred.reject(result);
            });

            return deferred.promise;
        },
        
        searchAll: function(qParams) {
            $location.path('haku/*');
            $location.search(qParams);
            $route.reload();
        }
    }
}])

/**
 *  Transform search result data
 */
.service('LearningOpportunitySearchResultTransformer', ['$filter', '$rootScope', function($filter, $rootScope) {
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

            // order provider types alphabetically
            if (result && result.providerTypeFacet && result.providerTypeFacet.facetValues) {
                result.providerTypeFacet.facetValues.sort(function(a, b) {
                    if (a.valueName < b.valueName) return -1;
                    else if (a.valueName > b.valueName) return 1;
                    else return 0;
                });
            }
        }
    }
}]);