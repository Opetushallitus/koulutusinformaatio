angular.module('kiApp.CookieService', ['ngResource']).

/*
 *	Utility to read and write cookies using the enhanced cookie (jQuery) library.
 *  By default cookies are prefixed with 'test'-prefix in all development and test environments
 *  to avoid cookie handling issues for subdomains in IE. In production cookie names are never prefixed. 
 */
service('CookieService', ['$location', 'HostResolver', function($location, HostResolver) {
	return {
		get: function(name) {
			var defaultConfig = {useLocalStorage: false};
			return $.cookie(name, defaultConfig);
		},

		set: function(name, value, config) {
			var defaultConfig = {useLocalStorage: false, path: '/'};
			config = config || defaultConfig;
			$.cookie(name, value, config);
		}
	};
}]);