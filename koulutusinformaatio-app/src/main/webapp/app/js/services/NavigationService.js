angular.module('kiApp.NavigationService', ['ngResource']).

service('NavigationService', ['$q', '$http', 'Config', function($q, $http, Config) {
    return {
        query: function(queryParam) {
            var deferred = $q.defer();
            var url = Config.get('navigationUrl');

            $http.get(url, {}).
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