/* Directives */

 angular.module('kiApp.directives', []).

/**
 *  Creates and controls the link "ribbon" of sibling LOs in child view
 */
 directive('kiSiblingRibbon', function() {
    return function(scope, element, attrs) {
        var result = "";
        scope.$watch('parentLO', function(parentData) {
            if (parentData) {

                // if parentLO has only 1 (or less) child, do not show ribbon
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
            }
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
            var home = i18n.t('breadcrumb-search-results');
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

/**
 *  Renders a text block with title. If no content exists the whole text block gets removed. 
 */
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
                    $(element).empty();
                    var titleElement = createTitleElement(title, attrs.anchor, attrs.level);
                    element.append(titleElement);
                    element.append(content);
                    //var contentElement = $('<p></p>');
                    //contentElement.append(content);
                    //element.replaceWith(titleElement);

                    //contentElement.insertAfter(titleElement);
                }
            }

            var createTitleElement = function(text, anchortag, level) {
                if (level) {
                    return $('<h' + level + ' id="' + anchortag + '">' + text + '</h' + level + '>');
                } else {
                    return $('<h3 id="' + anchortag + '">' + text + '</h3>');
                }
            }
        }
}).

/**
 *  Updates the title element of the page.
 */
directive('kiAppTitle', ['TitleService', function(TitleService) {
    return function(scope, element, attrs) {
        $(element).on('updatetitle', function(e, param) {
            element.html(param);
        });
        //element.html(TitleService.getTitle());
    };
}]).


directive('kiI18n', ['TranslationService', function(TranslationService) {
    return function(scope, element, attrs) {
        element.append(TranslationService.getTranslation(attrs.kiI18n));
    }    
}]);
