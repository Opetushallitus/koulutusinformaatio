"use strict";

angular.module('kiApp.services.SearchWizard', ['ngResource']).

/*
 *  SelectionBuilder is a general helper to build different kind of Search Wizard selections
 */
service('SelectionBuilder', ['SearchWizardConstants', 'TranslationService', function(SearchWizardConstants, TranslationService) {

    /*
     *  Selection represents a selection made in one phase of the Search Wizard
     */
    var Selection = function(name, code, opt, kind, prerequisite) {

        // name (or key) of selection
        this.name = name;

        // code value of selection
        this.code = code;

        // value of previous selection (used in education type selection phase)
        this.opt = opt;

        // kind of education (koulutuslaji)
        this.kind = kind;

        // prerequisite (pohjakoulutus) of selection
        this.prerequisite = prerequisite;

        // label of selection, used in UI
        this.label = TranslationService.getTranslation('searchwizard:' + opt + '-' + code );
    }

    var buildPhaseOneSelection = function(code) {
        return new Selection(SearchWizardConstants.keys.PHASEONE, code, SearchWizardConstants.keys.PHASEONE);
    },

    buildPhaseTwoSelection = function(code) {
        return new Selection(SearchWizardConstants.keys.PHASETWO, code, SearchWizardConstants.keys.PHASETWO)
    },

    buildEducationTypeSelection = function(code, opt, kind, prerequisite) {
        return new Selection(SearchWizardConstants.keys.EDTYPE, code, opt, kind, prerequisite);
    },

    buildThemeSelection = function(code, label, count) {
        var selection = new Selection(SearchWizardConstants.keys.THEME, code, SearchWizardConstants.keys.THEME);
        selection.label = label;
        selection.count = count;

        return selection;
    },

    buildTopicSelection = function(code, label, count) {
        var selection = new Selection(SearchWizardConstants.keys.TOPIC, code, SearchWizardConstants.keys.TOPIC);
        selection.label = label;
        selection.count = count;

        return selection;
    },

    buildSelection = function(name, code, opt, kind, prerequisite) {
        return new Selection(name, code, opt, kind, prerequisite);
    }

    return {
        buildPhaseOneSelection: buildPhaseOneSelection,
        buildPhaseTwoSelection: buildPhaseTwoSelection,
        buildEducationTypeSelection: buildEducationTypeSelection,
        buildThemeSelection: buildThemeSelection,
        buildTopicSelection: buildTopicSelection,
        buildSelection: buildSelection
    }

}]).

/*  
 *  Resolve options for each wizard phase
 */
