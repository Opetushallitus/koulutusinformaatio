describe('AuthService', function() {
    var utility, cookie;
    var cookieName = "auth";

    beforeEach(function() {
        module('kiApp', 'kiApp.AuthService', 'kiApp.CookieService');

        inject(function(AuthService, CookieService) {
            utility = AuthService;
            cookie = CookieService;
        });

        // delete cookie
        cookie.set(cookieName, null, {});
    });

    it('should return false for unset auth cookie', function() {
        var result = utility.isAuthenticated();
        expect(result).toBeFalsy();
    });

    it('should return true for set auth cookie', function() {
        cookie.set(cookieName, 'authCookieSet', {});
        var result = utility.isAuthenticated();
        expect(result).toBeTruthy();
    });
});