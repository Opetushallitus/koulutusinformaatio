/*  Services */

angular.module('kiApp.services',
[
    'ngResource', 
    'kiApp.HostResolver', 
    'kiApp.TranslationService',
    'kiApp.CookieService',
    'kiApp.AlertService',
    'kiApp.AuthService',
    'kiApp.SearchLearningOpportunityService',
    'kiApp.OrganisationService',
    'kiApp.LanguageService'
]).

service('SearchLocationService', ['$http', '$timeout', '$q', 'LanguageService', function($http, $timeout, $q, LanguageService) {

    return {
        query: function(queryParam) {
            var deferred = $q.defer();

            $http.get(window.url("koulutusinformaatio-app.location.search", queryParam, {
                lang: LanguageService.getLanguage()
            })).
            success(function(result) {
                deferred.resolve(result);
            }).
            error(function(result) {
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]).

service('AutocompleteService', ['$http', '$timeout', '$q', 'LanguageService', function($http, $timeout, $q, LanguageService) {

    return {
        query: function(queryParam) {
            var deferred = $q.defer();

            $http.get(window.url("koulutusinformaatio-app.lo.autocomplete", {
                term: queryParam,
                lang: LanguageService.getLanguage()
            })).
            success(function(result) {
                deferred.resolve(result);
            }).
            error(function(result) {
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]).

/**
 * Service for retrieving districts (maakunnat). Used in faceted search
 */
service('DistrictService', ['$http', '$timeout', '$q', 'LanguageService', function($http, $timeout, $q, LanguageService) {

    return {
        query: function() {
            var deferred = $q.defer();

            $http.get(window.url("koulutusinformaatio-app.location.districts", {
                lang: LanguageService.getLanguage()
            })).
            success(function(result) {
                deferred.resolve(result);
            }).
            error(function(result) {
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]).

/**
 * Service for retrieving municipalities belonging to a district. Used in faceted search
 */
service('ChildLocationsService', ['$http', '$timeout', '$q', 'LanguageService', function($http, $timeout, $q, LanguageService) {

    return {
        query: function(districtVal) {
            var lang = LanguageService.getLanguage();
            if(!lang || !districtVal || districtVal.length == 0){
                return $q.reject("Invalid district or lang");
            }
            var deferred = $q.defer();

            var params = {lang: lang, districts: []};
            for (var i = 0; i < districtVal.length; i++) {
                params.districts.push(districtVal[i].code);
            }
            
            $http.get(window.url("koulutusinformaatio-app.location.childlocations", params)).
            success(function(result) {
                deferred.resolve(result);
            }).
            error(function(result) {
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]).

/**
 *  Resource for requesting parent LO data
 */
service('ParentLOService', ['GeneralLOService', 'ParentLOTransformer', function(GeneralLOService, ParentLOTransformer) {
    
    return {
        query: function(options) {
            // BUG-1273: If the tutkinto id has no PKYO / ER ending, append one based on prerequisite
            if(options.id && (options.id.indexOf("_PKYO") < 0 && options.id.indexOf("_ER") < 0 && options.id.indexOf("_UUSI") < 0)){
                if(options.prerequisite == "ER") {
                    options.id = options.id + "_ER"
                } else {
                    options.id = options.id + "_PKYO"
                }
            }
            return GeneralLOService.query(options, "koulutusinformaatio-app.lo.tutkinto", ParentLOTransformer);
        }
    }
}]).

/**
 *  Resource for requesting child LO data
 */
service('ChildLOService', ['$http', '$timeout', '$q', '$rootScope', 'LanguageService', 'ChildLOTransformer', 'ParentLOService', function($http, $timeout, $q, $rootScope, LanguageService, ChildLOTransformer, ParentLOService) {
    return {
        query: function(options) {
            var deferred = $q.defer();
            var queryParams = {
                uiLang: LanguageService.getLanguage()
            }

            if (options.lang) {
                queryParams.lang = options.lang
            }

            var url = '../lo/child/';

            $http.get(window.url("koulutusinformaatio-app.lo.child", options.id, queryParams)).
            success(function(result) {
                ChildLOTransformer.transform(result);
                ParentLOService.query({
                    id: result.parent.id,
                    lang: options.lang
                }).then(function(presult) {
                    result.educationDegree = presult.lo.educationDegree;
                    var loResult = {
                        lo: result,
                        parent: presult.lo,
                        provider: presult.provider
                    }
                    deferred.resolve(loResult);    
                }, function(reason) {
                    $rootScope.error = true;
                    deferred.reject(reason);
                });
            }).
            error(function(result) {
                $rootScope.error = true;
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]).

service('SpecialLOService', ['GeneralLOService', 'ChildLOTransformer', function(GeneralLOService, ChildLOTransformer) {
    return {
        query: function(options) {
            return GeneralLOService.query(options, 'koulutusinformaatio-app.lo.special', ChildLOTransformer);
        }
    }
}]).

/**
 * Resource for requesting Upper Secondary LO data
 */
service('UpperSecondaryLOService', ['GeneralLOService', 'ChildLOTransformer', function(GeneralLOService, ChildLOTransformer) {
    return {
        query: function(options) {
            return GeneralLOService.query(options, "koulutusinformaatio-app.lo.upsec", ChildLOTransformer);
        }
    }
}]).

/**
 * Resource for requesting University of Applied Sciences LO data
 */
service('HigherEducationLOService', ['GeneralLOService', 'HigherEducationTransformer', function(GeneralLOService, HigherEducationTransformer) {
    return {
        query: function(options) {
            return GeneralLOService.query(options, "koulutusinformaatio-app.lo.highered", HigherEducationTransformer);
        }
    }
}]).

/**
 * Resource for requesting generic v1 koulutus LO data
 */
service('KoulutusLOService', ['GeneralLOService', 'HigherEducationTransformer', function(GeneralLOService, HigherEducationTransformer) {
    return {
        query: function(options) {
            return GeneralLOService.query(options, "koulutusinformaatio-app.lo.koulutus", HigherEducationTransformer);
        }
    }
}]).


/**
 * Resource for requesting adult vocational LO data
 */
service('AdultVocationalLOService', ['GeneralLOService', 'AdultVocationalTransformer', function(GeneralLOService, AdultVocationalTransformer) {
    return {
        query: function(options) {
            return GeneralLOService.query(options, "koulutusinformaatio-app.lo.adultvocational", AdultVocationalTransformer);
        }
    }
}]).

/**
 *  General service used to request LO data. Used by services for different LO types.
 */
service('GeneralLOService', ['$http', '$timeout', '$q', '$rootScope', 'LanguageService', function($http, $timeout, $q, $rootScope, LanguageService) {
    return {
        query: function(options, urlKey, transformer) {
            var deferred = $q.defer();
            var queryParams = {
                uiLang: LanguageService.getLanguage()
            }

            if (options.lang) {
                queryParams.lang = options.lang
            }
            if (options.prerequisite) {
                queryParams.prerequisite = options.prerequisite;
            }
            
            $http.get(window.url(urlKey, options.id, queryParams)).
            success(function(result) {
                transformer.transform(result, options.id);
                var loResult = {
                    lo: result,
                    provider: result.provider
                }
                
                deferred.resolve(loResult);
            }).
            error(function(result) {
                $rootScope.error = true;
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]).

/**
 * Resource for requesting University of Applied Sciences LO data
 */
service('HigherEducationPreviewLOService', ['$http', '$timeout', '$q', 'LanguageService', 'HigherEducationTransformer', 'AdultVocationalTransformer', 'Config', function($http, $timeout, $q, LanguageService, HigherEducationTransformer, AdultVocationalTransformer, Config) {
    return {
        query: function(options) {
            var deferred = $q.defer();
            var queryParams = {
                uiLang: LanguageService.getLanguage(),
                lang: LanguageService.getLanguage(),
                loType: options.loType,
                timestamp: Date.now()
            }

            if (options.lang) {
                queryParams.lang = options.lang
            }

            $http.get(window.url("koulutusinformaatio-app.lo.preview", options.id, queryParams)).
            success(function(result) {
                if (options.loType == 'ammatillinenaikuiskoulutus') {
                    AdultVocationalTransformer.transform(result, options.id);
                } else {
                    HigherEducationTransformer.transform(result);
                }
                result.preview = true;
                result.tarjontaEditUrl = window.url("tarjonta-app.koulutusEdit", result.id, Date.now());
                if (result.children) {
                    for (var i = 0; i < result.children.length; ++i) {
                        result.children[i].preview = true;
                    } 
                }
                if (result.applicationSystems) {
                    for (var i = 0; i < result.applicationSystems.length; ++i) {
                        var as = result.applicationSystems[i];
                        as.preview = true;
                        if (as.applicationOptions) {
                            for (var j = 0; j < as.applicationOptions.length; ++j) {
                                var ao = as.applicationOptions[j];
                                ao.preview = true;
                                ao.editUrl =  window.url("tarjonta-app.hakukohdeEdit", ao.id, Date.now());
                            }
                        }
                    } 
                }
                var loResult = {
                    lo: result,
                    provider: result.provider
                }
                deferred.resolve(loResult);
            }).
            error(function(result) {
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]).

/**
 * Transformer for parent LO data
 */
service('ParentLOTransformer', ['KiSorter', '$filter', '$rootScope', '_', 'UtilityService', function(KiSorter, $filter, $rootScope, _, UtilityService) {
    return {
        transform: function(result) {

            // se LO translation language
            if (result && result.translationLanguage) {
                $rootScope.translationLanguage = result.translationLanguage;
            }

            for (var loiIndex in result.lois) {
                if (result.lois.hasOwnProperty(loiIndex)) {
                    var loi = result.lois[loiIndex];
                    loi.availableTranslationLanguages = _.filter(loi.availableTranslationLanguages, function(item) { return item.value.toLowerCase() != result.translationLanguage});
                }
            } 
            
            result.teachLang = [];
            // set teaching languge as the first language in array
            for (var index in result.teachingLanguages) {
               var tl = result.teachingLanguages[index].name;
                if(!_.contains(result.teachLang, tl)){
                    result.teachLang.push(tl)
                }
            }

            _.each(result.applicationSystems, function(as) {
                as.isLisahaku = UtilityService.isLisahaku(as);
            });
            
        }
    }
}]).

/**
 * Transformer for child LO data
 */
service('HigherEducationTransformer', ['KiSorter', '$rootScope', '$filter', 'LanguageService', '_', 'UtilityService', function(KiSorter, $rootScope, $filter, LanguageService, _, UtilityService) {

    return {
        transform: function(result) {
            if(result.parentLos != null && result.parentLos.length){
                _.each(result.parentLos, function(parent) {
                    if (parent.type == 'TUTKINTO') {
                        parent.url = '#!/tutkinto/' + parent.id;
                        if(parent.id.indexOf("_UUSI") > 0){
                            parent.koulutusPrerequisite = "UUSI";
                        } else if(result.koulutusPrerequisite && result.koulutusPrerequisite.value){
                            parent.koulutusPrerequisite = result.koulutusPrerequisite.value;
                        }
                    } else if (parent.type == 'KOULUTUS') {
                        parent.url = '#!/koulutus/' + parent.id;
                    }
                });
            }
            
            if (result && result.translationLanguage) {
                $rootScope.translationLanguage = result.translationLanguage;
            }

            if (result && result.availableTranslationLanguages) {
                result.availableTranslationLanguages = _.filter(result.availableTranslationLanguages, function(item) { return item.value.toLowerCase() != result.translationLanguage});
            }

            if (result.startDate) {
                var startDate = new Date(result.startDate);
                result.startDate = startDate.getDate() + '.' + (startDate.getMonth() + 1) + '.' + startDate.getFullYear();
            }
            if (result.endDate) {
                var endDate = new Date(result.endDate);
                result.endDate = endDate.getDate() + '.' + (endDate.getMonth() + 1) + '.' + endDate.getFullYear();
            }
            for (var dateIndex in result.startDates) {
                var date = $filter('date')(result.startDates[dateIndex], 'd.M.yyyy');
                result.startDates[dateIndex] = date;
            }
            if (result.educationDegree && (result.educationDegree == 'koulutusasteoph2002_62' || result.educationDegree == 'koulutusasteoph2002_71')) {
                result.polytechnic = true;
            }
            result.teachingLanguage = _.first(result.teachingLanguages);

            if (result.themes != undefined && result.themes != null) {
                var distinctMap = {};
                var distinctArray = [];
                for (var i=0; i < result.themes.length;i++) {
                    var theme = result.themes[i];
                    if (distinctMap[theme.uri] == undefined) {
                        distinctMap[theme.uri] = theme;
                        distinctArray.push(theme);
                    }
                }
                result.themes = distinctArray;
            }
            
            result.applicationOffices = [];
            if (result.provider.applicationOffice) {
                result.provider.applicationOffice.providerName = result.provider.name;
                result.applicationOffices.push(result.provider.applicationOffice);
            }
            for (var curProvIndex in result.additionalProviders) {
                if (result.additionalProviders.hasOwnProperty(curProvIndex)) {
                    var curProvider = result.additionalProviders[curProvIndex];
                    var curApplicationOffice = curProvider.applicationOffice
                    if (curApplicationOffice) {
                        curApplicationOffice.providerName = curProvider.name;
                        result.applicationOffices.push(curApplicationOffice);
                    }
                }
            }
            
            result.multipleProviders = result.applicationOffices.length > 1;

            for (var asIndex in result.applicationSystems) {
                if (result.applicationSystems.hasOwnProperty(asIndex)) {
                    var as = result.applicationSystems[asIndex];
                    for (var aoIndex in as.applicationOptions) {
                        if (as.applicationOptions.hasOwnProperty(aoIndex)) {
                            var ao = as.applicationOptions[aoIndex];

                            if (ao.teachingLanguages && ao.teachingLanguages.length > 0) {
                                ao.teachLang = ao.teachingLanguages[0];

                                $rootScope.teachingLang = LanguageService.getLanguage();//ao.teachLang.toLowerCase();
                            }
                        }
                    }
                }
            }

            // sort exams by start date
            for (var asIndex in result.applicationSystems) {
                if (result.applicationSystems.hasOwnProperty(asIndex)) {
                    var as = result.applicationSystems[asIndex];
                    for (var aoIndex in as.applicationOptions) {
                        if (as.applicationOptions.hasOwnProperty(aoIndex)) {
                            var ao = as.applicationOptions[aoIndex];
                            for (var exam in ao.exams) {
                                if (ao.exams.hasOwnProperty(exam)) {
                                    if (ao.exams[exam].examEvents) {
                                        ao.exams[exam].examEvents.sort(function(a, b) {
                                            return a.start - b.start;
                                        });
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // sort application systems
            KiSorter.sortApplicationSystems(result.applicationSystems);

            // check if application system is of type Lisähaku
            for (var asIndex in result.applicationSystems) {
                if (result.applicationSystems.hasOwnProperty(asIndex)) {
                    var as = result.applicationSystems[asIndex];
                    as.isLisahaku = UtilityService.isLisahaku(as);
                }
            }
        }
    }
}]).

/**
 * Transformer for child LO data
 */
service('AdultVocationalTransformer', ['KiSorter', '$rootScope', '$filter', 'LanguageService', '_', 'UtilityService', function(KiSorter, $rootScope, $filter, LanguageService, _, UtilityService) {

    return {
        transform: function(result, loId) {

            if (result && result.translationLanguage) {
                $rootScope.translationLanguage = result.translationLanguage;
            }

            if (result && result.availableTranslationLanguages) {
                result.availableTranslationLanguages = _.filter(result.availableTranslationLanguages, function(item) { return item.value.toLowerCase() != result.translationLanguage});
            }

            if (result.startDate) {
                var startDate = new Date(result.startDate);
                result.startDate = startDate.getDate() + '.' + (startDate.getMonth() + 1) + '.' + startDate.getFullYear();
            }
            if (result.educationDegree && (result.educationDegree == 'koulutusasteoph2002_62' || result.educationDegree == 'koulutusasteoph2002_71')) {
                result.polytechnic = true;
            }
            result.teachingLanguage = _.first(result.teachingLanguages);

            if (result.themes != undefined && result.themes != null) {
                var distinctMap = {};
                var distinctArray = [];
                for (var i=0; i < result.themes.length;i++) {
                    var theme = result.themes[i];
                    if (distinctMap[theme.uri] == undefined) {
                        distinctMap[theme.uri] = theme;
                        distinctArray.push(theme);
                    }
                }
                result.themes = distinctArray;
            }
            
            

            for (var asIndex in result.applicationSystems) {
                if (result.applicationSystems.hasOwnProperty(asIndex)) {
                    var as = result.applicationSystems[asIndex];
                    for (var aoIndex in as.applicationOptions) {
                        if (as.applicationOptions.hasOwnProperty(aoIndex)) {
                            var ao = as.applicationOptions[aoIndex];

                            if (ao.teachingLanguages && ao.teachingLanguages.length > 0) {
                                ao.teachLang = ao.teachingLanguages[0];

                                $rootScope.teachingLang = LanguageService.getLanguage();//ao.teachLang.toLowerCase();
                            }
                        }
                    }
                }
            }

            for (var asIndex in result.applicationSystems) {
                if (result.applicationSystems.hasOwnProperty(asIndex)) {
                    var as = result.applicationSystems[asIndex];
                    for (var aoIndex in as.applicationOptions) {
                        if (as.applicationOptions.hasOwnProperty(aoIndex)) {
                            var ao = as.applicationOptions[aoIndex];
                            for (var exam in ao.exams) {
                                if (ao.exams.hasOwnProperty(exam)) {
                                    if (ao.exams[exam].examEvents) {
                                        ao.exams[exam].examEvents.sort(function(a, b) {
                                            return a.start - b.start;
                                        });
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // sort application systems
            KiSorter.sortApplicationSystems(result.applicationSystems);

            // check if application system is of type Lisähaku
            for (var asIndex in result.applicationSystems) {
                if (result.applicationSystems.hasOwnProperty(asIndex)) {
                    var as = result.applicationSystems[asIndex];
                    as.isLisahaku = UtilityService.isLisahaku(as);
                }
            }
            
            result.hasSelectedChild = false;
            result.isStandalone = false;
            if (result.children != null && result.children.length == 1) {
                result.isStandalone = true;
                result.selectedChild = result.children[0];
                if (result.selectedChild.startDate) {
                    var startDate = new Date(result.selectedChild.startDate);
                    result.selectedChild.startDate = startDate.getDate() + '.' + (startDate.getMonth() + 1) + '.' + startDate.getFullYear();
                }
                result.hasSelectedChild = true;
            } else {
                result.parentId = result.id;
                angular.forEach(result.children, function(child, childKey) {
                    if (child.startDate) {
                        var startDate = new Date(child.startDate);
                        child.startDate = startDate.getDate() + '.' + (startDate.getMonth() + 1) + '.' + startDate.getFullYear();
                    }
                    if (child.id == loId) {
                        result.selectedChild = child;
                        result.hasSelectedChild = true;
                    }
                });
                
            }
            
            if (result.selectedChild && result.selectedChild.contactPersons != null) {
                angular.forEach(result.selectedChild.contactPersons, function(person, key) {
                    person.isNayttotutkinto = true;
                });
            }
            
            if (!result.hasSelectedChild && result.children != null && result.children.length > 1) {
                result.id = result.children[0].id;
            } else {
                result.id = loId;
            }
        }
    }
}]).

/**
 * Transformer for child LO data
 */
service('ChildLOTransformer', ['UtilityService', 'KiSorter', '$rootScope', '$filter', '_', function(UtilityService, KiSorter, $rootScope, $filter, _) {

    return {
        transform: function(result) {
            var studyplanKey = "KOULUTUSOHJELMA";

            // set translation language for LO content
            if (result && result.translationLanguage) {
                $rootScope.translationLanguage = result.translationLanguage;
            }

            // remove current translation language from available translation languages
            for (var loiIndex in result.lois) {
                if (result.lois.hasOwnProperty(loiIndex)) {
                    var loi = result.lois[loiIndex];
                    loi.availableTranslationLanguages = _.filter(loi.availableTranslationLanguages, function(item) { return item.value.toLowerCase() != result.translationLanguage});
                    
                    // get target group from loi
                    if (loi.targetGroup) {
                        result.targetGroup = loi.targetGroup;
                    } 
                }
            } 
            
            // set loi basic info
            for (var loiIndex in result.lois) {
                if (result.lois.hasOwnProperty(loiIndex)) {
                    var loi = result.lois[loiIndex];

                    if (loi.startDate) {
                        var startDate = new Date(loi.startDate);
                        loi.startDate = startDate.getDate() + '.' + (startDate.getMonth() + 1) + '.' + startDate.getFullYear();
                    }
                    for (var dateIndex in loi.startDates) {
                        var date = $filter('date')(loi.startDates[dateIndex], 'd.M.yyyy');
                        loi.startDates[dateIndex] = date;
                    }
                    loi.teachingLanguage = _.first(loi.teachingLanguages);
                    loi.formsOfTeaching = loi.formOfTeaching;
                    loi.formOfTeaching = _.first(loi.formOfTeaching);
                    

                    if (loi.webLinks) {
                        loi.studyPlan = loi.webLinks[studyplanKey];
                    }
                }
            }

            // set teaching languge as the first language in array
            for (var index in result.lois) {
                if (result.lois.hasOwnProperty(index)) {
                    var loi = result.lois[index];
                    for (var asIndex in loi.applicationSystems) {
                        if (loi.applicationSystems.hasOwnProperty(asIndex)) {
                            var as = loi.applicationSystems[asIndex];
                            for (var aoIndex in as.applicationOptions) {
                                if (as.applicationOptions.hasOwnProperty(aoIndex)) {
                                    var ao = as.applicationOptions[aoIndex];

                                    if (ao.teachingLanguages && ao.teachingLanguages.length > 0) {
                                        ao.teachLang = ao.teachingLanguages[0];
                                        
                                        $rootScope.teachingLang = ao.teachLang.toLowerCase();
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // sort exams based on start time
            for (var loiIndex in result.lois) {
                if (result.lois.hasOwnProperty(loiIndex)) {
                    var loi = result.lois[loiIndex];
                    for (var asIndex in loi.applicationSystems) {
                        if (loi.applicationSystems.hasOwnProperty(asIndex)) {
                            var as = loi.applicationSystems[asIndex];
                            for (var aoIndex in as.applicationOptions) {
                                if (as.applicationOptions.hasOwnProperty(aoIndex)) {
                                    var ao = as.applicationOptions[aoIndex];
                                    for (var exam in ao.exams) {
                                        if (ao.exams.hasOwnProperty(exam)) {
                                            if (ao.exams[exam].examEvents) {
                                                ao.exams[exam].examEvents.sort(function(a, b) {
                                                    return a.start - b.start;
                                                });
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // check if application system is of type Lisähaku
            for (var loiIndex in result.lois) {
                if (result.lois.hasOwnProperty(loiIndex)) {
                    var loi = result.lois[loiIndex];
                    for (var asIndex in loi.applicationSystems) {
                        if (loi.applicationSystems.hasOwnProperty(asIndex)) {
                            var as = loi.applicationSystems[asIndex];
                            as.isLisahaku = UtilityService.isLisahaku(as);
                        }
                    }
                }
            }

            // group application systems by prerequisite
            var applicationSystemsByPrerequisite = {};
            angular.forEach(result.lois, function(loi, loikey) {
                angular.forEach(loi.applicationSystems, function(as, askey) {
                    as.loiId = loi.id;
                    if (applicationSystemsByPrerequisite[loi.prerequisite.value]) {
                        applicationSystemsByPrerequisite[loi.prerequisite.value].push(as);
                    } else {
                        applicationSystemsByPrerequisite[loi.prerequisite.value] = [];
                        applicationSystemsByPrerequisite[loi.prerequisite.value].push(as);
                    }

                });
            });

            // sort application systems and select active LOI
            var lois = [];
            angular.forEach(applicationSystemsByPrerequisite, function(asByPrerequisite, key){
                KiSorter.sortApplicationSystems(asByPrerequisite);
                
                if (asByPrerequisite.length > 0) {
                    var loiId = asByPrerequisite[0].loiId;
                }

                
                angular.forEach(result.lois, function(loi, loikey){
                    if (loi.id === loiId) {
                        loi.applicationSystems = asByPrerequisite;
                        loi.applicationSystems = UtilityService.groupByApplicationSystem(loi.applicationSystems);
                        lois.push(loi);
                    }
                });
            });

            result.lois = lois;

            // sort language selection alphabetically
            angular.forEach(result.lois, function(loi, loikey){
                UtilityService.sortLanguageSelection(loi.languageSelection);
            });

            

            // sort LOIs based on prerequisite
            if (result.lois) {
                result.lois.sort(function(a, b) {
                    if (a.prerequisite.description > b.prerequisite.description) return 1;
                    else if (a.prerequisite.description < b.prerequisite.description) return -1;
                    else return a.id > b.id ? 1 : -1;
                });
            }

            // aggregate childrefs from application options to application systems
            angular.forEach(result.lois, function(loi, loikey) {
                angular.forEach(loi.applicationSystems, function(as, askey) {
                    var children = [];
                    angular.forEach(as.applicationOptions, function(ao, aokey) {
                        angular.forEach(ao.childRefs, function(childref, childrefkey) {
                            var childFound = false;
                            angular.forEach(children, function(child) {
                                if (child.losId == childref.losId) {
                                    childFound = true;
                                }
                            });

                            if (!childFound) {
                                children.push(childref);
                            }
                        });
                    });

                    as.children = children;
                });
            });
        }
    }
}]).

service('SearchResultFacetTransformer', ['UtilityService', '$filter', function(UtilityService, $filter) {
    
    var getFacetValById = function(valueId, givenVals) {
        
        var selectedEdTypeFacetVal = undefined;
        var edTypeFacetVals = [];
        edTypeFacetVals = edTypeFacetVals.concat(givenVals);
        
        while (edTypeFacetVals != null && edTypeFacetVals.length > 0) {
            
            var currentValue = edTypeFacetVals.shift();
            
            if (currentValue != null && currentValue.valueId == valueId) {
                selectedEdTypeFacetVal = currentValue;
            }
            
            if (currentValue != null && selectedEdTypeFacetVal == undefined) {
                edTypeFacetVals = edTypeFacetVals.concat(currentValue.childValues);
            }
        }
        return selectedEdTypeFacetVal;
    };
    
    return {
        
        transform: function(result, facetFilters) {

            var loResult = result;
            
            var wasEducationType = false;
            var educationtypeSelection = undefined;
            var wasTheme = false;
            var wasTopic = false;
            var themeTopicSelection = undefined;
            
            angular.forEach(facetFilters, function(value, index) {
                var curVal = value.split(':')[1];
                var curField = value.split(':')[0];
                if ((curField == 'educationType_ffm')) {
                    
                    wasEducationType = true;
                    
                    educationtypeSelection = curVal;
                } else if (curField == 'theme_ffm') {
                    wasTheme = true;
                    themeTopicSelection = curVal;
                } else if (curField == 'topic_ffm') {
                    wasTopic = true;
                    themeTopicSelection = curVal;
                }
            });
            
            
            if (wasTheme) {
                var selectedThemeFacetVal = getFacetValById(themeTopicSelection, loResult.topicFacet.facetValues);
                topicFacetValues = [];
                topicFacetValues.push(selectedThemeFacetVal);
                
                loResult.topicFacetValues = topicFacetValues;
            } else if (wasTopic) {
                var selectedTopicFacetVal = getFacetValById(themeTopicSelection, loResult.topicFacet.facetValues);
                var parentThemeVal = getFacetValById(selectedTopicFacetVal.parentId, loResult.topicFacet.facetValues);
                parentThemeVal.childValues = [];
                parentThemeVal.childValues.push(selectedTopicFacetVal);
                
                topicFacetValues = [];
                topicFacetValues.push(parentThemeVal);
                
                loResult.topicFacetValues = topicFacetValues;
            } else {
                angular.forEach(loResult.topicFacet.facetValues, function(value, index) {
                    if(value.childValues && value.childValues.length > 0){
                        value.containsChildren = true;
                    }
                    value.childValues = [];
                });
                
                loResult.topicFacetValues = loResult.topicFacet.facetValues;
            }
            
            if (wasEducationType) {
                
                var selectedEdTypeFacetVal = getFacetValById(educationtypeSelection, loResult.edTypeFacet.facetValues);  
                if (selectedEdTypeFacetVal != undefined) {
                    
                    angular.forEach(selectedEdTypeFacetVal.childValues, function(value, index) {
                        if(value.childValues && value.childValues.length > 0){
                            value.containsChildren = true;
                        }
                        value.childValues = [];
                    });
                    
                    var parent = getFacetValById(selectedEdTypeFacetVal.parentId, loResult.edTypeFacet.facetValues);
                    
                    while (parent != undefined && parent != null) {
                        parent.childValues = [];
                        parent.childValues.push(selectedEdTypeFacetVal);
                        selectedEdTypeFacetVal = parent;
                        parent = getFacetValById(selectedEdTypeFacetVal.parentId, loResult.edTypeFacet.facetValues);
                    }
                    
                    edTypeFacetValues = [];
                    edTypeFacetValues.push(selectedEdTypeFacetVal);
                    
                    loResult.edTypeFacetValues = edTypeFacetValues;
                } else {
                    angular.forEach(loResult.edTypeFacet.facetValues, function(value, index) {
                        if(value.childValues && value.childValues.length > 0){
                            value.containsChildren = true;
                        }
                        value.childValues = [];
                    });
                    
                    loResult.edTypeFacetValues = loResult.edTypeFacet.facetValues;
                }
            } else {
                angular.forEach(loResult.edTypeFacet.facetValues, function(value, index) {
                    if(value.childValues && value.childValues.length > 0){
                        value.containsChildren = true;
                    }
                    value.childValues = [];
                });
                
                loResult.edTypeFacetValues = loResult.edTypeFacet.facetValues;
            }
            
            return loResult;
            
        }
    }
}]).

/**
 *  Resource for requesting LO provider picture
 */
service('LearningOpportunityProviderPictureService', ['$http', '$timeout', '$q', function($http, $timeout, $q) {
    return  {
        query: function(options) {
            var deferred = $q.defer();

            $http.get(window.url("koulutusinformaatio-app.lop.picture", options.providerId)).
            success(function(result) {
                deferred.resolve(result);
            }).
            error(function(result) {
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]).

/**
 *  Resource for requesting LO picture
 */
service('LearningOpportunityPictureService', ['$http', '$timeout', '$q', function($http, $timeout, $q) {
    return  {
        query: function(options) {
            var deferred = $q.defer();
            
            $http.get(window.url("koulutusinformaatio-app.lo.picture", options.pictureId)).
            success(function(result) {
                deferred.resolve(result);
            }).
            error(function(result) {
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]).


/**
 *  Service taking care of search term saving
 */
 service('SearchService', ['CookieService', function(CookieService) {
    var key = 'searchTerm';
    return {
        getTerm: function() {
            return CookieService.get(key) || '';
        },

        setTerm: function(newTerm) {
            if (newTerm) {
                CookieService.set(key, newTerm);
            }
        }
    };
}]).

/**
 *  Service keeping track of virkalija language selection
 */
service('VirkailijaLanguageService', ['CookieService', function(CookieService) {
    var defaultLanguage = 'fi';
    var key = 'virkailijaLang';

    return {
        getLanguage: function() {
            return CookieService.get(key) || defaultLanguage;
        },

        setLanguage: function(language) {
            CookieService.set(key, language);
        },

        getDefaultLanguage: function() {
            return defaultLanguage;
        }
    };
}]).

/**
 *  Service for maintaining application basket state
 */
service('ApplicationBasketService', ['$http', '$q', '$rootScope', 'LanguageService', 'UtilityService', 'CookieService', function($http, $q, $rootScope, LanguageService, UtilityService, CookieService) {
    var key = 'basket';
    var typekey = 'baskettype';
    var cookieConfig = {useLocalStorage: false, maxChunkSize: 2000, maxNumberOfCookies: 20, path: '/'};

    var transformData = function(result) {

        var createLinkToLo = function(ao) {
            var loRef = ao.type + '/';

            switch(ao.type) {
                case 'koulutus':
                    loRef += ao.parent.id;
                    break;
                case 'korkeakoulu':
                    loRef += ao.losRefs && ao.losRefs.length > 0 ? ao.losRefs[0].id : '';
                    break;
                case 'lukio':
                    loRef += ao.losId;
                    break;
                case 'tutkinto':
                    loRef += ao.parent.id;
                    break;
                case 'valmistava':
                    loRef += ao.parent.id;
                    break;
                case 'erityisopetus':
                    loRef += ao.parent.id;
                    break;
                case 'aikuislukio':
                    loRef += ao.losRefs && ao.losRefs.length > 0 ? ao.losRefs[0].id : '';
                    break;
                case 'ammatillinenaikuiskoulutus':
                    loRef += ao.losRefs && ao.losRefs.length > 0 ? ao.losRefs[0].id : '';
                    break;
            }

            loRef += '?';
            loRef += ao.prerequisite ? 'prerequisite=' + ao.prerequisite.value + '&' : '';
            loRef += 'tab=1';

            return loRef;
        };

        for (var asIndex in result) {
            if (result.hasOwnProperty(asIndex)) {
                var applicationDates = result[asIndex].applicationDates;
                if (applicationDates && applicationDates.length > 0) {
                    result[asIndex].applicationDates = applicationDates[0];
                }

                var applicationOptions = result[asIndex].applicationOptions;
                for (var i in applicationOptions) {
                    if (applicationOptions.hasOwnProperty(i)) {
                        var ao = applicationOptions[i];

                        if (ao.children && ao.children.length > 0) {
                            var childQualifications = Array.from(
                                new Set(ao.children.map(
                                    function (c) { return c.qualification }
                                ))).filter(
                                    function (q) { return q }
                                );
                            ao.qualification = childQualifications.length > 0 ? childQualifications.join(', ') : null;
                        }

                        // set teaching languge as the first language in array
                        if (ao.teachingLanguages && ao.teachingLanguages.length > 0) {
                            ao.teachLang = ao.teachingLanguages[0];
                        }

                        // set LOS id for lukio
                        // check if ao is of type lukio
                        ao.losId = (ao.children && ao.children.length > 0) ? ao.children[0].losId : '';

                        // transform type to lower case
                        ao.type = ao.type ? ao.type.toLowerCase() : '';

                        // set link to lo
                        ao.loRef = createLinkToLo(ao);
                    }
                }
            }
        }

        return result;
    };

    return {
        addItem: function(aoId, itemType) {

            var current = CookieService.get(key);

            if (current) {
                current = JSON.parse(current);

                // do not add same ao twice
                if (current.indexOf(aoId) < 0) {
                        current.push(aoId);
                }
            } else {
                current = [];
                current.push(aoId);
            }

            // save type if defined
            if (itemType) {
                CookieService.set(typekey, itemType, cookieConfig);
            }

            CookieService.set(key, JSON.stringify(current), cookieConfig);
        },

        removeItem: function(aoId) {
            if (this.getItemCount() > 1) {
                var value = CookieService.get(key);
                value = JSON.parse(value);

                var index = value.indexOf(aoId);
                value.splice(index, 1);

                CookieService.set(key, JSON.stringify(value), cookieConfig);
            } else {
                this.empty();
            }
        },

        empty: function() {
            CookieService.set(key, null, cookieConfig);
            CookieService.set(typekey, null, cookieConfig);
        },

        getItems: function() {
            return JSON.parse( CookieService.get(key) );
        },

        getItemCount: function() {
            return CookieService.get(key) ? JSON.parse( CookieService.get(key, false) ).length : 0;
        },

        isEmpty: function() {
            return this.getItemCount() <= 0;
        },

        getType: function() {
            if (!this.isEmpty()) {
                return CookieService.get(typekey);
            }
        },

        itemExists: function(aoId) {
            var result = false;
            angular.forEach(this.getItems(), function(item, key) {
                if (aoId == item) {
                    result = true;
                }
            });

            return result;
        },

        query: function(params) {
            $rootScope.isLoading = true;
            var deferred = $q.defer();
            var basketItems = this.getItems();

            var qParams = {
                uiLang: LanguageService.getLanguage(),
                aoId: []
            };

            for (var index = 0; index < basketItems.length; index++) {
                if (basketItems.hasOwnProperty(index)) {
                    qParams.aoId.push(basketItems[index]);
                }
            }

            $http.get(window.url("koulutusinformaatio-app.basket.items", qParams)).
            success(function(result) {
                result = transformData(result);
                $rootScope.isLoading = false;
                deferred.resolve(result);
            }).
            error(function(result) {
                $rootScope.error = true;
                $rootScope.isLoading = false;
                deferred.reject(result);
            });

            return deferred.promise;
        },

        sendByEmail: function(subject, to, captcha) {
            $rootScope.isLoading = true;
            var deferred = $q.defer();
            var emailData = {
                kieli: LanguageService.getLanguage(),
                otsikko: subject.trim(),
                vastaanottaja: to,
                captcha: captcha
            };
            var headers = {
                'CSRF': CookieService.get('CSRF')
            };
            var aoIds = this.getItems();
            emailData.koids = aoIds == null ? [] : aoIds;
            $http.post(window.url("omatsivut.muistilista"), emailData, headers).
                success(function(result) {
                    $rootScope.isLoading = false;
                    deferred.resolve(result);
                }).
                error(function(result) {
                    $rootScope.isLoading = false;
                    deferred.reject(result);
                });

            return deferred.promise;
        }
    }
}]).

/**
 *  Service for maintaining search filter state
 */
service('FilterService', [
    '$q',
    '$http',
    'UtilityService',
    'LanguageService',
    'kiAppConstants',
    '_',
    function($q, $http, UtilityService, LanguageService, kiAppConstants, _) {
    var filters = {};

    var filterIsEmpty = function(filter) {
        if (filter == undefined || filter == null) return true;
        else if (typeof filter == 'boolean' && !filter) return true;
        else if (filter instanceof Array && filter.length <= 0 ) return true;
        else return false;
    }

    var getLocationCodes = function() {
        var codes = [];
        angular.forEach(filters.locations, function(value, key) {
            codes.push(value.code);
        });

        return codes;
    }

    var set = function(newFilters) {
        filters = {};
        for (var i in newFilters) {
            if (newFilters.hasOwnProperty(i)) {
                var filter = newFilters[i];

                if (!filterIsEmpty(filter)) {
                    filters[i] = filter;
                }
            }
        }
    }

    return {
        query: function(queryParams) {
            var deferred = $q.defer();

            var locationCodes = (queryParams.locations && typeof queryParams.locations == 'string') ? UtilityService.getStringAsArray(queryParams.locations) : queryParams.locations || [];
            if (locationCodes.length > 0) {
                $http.get(window.url("koulutusinformaatio-app.location", {
                    lang: LanguageService.getLanguage(),
                    code: locationCodes
                })).
                success(function(result) {
                    queryParams.locations = result;
                    set(queryParams);
                    deferred.resolve();
                }).
                error(function(result) {
                    deferred.reject();
                });
            } else {
                queryParams.locations = [];
                set(queryParams);
                deferred.resolve();
            }

            return deferred.promise;
        },

        set: function(newFilters) {
            set(newFilters);
        },

        get: function() {
            var result =  {
                prerequisite: filters.prerequisite,
                locations: getLocationCodes(),
                ongoing: filters.ongoing,
                upcoming: filters.upcoming,
                upcomingLater: filters.upcomingLater,
                page: filters.page,
                articlePage: filters.articlePage,
                organisationPage: filters.organisationPage,
                langCleared: filters.langCleared,
                itemsPerPage: filters.itemsPerPage,
                sortCriteria: filters.sortCriteria,
                lopFilter: filters.lopFilter,
                educationCodeFilter: filters.educationCodeFilter,
                excludes: filters.excludes,
                facetFilters: filters.facetFilters,
                articleFacetFilters : filters.articleFacetFilters,
                organisationFacetFilters: filters.organisationFacetFilters,
                tab: filters.tab
            };

            angular.forEach(result, function(value, key) {
                if (value instanceof Array && value.length <= 0 || !value) {
                    delete result[key];
                }
            });


            return result;
        },

        clear: function() {
            filters = {};
        },

        getPrerequisite: function() {
            if (filters.prerequisite) {
                return filters.prerequisite;
            }
        },

        isOngoing: function() {
            return filters.ongoing;
        },

        isUpcoming: function() {
            return filters.upcoming;
        },
        
        isUpcomingLater: function() {
            return filters.upcomingLater;
        },

        getLocations: function() {
            return filters.locations;
        },

        getLocationNames: function() {
            var locations = [];
            angular.forEach(filters.locations, function(value, key) {
                locations.push(value.name);
            });

            return locations;
        },

        setPage: function(value) {
            if (value && !isNaN(value)) {
                filters.page = parseInt(value);
            } else {
                filters.page = 1;
            }
        },

        getPage: function() {
            if (filters.page) {
                return typeof filters.page === 'string' ? parseInt(filters.page) : filters.page;
            } else {
                return 1;
            }
        },

        setArticlePage: function(value) {
            if (value && !isNaN(value)) {
                filters.articlePage = parseInt(value);
            } else {
                filters.articlePage = 1;
            }
        },

        getArticlePage: function() {
            if (filters.articlePage) {
                return typeof filters.articlePage === 'string' ? parseInt(filters.articlePage) : filters.articlePage;
            } else {
                return 1;
            }
        },

        setOrganisationPage: function(value) {
            if (value && !isNaN(value)) {
                filters.organisationPage = parseInt(value);
            } else {
                filters.organisationPage = 1;
            }
        },

        getOrganisationPage: function() {
            if (filters.organisationPage) {
                return typeof filters.organisationPage === 'string' ? parseInt(filters.organisationPage) : filters.organisationPage;
            } else {
                return 1;
            }
        },

        getLocationCodes: getLocationCodes,

        getParams: function() {
            var params = '';
            params += filters.prerequisite ? '&prerequisite=' + filters.prerequisite : '';
            params += (filters.locations && filters.locations.length > 0) ? '&locations=' + getLocationCodes().join(',') : '';
            params += filters.ongoing ? '&ongoing' : '';
            params += filters.upcoming ? '&upcoming' : '';
            params += filters.upcomingLater ? '&upcomingLater' : '';
            params += filters.page ? '&page=' + filters.page : '';
            params += (filters.facetFilters && filters.facetFilters.length > 0) ? '&facetFilters=' + filters.facetFilters.join(',') : '';
            params += filters.langCleared ? '&langCleared=' + filters.langCleared : '';
            params += filters.itemsPerPage ? '&itemsPerPage=' + filters.itemsPerPage : '';
            params += filters.sortCriteria ? '&sortCriteria=' + filters.sortCriteria : '';
            params += filters.lopFilter ? '&lopFilter=' + filters.lopFilter : '';
            params += filters.educationCodeFilter ? '&educationCodeFilter=' + filters.educationCodeFilter : '';
            params += (filters.excludes && filters.excludes.length > 0) ? '&excludes=' + filters.excludes.join('|') : '';
            params += (filters.articleFacetFilters && filters.articleFacetFilters.length > 0) ? '&articleFacetFilters=' + filters.articleFacetFilters.join(',') : '';
            params += (filters.organisationFacetFilters && filters.organisationFacetFilters.length > 0) ? '&organisationFacetFilters=' + filters.organisationFacetFilters.join(',') : '';
            params += filters.tab ? '&tab=' + filters.tab : '';    
            params = params.length > 0 ? params.substring(1, params.length) : '';
            return params;
        },
        
        getFacetFilters: function() {
            if (filters.facetFilters != undefined && (typeof filters.facetFilters == 'string' || filters.facetFilters instanceof String)) {
                filters.facetFilters = filters.facetFilters.split(',');
                return filters.facetFilters;
            }
            return filters.facetFilters;
        },
        
        getArticleFacetFilters: function() {
            if (filters.articleFacetFilters != undefined && (typeof filters.articleFacetFilters == 'string' || filters.articleFacetFilters instanceof String)) {
                filters.articleFacetFilters = filters.articleFacetFilters.split(',');
                return filters.articleFacetFilters;
            }
            return filters.articleFacetFilters;
        },

        getOrganisationFacetFilters: function() {
            if (filters.organisationFacetFilters != undefined && (typeof filters.organisationFacetFilters == 'string' || filters.organisationFacetFilters instanceof String)) {
                filters.organisationFacetFilters = filters.organisationFacetFilters.split(',');
                return filters.organisationFacetFilters;
            }
            return filters.organisationFacetFilters;
        },
        
        getLopFilter: function() {
            return filters.lopFilter;
        },
        
        getEducationCodeFilter: function() {
            return filters.educationCodeFilter;
        },
        
        getExcludes: function() {
            if (filters.excludes != undefined && (typeof filters.excludes == 'string' || filters.excludes instanceof String)) {
                filters.excludes = filters.excludes.split('|');
            }
            return filters.excludes;
        },
        
        getLangCleared: function() {
            return filters.langCleared;
        },

        getItemsPerPage: function() {
            if (filters.itemsPerPage) {
                return typeof filters.itemsPerPage === 'string' ? parseInt(filters.itemsPerPage) : filters.itemsPerPage;
            } else {
                return kiAppConstants.searchResultsPerPage;
            }
        },

        setItemsPerPage: function(value) {
            if (value && !isNaN(value)) {
                filters.itemsPerPage = parseInt(value);
            } else {
                filters.itemsPerPage = kiAppConstants.searchResultsPerPage;
            }
        },

        getSortCriteria: function() {
            if (filters.sortCriteria) {
                return filters.sortCriteria;
            } else {
                return kiAppConstants.defaultSortCriteria;
            }
        },

        setSortCriteria: function(value) {
            if (value) {
                filters.sortCriteria = value;
            } else {
                filters.sortCriteria = kiAppConstants.defaultSortCriteria;
            }
        }
    };
}]).

/**
 * Keeps up information about collapse block hide/show status
 */
service('CollapseBlockService', function() {
    var blocks = {};

    return {
        setBlock: function(id, value) {
            if (value) {
                blocks[id] = value;
            } else {
                blocks[id] = false;
            }
        },

        getBlock: function(id) {
            if (blocks[id] === undefined) {
                return true;
            } else {
                return blocks[id];
            }
        }
    }
}).

/**
 *  Sort application systems and application options by complex rules
 */
service('KiSorter', ['UtilityService', function(UtilityService) {
    var sortApplicationSystems = function(applicationSystems) {

        var isHakuKaynnissa = function(as) {
            var isOngoing = false;
            angular.forEach(as.applicationOptions, function(item) {
                if (item.canBeApplied) {
                    isOngoing = true;
                }
            });

            return isOngoing;
        }

        var isHakuTulossaHakuun = function(as) {
            var result = false;
            angular.forEach(as.applicationOptions, function(ao) {
                if (!ao.canBeApplied && ao.nextApplicationPeriodStarts) {
                    result = true;
                }
            });

            return result;
        }

        var isHakuPaattynyt = function(as) {
            return (!isHakuKaynnissa(as) && !isHakuTulossaHakuun(as));
        }

        var isVarsinainenYhteishaku = function(as) {
            return UtilityService.isVarsinainenHaku(as) && UtilityService.isYhteishaku(as);
        }

        var isYhteishaunLisahaku = function(as) {
            return UtilityService.isLisahaku(as) && UtilityService.isYhteishaku(as);
        }

        var getEarliestStartDate = function(as) {
            var earliest = -1;
            angular.forEach(as.applicationOptions, function(ao) {
                if (earliest < 0 || ao.applicationStartDate < earliest) {
                    earliest = ao.applicationStartDate;
                }
            });

            return earliest;
        }

        var getEarliestEndDate = function(as) {
            var earliest = -1;
            angular.forEach(as.applicationOptions, function(ao) {
                if (earliest < 0 || ao.applicationEndDate < earliest) {
                    earliest = ao.applicationEndDate;
                }
            });

            return earliest;
        }


        if (applicationSystems) {
            applicationSystems.sort(function(a, b) {

                /*
                Hakujen järjestys:
                1. Jos yhteishakuun on alle kaksi viikkoa näytetään ensin yhteishaku
                2. Käynnissä oleva haku aina ennen ei-käynnissä olevaa hakua
                    •   jos käynnissä sekä varsinainen yhteishaku että päättyneen yhteishaun lisähaku, näytetään varsinaisen yhteishaun hakukohde ensin
                3. Tulossa oleva haku ennen mennyttä hakua
                4. Varsinainen yhteishaku ennen muun tyyppisiä hakuja
                5. Yhteishaun lisähaku ennen muun tyyppisiä hakuja
                6. Aikajärjestys:
                    •   tulevissa hauissa alkamispäivän mukaan laskevassa järjestyksessä
                    •   menneissä hauissa alkamispäivän mukaan nousevassa järjestyksessä
                */

                var dateAfterTwoWeeks = new Date(+new Date + (1000 * 60 * 60 * 24 * 14));
                if(isVarsinainenYhteishaku(a) || isVarsinainenYhteishaku(b)){
                    if (isVarsinainenYhteishaku(a) && !isVarsinainenYhteishaku(b) && getEarliestStartDate(a) < dateAfterTwoWeeks){
                        return -1;
                    } else if (!isVarsinainenYhteishaku(a) && isVarsinainenYhteishaku(b) && getEarliestStartDate(b) < dateAfterTwoWeeks){
                        return 1;
                    }
                }
                
                if (isHakuKaynnissa(a) && isHakuKaynnissa(b)) {
                    if (isVarsinainenYhteishaku(a) != isVarsinainenYhteishaku(b)) {
                        return isVarsinainenYhteishaku(a) ? -1 : 1
                    } else if (isYhteishaunLisahaku(a) != isYhteishaunLisahaku(b)) {
                        return isYhteishaunLisahaku(a) ? -1 : 1;
                    } else {
                        return getEarliestEndDate(a) - getEarliestEndDate(b);
                    }
                } else if (isHakuKaynnissa(a) != isHakuKaynnissa(b)) {
                    return isHakuKaynnissa(a) ? -1 : 1;
                } else if (isHakuTulossaHakuun(a) && isHakuPaattynyt(b)) {
                    return -1;
                } else if(isHakuTulossaHakuun(b) && isHakuPaattynyt(a)) {
                    return 1;
                } else {
                    if (isVarsinainenYhteishaku(a) != isVarsinainenYhteishaku(b)) {
                        return isVarsinainenYhteishaku(a) ? -1 : 1
                    } else if (isYhteishaunLisahaku(a) != isYhteishaunLisahaku(b)) {
                        return isYhteishaunLisahaku(a) ? -1 : 1;
                    } else if (isHakuTulossaHakuun(a) && isHakuTulossaHakuun(b)) {
                        return getEarliestStartDate(a) - getEarliestStartDate(b);
                    } else {
                        return getEarliestEndDate(a) - getEarliestEndDate(b);
                    }
                }
            });
        }
    };

    var sortApplicationOptions = function(applicationOptions) {
        if (applicationOptions) {
            applicationOptions.sort(function(a, b) {

                /*
                Hakukohteiden järjestys:
                1. Käynnissä oleva hakukohde
                2. Hakukohde, jonka haku alkaa ensimmäisenä
                3. Hakukohde, jonka nimi on aakkosissa ensimmäisenä
                */
                if (a.canBeApplied != b.canBeApplied) {
                    return a.canBeApplied ? -1 : 1;
                } else if (a.applicationStartDate != b.applicationStartDate) {
                    return a.applicationStartDate - b.applicationStartDate;
                } else {
                    if (a.name < b.name) return -1;
                    else if (a.name > b.name) return 1;
                    else return 0;
                }
            });
        }
    };

    return {
        sortApplicationSystems: function(applicationSystems) {
            sortApplicationSystems(applicationSystems);
            angular.forEach(applicationSystems, function(as) {
                sortApplicationOptions(as.applicationOptions);
            });
        }
    }
}]).

/**
 *  Service for retrieving translated values for text
 */
service('UtilityService', function() {
    var hakutapa = {
        yhteishaku: '01',
        erillishaku: '02',
        jatkuva: '03'
    };

    var hakutyyppi = {
        varsinainen: '01',
        taydennys: '02',
        lisa: '03'
    };

    var isLisahaku = function(as) {
        return as.hakutyyppi == hakutyyppi.lisa;
    }

    var isYhteishaku = function(as) {
        return as.hakutapa == hakutapa.yhteishaku;
    }

    var isVarsinainenHaku = function(as) {
        return as.hakutyyppi == hakutyyppi.varsinainen;
    }

    return {
        getApplicationOptionById: function(aoId, aos) {
            if (aos && aos.length > 0) {
                for (var index in aos) {
                    if (aos.hasOwnProperty(index)) {
                        if (aos[index].id == aoId) {
                            return aos[index];
                        }
                    }
                }
            }
        },
        getStringAsArray: function(stringToArray) {
            var delimiter = ',';
            if (stringToArray && typeof stringToArray == 'string') {
                return stringToArray.split(delimiter);
            }
        },
        isLukio: function(lo) {
            return lo.educationDegree == 31 ? true : false;
        },
        isYhteishaku: isYhteishaku,
        isVarsinainenHaku: isVarsinainenHaku,
        isLisahaku: isLisahaku,
        groupByApplicationSystem: function(applicationSystems) {
            result = [];
            angular.forEach(applicationSystems, function(as, askey){
                var found;
                angular.forEach(result, function(item, itemkey){
                    if (item.id == as.id) {
                        found = item;
                    }
                });

                if (found) {
                    // add application options to found item
                    angular.forEach(as.applicationOptions, function(ao, aokey) {
                        var aoFound = false;
                        angular.forEach(found.applicationOptions, function(aoitem, aoitemkey){
                            if (ao.id == aoitem.id) {
                                aoFound = true;
                            }
                        });

                        if (!aoFound) {
                            found.applicationOptions.push(ao);
                        }
                    });
                } else {
                    result.push(as);
                }
            });

            return result;
        },
        sortLanguageSelection: function(languageSelection) {
            if (languageSelection) {
                languageSelection.sort(function(a, b) {
                    if(a.subjectCode < b.subjectCode) return -1;
                    if(a.subjectCode > b.subjectCode) return 1;
                    return 0;
                });
            }
        },
        sortLocationsByName: function(locations) {
            locations.sort(function(a, b) {
                if (a.name > b.name) {
                    return 1;
                } else if (a.name < b.name) {
                    return -1;
                } else {
                    return 0;
                }
            });
        },
        padWithZero: function(number) {
            number = number.toString();
            if (number.length <= 1) {
                return "0" + number;
            } else {
                return number;
            }
        },
        getTemplateByLoType: function(type) {
            var map = {
                aikuistenperusopetus: 'aikuislukio'
            };
            
            return map[type] || type;
        },
        /**
         * Use this to convert exam or other dates from finnish time to
         * current timezone time WITH the same date and hours. This is
         * needed to show exam dates with the same date and hour in every
         * timezone.
         */
        convertTimestampToCurrentTime: function(time) {
            //Split to get date parts in fin locale
            //Example of split: ["2016", "09", "14", "13", "27", "47", "03", "00"]
            var t = moment.tz(time, "Europe/Helsinki").format().split(/[^0-9]/),
              year = t[0],
              month = t[1] - 1,
              day = t[2],
              hours = t[3],
              minutes = t[4];
            return new Date(year, month, day, hours, minutes);
        }
    };
});
