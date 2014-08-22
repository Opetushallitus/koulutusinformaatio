/**
 *  Service for retrieving translated values for text
 */
angular.module('kiApp.TranslationService', ['ngResource']).

service('TranslationService', ['$rootScope', function($rootScope) {
    return {
        getTranslation: function(key, options) {
            if (key) {
                return i18n.t(key, options);
            }
        },

        getTranslationByLanguage: function(key, lang) {
            if (key && lang) {
                console.log(i18n.t(key, { lng: lang }));
                return i18n.t(key, { lng: lang });
            }
        },

        getTranslationByTeachingLanguage: function(key) {
        	var lang = $rootScope.translationLanguage;
        	if (key) {
        		if (lang) {
        			return i18n.t(key, { lng: lang });
        		} else {
        			return this.getTranslation(key);
        		}
        	}
        }
    }
}]);