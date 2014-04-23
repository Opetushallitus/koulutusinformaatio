angular.module('kiApp.CookieService', ['ngResource']).

service('CookieService', ['$location', 'HostResolver', function($location, HostResolver) {

	var defaultConfig = {useLocalStorage: false, path: '/'};

	return {
		get: function(name) {
			var prefix = HostResolver.getCookiePrefixByDomain( $location.host() );
			name = prefix + name;
			return $.cookie(name);
		},

		set: function(name, value, config) {
			config = config || defaultConfig;
			var prefix = HostResolver.getCookiePrefixByDomain( $location.host() );
			name = prefix + name;
			$.cookie(name, value, config);
		},
	}
}]);