/**
 *  Directive for showing an ajax loader (spinner).
 *  Usage:
 *
 *  <div data-ki-ajax-loader data-active="isLoading"></div>
 *  shows the spinner when isLoading evaluates to true
 */
angular.module('kiApp.directives.AjaxLoader', []).

directive('kiAjaxLoader', function () {
    return {
        restrict: 'A',
        template: 
            '<div class="ajax-loader text-center">' +
                '<img src="img/ajax-loader-big.gif" alt="Loading content"/>' +
            '</div>',
        scope: {
            active: '='
        },
        link: function (scope, elm, attrs) {
            scope.$watch('active', function (v) {
                if (v) {
                    elm.show();
                } else {
                    elm.hide();
                }
            });
        }
    };
});