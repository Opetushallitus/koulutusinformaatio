"use strict"

angular.module('Intro', ['angular-intro'])

.controller('IntroCtrl', ['$scope', '$location', 'IntroService', 'TranslationService', function($scope, $location, IntroService, TranslationService) {

    IntroService.query().then(function(result) {
        var posts = [];
        if (result && result.posts) {
            angular.forEach(result.posts, function(post) {
                var id = post['taxonomy_oph-help'].length > 0 ? post['taxonomy_oph-help'][0].slug : '';
                posts.push({
                    element: '.' + id,
                    intro: post.content,
                    position: IntroService.getPositionByStep(id)
                });
            });
        }

        $scope.introOptions = {
            steps: posts,
            showStepNumbers: false,
            exitOnOverlayClick: true,
            exitOnEsc: true,
            nextLabel: TranslationService.getTranslation('intro-next'),
            prevLabel: '',
            skipLabel: '×',
            doneLabel: '×'
        };
    }); 

    // intro launcher shown only on search page
    $scope.showIntroLauncher = function() {
        if ($location.path().indexOf('/haku/') > -1) {
            return true;
        }
        
        return false;
    };
}])

.service('IntroService', ['$q', '$http', 'Config', '_', function($q, $http, Config, _) {
    return {
        query: function() {
            var deferred = $q.defer(),
                url = Config.get('introUrl');

            $http.get(url, {}).
            success(function(result) {

                // sort posts by article slug
                result.posts.sort(function(a, b) {
                    var ataxonomy = a['taxonomy_oph-help'],
                        btaxonomy = b['taxonomy_oph-help'];

                    if (ataxonomy.length > 0 && btaxonomy.length > 0) {
                        ataxonomy = ataxonomy[0];
                        btaxonomy = btaxonomy[0];

                        if (ataxonomy.slug < btaxonomy.slug) {
                            return -1;
                        }
                        
                        if (ataxonomy.slug > btaxonomy.slug) {
                            return 1;
                        } 
                            
                        return 0;
                    }

                    return 0;
                });

                deferred.resolve(result);
            }).
            error(function(result) {
                deferred.reject(result);
            });

            return deferred.promise;
        },

        getPositionByStep: function(stepId) {
            var positions = [
                {
                    slug: 'ki-search-intro-step1',
                    position: 'bottom'
                },
                {
                    slug: 'ki-search-intro-step2',
                    position: 'bottom'
                },
                {
                    slug: 'ki-search-intro-step3',
                    position: 'top'
                }
            ];

            var item = _.find(positions, function(item) { return item.slug === stepId; });
            return item ? item.position : undefined;

        }
    };
}]);