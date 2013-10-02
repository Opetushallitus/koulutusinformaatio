// load json polyfill if not present
Modernizr.load([
{
    test: window.JSON,
    nope: 'lib/modernizr/json3.min.js'
}
]);
