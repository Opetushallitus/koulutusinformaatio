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

        getLanguageFromHost = function(host) {
            var x = host.split('.')
            if (x.length < 2)
                return null
            switch (x[x.length - 2]) {
            case 'opintopolku': return 'fi'
            case 'studieinfo': return 'sv'
            case 'studyinfo': return 'en'
            }
            return null
        }

        // initialize app language with value from cookie
        currentLanguage = getLanguageFromHost(window.location.host) || CookieService.get(key) || defaultLanguage,

        
        getLanguage = function() {
            return currentLanguage;
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