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
function HeaderCtrl($scope, ApplicationBasketService, LanguageService, Config) {
    $scope.lang = LanguageService.getLanguage();

    $scope.appBasketItemCount = function() {
        return ApplicationBasketService.getItemCount();
    }

    $scope.links = {
        frontpage: Config.get('frontpageUrl')
    }

    $scope.images = {
        logo: 'img/opintopolku_large-' + $scope.lang + '.png'
    }
};

/**
 *  Controls footer actions
 */
function FooterCtrl($scope, LanguageService, Config) {
    var lang = LanguageService.getLanguage();

    $scope.locales = {
        opetushallitus: i18n.t('opetushallitus-address-line-1'),
        opetusministerio: i18n.t('opetusministerio-address-line-1')
    };

    $scope.links = {
        opetushallitus: Config.get('ophUrl'),
        opetusministerio: Config.get('okmUrl'),
        rekisteriseloste: Config.get('rekisteriselosteUrl')
    }

    $scope.images = {
        opetushallitus: 'img/OPH_logo-' + lang + '.png',
        opetusministerio: 'img/OKM_logo-' + lang + '.png'
    }
};