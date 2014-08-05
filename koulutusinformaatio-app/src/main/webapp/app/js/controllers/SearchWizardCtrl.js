/**
 *  Search wizard
 */

angular.module('SearchWizard', []).

constant('SearchWizardConstants', {
    prerequisites: {
        PK: 'pk',
        YO: 'yo',
        MM: 'mm'
    },
    phases: {
        BASEED: 'baseeducation',
        EDTYPE: 'educationtype',
        THEME: 'theme',
        TOPIC: 'topic',
        LOCATION: 'location'
    },
    keys: {
        BASEED: 'prerequisites',
        EDTYPE: 'educationType_ffm',
        THEME: 'theme_ffm',
        TOPIC: 'topic_ffm',
        LOCATION: 'locations'
    }
}).

controller('SearchWizardCtrl', [
    '$scope', 
    '$rootScope', 
    '$routeParams',
    '$location',
    'TranslationService', 
    'SearchLearningOpportunityService',
    'SearchWizardService',
    'SearchWizardSelectionsService',
    'SearchWizardPhaseService',
    'SearchWizardConstants',
    function($scope, $rootScope, $routeParams, $location, TranslationService, SearchLearningOpportunityService, SearchWizardService, SearchWizardSelectionsService, SearchWizardPhaseService, SearchWizardConstants) {
        $rootScope.title = TranslationService.getTranslation('searchwizard:title') + ' - ' + TranslationService.getTranslation('sitename');

        var initPhase = function(phase) {
            $scope.phaseIsLoading = true;
            SearchLearningOpportunityService.query(
                SearchWizardSelectionsService.getAsSearchParams()
            ).then(function(result) {
                $scope.currentPhase = phase;
                $scope.resultCount = result.loCount;
                $scope.phase = {
                    options: SearchWizardPhaseService.getPhase(phase, result)
                }
                $scope.selections = SearchWizardSelectionsService.getSelections();
                $scope.baseEducation = SearchWizardSelectionsService.getSelectionValueByKey(SearchWizardConstants.keys.BASEED);
                $scope.phaseIsLoading = false;
            });
        };

        var gotoNextPhase = function() {
            var nextPhase = SearchWizardPhaseService.getNextPhase($scope.currentPhase);
            initPhase(nextPhase);
        };

        $scope.isFirstPhase = function() {
            return SearchWizardPhaseService.isFirstPhase($scope.currentPhase);
        };

        $scope.gotoPreviousPhase = function() {
            var previousPhase = SearchWizardPhaseService.getPreviousPhase($scope.currentPhase);
            SearchWizardSelectionsService.removeLatestSelection();
            initPhase(previousPhase);
        };

        $scope.makeSelection = function(name, value, label) {
            SearchWizardSelectionsService.addSelection(name, value, label);
            gotoNextPhase();
        };

        $scope.showResults = function() {
            var searchUrl = SearchWizardSelectionsService.getSearchUrl();
            SearchWizardSelectionsService.clearSelections();
            $location.url( searchUrl );
        };

        $scope.currentPhase = SearchWizardPhaseService.getFirstPhase();
        initPhase($scope.currentPhase);
    }
]).

/*  
 *  Resolve options for each wizard phase
 */
