/*  Services */

angular.module('kiApp.services', ['ngResource']).

/**
 *  Resource for making string based search
 */
 factory('LearningOpportunity', function($resource) {
    return $resource('../lo/search/:queryString', {}, {
        query: {method:'GET', isArray:true}
    });
}).

/**
 *  Resource for requesting LO data (parent and its children)
 */
 factory('ParentLearningOpportunity', function($resource) {
    return $resource('../lo/:parentId', {}, {
        query: {method:'GET', isArray:false}
    });
}).

/**
 *  Resource for requesting AO data
 */
 factory('ApplicationOption', function($resource) {
    return $resource('../ao/search/:asId/:lopId', {}, {
        query: {method:'GET', isArray:true}
    });
}).

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

/**
 *  Service taking care of search term saving
 */
 service('LODataService', function() {
    var data;

    return {
        getLOData: function() {
            return data;
        },

        getChildData: function(id) {
            var result;
            for (var index in data.children) {
                if (data.children[index].id == id) {
                    result = data.children[index];
                    break;
                }
            }

            return result;
        },

        setLOData: function(newData) {
            data = newData;
        },

        dataExists: function(id) {
            return data && data.id == id; 
        }
    };
}).

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
});