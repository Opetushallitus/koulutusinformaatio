(function(angular) {
	'use strict';

	angular.module('angulartics.piwik', ['angulartics'])
	.config(['$analyticsProvider', function ($analyticsProvider) {

		$analyticsProvider.registerPageTrack(function (path) {
			if (_paq) {
				var documentTitle = document.domain + '/' + document.title
				_paq.push(['setDocumentTitle', documentTitle]);
				_paq.push(['setCustomUrl', path]);
				_paq.push(['trackPageView', documentTitle]);
			}
		});

		$analyticsProvider.registerSiteSearchTrack(function (keyword, category, resultCount) {
			if (_paq) _paq.push(['trackSiteSearch', keyword, category, resultCount]);
		});

		$analyticsProvider.registerEventTrack(function (action, properties) {
			// no event tracking in piwik
		});

	}]);
})(angular);