service('SearchWizardService', ['SearchWizardConstants',
    function(SearchWizardConstants) {
        return {
            getPrerequisites: function() {
                return [
                    SearchWizardConstants.prerequisites.PK,
                    SearchWizardConstants.prerequisites.YO,
                    SearchWizardConstants.prerequisites.MM
                ];
            },

            getEducationTypesByPrerequisite: function(prerequisite) {
                if (prerequisite === SearchWizardConstants.prerequisites.PK) {
                    return [
                        'et01.03.01',   // Ammatillinen koulutus
                        'et01.01',      // Lukiokoulutus
                        'et01.02',      // Kaksoistutkinto
                        'et02.01.02',   // Ammattistartti
                        'et02.01.01',   // 10. luokka
                        'et02.015',     // Kotitalousopetus
                        'et02.05',      // Kansanopistojen pitkät linjat
                        'et01.03.02',   // Ammatillinen erityisopetus
                        'et02.02'       // Valmentava ja kuntouttava opetus ja ohjaus
                    ];
                } else if (prerequisite === SearchWizardConstants.prerequisites.YO) {
                    return [
                        'et01.03.01',   // Ammatillinen koulutus
                        'et01.04.01',   // Ammattikorkeakoulututkinto
                        'et01.05.01',   // Yliopisto (alempi)
                        'et02.05',      // Kansanopistojen pitkät linjat
                        'et02.015'      // Kotitalousopetus
                    ];
                } else {
                    return [
                        'et02.01.04',   // Maahanmuuttajien lukioon valmistava
                        'et02.01.03'    // Ammatilliseen koulutukseen valmistava
                    ];
                }
            },

            getTopicsByTheme: function(themeId, searchResult) {
                var result;
                angular.forEach(searchResult.topicFacet.facetValues, function(theme, key) {
                    if (theme.valueId === themeId) {
                        result = theme.childValues;
                    }
                });

                return result;
            },

            getEducationTypesRequiringThemeSelection: function() {
                return [
                    'et01.03.01',   // Ammatillinen koulutus
                    'et01.02',      // Kaksoistutkinto
                    'et01.03.02',   // Ammatillinen erityisopetus
                    'et01.03.01',   // Ammatillinen koulutus
                    'et01.04.01',   // Ammattikorkeakoulututkinto
                    'et01.05.01'    // Yliopisto (alempi)
                ];
            },

            getOptionsWithNoSelection: function() {
                return [
                    {
                        key: SearchWizardConstants.keys.BASEED,
                        value: SearchWizardConstants.prerequisites.MM
                    }
                ];
            }
        }
    }
]).

/*
 *  
 */
service('SearchWizardPhaseService', ['SearchWizardService', 'SearchWizardSelectionsService', 'SearchWizardConstants', '_',
    function(SearchWizardService, SearchWizardSelectionsService, SearchWizardConstants, _) {
        var phases = [
            SearchWizardConstants.phases.BASEED,
            SearchWizardConstants.phases.EDTYPE,
            SearchWizardConstants.phases.THEME,
            SearchWizardConstants.phases.TOPIC,
            SearchWizardConstants.phases.LOCATION
        ];
        
        return {
            getPhase: function(phase, searchResult) {
                if (phase === SearchWizardConstants.phases.BASEED) {
                    return SearchWizardService.getPrerequisites();
                } else if (phase === SearchWizardConstants.phases.EDTYPE) {
                    return SearchWizardService.getEducationTypesByPrerequisite(SearchWizardSelectionsService.getSelectionValueByKey(SearchWizardConstants.keys.BASEED));
                } else if (phase === SearchWizardConstants.phases.THEME) {
                    return searchResult.topicFacet.facetValues;
                } else if (phase === SearchWizardConstants.phases.TOPIC) {
                    return SearchWizardService.getTopicsByTheme(SearchWizardSelectionsService.getSelectionValueByKey(SearchWizardConstants.keys.THEME), searchResult);
                } else if (phase === SearchWizardConstants.phases.LOCATION) {
                    // does not require specific model
                }
            },

            isFirstPhase: function(currentPhase) {
                return currentPhase === phases[0];
            },

            getFirstPhase: function() {
                return phases[0];
            },

            getNextPhase: function(currentPhase) {
                var index = phases.indexOf(currentPhase) + 1;
                // skip theme and topic selection if selected education type does not require it
                if (phases[index] === SearchWizardConstants.phases.THEME) {
                    var edTypesRequiringTheme = SearchWizardService.getEducationTypesRequiringThemeSelection();
                    if (edTypesRequiringTheme.indexOf( SearchWizardSelectionsService.getSelectionValueByKey(SearchWizardConstants.keys.EDTYPE) ) < 0) {
                        var indexAfterTopic = phases.indexOf(SearchWizardConstants.phases.TOPIC) + 1;
                        return phases[indexAfterTopic];
                    }
                }
                
                return phases[index];
            },

            getPreviousPhase: function(currentPhase) {
                var index = phases.indexOf(currentPhase) - 1;
                // skip topic and theme selection if selected education type does not require it
                if (phases[index] === SearchWizardConstants.phases.TOPIC) {
                    var edTypesRequiringTheme = SearchWizardService.getEducationTypesRequiringThemeSelection();
                    if (edTypesRequiringTheme.indexOf( SearchWizardSelectionsService.getSelectionValueByKey(SearchWizardConstants.keys.EDTYPE) ) < 0) {
                        var indexBeforeTheme = phases.indexOf(SearchWizardConstants.phases.THEME) - 1;
                        return phases[indexBeforeTheme];
                    }
                }

                return phases[index];
            }
        }
    }
]).


