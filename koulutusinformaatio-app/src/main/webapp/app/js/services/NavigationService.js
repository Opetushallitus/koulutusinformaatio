angular.module('kiApp.NavigationService', ['ngResource']).

service('NavigationService', ['$q', '$http', 'LanguageService', function($q, $http, LanguageService) {
    return {
        query: function(queryParam) {
            var deferred = $q.defer();

            $http.get('mocks/navigation.json', {}).
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