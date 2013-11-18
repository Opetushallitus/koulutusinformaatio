(function(angular) {
	'use strict';

	angular.module('angulartics.piwik', ['angulartics'])
	.config(['$analyticsProvider', function ($analyticsProvider) {

		$analyticsProvider.registerPageTrack(function (path, variables) {

			// remove query params from path (piwik does not like queryparams with hashbangs)
			var queryParamIndex = path.indexOf('?');
			if (queryParamIndex >= 0) {
				path = path.substring(0, queryParamIndex);
			}

			// track page view
			if (_paq) {
				var documentTitle = document.domain + '/' + document.title;
				_paq.push(['setDocumentTitle', documentTitle]);
				_paq.push(['setCustomUrl', path]);

				// add custom variables
				if (variables) {
					if (variables.visit) {
						angular.forEach(variables.visit, function(item, itemkey) {
							_paq.push(['setCustomVariable', itemkey+1, item.name, item.value, 'visit']);
						});
					}

					if (variables.page) {
						angular.forEach(variables.page, function(item, itemkey) {
							_paq.push(['setCustomVariable', itemkey+1, item.name, item.value, 'page']);
						});
					}
				}

				_paq.push(['trackPageView', documentTitle]);
			}
		});

		$analyticsProvider.registerSiteSearchTrack(function (keyword, category, resultCount) {
			if (_paq) {
				_paq.push(['trackSiteSearch', keyword, category, resultCount]);
			}
		});

		$analyticsProvider.registerEventTrack(function (action, properties) {
			// no event tracking in piwik
		});

	}]);
})(angular);