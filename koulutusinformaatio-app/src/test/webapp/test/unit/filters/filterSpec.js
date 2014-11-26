describe('Filters', function() {
    var filter;

    beforeEach(function() {
        module('kiApp', 'kiApp.filters');

        inject(function($filter) {
            filter = $filter;
        });
    });

    describe('replace headers', function() {

        it('should replace all h1 tags with h3 tags', function() {
            var value = filter('headers')('<h1><span>asdf</span></h1><h4>qwerty</h4><h1>1234</h1>');
            expect(value).toEqual('<h3><span>asdf</span></h3><h4>qwerty</h4><h3>1234</h3>');
        });

        it('should replace all h2 tags with h3 tags', function() {
            var value = filter('headers')('<h2>asdf</h2><span>qwerty</span><h2>1234<div></div></h2>');
            expect(value).toEqual('<h3>asdf</h3><span>qwerty</span><h3>1234<div></div></h3>');
        });

        it('should replace all h1 and h2 tags with h3 tags', function() {
            var html = '<h1>h1 content</h1><h2>h2 content</h2><h3>h3 content</h3><h2>title 2</h2><h3>title 3</h3><h3>title 3 2</h3>';
            var output = '<h3>h1 content</h3><h3>h2 content</h3><h3>h3 content</h3><h3>title 2</h3><h3>title 3</h3><h3>title 3 2</h3>';
            expect(filter('headers')(html)).toEqual(output);
        });

    });

    describe('replace links', function() {
        it('should add target blank to all links', function() {
            var result = filter('externalLinks')('<a href="abc">link</a><span>something</span><a href="def">another link</a>');
            expect(result).toEqual('<a target="_blank" href="abc">link</a><span>something</span><a target="_blank" href="def">another link</a>');
        });
    });

    describe('replace tables', function() {
        it('should add class attribute to table tags', function() {
            var result = filter('tables')('<table><tr><td></td></tr></table>');
            expect(result).toEqual('<table class="table table-striped table-condensed table-responsive"><tr><td></td></tr></table>');
        });
    });

    describe('tarjonta content filters', function() {

        it('should replace headers, links and tables', function() {
            var html = '<h1>title h1</h1><a href="abc">abc</a><h2>title h2</h2><table><tr><td></td></tr></table>';
            var output = '<h3>title h1</h3><a target="_blank" href="abc">abc</a><h3>title h2</h3><table class="table table-striped table-condensed table-responsive"><tr><td></td></tr></table>';
            expect(filter('tarjontaFilter')(html)).toEqual(output);
        });

    })
});