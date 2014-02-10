/**
 * Redirects old links without hashbang
 */
(function() {
	var loc = window.location.href
    if (loc.indexOf("#") != -1 &&  loc.indexOf("#!") < 0 ) {
        window.location.href = loc.replace("#", "#!");
    }
})();