service('SearchWizardService', ['SearchWizardConstants', 'SelectionBuilder',
    function(SearchWizardConstants, SelectionBuilder) {
        return {

            getPhaseOneOptions: function() {
                return [
                    SelectionBuilder.buildPhaseOneSelection(SearchWizardConstants.phaseOneOpts.PK),
                    SelectionBuilder.buildPhaseOneSelection(SearchWizardConstants.phaseOneOpts.YO),
                    SelectionBuilder.buildPhaseOneSelection(SearchWizardConstants.phaseOneOpts.MM),
                    SelectionBuilder.buildPhaseOneSelection(SearchWizardConstants.phaseOneOpts.AT),
                    SelectionBuilder.buildPhaseOneSelection(SearchWizardConstants.phaseOneOpts.ON_TUTKINTO),
                    SelectionBuilder.buildPhaseOneSelection(SearchWizardConstants.phaseOneOpts.EI_TUTKINTOA),
                    SelectionBuilder.buildPhaseOneSelection(SearchWizardConstants.phaseOneOpts.MM_AIKU)
                ];
            },

            getPhaseTwoOptions: function(opt) {
                if (opt === SearchWizardConstants.phaseOneOpts.ON_TUTKINTO) {
                    return [
                        SelectionBuilder.buildPhaseTwoSelection(SearchWizardConstants.phaseTwoOpts.YO_AIKU),
                        SelectionBuilder.buildPhaseTwoSelection(SearchWizardConstants.phaseTwoOpts.AMM_PT_AIKU),
                        SelectionBuilder.buildPhaseTwoSelection(SearchWizardConstants.phaseTwoOpts.ALEMPI_KK_AIKU)
                    ];
                } else if (opt === SearchWizardConstants.phaseOneOpts.EI_TUTKINTOA) {
                    return [
                        SelectionBuilder.buildPhaseTwoSelection(SearchWizardConstants.phaseTwoOpts.PK_AIKU),
                        SelectionBuilder.buildPhaseTwoSelection(SearchWizardConstants.phaseTwoOpts.LUKIO_AIKU)
                    ];
                } 
            },

            getEducationTypesByOption: function(opt) {
                if (opt === SearchWizardConstants.phaseOneOpts.PK) {
                    return [
                        SelectionBuilder.buildEducationTypeSelection('et01.03.01', opt, SearchWizardConstants.educationKind.NUORTEN, SearchWizardConstants.prerequisites.PK),   // Ammatillinen koulutus
                        SelectionBuilder.buildEducationTypeSelection('et01.01', opt),                                                                                           // Lukiokoulutus
                        SelectionBuilder.buildEducationTypeSelection('et01.02', opt, SearchWizardConstants.educationKind.NUORTEN, SearchWizardConstants.prerequisites.PK),      // Kaksoistutkinto
                        SelectionBuilder.buildEducationTypeSelection('et02.01.02', opt, SearchWizardConstants.educationKind.NUORTEN),                                           // Ammattistartti
                        SelectionBuilder.buildEducationTypeSelection('et02.01.01', opt, SearchWizardConstants.educationKind.NUORTEN),                                           // 10. luokka
                        SelectionBuilder.buildEducationTypeSelection('et02.015', opt, undefined, SearchWizardConstants.prerequisites.PK),                                       // Kotitalousopetus
                        SelectionBuilder.buildEducationTypeSelection('et02.05', opt, undefined, SearchWizardConstants.prerequisites.PK),                                        // Kansanopistojen pitkät linjat
                        SelectionBuilder.buildEducationTypeSelection('et01.03.02', opt, SearchWizardConstants.educationKind.NUORTEN, SearchWizardConstants.prerequisites.PK),   // Ammatillinen erityisopetus
                        SelectionBuilder.buildEducationTypeSelection('et02.02', opt, SearchWizardConstants.educationKind.NUORTEN, SearchWizardConstants.prerequisites.PK)       // Valmentava ja kuntouttava opetus ja ohjaus
                    ];
                } else if (opt === SearchWizardConstants.phaseOneOpts.YO) {
                    return [
                        SelectionBuilder.buildEducationTypeSelection('et01.03.01', opt, SearchWizardConstants.educationKind.NUORTEN, SearchWizardConstants.prerequisites.YO),   // Ammatillinen koulutus
                        SelectionBuilder.buildEducationTypeSelection('et01.04.01', opt, undefined, SearchWizardConstants.prerequisites.YO),                                     // Ammattikorkeakoulututkinto
                        SelectionBuilder.buildEducationTypeSelection('et01.05.01', opt, undefined, SearchWizardConstants.prerequisites.YO),                                     // Yliopisto (alempi)
                        SelectionBuilder.buildEducationTypeSelection('et02.05', opt, undefined, SearchWizardConstants.prerequisites.YO),                                        // Kansanopistojen pitkät linjat
                        SelectionBuilder.buildEducationTypeSelection('et02.015', opt, undefined, SearchWizardConstants.prerequisites.YO)                                        // Kotitalousopetus
                    ];
                } else if (opt === SearchWizardConstants.phaseOneOpts.AT) {
                    return [
                        SelectionBuilder.buildEducationTypeSelection('et01.04.01', opt, undefined, SearchWizardConstants.prerequisites.AT), // Ammattikorkeakoulututkinto
                        SelectionBuilder.buildEducationTypeSelection('et01.05', opt, undefined, SearchWizardConstants.prerequisites.AT)     // Yliopisto
                    ];
                } else if (opt === SearchWizardConstants.phaseOneOpts.MM) {
                    return [
                        SelectionBuilder.buildEducationTypeSelection('et02.01.04', opt, SearchWizardConstants.educationKind.NUORTEN), // Maahanmuuttajien lukioon valmistava
                        SelectionBuilder.buildEducationTypeSelection('et02.01.03', opt, SearchWizardConstants.educationKind.NUORTEN)  // Ammatilliseen koulutukseen valmistava
                    ];
                } else if (opt === SearchWizardConstants.phaseTwoOpts.YO_AIKU) {
                    return [
                        SelectionBuilder.buildEducationTypeSelection('et01.03.01', opt, SearchWizardConstants.educationKind.NUORTEN, SearchWizardConstants.prerequisites.YO),   // Ammatillinen perustutkinto 3-vuotisena
                        SelectionBuilder.buildEducationTypeSelection('et01.03.01', opt, SearchWizardConstants.educationKind.AIKUIS),                                            // Ammatillinen perustutkinto aikuiskoulutuksena
                        SelectionBuilder.buildEducationTypeSelection('et01.03.03', opt, SearchWizardConstants.educationKind.AIKUIS),                                            // Ammattitutkinto aikuiskoulutuksena
                        SelectionBuilder.buildEducationTypeSelection('et01.03.04', opt, SearchWizardConstants.educationKind.AIKUIS),                                            // Erikoisammattitutkinto aikuiskoulutuksena
                        SelectionBuilder.buildEducationTypeSelection('et01.04.01', opt),                                                                                        // Ammattikorkeakoulututkinto
                        SelectionBuilder.buildEducationTypeSelection('et01.05.01', opt),                                                                                        // Alempi tutkinto yliopistossa
                        SelectionBuilder.buildEducationTypeSelection('et02.05', opt, undefined, SearchWizardConstants.prerequisites.YO)                                         // Kansanopistojen pitkät linjat
                    ];
                } else if (opt === SearchWizardConstants.phaseTwoOpts.AMM_PT_AIKU) {
                    return [
                        SelectionBuilder.buildEducationTypeSelection('et01.03.03', opt, SearchWizardConstants.educationKind.AIKUIS),    // Ammattitutkinto aikuiskoulutuksena
                        SelectionBuilder.buildEducationTypeSelection('et01.03.04', opt, SearchWizardConstants.educationKind.AIKUIS),    // Erikoisammattitutkinto aikuiskoulutuksena
                        SelectionBuilder.buildEducationTypeSelection('et01.04.01', opt),                                                // Ammattikorkeakoulututkinto
                        SelectionBuilder.buildEducationTypeSelection('et01.05.01', opt),                                                // Alempi tutkinto yliopistossa (kandidaatti ym)
                        SelectionBuilder.buildEducationTypeSelection('et02.05', opt, undefined, SearchWizardConstants.prerequisites.YO) // Kansanopistojen koulutukset
                    ];
                } else if (opt === SearchWizardConstants.phaseTwoOpts.ALEMPI_KK_AIKU) {
                    return [
                        SelectionBuilder.buildEducationTypeSelection('et01.04.02', opt), // Ylempi ammattikorkeakoulututkinto
                        SelectionBuilder.buildEducationTypeSelection('et01.05.02', opt)  // Ylempi tutkinto yliopistossa (maisteri ym.)
                    ];
                } else if (opt === SearchWizardConstants.phaseTwoOpts.PK_AIKU) {
                    return [
                        SelectionBuilder.buildEducationTypeSelection('et01.01.02', opt),                                                                                        // Aikuislukio ja ylioppilastutkinto
                        SelectionBuilder.buildEducationTypeSelection('et01.03.01', opt, SearchWizardConstants.educationKind.NUORTEN, SearchWizardConstants.prerequisites.PK),   // Ammatillinen perustutkinto kolmivuotisena
                        SelectionBuilder.buildEducationTypeSelection('et01.03.01', opt, SearchWizardConstants.educationKind.AIKUIS),                                            // Ammatillinen perustutkinto aikuiskoulutuksena
                        SelectionBuilder.buildEducationTypeSelection('et01.03.03', opt, SearchWizardConstants.educationKind.AIKUIS),                                            // Ammattitutkinto aikuiskoulutuksena
                        SelectionBuilder.buildEducationTypeSelection('et01.03.04', opt, SearchWizardConstants.educationKind.AIKUIS),                                            // Erikoisammattitutkinto aikuiskoulutuksena
                        SelectionBuilder.buildEducationTypeSelection('et02.01.02', opt, SearchWizardConstants.educationKind.AIKUIS),                                            // Ammattistartti aikuisille
                        SelectionBuilder.buildEducationTypeSelection('et02.02', opt, SearchWizardConstants.educationKind.AIKUIS)                                                // Valmentava ja kuntouttava opetus ja ohjaus aikuisille
                    ];
                } else if (opt === SearchWizardConstants.phaseTwoOpts.LUKIO_AIKU) {
                    return [
                        SelectionBuilder.buildEducationTypeSelection('et01.03.01', opt, SearchWizardConstants.educationKind.NUORTEN, SearchWizardConstants.prerequisites.YO),   // Ammatillinen perustutkinto kolmivuotisena
                        SelectionBuilder.buildEducationTypeSelection('et01.03.01', opt, SearchWizardConstants.educationKind.AIKUIS),                                            // Ammatillinen perustutkinto aikuiskoulutuksena
                        SelectionBuilder.buildEducationTypeSelection('et01.03.01', opt, SearchWizardConstants.educationKind.AIKUIS),                                            // Ammattitutkinto aikuiskoulutuksena
                        SelectionBuilder.buildEducationTypeSelection('et01.03.01', opt, SearchWizardConstants.educationKind.AIKUIS)                                             // Erikoisammattitutkinto aikuiskoulutuksena
                    ];
                } else if (opt === SearchWizardConstants.phaseOneOpts.MM_AIKU) {
                    return [
                        SelectionBuilder.buildEducationTypeSelection('et02.01.03', opt, SearchWizardConstants.educationKind.NUORTEN),   // MAVA -Maahanmuuttajien ammatilliseen peruskoulutukseen valmistava koulutus
                        SelectionBuilder.buildEducationTypeSelection('et02.01.04', opt, SearchWizardConstants.educationKind.AIKUIS)     // LUVA - maahanmuuttajien lukioon valmistava koulutus
                    ];
                } 
            },

            getThemes: function(searchResult) {
                var themes = [];
                angular.forEach(searchResult.topicFacet.facetValues, function(theme) {
                    themes.push( SelectionBuilder.buildThemeSelection(theme.valueId, theme.valueName, theme.count) );
                })
                
                return themes;
            },


            getTopicsByTheme: function(themeId, searchResult) {
                var theme = _.find(searchResult.topicFacet.facetValues, function(theme) { return theme.valueId === themeId }),
                    topics = [];

                angular.forEach(theme.childValues, function(topic) {
                    topics.push( SelectionBuilder.buildTopicSelection(topic.valueId, topic.valueName, topic.count) )
                });

                return topics;
            },

            getPhaseOneSelectionsRequiringPhaseTwoSelection: function() {
                return [
                    SearchWizardConstants.phaseOneOpts.ON_TUTKINTO,
                    SearchWizardConstants.phaseOneOpts.EI_TUTKINTOA
                ];
            },

            getEducationTypesRequiringThemeSelection: function() {
                return [
                    'et01.03.01',   // Ammatillinen koulutus
                    'et01.02',      // Kaksoistutkinto
                    'et01.03.02',   // Ammatillinen erityisopetus
                    'et01.03.01',   // Ammatillinen koulutus
                    'et01.04.01',   // Ammattikorkeakoulututkinto
                    'et01.05',      // Yliopistotutkinto
                    'et01.05.01',   // Yliopisto (alempi)
                    'et01.03.03',   // Ammattitutkinto aikuiskoulutuksena
                    'et01.03.04',   // Erikoisammattitutkinto aikuiskoulutuksena
                    'et01.04.02',   // Ylempi ammattikorkeakoulututkinto
                    'et01.05.02'    // Ylempi tutkinto yliopistossa
                ];
            },

            getOptionsWithNoSelection: function() {
                return [
                    {
                        key: SearchWizardConstants.keys.PHASEONE
                    },
                    {
                        key: SearchWizardConstants.keys.PHASETWO
                    }
                ];
            }
        }
    }
]).

