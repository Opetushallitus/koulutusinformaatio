angular.module('kiApp.OrganisationService', ['ngResource']).

/**
 * Service for retrieving districts (maakunnat). Used in faceted search
 */
service('OrganisationService', ['$http', '$timeout', '$q', function($http, $timeout, $q) {

    return {
        query: function(id) {
            var deferred = $q.defer();

            $http.get('../lop/' + id).
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