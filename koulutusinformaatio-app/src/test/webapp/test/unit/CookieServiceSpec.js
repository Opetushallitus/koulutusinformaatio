describe('CookieService', function() {
    var utility,
        cookieName = 'cookiename',
        cookieValue = 'cookievalue'

    beforeEach(function() {
        module('kiApp', 'kiApp.CookieService');

        inject(function(CookieService) {
            utility = CookieService;
        });

        // delete cookie
        utility.set(cookieName, null, {}, false);
        utility.set(cookieName, null, {}, true);
    });

    it('should return correct cookie value', function() {
        utility.set(cookieName, cookieValue, {});
        var result = utility.get(cookieName);
        expect(result).toEqual(cookieValue);
    });

    it('should return null value for non-prefixed cookie', function() {
        utility.set(cookieName, cookieValue, {});
        var result = utility.get(cookieName, false);
        expect(result).toBeNull();
    });

    it('should return null value for prefixed cookie', function() {
        utility.set(cookieName, cookieValue, {}, false);
        var result = utility.get(cookieName);
        expect(result).toBeNull();
    });

    it('should return correct value for prefixed cookie', function() {
        utility.set(cookieName, cookieValue, {}, true);
        var result = utility.get(cookieName, true);
        expect(result).toEqual(cookieValue);
    });

});