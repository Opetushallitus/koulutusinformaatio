/*  Services */

angular.module('kiApp.services', ['ngResource']).
factory('LearningOpportunity', function($resource) {
    return $resource('../lo/search/:queryString', {}, {
        query: {method:'GET', isArray:true}
    });
}).
factory('ParentLearningOpportunity', function($resource) {
    return $resource('../lo/:parentId', {}, {
        query: {method:'GET', isArray:false}
    });
}).
factory('ApplicationOption', function($resource) {
    return $resource('../ao/search/:asId/:lopId', {}, {
        query: {method:'GET', isArray:true}
    });
}).
service('SearchService', function() {
    var term;

    return {
        getTerm: function() {
            return term;
        },

        setTerm: function(newTerm) {
            term = newTerm;
        }
    };
}).
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

                if (index < parentData.children.length - 1) { 
                    result += '<span class="list-separator">|</span>';
                }
            }

            element.html(result);
        }, true);
        
    }
});



/* Controllers */

function IndexCtrl($scope, $routeParams, LearningOpportunity, $location) {

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

function SearchCtrl($scope, $routeParams, LearningOpportunity, ParentLearningOpportunity, SearchService, $location) {
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
            console.log($scope.queryString);
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

function InfoCtrl($scope, $routeParams, LearningOpportunity, ParentLearningOpportunity, SearchService, ApplicationOption, $location) {
    $scope.queryString = SearchService.getTerm();

    if ($routeParams.parentId) {
        $scope.parentId = $routeParams.parentId;
        $scope.parentLO = ParentLearningOpportunity.query({parentId: $routeParams.parentId}, function(data) {
            for (var index in data.children) {
                if (data.children[index].id == $routeParams.childId) {
                    $scope.childLO = data.children[index];
                    break;
                }
            }

            $scope.viewLO = $scope.childLO ? $scope.childLO : $scope.parentLO;

            /*
            $scope.ao = ApplicationOption.query({asId: $scope.parentLO.provider.applicationSystemIds[0], lopId: $scope.parentLO.provider.id});
            console.log($scope.ao);
            */
        });

        
    }

    $scope.back = function() {
        $location.path('/haku/' + SearchService.getTerm());
    }

    // TODO: how to make sure DOM is ready?
    $scope.$on('$viewContentLoaded', tabsMenu.build);
};



/*  Application module */

angular.module('kiApp', ['kiApp.services']).
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
