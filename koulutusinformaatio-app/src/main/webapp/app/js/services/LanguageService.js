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
            var domain = x[x.length - 2];
            if (domain.indexOf('opintopolku') > -1) {
                return 'fi';
            } else if (domain.indexOf('studieinfo') > -1) {
                return 'sv';
            } else if (domain.indexOf('studyinfo') > -1) {
                return 'en'
            }
            return 'fi'
        },
        
        getLanguageFromQueryParam = function(host) {
            var lang = $location.search().lang
            var allowedLangs = ['fi', 'sv', 'en'];
            if(_.contains(allowedLangs, lang)) {
                return lang;
            }
        },

        // try to initialize app language with value derived from domain or cookie
        currentLanguage = getLanguageFromQueryParam() || getLanguageFromHost(window.location.host) || CookieService.get(key) || defaultLanguage,
        
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