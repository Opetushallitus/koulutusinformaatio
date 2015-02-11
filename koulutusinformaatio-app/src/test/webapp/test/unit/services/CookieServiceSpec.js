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
        utility.set(cookieName, null, {});
        utility.set(cookieName, null, {});
    });

    it('should return correct cookie value', function() {
        utility.set(cookieName, cookieValue, {});
        var result = utility.get(cookieName);
        expect(result).toEqual(cookieValue);
    });

    it('should return null value for non-set cookie', function() {
        var result = utility.get(cookieName);
        expect(result).toBeNull();
    });

});