/*  Services */

angular.module('kiApp.services', ['ngResource']).

/**
 *  Resource for making string based search
 */
 factory('LearningOpportunity', function($resource) {
    return $resource('../lo/search/:queryString', {}, {
        query: {method:'GET', isArray:true}
    });
}).

/**
 *  Resource for requesting LO data (parent and its children)
 */
 factory('ParentLearningOpportunity', function($resource) {
    return $resource('../lo/:parentId', {}, {
        query: {method:'GET', isArray:false}
    });
}).

/**
 *  Resource for requesting AO data
 */
 factory('ApplicationOption', function($resource) {
    return $resource('../ao/search/:asId/:lopId', {}, {
        query: {method:'GET', isArray:true}
    });
}).

/**
 *  Service taking care of search term saving
 */
 service('SearchService', function($cookies) {
    return {
        getTerm: function() {
            return $cookies.searchTerm;
        },

        setTerm: function(newTerm) {
            $cookies.searchTerm = newTerm;
        }
    };
}).

/**
 *  Service taking care of search term saving
 */
 service('LODataService', function() {
    var data;

    return {
        getLOData: function() {
            return data;
        },

        getChildData: function(id) {
            var result;
            for (var index in data.children) {
                if (data.children[index].id == id) {
                    result = data.children[index];
                    break;
                }
            }

            return result;
        },

        setLOData: function(newData) {
            data = newData;
        },

        dataExists: function(id) {
            return data && data.id == id; 
        }
    };
});


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


/* Controllers */

/**
 *  Controller for index view
 */
 function IndexCtrl($scope, $routeParams, LearningOpportunity, SearchService, $location) {

    // route to search page
    $scope.search = function() {
        if ($scope.queryString) {
            $location.path('/haku/' + $scope.queryString);
        }
    };

    // launch navigation script
    $scope.initNavigation = function() {
        OPH.Common.initDropdownMenu();
    }
};

/**
 *  Controller for search functionality 
 */
 function SearchCtrl($scope, $routeParams, LearningOpportunity, SearchService, $location) {
    $scope.queryString = SearchService.getTerm();

    if ($routeParams.queryString) {
        $scope.loResult = LearningOpportunity.query({queryString: $routeParams.queryString});
        $scope.queryString = $routeParams.queryString;
        $scope.showFilters = $scope.queryString ? true : false;

        SearchService.setTerm($routeParams.queryString);
    }

    // Perform search using LearningOpportunity service
    $scope.search = function() {
        if ($scope.queryString) {
            SearchService.setTerm($scope.queryString);
            $location.path('/haku/' + $scope.queryString);
        }
    };

    // Forward to parent learning opportunity info page
    $scope.selectLO = function(parentLOId, LOId) {
        var path = parentLOId ? parentLOId + '/' + LOId : LOId;
        $location.path('/info/' + path);
    };

    // launch navigation script
    $scope.initNavigation = function() {
        OPH.Common.initDropdownMenu();
    };
};

/**
 *  Controller for info views (parent and child)
 */
 function InfoCtrl($scope, $routeParams, ParentLearningOpportunity, SearchService, LODataService, $location, $anchorScroll) {
    $scope.queryString = SearchService.getTerm();

    // fetch data for parent and its children LOs
    if ($routeParams) {
        $scope.parentId = $routeParams.parentId;
        if (!LODataService.dataExists($scope.parentId)) {
            $scope.parentLO = ParentLearningOpportunity.query({parentId: $routeParams.parentId}, function(data) {
                LODataService.setLOData(data);
                $scope.childLO = LODataService.getChildData($routeParams.childId);
            });
        } else {
            $scope.parentLO = LODataService.getLOData();
            $scope.childLO = LODataService.getChildData($routeParams.childId);
        }
    }

    $scope.scrollToAnchor = function(id) {
        $('body').scrollTop($('#' + id).offset().top);
    };

    $scope.initTabs = tabsMenu.build;

    $scope.lorem = 'Lorem ipsum...';

    // trigger once content is loaded
    $scope.$on('$viewContentLoaded', tabsMenu.build);
};



/*  Application module */

angular.module('kiApp', ['kiApp.services', 'kiApp.directives', 'ngCookies']).
config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
    $routeProvider.when('/haku/:queryString', {templateUrl: 'partials/hakutulokset.html', controller: SearchCtrl});
    $routeProvider.when('/index/', {templateUrl: 'partials/etusivu.html', controller: IndexCtrl});
    $routeProvider.when('/info/:parentId', {templateUrl: 'partials/ylataso.html', controller: InfoCtrl});
    $routeProvider.when('/info/:parentId/:childId', {templateUrl: 'partials/alataso.html', controller: InfoCtrl});
    $routeProvider.otherwise({redirectTo: '/index/'});

    //$locationProvider.html5Mode(true);
}]);

var OPH = OPH || {};

OPH.Common = {
    initDropdownMenu: function() {
        dropDownMenu.build();
    }
};
