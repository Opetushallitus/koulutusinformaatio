/**
 *  Controls the selected user interface language
 */
function LanguageCtrl($scope, LanguageService) {
    $scope.changeLanguage = function(code) {
       LanguageService.setLanguage(code);
       i18n.setLng(code);
       document.location.reload(true);
   }
};

/**
 *  Controls header actions
 */
function HeaderCtrl($scope, ApplicationBasketService, TranslationService, LanguageService, Config) {
    $scope.lang = LanguageService.getLanguage();

    $scope.appBasketItemCount = function() {
        return ApplicationBasketService.getItemCount();
    }

    $scope.links = {
        frontpage: Config.get('frontpageUrl'),
        textversion: Config.get('textVersionUrl')
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

/**
 *  Controls footer actions
 */
function FooterCtrl($scope, LanguageService, TranslationService, Config) {
    var lang = LanguageService.getLanguage();

    $scope.locales = {
        opetushallitus: TranslationService.getTranslation('opetushallitus-address-line-1'),
        opetusministerio: TranslationService.getTranslation('opetusministerio-address-line-1')
    };

    $scope.links = {
        opetushallitus: Config.get('ophUrl'),
        opetusministerio: Config.get('okmUrl'),
        rekisteriseloste: Config.get('rekisteriselosteUrl'),
        hakemisto: Config.get('sitemapUrl')
    }

    $scope.images = {
        logo: 'img/opintopolku_large-' + lang + '.png',
        opetushallitus: 'img/OPH_logo-' + lang + '.png',
        opetusministerio: 'img/OKM_logo-' + lang + '.png'
    }
};