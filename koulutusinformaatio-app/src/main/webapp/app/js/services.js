/*  Services */

angular.module('kiApp.services', ['ngResource']).

/**
 *  Resource for making string based search
 */
 /*
 factory('SearchLearningOpportunity', function($resource) {
    return $resource('../lo/search/:queryString', {}, {
        query: {method:'GET', isArray:true}
    });
}).
*/
service('SearchLearningOpportunityService', ['$http', '$timeout', '$q', function($http, $timeout, $q) {
    return {
        query: function(params) {
            var deferred = $q.defer();

            $http.get('../lo/search/' + params.queryString).
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
 *  Resource for requesting LO data (parent and its children)
 */
 /*
 factory('ParentLearningOpportunity', function($resource) {
    return $resource('../lo/:parentId', {}, {
        query: {method:'GET', isArray:false}
    });
}).
*/

service('ParentLearningOpportunityService', ['$http', '$timeout', '$q', 'LanguageService', function($http, $timeout, $q, LanguageService) {
    return {
        query: function(params) {
            var deferred = $q.defer();
            var descriptionLanguage = LanguageService.getDescriptionLanguage();

            $http.get('../lo/' + params.parentId, {
            //$http.get('mock/parent-' + descriptionLanguage + '.json', {
                params: {
                    lang: descriptionLanguage
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
 *  
 */
service('ChildLearningOpportunityService', ['$http', '$timeout', '$q', 'LanguageService', function($http, $timeout, $q, LanguageService) {
    return {
        query: function(params) {
            var deferred = $q.defer();
            var descriptionLanguage = LanguageService.getDescriptionLanguage();

            $http.get('../lo/' + params.parentId + '/' + params.closId + '/' + params.cloiId, {
            //$http.get('mock/child-' + descriptionLanguage + '.json', {
                params: {
                    lang: descriptionLanguage
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
 *  Service taking care of search term saving
 */
 service('SearchService', function($cookies) {
    return {
        getTerm: function() {
            return $cookies.searchTerm;
        },

        setTerm: function(newTerm) {
            $cookies.searchTerm = newTerm;
        }
    };
}).

service('LanguageService', function($cookies) {
    var defaultLanguage = 'fi';

    return {
        getLanguage: function() {
            if ($cookies.language) {
                return $cookies.language;
            } else {
                return defaultLanguage;
            }
        },

        setLanguage: function(language) {
            $cookies.language = language;
        },

        getDescriptionLanguage: function() {
            if ($cookies.descriptionlanguage) {
                return $cookies.descriptionlanguage;
            } else {
                return defaultLanguage;
            }
        },

        setDescriptionLanguage: function(language) {
            $cookies.descriptionlanguage = language;
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
 *  Service handling page titles
 */
 service('TitleService', function() {
    var title;
    
    return {
        setTitle: function(value) {
            title = value + ' - Opintopolku.fi';

            // TODO: could this be done in angular way?
            $('title').trigger('updatetitle', [title]);
        },

        getTitle: function() {
            return title;
        }
    }
}).

service('TranslationService', function() {
    return {
        getTranslation: function(key) {
            if (key) {
                return i18n.t(key);
            }
        }
    }
});