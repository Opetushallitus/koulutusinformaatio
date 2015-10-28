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
        var labelKey = 'searchwizard:' + opt + '-' + code;
        labelKey += kind ? ('-' + kind.charAt(kind.length-1)) : '';
        this.label = TranslationService.getTranslation(labelKey);
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
                        SelectionBuilder.buildPhaseTwoSelection(SearchWizardConstants.phaseTwoOpts.LUKIO_AIKU),
                        SelectionBuilder.buildPhaseTwoSelection(SearchWizardConstants.phaseTwoOpts.EI_PK_AIKU)
                    ];
                } 
            },

            getEducationTypesByOption: function(opt) {
                if (opt === SearchWizardConstants.phaseOneOpts.PK) {
                    return [
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AMMATILLINEN_KOULUTUS, opt, SearchWizardConstants.educationKind.NUORTEN, SearchWizardConstants.prerequisites.PK),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.LUKIOKOULUTUS, opt),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.KAKSOISTUTKINTO, opt, SearchWizardConstants.educationKind.NUORTEN, SearchWizardConstants.prerequisites.PK),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.KYMPPILUOKKA, opt, SearchWizardConstants.educationKind.NUORTEN),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.VALMA, opt, undefined),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.KANSANOPISTOJEN_PITKAT_LINJAT, opt, undefined, SearchWizardConstants.prerequisites.PK),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AMMATILLINEN_ERITYISOPETUS, opt, SearchWizardConstants.educationKind.NUORTEN, SearchWizardConstants.prerequisites.PK),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.TELMA, opt, undefined),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.VALMA_ER, opt, undefined)
                    ];
                } else if (opt === SearchWizardConstants.phaseOneOpts.YO) {
                    return [
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AMMATILLINEN_KOULUTUS, opt, SearchWizardConstants.educationKind.NUORTEN, SearchWizardConstants.prerequisites.YO),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AMMATTIKORKEAKOULUTUTKINTO, opt, undefined, SearchWizardConstants.prerequisites.YO),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.YLIOPISTO, opt, undefined, SearchWizardConstants.prerequisites.YO),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.KANSANOPISTOJEN_PITKAT_LINJAT, opt, undefined, SearchWizardConstants.prerequisites.YO),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AVOIN_AMMATTIKORKEAKOULUTUTKINTO, opt),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AVOIN_YLIOPISTO, opt)
                    ];
                } else if (opt === SearchWizardConstants.phaseOneOpts.AT) {
                    return [
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AMMATTIKORKEAKOULUTUTKINTO, opt, undefined, SearchWizardConstants.prerequisites.AT),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.YLIOPISTO, opt, undefined, SearchWizardConstants.prerequisites.AT),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AVOIN_AMMATTIKORKEAKOULUTUTKINTO, opt),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AVOIN_YLIOPISTO, opt)
                    ];
                } else if (opt === SearchWizardConstants.phaseOneOpts.MM) {
                    return [
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.MAAHANMUUTTAJIEN_LUKIOON_VALMISTAVA, opt, SearchWizardConstants.educationKind.NUORTEN),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.VALMA, opt, undefined),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.VALMA_ER, opt, undefined)
                    ];
                } else if (opt === SearchWizardConstants.phaseTwoOpts.YO_AIKU) {
                    return [
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AMMATILLINEN_KOULUTUS, opt, SearchWizardConstants.educationKind.NUORTEN, SearchWizardConstants.prerequisites.YO),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AMMATILLINEN_KOULUTUS, opt, SearchWizardConstants.educationKind.AIKUIS),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AMMATTITUTKINTO, opt, SearchWizardConstants.educationKind.AIKUIS),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.ERIKOISAMMATTITUTKINTO, opt, SearchWizardConstants.educationKind.AIKUIS),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AMMATTIKORKEAKOULUTUTKINTO, opt),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.YLIOPISTO, opt),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.KANSANOPISTOJEN_PITKAT_LINJAT, opt, undefined, SearchWizardConstants.prerequisites.YO),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AVOIN_AMMATTIKORKEAKOULUTUTKINTO, opt),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AVOIN_YLIOPISTO, opt)
                    ];
                } else if (opt === SearchWizardConstants.phaseTwoOpts.AMM_PT_AIKU) {
                    return [
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AMMATTITUTKINTO, opt, SearchWizardConstants.educationKind.AIKUIS),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.ERIKOISAMMATTITUTKINTO, opt, SearchWizardConstants.educationKind.AIKUIS),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AMMATTIKORKEAKOULUTUTKINTO, opt),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.YLIOPISTO, opt),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.KANSANOPISTOJEN_PITKAT_LINJAT, opt, undefined, SearchWizardConstants.prerequisites.YO),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AVOIN_AMMATTIKORKEAKOULUTUTKINTO, opt),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AVOIN_YLIOPISTO, opt)
                    ];
                } else if (opt === SearchWizardConstants.phaseTwoOpts.ALEMPI_KK_AIKU) {
                    return [
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.YLEMPI_AMMATTIKORKEAKOULUTUTKINTO, opt),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.YLEMPI_YLIOPISTO, opt),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AVOIN_AMMATTIKORKEAKOULUTUTKINTO, opt),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AVOIN_YLIOPISTO, opt)
                    ];
                } else if (opt === SearchWizardConstants.phaseTwoOpts.PK_AIKU) {
                    return [
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AIKUISLUKIO, opt),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AMMATILLINEN_KOULUTUS, opt, SearchWizardConstants.educationKind.NUORTEN, SearchWizardConstants.prerequisites.PK),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AMMATILLINEN_KOULUTUS, opt, SearchWizardConstants.educationKind.AIKUIS),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AMMATTITUTKINTO, opt, SearchWizardConstants.educationKind.AIKUIS),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.ERIKOISAMMATTITUTKINTO, opt, SearchWizardConstants.educationKind.AIKUIS),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.VALMA, opt, SearchWizardConstants.educationKind.AIKUIS),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.TELMA, opt, SearchWizardConstants.educationKind.AIKUIS)
                    ];
                } else if (opt === SearchWizardConstants.phaseTwoOpts.EI_PK_AIKU) {
                    return [
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AIKUISTENPERUSOPETUS, opt, SearchWizardConstants.educationKind.AIKUIS),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AMMATILLINEN_KOULUTUS, opt, SearchWizardConstants.educationKind.AIKUIS),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AMMATTITUTKINTO, opt, SearchWizardConstants.educationKind.AIKUIS),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.ERIKOISAMMATTITUTKINTO, opt, SearchWizardConstants.educationKind.AIKUIS),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.VALMA, opt, SearchWizardConstants.educationKind.AIKUIS)
                    ];
                } else if (opt === SearchWizardConstants.phaseTwoOpts.LUKIO_AIKU) {
                    return [
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AMMATILLINEN_KOULUTUS, opt, SearchWizardConstants.educationKind.NUORTEN, SearchWizardConstants.prerequisites.YO),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AMMATILLINEN_KOULUTUS, opt, SearchWizardConstants.educationKind.AIKUIS),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AMMATTITUTKINTO, opt, SearchWizardConstants.educationKind.AIKUIS),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.ERIKOISAMMATTITUTKINTO, opt, SearchWizardConstants.educationKind.AIKUIS),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AVOIN_AMMATTIKORKEAKOULUTUTKINTO, opt),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AVOIN_YLIOPISTO, opt)
                    ];
                } else if (opt === SearchWizardConstants.phaseOneOpts.MM_AIKU) {
                    return [
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.MAAHANMUUTTAJIEN_LUKIOON_VALMISTAVA, opt, SearchWizardConstants.educationKind.AIKUIS),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.VALMA, opt, SearchWizardConstants.educationKind.AIKUIS),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.VALMA_ER, opt, SearchWizardConstants.educationKind.AIKUIS),
                        SelectionBuilder.buildEducationTypeSelection(Config.educationTypes.AIKUISTENPERUSOPETUS, opt, SearchWizardConstants.educationKind.AIKUIS)
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
                    Config.educationTypes.AMMATILLINEN_KOULUTUS,
                    Config.educationTypes.KAKSOISTUTKINTO,
                    Config.educationTypes.AMMATILLINEN_ERITYISOPETUS,
                    Config.educationTypes.AMMATILLINEN_KOULUTUS,
                    Config.educationTypes.AMMATTIKORKEAKOULUTUTKINTO,
                    Config.educationTypes.YLIOPISTO,
                    Config.educationTypes.ALEMPI_YLIOPISTO,
                    Config.educationTypes.AMMATTITUTKINTO,
                    Config.educationTypes.ERIKOISAMMATTITUTKINTO,
                    Config.educationTypes.YLEMPI_AMMATTIKORKEAKOULUTUTKINTO,
                    Config.educationTypes.YLEMPI_YLIOPISTO,
                    Config.educationTypes.KANSANOPISTOJEN_PITKAT_LINJAT,
                    Config.educationTypes.AVOIN_AMMATTIKORKEAKOULUTUTKINTO,
                    Config.educationTypes.AVOIN_YLIOPISTO
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

            isLastPhase: function(currentPhase) {
                return currentPhase === phases[phases.length-1];
            },

            getFirstPhase: function() {
                return phases[0];
            },

            // get next phase of a specific phase
            getNextPhase: function(currentPhase, searchResult) {
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

                // skip topic selection if theme contains 0 topics
                if (phases[index] === SearchWizardConstants.phases.TOPIC) {
                    var topics = SearchWizardService.getTopicsByTheme(SearchWizardSelectionsService.getSelectionValueByKey(SearchWizardConstants.keys.THEME), searchResult);
                    if (!topics || topics.length <= 0) {
                        var indexAfterTopic = phases.indexOf(SearchWizardConstants.phases.TOPIC) + 1;
                        return phases[indexAfterTopic];
                    }
                }
                
                return phases[index];
            },

            // get previous phase of a specific phase
            getPreviousPhase: function(currentPhase, searchResult) {
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

                // skip topic selection if theme contains 0 topics
                if (phases[index] === SearchWizardConstants.phases.TOPIC) {
                    var topics = SearchWizardService.getTopicsByTheme(SearchWizardSelectionsService.getSelectionValueByKey(SearchWizardConstants.keys.THEME), searchResult);
                    if (!topics || topics.length <= 0) {
                        var indexBeforeTopic = phases.indexOf(SearchWizardConstants.phases.TOPIC) - 1;
                        return phases[indexBeforeTopic];
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

                        if (selection.prerequisite) {
                            result.facetFilters.push('prerequisites:' + selection.prerequisite);
                        }

                    }
                });

                // add current UI language as teaching language
                result.facetFilters.push('teachingLangCode_ffm:' + LanguageService.getLanguage().toUpperCase());

                // add locations
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
                            query += '&facetFilters=kindOfEducation_ffm:' + selection.kind;
                        }

                        if (selection.prerequisite) {
                            query += '&facetFilters=prerequisites:' + selection.prerequisite;
                        }
                    }
                });

                // add current UI language as teaching language
                query += '&facetFilters=teachingLangCode_ffm:' + LanguageService.getLanguage().toUpperCase();

                // add locations
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