/*
	@license Angular Treeview version 0.1.6
	ⓒ 2013 AHN JAE-HA http://github.com/eu81273/angular.treeview
	License: MIT


	[TREE attribute]
	angular-treeview: the treeview directive
	tree-id : each tree's unique id.
	tree-model : the tree model on $scope.
	node-id : each node's id
	node-label : each node's label
	node-children: each node's children

	<div
		data-angular-treeview="true"
		data-tree-id="tree"
		data-tree-model="roleList"
		data-node-id="roleId"
		data-node-label="roleName"
		data-node-children="children" >
	</div>
*/

(function ( angular ) {
'use strict';

angular.module( 'angularTreeview', [] ).

	directive( 'treeModel', ['$compile', 'TreeService', function( $compile, TreeService ) {
		return {
			restrict: 'A',
			link: function ( scope, element, attrs ) {
				//tree id
				var treeId = attrs.treeId;
			
				//tree model
				var treeModel = attrs.treeModel;

				//node id
				var nodeId = attrs.nodeId || 'id';

				//node label
				var nodeLabel = attrs.nodeLabel || 'label';

				//children
				var nodeChildren = attrs.nodeChildren || 'children';

				//tree template
				var template =
					'<ul class="nobullet">' +
						'<li data-ng-repeat="node in ' + treeModel + '">' +
							'<i class="collapsed" data-ng-show="getStatus(node) == \'collapsed\'" data-ng-click="' + treeId + '.selectNodeHead(node)"></i>' +
							'<i class="expanded" data-ng-show="getStatus(node) == \'expanded\'" data-ng-click="' + treeId + '.selectNodeHead(node)"></i>' +
							'<i class="normal" data-ng-hide="getStatus(node) == \'normal\'"></i> ' +
							
                			'<span title="{{node.valueName}}" data-ng-show="!isSelected(node) && (node.count > 0)" class="facet-item">' +
                    			'<a href="javascript:void(0)" data-ng-click="selectFacetFilter(node.valueId, node.facetField); ' + treeId + '.selectNodeHead(node)">{{node.valueName}} ({{node.count}})</a>' +
                			'</span>' +
	                		'<span title="{{node.valueName}}" data-ng-show="!isSelected(node) && (node.count <= 0)" class="facet-item inactive">' +
	                    		'{{node.valueName}} ({{node.count}})' +
	                		'</span>' +
	                		'<span title="{{node.valueName}}" data-ng-show="isSelected(node)" class="facet-item selected">' +
	                    		'<span data-ng-click="' + treeId + '.selectNodeHead(node)">{{node.valueName}}</span>' +
	                    		'<a title="{{locales.removeFacet}}" href="javascript:void(0)" class="remove" data-ng-click="removeSelection(node)"></a>' +
	                		'</span>' +

							'<div data-ng-show="isExpanded(node)" data-tree-id="' + treeId + '" data-tree-model="node.' + nodeChildren + '" data-node-id=' + nodeId + ' data-node-label=' + nodeLabel + ' data-node-children=' + nodeChildren + '></div>' +
						'</li>' +
					'</ul>';


				//check tree id, tree model
				if( treeId && treeModel ) {

					//root node
					if( attrs.angularTreeview ) {
					
						//create tree object if not exists
						scope[treeId] = scope[treeId] || {};

						//if node head clicks,
						scope[treeId].selectNodeHead = scope[treeId].selectNodeHead || function( selectedNode ){

							//Collapse or Expand
							//selectedNode.expanded = !selectedNode.expanded;
							TreeService.set(treeId, selectedNode[nodeId], scope.hasSelectedChilds(selectedNode));
						};

						//if node label clicks,
						scope[treeId].selectNodeLabel = scope[treeId].selectNodeLabel || function( selectedNode ){

							//remove highlight from previous node
							if( scope[treeId].currentNode && scope[treeId].currentNode.selected ) {
								scope[treeId].currentNode.selected = undefined;
							}

							//set highlight to selected node
							selectedNode.selected = 'selected';

							//set currentNode
							scope[treeId].currentNode = selectedNode;
						};
					}

					//Rendering template.
					element.html('').append( $compile( template )( scope ) );
				}


				scope.getStatus = function(node) {
					var cachedValue = TreeService.get(treeId, node.valueId, scope.hasSelectedChilds(node));
					if (!node[nodeChildren] || node[nodeChildren].length <= 0) {
						return 'normal';
					} else if (cachedValue != undefined) {
						if (cachedValue) {
							return 'expanded';
						} else {
							return 'collapsed';
						}
					} else if (scope.hasSelectedChilds(node)) {
						return 'expanded';
					} else {
						return 'collapsed';
					}
				};

				// tells if children should be expanded
				scope.isExpanded = function(node) {
					var result = TreeService.get(treeId, node.valueId, scope.hasSelectedChilds(node));
					if (result == undefined) {
						return scope.hasSelectedChilds(node);
					} else {
						return result;
					}
				};

				// checks if current node has selected children
				scope.hasSelectedChilds = function(node) {
					if (node[nodeChildren] && node[nodeChildren].length > 0) {
						var result = false;
						angular.forEach(node[nodeChildren], function(child, key){
							if (scope.isSelected(child)) {
								result = true;
							}
						});

						if (result) {
							return true
						} else {
							var recRes = false;
							angular.forEach(node[nodeChildren], function(child, key){
								recRes = scope.hasSelectedChilds(child);
							});

							return recRes;
						}
					} else {
						return false;
					}
				};
			}
		};
	}])

	.service('TreeService', function() {
		var cache = [];

		return {
			set: function(treeId, itemId, defaultValue) {
				if (cache[treeId][itemId] != undefined) {
					cache[treeId][itemId] = !cache[treeId][itemId];
				} else {
					cache[treeId][itemId] = !defaultValue;
				}
			},
			get: function(treeId, itemId, defaultValue) {
				if (cache[treeId]) {
					return cache[treeId][itemId];
				} else {
					cache[treeId] = [];
				}
			},
			clear: function() {
				cache = [];
			}
		}
	})

})( angular );
