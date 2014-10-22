"use strict";

/**
 *  Service for retrieving translated values for text
 */
angular.module('kiApp.TranslationService', ['ngResource']).

service('TranslationService', ['LanguageService', function(LanguageService) {
    var getTranslation = function(key, options) {
            if (key) {
                key = key.replace(/\./g, '');
                return i18n.t(key, options);
            }
        },

        getTranslationByLanguage = function(key, lang) {
            if (key && lang && LanguageService.isSupportedLanguage(lang)) {
                return getTranslation(key, { lng: lang });
            }
            
            return getTranslation(key);
        };

    return {
        getTranslation: getTranslation,
        getTranslationByLanguage: getTranslationByLanguage
    };
}]);