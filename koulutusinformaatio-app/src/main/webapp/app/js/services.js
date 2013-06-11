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
 *  Resource for requesting parent LO data
 */
service('ParentLearningOpportunityService', ['$http', '$timeout', '$q', 'LanguageService', function($http, $timeout, $q, LanguageService) {
    var transformData = function(result) {
        var translationLanguageIndex = result.availableTranslationLanguages.indexOf(result.translationLanguage);
        result.availableTranslationLanguages.splice(translationLanguageIndex, 1);

        var applicationSystems = [];

        for (var index in result.applicationOptions) {
            if (result.applicationOptions.hasOwnProperty(index)) {
                var ao = result.applicationOptions[index];
                if (ao.applicationSystem && ao.applicationSystem.applicationDates && ao.applicationSystem.applicationDates.length > 0) {
                    ao.applicationSystem.applicationDates = ao.applicationSystem.applicationDates[0];
                }
                result.applicationSystem = ao.applicationSystem;
            }
        }

        // set teaching languge as the first language in array
        for (var index in result.applicationOptions) {
            if (result.applicationOptions.hasOwnProperty(index)) {
                var ao = result.applicationOptions[index];
                if (ao.teachingLanguages && ao.teachingLanguages.length > 0) {
                    ao.teachLang = ao.teachingLanguages[0];
                }
            }
        }    
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
 *  Resource for requesting child LO data
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
        },

        setTerm: function(newTerm) {
            $.cookie(key, newTerm, {useLocalStorage: false, path: '/'});
        }
    };
}).

/**
 *  Service keeping track of the current language selection
 */
service('LanguageService', function() {
    var defaultLanguage = 'fi';
    var key = 'language';

    return {
        getLanguage: function() {
            return $.cookie(key) || defaultLanguage;
        },

        setLanguage: function(language) {
            $.cookie(key, language, {useLocalStorage: false, path: '/'});
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

/**
 *  Service for retrieving translated values for text
 */
service('TranslationService', function() {
    return {
        getTranslation: function(key) {
            if (key) {
                return i18n.t(key);
            }
        }
    }
})

/**
 *  Service for maintaining application basket state
 */
.service('ApplicationBasketService', ['$http', '$q', function($http, $q) {
    var key = 'basket';

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
                if (applicationDates.length > 0) {
                    result[asIndex].applicationDates = applicationDates[0];
                }

                var applicationOptions = result[asIndex].applicationOptions;
                for (var i in applicationOptions) {
                    if (applicationOptions.hasOwnProperty(i)) {
                        if (applicationOptions[i].children.length > 0) {
                            result[asIndex].applicationOptions[i].qualification = applicationOptions[i].children[0].qualification;
                            result[asIndex].applicationOptions[i].prerequisite = applicationOptions[i].children[0].prerequisite;
                        }

                        if (!result[asIndex].applicationOptions[i].deadlines) {
                            result[asIndex].applicationOptions[i].deadlines = [];
                        }

                        if (result[asIndex].applicationOptions[i].attachmentDeliveryDeadline) {
                            result[asIndex].applicationOptions[i].deadlines.push({
                                name: 'Liitteet',
                                value: result[asIndex].applicationOptions[i].attachmentDeliveryDeadline
                            });
                        }

                        // set teaching languge as the first language in array

                        var ao = applicationOptions[i];
                        if (ao.teachingLanguages && ao.teachingLanguages.length > 0) {
                            ao.teachLang = ao.teachingLanguages[0];
                        }
                    }
                }
            }
        }

        return result;
    };

    return {
        addItem: function(aoId) {

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

            $.cookie(key, JSON.stringify(current), {useLocalStorage: false, maxChunkSize: 2000, maxNumberOfCookies: 20, path: '/'});

            updateBasket(this.getItemCount());
        },

        removeItem: function(aoId) {
            var value = $.cookie(key);
            value = JSON.parse(value);

            var index = value.indexOf(aoId);
            value.splice(index, 1);

            $.cookie(key, JSON.stringify(value), {useLocalStorage: false, maxChunkSize: 2000, maxNumberOfCookies: 20, path: '/'});

            updateBasket(this.getItemCount());
        },

        empty: function() {
            $.cookie(key, null, {useLocalStorage: false, maxChunkSize: 2000, maxNumberOfCookies: 20, path: '/'});
            //$.cookie(key, null, {useLocalStorage: false, maxChunkSize: 2000, maxNumberOfCookies: 20, path: '/'});
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

        query: function(params) {
            var deferred = $q.defer();
            var basketItems = this.getItems();

            var qParams = '';

            
            for (var index in basketItems) {
                if (basketItems.hasOwnProperty(index)) {
                    qParams += '&aoId=' + basketItems[index];
                }
            }

            qParams = qParams.substring(1, qParams.length);
            

            $http.get('../basket/items?' + qParams).
            //$http.get('mock/ao.json').
            success(function(result) {
                result = transformData(result);
                deferred.resolve(result);
            }).
            error(function(result) {
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]);