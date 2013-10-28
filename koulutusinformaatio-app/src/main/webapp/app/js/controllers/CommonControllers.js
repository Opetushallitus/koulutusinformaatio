/**
 *  Controls the selected user interface language
 */
function LanguageCtrl($scope, LanguageService) {
    $scope.changeLanguage = function(code) {
        /*
        _paq.push(['setCustomVariable', 
            1, // Index, the number from 1 to 5 where this custom variable name is stored 
            "Kieli", // Name, the name of the variable, for example: Gender, VisitorType 
            code, // Value, for example: "Male", "Female" or "new", "engaged", "customer" 
            "visit" // Scope of the custom variable, "visit" means the custom variable applies to the current visit 
        ]);
        _paq.push(['trackPageView']);
        */

       LanguageService.setLanguage(code);
       i18n.setLng(code);
       document.location.reload(true);
   }
};

/**
 *  Controls header actions
 */
function HeaderCtrl($scope, ApplicationBasketService, LanguageService, appConfig) {
    $scope.appBasketItemCount = function() {
        return ApplicationBasketService.getItemCount();
    }

    $scope.lang = LanguageService.getLanguage();
    $scope.appConfig = appConfig;
};

/**
 *  Controls footer actions
 */
function FooterCtrl($scope, LanguageService, appConfig) {
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
            rekisteriseloste: appConfig.rekisteriselosteUrlFi
        }
    } else {
        $scope.images = {
            opetushallitus: 'img/OPH_logo-sv.png',
            opetusministerio: 'img/OKM_logo-sv.png'
        }

        $scope.links = {
            opetushallitus: 'http://www.oph.fi/startsidan',
            opetusministerio: 'http://www.minedu.fi/OPM/?lang=sv',
            rekisteriseloste: appConfig.rekisteriselosteUrlSv
        }
    }
};