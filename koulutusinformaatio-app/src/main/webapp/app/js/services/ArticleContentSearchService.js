angular.module('kiApp.ArticleContentSearchService', ['ngResource']).

service('ArticleContentSearchService', ['$q', '$http', 'Config', function($q, $http, Config) {
    return {
        query: function(queryParam) {
            var deferred = $q.defer();
            var url = Config.get('frontpageUrl');

            /*
            $http.get(url, {
                params: {
                    s: queryParam,
                    json: 1
                }
            }).
*/
            $http.get('mocks/wp-content.json', {}).
            success(function(result) {
                deferred.resolve(result);
                //console.log(result);
            }).
            error(function(result) {
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]);