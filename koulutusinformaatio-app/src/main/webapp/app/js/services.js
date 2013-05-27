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
        result.teachingLanguage = getFirstItemInList(result.teachingLanguages); // ? result.teachingLanguages[0] : '';
        result.formOfEducation = getFirstItemInList(result.formOfEducation); // ? result.formOfEducation[0] : '';
    };

    var getFirstItemInList = function(list) {
        if (list && list[0]) {
            return list[0];
        } else {
            return '';
        }
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
            return $.cookie(key);
            //return $.jStorage.get(key);
            //return $.cookie('searchTerm');
        },

        setTerm: function(newTerm) {
            $.cookie(key, newTerm, {useLocalStorage: false});
            //console.log(newTerm);
            //$.jStorage.set(key, newTerm);
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
            return $.cookie(key) || defaultLanguage;
            //return $.jStorage.get(key) || defaultLanguage;
            //return $.cookie('language') || defaultLanguage;
        },

        setLanguage: function(language) {
            $.cookie(key, language, {useLocalStorage: false});
            //$.jStorage.set(key, language);
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
})

.service('ApplicationBasketService', ['$http', '$q', function($http, $q) {
    var key = 'basket';
    return {
        addItem: function(aoId) {

            var current = $.cookie(key);

            if (current) {
                current = JSON.parse(current);
                current.push(aoId);
            } else {
                current = [];
                current.push(aoId);
            }

            $.cookie(key, JSON.stringify(current), {useLocalStorage: false, maxChunkSize: 2000, maxNumberOfCookies: 20});
        },

        removeItem: function(aoId) {
            var value = $.cookie(key);
            value = JSON.parse(value);

            var index = value.indexOf(aoId);
            value.splice(index, 1);

            $.cookie(key, JSON.stringify(value), {useLocalStorage: false, maxChunkSize: 2000, maxNumberOfCookies: 20});
        },

        getItems: function() {
            return JSON.parse($.cookie(key));
        },

        query: function(params) {
            var deferred = $q.defer();

            //$http.get('../lo/search/'  params.queryString).
            $http.get('mock/ao.json').
            success(function(result) {
                deferred.resolve(result);
            }).
            error(function(result) {
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]);