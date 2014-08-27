"use strict";

angular.module('kiApp.AuthService', ['ngResource']).

/*
 *  Service for checking if user is authenticated or not. Currently simply checks if cookie exists
 */
service('AuthService', ['CookieService', function(CookieService) {
    var cookieName = 'auth';
    return {
        isAuthenticated: function() {
            return CookieService.get(cookieName) ? true : false;
        }
    };
}]);