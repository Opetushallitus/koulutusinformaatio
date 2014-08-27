angular.module('kiApp.directives.AjaxLoader', []).

directive('ajaxLoader', function () {
    return {
        restrict: 'A',
        template: 
            '<div class="ajax-loader text-center">' +
                '<img src="img/ajax-loader-big.gif" />' +
            '</div>',
        link: function (scope, elm, attrs) {
            scope.isLoading = function () {
                return !scope.lo;
            };

            scope.$watch(scope.isLoading, function (v) {
                if (v) {
                    elm.show();
                } else {
                    elm.hide();
                }
            });
        }
    };
});