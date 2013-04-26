/* Directives */

 angular.module('kiApp.directives', []).

/**
 *  Creates and controls the link "ribbon" of sibling LOs in child view
 */
 directive('kiSiblingRibbon', function() {
    return function(scope, element, attrs) {
        var result = "";
        scope.$watch('parentLO', function(parentData) {
            if (parentData.children && parentData.children.length <= 1) {
                return;
            } 

            for(var index in parentData.children) {
                var child = parentData.children[index];
                var isCurrentSelection = child.id == scope.childLO.id ? true : false;
                var clazz = isCurrentSelection ? 'disabled' : '';
                result += '<a href="#/info/' + parentData.id + '/' + child.id + '" class="' + clazz + '">' + child.degreeTitle + '</a>';
            }

            element.html(result);
        }, true);
        
    }
}).

/**
 *  Creates and controls the breadcrumb 
 */
 directive('kiBreadcrumb', ['$location', 'SearchService', function($location, SearchService) {
    return {
        restrict: 'E,A',
        templateUrl: 'partials/breadcrumb.html',
        link: function(scope, element, attrs) {
            var home = "Hakutulokset";
            var parent;
            var child;

            scope.$watch('parentLO.name', function(data) {
                parent = data;
                update();
            }, true);

            scope.$watch('childLO.degreeTitle', function(data) {
                child = data;
                update();
            }, true);

            var update = function() {
                //scope.breadcrumbItems = [{value: 'Hakutulokset', cssClass: ''}];
                scope.breadcrumbItems = [];
                pushItem({name: home, callback: scope.search});
                pushItem({name: parent, callback: scope.goto});
                pushItem({name: child, callback: scope.goto});
            };

            var pushItem = function(item) {
                if (item.name) {
                    scope.breadcrumbItems.push(item);
                }
            };

            scope.search = function() {
                $location.path('/haku/' + SearchService.getTerm());
            }

            scope.goto = function() {
                $location.path('/info/' + scope.parentLO.id );
            }
        }
    };
}]).

directive('renderTextBlock', function() {
    return function(scope, element, attrs) {
        var title;
        var content;

        attrs.$observe('title', function(value) {
            title = value;
            update();
        });

        attrs.$observe('content', function(value) {
            content = value;
            update();
        });

        var update = function() {
            if (content) {
                var titleElement = createTitleElement(title, attrs.anchor, attrs.level);
                element.append(titleElement);
                element.append(content);
            }
        }

        /*
        scope.$watch('parentLO', function(data) {
            console.log(attrs.content);
            if (attrs.content) {
                var title = createTitleElement(attrs);
                element.append(title);
                element.append(attrs.content);
            }
        });
*/

        var createTitleElement = function(text, anchortag, level) {
            if (level) {
                return $('<h' + level + ' id="' + anchortag + '">' + text + '</h' + level + '>');
            } else {
                return $('<h3 id="' + anchortag + '">' + text + '</h3>');
            }
        }
    };
});