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
    var transformData = function(result) {
        var translationLanguageIndex = result.availableTranslationLanguages.indexOf(result.translationLanguage);
        result.availableTranslationLanguages.splice(translationLanguageIndex, 1);
    };

    return {
        query: function(options) {
            var deferred = $q.defer();

            $http.get('../lo/' + options.parentId, {
            //$http.get('mock/parent-' + descriptionLanguage + '.json', {
                params: {
                    lang: options.language
                }
            }).
            success(function(result) {
                transformData(result);
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

    // TODO: could we automate data transformation somehow?
    var transformData = function(result) {
        var translationLanguageIndex = result.availableTranslationLanguages.indexOf(result.translationLanguage);
        result.availableTranslationLanguages.splice(translationLanguageIndex, 1);

        var startDate = new Date(result.startDate);
        result.startDate = startDate.getDate() + '.' + (startDate.getMonth() + 1) + '.' + startDate.getFullYear();
        result.teachingLanguage = result.teachingLanguages[0] ? result.teachingLanguages[0] : '';
        result.formOfEducation = result.formOfEducation[0] ? result.formOfEducation[0] : '';
    };

    return {
        query: function(options) {
            var deferred = $q.defer();

            $http.get('../lo/' + options.parentId + '/' + options.closId + '/' + options.cloiId, {
            //$http.get('mock/child-' + descriptionLanguage + '.json', {
                params: {
                    lang: options.language
                }
            }).
            success(function(result) {
                transformData(result);
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
            return $.jStorage.get(key);
            //return $.cookie('searchTerm');
        },

        setTerm: function(newTerm) {
            //console.log(newTerm);
            $.jStorage.set(key, newTerm);
            //$.cookie('searchTerm', newTerm);
        }
    };
}).

service('LanguageService', function() {
    var defaultLanguage = 'fi';
    var key = 'language';

    //console.log($);

    return {
        getLanguage: function() {
            return $.jStorage.get(key) || defaultLanguage;
            //return $.cookie('language') || defaultLanguage;
        },

        setLanguage: function(language) {
            $.jStorage.set(key, language);
            //$.cookie('language', language);
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