angular.module('kiApp.ArticleContentSearchService', ['ngResource']).

service('ArticleContentSearchService', ['$q', '$http', 'Config', function($q, $http, Config) {
    return {
        query: function(options) {
            var deferred = $q.defer();
            var url = Config.get('frontpageUrl');
            
            url += 'page/' + options.page + '/';
            $http.get(url, {
                params: {
                    s: options.queryString,
                    json: 1
                }
            }).
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