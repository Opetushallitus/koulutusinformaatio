/**
 * Root controller for whole app
 */
function RootCtrl($rootScope, $translate) {
    $rootScope.$on("$locationChangeStart", function(event, next, current) { 
        delete $rootScope.error;
        delete $rootScope.hideSearchbar;
    });

    function readLanguageCookie() {
        try {
            var lang = jQuery.cookie(i18n.options.cookieName)
            return lang != null ? lang : "fi"
        } catch(e) {
            return "fi"
        }
    }

    $rootScope.lang = readLanguageCookie();


};
