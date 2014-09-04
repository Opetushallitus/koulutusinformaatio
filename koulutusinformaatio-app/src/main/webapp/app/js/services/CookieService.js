angular.module('kiApp.CookieService', ['ngResource']).

/*
 *	Utility to read and write cookies using the enhanced cookie (jQuery) library.
 *  By default cookies are prefixed with 'test'-prefix in all development and test environments
 *  to avoid cookie handling issues for subdomains in IE. In production cookie names are never prefixed. 
 */
service('CookieService', ['$location', 'HostResolver', function($location, HostResolver) {

	

	return {
		get: function(name, usePrefix) {
			var defaultConfig = {useLocalStorage: false};
			usePrefix = (usePrefix === undefined) ? true : usePrefix;

			var prefix = HostResolver.getCookiePrefixByDomain( $location.host() );
			name = usePrefix ? prefix + name : name;
			return $.cookie(name, defaultConfig);
		},

		set: function(name, value, config, usePrefix) {
			var defaultConfig = {useLocalStorage: false, path: '/'};
			usePrefix = (usePrefix === undefined) ? true : usePrefix;

			config = config || defaultConfig;
			var prefix = HostResolver.getCookiePrefixByDomain( $location.host() );
			name = usePrefix ? prefix + name : name;
			$.cookie(name, value, config);
		},
	}
}]);