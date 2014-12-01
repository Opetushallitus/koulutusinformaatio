"use strict";

/**
 *  Search wizard implements a functionality where user filters out educations of interest by making filtering selections phase by phase.
 *  Results of this wizard are shown in the search result view by generating a query string from these filtering selections.
 */

angular.module('kiApp.SearchWizard', ['kiApp.directives.SearchWizard', 'kiApp.services.SearchWizard']).

/*
 *  Constants used by Search Wizard
 */
constant('SearchWizardConstants', {
    phaseOneOpts: {
        PK: 'pk',
        YO: 'yo',
        MM: 'mm',
        AT: 'at',
        ON_TUTKINTO: 'on_tutkinto',
        EI_TUTKINTOA: 'ei_tutkintoa',
        MM_AIKU: 'mm_aiku'
    },
    phaseTwoOpts: {
        YO_AIKU: 'yo_aiku',
        AMM_PT_AIKU: 'amm_pt_aiku',
        ALEMPI_KK_AIKU: 'alempi_kk_aiku',
        PK_AIKU: 'pk_aiku',
        LUKIO_AIKU: 'lukio_aiku'
    },
    phases: {
        PHASEONE: 'phaseone',
        PHASETWO: 'phasetwo',
        EDTYPE: 'educationtype',
        THEME: 'theme',
        TOPIC: 'topic',
        LOCATION: 'location'
    },
    keys: {
        PHASEONE: 'phaseone',
        PHASETWO: 'phasetwo',
        EDTYPE: 'educationType_ffm',
        KIND: 'kindOfEducation_ffm',
        THEME: 'theme_ffm',
        TOPIC: 'topic_ffm',
        LOCATION: 'locations'
    },
    educationKind: {
        NUORTEN: 'koulutuslaji_n',
        AIKUIS: 'koulutuslaji_a'
    },
    prerequisites: {
        PK: 'pk',
        YO: 'yo',
        AT: 'at'
    }
}).

/*
 *  Controller for the Search Wizard component. Keeps track of the current phase and requests its model.
 */
controller('SearchWizardCtrl', [
    '$scope', 
    '$rootScope', 
    '$routeParams',
    '$location',
    '$sanitize',
    'TranslationService', 
    'SearchLearningOpportunityService',
    'SearchWizardService',
    'SearchWizardSelectionsService',
    'SearchWizardPhaseService',
    'SearchWizardConstants',
    'SelectionBuilder',
    '_',
    function($scope, $rootScope, $routeParams, $location, $sanitize, TranslationService, SearchLearningOpportunityService, SearchWizardService, SearchWizardSelectionsService, SearchWizardPhaseService, SearchWizardConstants, SelectionBuilder, _) {
        $rootScope.title = TranslationService.getTranslation('searchwizard:title') + ' - ' + TranslationService.getTranslation('sitename');

        // do not show search bar in search wizard pages
        $rootScope.hideSearchbar = true;

        // init wizard based on query params
        var initWizard = function() {
            var qParams = $location.search();
            var phase;
            angular.forEach(qParams, function(val, key) {
                // sanitize query params
                if (typeof val === 'string') {
                    val = $sanitize(val);
                    qParams[key] = val;
                }

                // add each pre-selection from query params
                var isValidKey = _.contains(_.values(SearchWizardConstants.keys), key);
                if (isValidKey) {
                    var selection = SelectionBuilder.buildSelection(key, val, key);
                    SearchWizardSelectionsService.addSelection(selection);
                } else if (key === 'phase') {
                    phase = val;
                }
            });

            // initialize phase set in query params
            if (phase) {
                $scope.currentPhase = SearchWizardPhaseService.getNextPhase(phase);
            }

            if (!$scope.currentPhase) {
                $scope.currentPhase = SearchWizardPhaseService.getFirstPhase();
            }

            initPhase($scope.currentPhase);
        };

        // fetch phase model
        var initPhase = function(phase) {
            $scope.phaseIsLoading = true;
            SearchLearningOpportunityService.query(
                SearchWizardSelectionsService.getAsSearchParams(), true
            ).then(function(result) {
                $scope.searchResult = result;
                $scope.currentPhase = phase;
                $scope.resultCount = result.loCount;
                $scope.phase = {
                    options: SearchWizardPhaseService.getPhase(phase, result)
                };
                $scope.selections = SearchWizardSelectionsService.getSelections();
                $scope.baseEducation = SearchWizardSelectionsService.getSelectionValueByKey(SearchWizardConstants.keys.PHASEONE);
                $scope.phaseSelection = {};
                $scope.phaseIsLoading = false;
            });
        };

        var gotoNextPhase = function() {
            var nextPhase = SearchWizardPhaseService.getNextPhase($scope.currentPhase, $scope.searchResult);
            initPhase(nextPhase);
        };

        // tell if current phase is first
        $scope.isFirstPhase = function() {
            return SearchWizardPhaseService.isFirstPhase($scope.currentPhase);
        };

        $scope.isLastPhase = function() {
            return SearchWizardPhaseService.isLastPhase($scope.currentPhase);
        };

        // rewind back to previous wizard phase
        $scope.gotoPreviousPhase = function() {
            var previousPhase = SearchWizardPhaseService.getPreviousPhase($scope.currentPhase, $scope.searchResult);
            SearchWizardSelectionsService.removeLatestSelection();
            initPhase(previousPhase);
        };

        // make a filtering selection for phase
        $scope.makeSelection = function(option, label) {
            SearchWizardSelectionsService.addSelection(option, label);
            gotoNextPhase();
        };

        // generate search url and redirect to search result view
        $scope.showResults = function() {
            var searchUrl = SearchWizardSelectionsService.getSearchUrl();
            SearchWizardSelectionsService.clearSelections();
            $location.url( searchUrl );
        };

        initWizard();        
    }
]);