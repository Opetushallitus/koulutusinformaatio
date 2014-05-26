angular.module('kiApp.FacetTree', []).

directive('facetTree', function($compile) {
    return {
        restrict: 'A',
        replace: true,
        link: function($scope, element, attrs) {

            var update = function() {
                template = '<ul data-ng-if="isNotLeaf(' + attrs.treeModel + ')">' +
                    '<li data-ng-repeat="node in ' + attrs.treeModel + '">' + 
                        '<span data-ng-if="!isSelected(node) && (node.count > 0)" class="facet-item">' +
                            '<a href="javascript:void(0)" data-ng-click="selectFacetFilter(node.valueId, node.facetField);" data-facet-title="node">{{node.valueName}} ({{node.count}})</a>' +
                        '</span>' +
                        '<span data-ng-show="isSelected(node)" class="facet-item selected">' +
                            '<span data-facet-title="node">{{node.valueName}}</span>' +
                            '<a title="{{locales.removeFacet}}" href="javascript:void(0)" class="remove" data-ng-click="removeSelection(node)"></a>' +
                        '</span>' +
                        '<ul data-ng-if="node.childValues" data-facet-tree data-tree-model="node.childValues"></ul>' +
                    '</li>' +
                '</ul>';

                var newElement = angular.element(template);
                $compile(newElement)($scope);
                element.replaceWith(newElement);
            }

            var hasChildren = function(nodes) {
                var result = false;
                angular.forEach(nodes, function(node, key) {
                    if (node.childValues && node.childValues.length > 0) {
                        result = true;
                    }
                });

                return result;
            }

            var isRoot = function(nodes) {
                var result = false;
                angular.forEach(nodes, function(node, key) {
                    if (!node.parentId) {
                        result = true;
                    }
                });

                return result;
            }

            var nodeIsSelected = function(nodes) {
                var result = false;
                angular.forEach(nodes, function(node, key) {
                    if ($scope.isSelected(node)) {
                        result = true;
                    }
                });

                return result;
            }

            $scope.isNotLeaf = function(nodes) {
                return isRoot(nodes) || hasChildren(nodes) || nodeIsSelected(nodes);

            }

            update();
        }
    }
}).

directive('facetTreeLeaves', function() {
    return {
        restrict: 'A',
        replace: true,
        scope: {
            treeModel: '=',
            selectNode: '&',
            nodeIsSelected: '&'
        },
        template: 
            '<ul data-ng-if="showLeaves" class="nobullet">' + 
                '<li class="bold block margin-top-1" data-ki-i18n="facet-focus-filter"></li>' +
                '<li data-ng-repeat="node in leaves" class="facet-tree-leaf">' +
                    '<span title="{{node.valueName}}" data-ng-if="node.count > 0" class="facet-item">' +
                        '<a href="javascript:void(0)" data-ng-click="selectFacetFilter(node.valueId, node.facetField);" data-facet-title="fv">{{node.valueName}} ({{node.count}})</a>' +
                    '</span>' +
                '</li>' +
            '</ul>'
            ,
        link: function($scope, element, attrs) {

            var childIsLeaf = function(child) {
                return child.childValues == null || (child.childValues && child.childValues.length <= 0);
            }

            var childIsRoot = function(child) {
                return child.parentId == null;
            }

            var isSelected = $scope.nodeIsSelected();
            $scope.selectFacetFilter = $scope.selectNode();
            
            $scope.$watch('treeModel', function(value) {
                var iterate = function(children) {
                    var result;
                    angular.forEach(children, function(child) {
                        if (childIsLeaf(child) && !childIsRoot(child) && isSelected(child)) {
                            result = undefined;
                        } else if (childIsLeaf(child) && !childIsRoot(child)) {
                            result =  children;
                        } else {
                            result = iterate(child.childValues);
                        }
                    });

                    return result;
                }

                $scope.leaves = iterate(value);
                $scope.showLeaves = $scope.leaves ? true : false;
            });
        }

    }
});