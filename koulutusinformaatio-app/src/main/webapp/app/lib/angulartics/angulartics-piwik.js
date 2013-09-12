(function(angular) {
	'use strict';

	angular.module('angulartics.piwik', ['angulartics'])
	.config(['$analyticsProvider', function ($analyticsProvider) {

		$analyticsProvider.registerPageTrack(function (path) {
			console.log('page tracked: ' + path);
			if(_paq) _paq.push(['trackPageView', path]);
		});

		$analyticsProvider.registerSiteSearchTrack(function (keyword, category, resultCount) {
			if(_paq) _paq.push(['trackSiteSearch', keyword, category, resultCount]);
		});

		$analyticsProvider.registerEventTrack(function (action, properties) {
			//if(_paq) _paq.push(['trackPageView', action]);
		});

	}]);
})(angular);