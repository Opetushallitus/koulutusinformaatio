angular.module('kiApp.filters', [])

.filter('escape', function() {
  return window.escape;
})

.filter('encodeURIComponent', function() {
    return window.encodeURIComponent;
})

// adds target blank to links
.filter('externalLinks', function() {
    return function(val) {
        if (val) {
            val = val.replace(/<a/gi, '<a target="_blank"');
        }
        
        return val;
    }
})

// add required css classes to all tables
.filter('tables', function() {
    return function(val) {
        if (val) {
            val = val.replace(/<\s*table.*?>/gi, '<table class="table table-striped table-condensed table-responsive">');
        }

        return val;
    }
})

// replace h1 and h2 headers with h3 headers
.filter('headers', function() {
    return function(val) {
        // replace h1 with h3
        val = val.replace(/<h1/gi, '<h3');
        val = val.replace(/<\/h1>/gi, '</h3>');

        // replace h2 with h3
        val = val.replace(/<h2/gi, '<h3');
        val = val.replace(/<\/h2>/gi, '</h3>');

        return val;
    }
})

// combines a bunch of filters (used for textual content from tarjonta)
.filter('tarjontaFilter', ['$filter', function($filter) {
    return function(val) {
        val = $filter('externalLinks')(val);
        val = $filter('tables')(val);
        val = $filter('headers')(val);

        return val;
    }
}])

.filter('unsafe', function($sce) {
    return function(val) {
        return $sce.trustAsHtml(val);
    };
})

.filter('columnize', function() {
    var cache = {};
    return function(arr, size) {
        var result = [];
        var cursor = 0;
        while (arr.length > cursor) {
            if (arr.length - cursor > size) {
                var subarr = arr.slice(cursor, cursor+size);
                result.push(subarr);
                cursor += size;
            } else {
                var subarr = arr.slice(cursor, arr.length);
                result.push(subarr);
                cursor = arr.length;
            }
        }

        var arrString = JSON.stringify(arr);
        var fromCache = cache[arrString+size];
        if (JSON.stringify(fromCache) === JSON.stringify(result)) {
            return fromCache;
        }
        cache[arrString+size] = result;

        return result;
    }
}); 