/*
 *  Resolves phase model and phases before and after current phase
 */
service('SearchWizardPhaseService', ['SearchWizardService', 'SearchWizardSelectionsService', 'SearchWizardConstants', '_',
    function(SearchWizardService, SearchWizardSelectionsService, SearchWizardConstants, _) {

        var phases = [
            SearchWizardConstants.phases.PHASEONE,
            SearchWizardConstants.phases.PHASETWO,
            SearchWizardConstants.phases.EDTYPE,
            SearchWizardConstants.phases.THEME,
            SearchWizardConstants.phases.TOPIC,
            SearchWizardConstants.phases.LOCATION
        ];
        
        return {

            // get model for specific phase
            getPhase: function(phase, searchResult) {

                var opt = SearchWizardSelectionsService.getSelectionValueByKey(SearchWizardConstants.keys.PHASETWO) || SearchWizardSelectionsService.getSelectionValueByKey(SearchWizardConstants.keys.PHASEONE);
                if (phase === SearchWizardConstants.phases.PHASEONE) {
                    return SearchWizardService.getPhaseOneOptions();
                } else if (phase === SearchWizardConstants.phases.PHASETWO) {
                    return SearchWizardService.getPhaseTwoOptions(opt);
                } else if (phase === SearchWizardConstants.phases.EDTYPE) {
                    return SearchWizardService.getEducationTypesByOption(opt);
                } else if (phase === SearchWizardConstants.phases.THEME) {
                    return SearchWizardService.getThemes(searchResult);
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

            // get next phase of a specific phase
            getNextPhase: function(currentPhase) {
                var index = phases.indexOf(currentPhase) + 1;

                // check if phase two selection is required
                if (phases[index] == SearchWizardConstants.phases.PHASETWO) {
                    var phaseOneSelectionsRequiringPhaseTwoSelection = SearchWizardService.getPhaseOneSelectionsRequiringPhaseTwoSelection();
                    if (phaseOneSelectionsRequiringPhaseTwoSelection.indexOf( SearchWizardSelectionsService.getSelectionValueByKey( SearchWizardConstants.keys.PHASEONE ) ) < 0) {
                        var indexAfterPhaseTwo = phases.indexOf( SearchWizardConstants.phases.PHASETWO ) + 1;
                        return phases[indexAfterPhaseTwo];
                    }
                }

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

            // get previous phase of a specific phase
            getPreviousPhase: function(currentPhase) {
                var index = phases.indexOf(currentPhase) - 1;

                // check if phase two selection is required
                if (phases[index] == SearchWizardConstants.phases.PHASETWO) {
                    var phaseOneSelectionsRequiringPhaseTwoSelection = SearchWizardService.getPhaseOneSelectionsRequiringPhaseTwoSelection();
                    if (phaseOneSelectionsRequiringPhaseTwoSelection.indexOf( SearchWizardSelectionsService.getSelectionValueByKey( SearchWizardConstants.keys.PHASEONE ) ) < 0) {
                        var indexAfterPhaseTwo = phases.indexOf( SearchWizardConstants.phases.PHASETWO ) - 1;
                        return phases[indexAfterPhaseTwo];
                    }
                }

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

/*
 *  Saves the selections made during different wizard phases. Used for making and removing selections 
 *  and transforming selections into query string.
 */
service('SearchWizardSelectionsService', ['SearchWizardService', 'LanguageService', '_',
    function(SearchWizardService, LanguageService, _) {
        // default params for search
        var defaultParams = {
            queryString: '*',
            start: 0,
            rows: 0,
            lang: LanguageService.getLanguage()
        },
            selections = [],    // phase selections are saved here
            locations = [];     // selected locations are saved here

        // check if a selection should be included in search params (all selection do not effect the search)
        var isSelectionActualFilter = function(selection) {
            // some wizard option selections do not make actual result filtering
            var selectionless = SearchWizardService.getOptionsWithNoSelection();
            var found = false;
            angular.forEach(selectionless, function(item) {
                if (selection.name === item.key && (selection.code === item.value || item.value === undefined)) {
                    found = true;
                }
            });

            return !found;
        }

        return {
            getSelections: function() {
                return angular.copy(selections);
            },

            // return selection value by key
            getSelectionValueByKey: function(key) {
                var result;
                angular.forEach(selections, function(selection) {
                    if (selection.name === key) {
                        result = selection.code;
                    }
                });

                return result;
            },

            // returns selections as search params, used for fetching saerch results between phases
            getAsSearchParams: function() {
                var result = {};
                result.facetFilters = [];
                result.locations = [];
                angular.forEach(selections, function(selection) {
                    if (isSelectionActualFilter(selection)) {
                        result.facetFilters.push(selection.name + ':' + selection.code);

                        if (selection.kind) {
                            result.facetFilters.push('kindOfEducation_ffm:' + selection.kind)
                        }

                    }
                });

                angular.forEach(locations, function(location) {
                    result.locations.push('city:' + location.name);
                });

                _.extend(result, defaultParams);
                return result;
            },

            // build the final search url from phase selections, used to redirect to search result view
            getSearchUrl: function() {
                var action = 'haku/';
                var query = '?';
                
                angular.forEach(selections, function(selection) {
                    if (isSelectionActualFilter(selection)) {
                        query += '&facetFilters=' + selection.name + ':' +  selection.code;

                        if (selection.kind) {
                            query += '&facetFilters=' + 'kindOfEducation_ffm:' + selection.kind;
                        }
                    }
                });

                angular.forEach(locations, function(location) {
                    query += '&locations=' +  location.code;
                });

                query += '&tab=los';

                return action + defaultParams.queryString + query;
            },

            // add a single selection
            addSelection: function(option) {
                selections.push(option);
            },

            // add a single location
            addLocation: function(location) {
                locations.push(location);
            },

            // removes the latest selection
            removeLatestSelection: function() {
                selections.pop();
            },

            // clear selections and locations
            clearSelections: function() {
                selections = [];
                locations = [];
            }
        }

    }
]);