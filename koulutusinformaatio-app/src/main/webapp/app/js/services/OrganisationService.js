angular.module('kiApp.OrganisationService', ['ngResource']).

/**
 * Service for retrieving districts (maakunnat). Used in faceted search
 */
service('OrganisationService', ['$http', '$rootScope', '$q', 'LanguageService', function($http, $rootScope, $q, LanguageService) {

    return {
        query: function(id) {
            $rootScope.isLoading = true;
            var deferred = $q.defer();

            $http.get(window.url("koulutusinformaatio-service.lop", id, {
                lang: LanguageService.getLanguage()
            })).
            success(function(result) {
                $rootScope.isLoading = false;
                deferred.resolve(result);
            }).
            error(function(result) {
                $rootScope.isLoading = false;
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]);