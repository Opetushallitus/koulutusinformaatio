angular.module('kiApp.LanguageService', ['ngResource']).

/**
 *  Service keeping track of the current language selection
 */
service('LanguageService', ['CookieService', '$location', '_', function(CookieService, $location, _) {
    var languages = {
            finnish: 'fi',
            swedish: 'sv',
            english: 'en'
        },
        supportedLanguages = _.values(languages),
        defaultLanguage = languages.finnish,
        key = 'i18next',

        getLanguage = function() {
            return CookieService.get(key) || defaultLanguage;
        },

        setLanguage = function(language) {
            CookieService.set(key, language);
        },

        getDefaultLanguage = function() {
            return defaultLanguage;
        },

        isSupportedLanguage = function(lang) {
            return supportedLanguages.indexOf(lang) > -1;
        };

    return {
        getLanguage: getLanguage,
        setLanguage: setLanguage,
        getDefaultLanguage: getDefaultLanguage,
        isSupportedLanguage: isSupportedLanguage
    };
}]);