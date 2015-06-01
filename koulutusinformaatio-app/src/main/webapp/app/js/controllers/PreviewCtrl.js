/*
 *  Controllers used in preview mode
 */

/**
 *  Controls the selected user interface language
 */
function LanguageCtrl($scope, LanguageService, $window, $location) {
    $scope.changeLanguage = function(code) {
        $window.location.href += '?lang=' + code;
        $window.location.reload();
    }
};

/**
 *  Controls header actions
 */
function HeaderCtrl($scope, ApplicationBasketService, TranslationService, LanguageService, Config) {
    $scope.lang = LanguageService.getLanguage();

    $scope.links = {
        frontpage: Config.get('frontpageUrl')
    }

    $scope.locales = {
        'tofrontpage': TranslationService.getTranslation('tooltip:to-frontpage'),
        'checklist': TranslationService.getTranslation('tooltip:checklist'),
        'opintopolkufi':  TranslationService.getTranslation('tooltip:opintopolku-fi'),
        'opintopolkusv':  TranslationService.getTranslation('tooltip:opintopolku-sv')
    }

    $scope.images = {
        logo: 'img/opintopolku_large-' + $scope.lang + '.png'
    }
};