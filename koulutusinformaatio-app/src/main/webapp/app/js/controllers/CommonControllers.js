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
function HeaderCtrl($scope, ApplicationBasketService, LanguageService) {
    $scope.appBasketItemCount = function() {
        return ApplicationBasketService.getItemCount();
    }

    $scope.lang = LanguageService.getLanguage();
};

/**
 *  Controls footer actions
 */
function FooterCtrl($scope, LanguageService, kiAppConstants) {
    $scope.locales = {
        opetushallitus: i18n.t('opetushallitus-address-line-1'),
        opetusministerio: i18n.t('opetusministerio-address-line-1')
    };
    
    if (LanguageService.getLanguage() == LanguageService.getDefaultLanguage()) {
        $scope.images = {
            opetushallitus: 'img/OPH_logo.png',
            opetusministerio: 'img/OKM_logo.png'
        }

        $scope.links = {
            opetushallitus: 'http://www.oph.fi/etusivu',
            opetusministerio: 'http://www.minedu.fi/OPM/',
            rekisteriseloste: kiAppConstants.contextRoot + 'rekisteriseloste.html'
        }
    } else {
        $scope.images = {
            opetushallitus: 'img/OPH_logo-sv.png',
            opetusministerio: 'img/OKM_logo-sv.png'
        }

        $scope.links = {
            opetushallitus: 'http://www.oph.fi/startsidan',
            opetusministerio: 'http://www.minedu.fi/OPM/?lang=sv',
            rekisteriseloste: kiAppConstants.contextRoot + 'sv/rekisteriseloste.html'
        }
    }
};