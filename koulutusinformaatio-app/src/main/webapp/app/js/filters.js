angular.module('kiApp.filters', [])

.filter('escape', function() {
  return window.escape;
})

.filter('encodeURIComponent', function() {
    return window.encodeURIComponent;
})

// adds target blank to links
.filter('externalLinks', ['UtilityService', function(UtilityService) {
    return function(val) {
        if (val) {
            val = UtilityService.replaceAll(/<a/g, '<a target="_blank"', val);
        }
        
        return val;
    }
}])

.filter('tables', function() {
    return function(val) {
        if (val) {
            val = val.replace(/<\s*table.*?>/gi, '<table class="table table-striped table-condensed table-responsive>"');
        }

        return val;
    }
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