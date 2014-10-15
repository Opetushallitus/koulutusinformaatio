function OrganisationCtrl($scope, $rootScope, $routeParams, OrganisationService, LearningOpportunityProviderPictureService, TranslationService) {
    
    OrganisationService.query($routeParams.id).then(function(result) {
        $scope.provider = result;
        $rootScope.title = result.name + ' - ' + TranslationService.getTranslation('sitename');

        if (result && result.pictureFound) {
            LearningOpportunityProviderPictureService.query({providerId: result.id}).then(function(result) {
                $scope.providerImage = result;
            });
        }
    });

};