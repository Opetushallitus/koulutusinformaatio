angular.module('kiApp.SelectAreaDialog', [])

.directive('kiSelectAreaDialog', function() {
	return {
		restrict: 'A',
		scope: true,
		controller: function($scope, $modal) {
			$scope.openModal = function() {
				var currentFocusedElement = $(':focus');

				var modalIntance = $modal.open({
					templateUrl: 'templates/selectArea.html',
					backdrop: 'static',
					controller: LocationDialogCtrl
				});

				modalIntance.result.then(
					function(result) {
						$scope.setFilteredLocations(result);
						$scope.change();

						currentFocusedElement.focus();
					},
					function(result) {
						currentFocusedElement.focus();
					});
			}
		}
	}
});


function LocationDialogCtrl($scope, $modalInstance, $timeout, ChildLocationsService, UtilityService, DistrictService, TranslationService) {

	$timeout(function(){
		$('#select-location-dialog').attr('aria-hidden', 'false');
	}, 0);

	$scope.titleLocales = {
		close: TranslationService.getTranslation('tooltip:close'),
		removeFacet: TranslationService.getTranslation('tooltip:remove-facet')
	}

	DistrictService.query().then(function(result) {
		$scope.distResult = result;
		$scope.distResult.unshift({name: TranslationService.getTranslation('koko') + ' ' + TranslationService.getTranslation('suomi'), code: '-1'});

        // IE requires this to redraw select boxes after data is loaded
        $timeout(function() {
        	$("#districtSelection").css("width", '80%');
        }, 0);
    });

	$scope.cancel = function() {
		$('#select-location-dialog').attr('aria-hidden', 'true');
		$modalInstance.dismiss('cancel');
	}

	$scope.changeSelection = function() {
		selectMunicipality();
	}

	var doMunicipalitySearch = function() {
		var queryDistricts = [];
		if ($scope.muniResult != undefined) {
			$scope.muniResult.length = 0;
		} else {
			$scope.muniResult = [];
		}
		if ($scope.isWholeAreaSelected($scope.selectedDistricts)) {
			queryDistricts = $scope.distResult;
		} else {
			queryDistricts = $scope.selectedDistricts;
		}
		ChildLocationsService.query(queryDistricts).then(function(result) {

			if (!$scope.isWholeAreaSelected($scope.selectedDistricts)) {
				UtilityService.sortLocationsByName(result);
				$scope.muniResult.push.apply($scope.muniResult, queryDistricts);
				$scope.muniResult.push.apply($scope.muniResult, result);
			} else {
				$scope.muniResult.push.apply($scope.muniResult, result);
			}

            // IE requires this to redraw select boxes after data is loaded
            $timeout(function() {
            	$("#municipalitySelection").css("width", '80%');
            }, 0);
            
        });
	}

	var selectMunicipality = function() {
		if (!$scope.selectedMunicipalities) {
			$scope.selectedMunicipalities = [];
		}

		angular.forEach($scope.selectedMunicipality, function(mun, munkey){

			var found = false;
			angular.forEach($scope.selectedMunicipalities, function(value, key){
				if (value.code == mun.code) {
					found = true;
				}
			});

			if (!found) {
				$scope.selectedMunicipalities.push(mun);
			}

		});
	}

	$scope.$watch('selectedDistricts', function(value) {
		if (value) {
			doMunicipalitySearch();
		}
	});

	$scope.removeMunicipality = function(code) {
		angular.forEach($scope.selectedMunicipalities, function(mun, key) {
			if (code == mun.code) {
				$scope.selectedMunicipalities.splice(key, 1);
			}
		});

		// move focus to municipality selection area
		$('#municipalitySelection').focus();
	}

	$scope.isWholeAreaSelected = function(areaArray) {
		for (var i = 0; i < areaArray.length; i++) {
			if (areaArray[i].code == '-1') {
				return true;
			}
		}
		return false;
	}

	$scope.filterBySelLocations = function() {
		$modalInstance.close($scope.selectedMunicipalities);
	}

}