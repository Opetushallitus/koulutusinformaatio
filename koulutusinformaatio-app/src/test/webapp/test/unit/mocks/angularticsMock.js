//angular.module('$analytics', []);
angular.module('$analyticsProvider', []);
angular.module('angulartics', [])
    .provider('$analytics', function () { 
        return { 
            $get: function() { 
                return {
                    settings: {},
                    pageTrack: function () {},
                    eventTrack: function () {},
                    siteSearchTrack: function () {}
                }
            },
            virtualPageviews: function () {},
            firstPageview: function () {},
            registerPageTrack: function () {},
            registerEventTrack: function () {},
            registerSiteSearchTrack: function () {}
        } 
    });

angular.module('angulartics.piwik', []);