service('SearchWizardSelectionsService', ['SearchWizardService', 'LanguageService', '_',
    function(SearchWizardService, LanguageService, _) {
        var defaultParams = {
            queryString: '*',
            start: 0,
            rows: 0,
            lang: LanguageService.getLanguage()
        };
        var selections = [];
        var locations = [];

        var isSelectionActualFilter = function(selection) {
            // some wizard option selections do not make actual result filtering
            var selectionless = SearchWizardService.getOptionsWithNoSelection();
            var found = false;
            angular.forEach(selectionless, function(item) {
                if (selection.name === item.key && selection.value === item.value) {
                    found = true;
                }
            });

            return !found;
        }

        return {
            getSelections: function() {
                return angular.copy(selections);
            },

            getSelectionValueByKey: function(key) {
                var result;
                angular.forEach(selections, function(selection) {
                    if (selection.name === key) {
                        result = selection.value;
                    }
                });

                return result;
            },

            getAsSearchParams: function() {
                var result = {};
                result.facetFilters = [];
                result.locations = [];
                angular.forEach(selections, function(selection) {
                    if (isSelectionActualFilter(selection)) {
                        result.facetFilters.push(selection.name + ':' + selection.value);
                    }
                });

                angular.forEach(locations, function(location) {
                    result.locations.push('city:' + location.name);
                });

                _.extend(result, defaultParams);
                return result;
            },

            getSearchUrl: function() {
                var action = 'haku/';
                var query = '?';
                
                angular.forEach(selections, function(selection) {
                    if (isSelectionActualFilter(selection)) {
                        query += '&facetFilters=' + selection.name + ':' +  selection.value;
                    }
                });

                angular.forEach(locations, function(location) {
                    query += '&locations=' +  location.code;
                });

                query += '&tab=los';

                return action + defaultParams.queryString + query;
            },

            addSelection: function(name, value, label) {
                var item = {};
                item.name = name;
                item.value = value;
                item.label = label;
                selections.push(item);
            },

            addLocation: function(location) {
                locations.push(location);
            },

            removeLatestSelection: function() {
                selections.pop();
            },

            clearSelections: function() {
                selections = [];
                locations = [];
            }
        }

    }
]).

directive('kiSearchwizardLocationSelector', ['SearchLocationService', 'SearchWizardSelectionsService', 'TranslationService',
    function(SearchLocationService, SearchWizardSelectionsService, TranslationService) {
        return {
            restrict: 'A',
            templateUrl: 'partials/searchwizard/locationSelector.html',
            link: function(scope, element, attrs) {

                scope.add = function() {
                    SearchWizardSelectionsService.addLocation(scope.location);
                }

                scope.getLocations = function($viewValue) {
                    return SearchLocationService.query($viewValue);
                }

                scope.placeholder = TranslationService.getTranslation('location-filter-placeholder');
            }
        }
    }
]).

directive('phaseLoader', function () {
    return {
        restrict: 'A',
        template: 
            '<div class="ajax-loader text-center">' +
                '<img src="img/ajax-loader-big.gif" />' +
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
});;