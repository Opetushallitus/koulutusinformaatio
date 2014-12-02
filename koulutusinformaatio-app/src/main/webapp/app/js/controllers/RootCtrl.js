/**
 * Root controller for whole app
 */
function RootCtrl($rootScope) {
    $rootScope.$on("$locationChangeStart", function(event, next, current) { 
        delete $rootScope.error;
        delete $rootScope.hideSearchbar;
    });
};