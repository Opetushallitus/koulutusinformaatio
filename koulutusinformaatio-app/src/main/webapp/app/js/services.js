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
    var transformData = function(result) {
        for (var index in result.results) {
            if (result.results.hasOwnProperty(index)) {
                var resItem = result.results[index];
                if (resItem.parentId) {
                    resItem.linkHref = '#/info/' + resItem.parentId + '/' + resItem.losId + '/' + resItem.id;
                } else {
                    resItem.linkHref = '#/info/' + resItem.id
                }
            }
        }
    };

    return {
        query: function(params) {
            var deferred = $q.defer();

            $http.get('../lo/search/' + params.queryString, {
                params: {
                    start: params.start,
                    rows: params.rows
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
                    //ao.applicationSystem.applicationDates.startDate = "1369958400000";
                    //ao.applicationSystem.applicationDates.endDate = "1372636800000";
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

        /*
        result.provider.social = {
            facebook: 'http://www.facebook.com',
            linkedin: 'http://www.linkedin.com',
            twitter: 'http://www.twitter.com',
            googleplus: 'http://plus.google.com'
        };
        */
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
        result.teachingLanguage = getFirstItemInList(result.teachingLanguages);
        result.formOfEducation = getFirstItemInList(result.formOfEducation);

        // add current child to sibligs
        if (result.related) {
            result.related.push({
                loiId: result.loiId, 
                losId: result.losId,
                name: result.name
            });

            // sort siblings alphabetically
            result.related = result.related.sort(function(a, b) {
                if (a.name > b.name) return 1;
                else if (a.name < b.name) return -1;
                else return a.loiId > b.loiId ? 1 : -1;
            });
        }
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
                if (applicationDates.length > 0) {
                    result[asIndex].applicationDates = applicationDates[0];
                    //result[asIndex].applicationDates.startDate = "1369958400000";
                    //result[asIndex].applicationDates.endDate = "1372636800000";
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
                current.push(itemType);
                current.push(aoId);
            }

            $.cookie(key, JSON.stringify(current), cookieConfig);

            updateBasket(this.getItemCount());
        },

        removeItem: function(aoId) {
            var value = $.cookie(key);
            value = JSON.parse(value);

            var index = value.indexOf(aoId);
            value.splice(index, 1);

            $.cookie(key, JSON.stringify(value), cookieConfig);

            updateBasket(this.getItemCount());
        },

        empty: function() {
            $.cookie(key, null, cookieConfig);
            updateBasket(this.getItemCount());
        },

        getItems: function() {
            return JSON.parse($.cookie(key));
        },

        getItemCount: function() {
            return $.cookie(key) ? JSON.parse($.cookie(key)).length - 1 : 0;
        },

        isEmpty: function() {
            return this.getItemCount() <= 0;
        },

        getType: function() {
            if (!this.isEmpty()) {
                var basket = this.getItems();
                return basket[0];
            }
        },

        query: function(params) {
            var deferred = $q.defer();
            var basketItems = this.getItems();

            var qParams = '';

            
            for (var index = 1; index < basketItems.length; index++) {
                if (basketItems.hasOwnProperty(index)) {
                    qParams += '&aoId=' + basketItems[index];
                }
            }

            qParams = qParams.substring(1, qParams.length);
            
            $http.get('../basket/items?' + qParams).
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
}]).

/**
 *  Service for retrieving translated values for text
 */
service('UtilityService', function() {
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
        }
    };
});