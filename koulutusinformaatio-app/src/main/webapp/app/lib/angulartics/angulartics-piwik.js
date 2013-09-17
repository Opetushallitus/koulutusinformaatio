(function(angular) {
	'use strict';

	angular.module('angulartics.piwik', ['angulartics'])
	.config(['$analyticsProvider', function ($analyticsProvider) {

		$analyticsProvider.registerPageTrack(function (path) {
			if(_paq) {
				_paq.push(['setDocumentTitle', document.domain + "/" + document.title]);
				_paq.push(['trackPageView', path]);
			}
		});

		$analyticsProvider.registerSiteSearchTrack(function (keyword, category, resultCount) {
			if(_paq) _paq.push(['trackSiteSearch', keyword, category, resultCount]);
		});

		$analyticsProvider.registerEventTrack(function (action, properties) {
			// no event tracking in piwik
		});

	}]);
})(angular);