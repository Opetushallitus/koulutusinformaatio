/* Directives */

 angular.module('kiApp.directives', []).

directive('kiRenderContactInfo', function() {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/contactInfo.html',
        link: function(scope, element, attrs) {
            scope.anchor = attrs.anchor;
        }
    }
}).

directive('kiEmail', function() {
    return {
        restrict: 'E,A',
        link: function(scope, element, attrs) {
            attrs.$observe('kiEmail', function(data) {
                if (data) {
                    element.html(data.replace('@', '(at)'));
                }
            });
        }
    }
}).

/**
 *  Creates and controls the location filter element
 */
 directive('kiLocationFilter', function() {
    return {
        restrict: 'E,A',
        templateUrl: 'partials/locationFilter.html',

        link: function(scope, element, attrs) {
            scope.locations = [];

            scope.remove = function(element) {
                scope.locations.splice(scope.locations.indexOf(element), 1);
                scope.change();
            }

            scope.add = function() {
                if (scope.location && scope.locations.indexOf(scope.location) < 0) {
                    scope.locations.push(scope.location);
                    scope.location = '';
                    scope.change();
                }
            }
        }
    };
 }).

/**
 *  Creates and controls language selector for description language
 */
 directive('kiLanguageRibbon', ['$routeParams', function($routeParams) {
    return {
        restrict: 'E,A',
        templateUrl: 'partials/languageRibbon.html',

        link: function(scope, element, attrs) {
            scope.label = i18n.t('description-language-selection');
            scope.isChild = ($routeParams.closId && $routeParams.cloiId) ? true : false;

            scope.$watch('childLO', function(data) {
                scope.hasMultipleTranslations = scope.childLO && scope.childLO.availableTranslationLanguages.length >= 1;    
            });

            scope.$watch('parentLO', function(data) {
                scope.hasMultipleTranslations = scope.parentLO && scope.parentLO.availableTranslationLanguages.length >= 1;    
            });
        }
    };
 }]).

/**
 *  Creates and controls the link "ribbon" of sibling LOs in child view
 */
  directive('kiSiblingRibbon', ['$location', '$routeParams', function($location, $routeParams) {
    return {
        restrict: 'E,A',
        template: '<ul class="ribbon-content"><li ng-repeat="relatedChild in childLO.related" ><a ng-click="changeChild(relatedChild)" ng-class="siblingClass(relatedChild)">{{relatedChild.name}}</a></li></ul>',
        link: function(scope, element, attrs) {

            scope.siblingClass = function(sibling) {
                if (sibling.losId == $routeParams.closId && sibling.loiId == $routeParams.cloiId) {
                    return 'disabled';
                } else {
                    return '';
                }
            }

            scope.changeChild = function(sibling) {
                $location.path('/info/' + scope.parentLO.id + '/' + sibling.losId + '/' + sibling.loiId);
            }
        }
    }
}]).


/**
 *  Creates and controls the breadcrumb
 */
 directive('kiBreadcrumb', ['$location', 'SearchService', function($location, SearchService) {
    return {
        restrict: 'E,A',
        templateUrl: 'partials/breadcrumb.html',
        link: function(scope, element, attrs) {
            var home = 'home';
            var search = i18n.t('breadcrumb-search-results');
            var parent;
            var child;

            scope.$watch('parentLO.name', function(data) {
                parent = data;
                update();
            }, true);

            scope.$watch('childLO.name', function(data) {
                child = data;
                update();
            }, true);

            var update = function() {
                scope.breadcrumbItems = [];
                pushItem({name: home, callback: scope.gohome});
                pushItem({name: search, callback: scope.search});
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
            };

            scope.goto = function() {
                $location.path('/info/' + scope.parentLO.id );
            };

            scope.gohome = function() {
                $location.path('#');
            };
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
                title = i18n.t(value); //value;
                update();
            });

            attrs.$observe('content', function(value) {
                content = value;
                update();
            });

            var update = function() {
                if (content || attrs.force) {
                    $(element).empty();
                    var titleElement = createTitleElement(title, attrs.anchor, attrs.level);
                    element.append(titleElement);

                    // replace line feed with <br>
                    //content = content.replace(/(\r\n|\n|\r)/g,"<br />");
                    element.append(content);
                    //var contentElement = $('<p></p>');
                    //contentElement.append(content);
                    //element.replaceWith(titleElement);

                    //contentElement.insertAfter(titleElement);
                }
            }

            var createTitleElement = function(text, anchortag, level) {
                var idAttr = anchortag ? 'id="' + anchortag + '"' : '';
                if (level) {
                    return $('<h' + level + ' ' + idAttr + '>' + text + '</h' + level + '>');
                } else {
                    return $('<h2 ' + idAttr + '>' + text + '</h2>');
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

/**
 *  Creates a human readable date from timestamp
 */
directive('kiTimestamp', function() {
    return function(scope, element, attrs) {
        attrs.$observe('kiTimestamp', function(value) {
            value = parseInt(value);
            var date = new Date(value);
            element.append(date.getDate() + '.' + (date.getMonth() + 1) + '.' + date.getFullYear());
        });
    }
}).

/**
 *  Fetches a trasnlation with the given key and inserts it inside the element
 */
directive('kiI18n', ['TranslationService', function(TranslationService) {
    return function(scope, element, attrs) {
        attrs.$observe('kiI18n', function(value) {
            element.append(TranslationService.getTranslation(value));
        });
    }    
}]);
