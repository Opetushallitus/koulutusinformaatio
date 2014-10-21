/**
 *  Controller for organisation view.
 */

function OrganisationCtrl($scope, $rootScope, $routeParams, OrganisationService, LearningOpportunityProviderPictureService, TranslationService) {
    
    // search the selected organisation
    OrganisationService.query($routeParams.id).then(function(result) {
        $scope.provider = result;
        $rootScope.title = result.name + ' - ' + TranslationService.getTranslation('sitename');

        // if organisation has a picture, fetch it
        if (result && result.pictureFound) {
            LearningOpportunityProviderPictureService.query({providerId: result.id}).then(function(result) {
                $scope.providerImage = result;
            });
        }
    });

};