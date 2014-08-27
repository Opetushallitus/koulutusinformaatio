"use strict";

/*
 *  Directives to implement content blocks received from tarjonta
 */
angular.module('kiApp.directives.ContentBlocks', []).

/**
 * Render contact info block
 */
directive('kiContactInfo', function() {
    return {
        restrict: 'A',
        templateUrl: 'templates/contactInfo.html',
        scope: true,
        link: function(scope, element, attrs) {
            scope.anchor = attrs.anchor;

            scope.$watch('provider', function(data) {
                if (data) {
                    scope.showContact = (data.visitingAddress ||
                        data.postalAddress ||
                        data.name ||
                        data.email ||
                        data.phone ||
                        data.fax ||
                        data.webPage) ? true : false;
                }
            });
        }
    };
}).

/**
 * Render contact info block
 */
directive('kiInfoCenterAddress', function() {
    return {
        restrict: 'A',
        templateUrl: 'templates/infoCenterAddress.html',
        scope: false,
        link: function(scope, element, attrs) {

            scope.$watch('provider.applicationOffice', function(data) {
                if (data) {
                    scope.showContact = (data.visitingAddress ||
                        data.postalAddress ||
                        data.name ||
                        data.email ||
                        data.phone ||
                        data.www) ? true : false;
                }
            });
        }
    };
}).

/**
 *  Render contact person info
 */
directive('kiContactPersonInfo', function() {
    return {
        restrict: 'A',
        templateUrl: 'templates/contactPersonInfo.html',
        scope: {
            contactPersons: '=content'
        },
        controller: function($rootScope, $scope) {
            $rootScope.$watch('translationLanguage', function(value) {
                $scope.translationLanguage = value;
            });
        }
    };
}).

/**
 *  Render student benefits block
 */
directive('kiStudentBenefits', ['CollapseBlockService', function(CollapseBlockService) {
    return {
        restrict: 'A',
        require: '^kiCollapseBlock',
        templateUrl: 'templates/studentBenefits.html',
        link: function($scope, element, attrs) {

            $scope.$watch('content', function(value) {
                if (value) {
                    $scope.provider = value;
                    var showStudentBenefits = (value.living ||
                        value.livingExpenses ||
                        value.dining ||
                        value.healthcare) ? true : false;
                    CollapseBlockService.setBlock($scope.blockId, showStudentBenefits);      
                }
            });
        }
    };
}]).

/**
 *  Render general organization information block
 */
directive('kiOrganization', ['CollapseBlockService', function(CollapseBlockService) {
    return {
        restrict: 'A',
        require: '^kiCollapseBlock',
        templateUrl: 'templates/organization.html',
        link: function($scope, element, attrs) {

            $scope.$watch('content', function(value) {
                if (value) {
                    $scope.provider = value;
                    var showOrganization = (value.description ||
                        value.learningEnvironment || value.accessibility || value.living) ? true : false;
                    CollapseBlockService.setBlock($scope.blockId, showOrganization);      
                }
            });
        }
    };
}]).

/**
 *  Render organization image
 */
directive('kiOrganizationImage', function() {
    return function(scope, element, attrs) {
        scope.$watch('providerImage', function(data) {
            if (data && data.pictureEncoded) {
                var imgElem = $('<img>', {
                    src: 'data:image/jpeg;base64,' + data.pictureEncoded,
                    alt: 'Oppilaitoksen kuva'
                });
                imgElem.addClass('img-responsive');

                $(element).empty();
                element.append(imgElem);
            }
        });
    };
}).

/**
 *  Render children as link list
 */
directive('kiChildren', ['CollapseBlockService', function(CollapseBlockService) {
    return {
        restrict: 'A',
        require: '^kiCollapseBlock',
        templateUrl: 'templates/children.html',
        controller: function($rootScope, $scope) {
            $rootScope.$watch('translationLanguage', function(value) {
                $scope.translationLanguage = value;
            });

            $scope.$watch('content', function(value) {
                if (value) {
                    $scope.children = value;
                    var showChildren = value && value.length > 0 ? true : false;
                    CollapseBlockService.setBlock($scope.blockId, showChildren);      
                } else {
                    CollapseBlockService.setBlock($scope.blockId, false);    
                }
            });
        }
    };
}]).


/**
 *  Render professional titles
 */
directive('kiProfessionalTitles', ['CollapseBlockService', function(CollapseBlockService) {
    return {
        restrict: 'A',
        require: '^kiCollapseBlock',
        templateUrl: 'templates/professionalTitles.html',
        controller: function($rootScope, $scope) {
            $rootScope.$watch('translationLanguage', function(value) {
                $scope.translationLanguage = value;
            });

            $scope.$watch('content', function(value) {
                if (value) {
                    var showContent = value && value.length > 0 ? true : false;
                    CollapseBlockService.setBlock($scope.blockId, showContent);      
                } else {
                    CollapseBlockService.setBlock($scope.blockId, false);    
                }
            });
        }
    };
}]).

/**
 *  Render diplomas
 */
directive('kiDiploma', ['CollapseBlockService', function(CollapseBlockService) {
    return {
        restrict: 'A',
        require: '^kiCollapseBlock',
        templateUrl: 'templates/diploma.html',
        controller: function($scope) {
            $scope.$watch('content', function(value) {
                if (value) {
                    $scope.diplomas = value;
                    var showDiplomas = value && value.length > 0 ? true : false;
                   CollapseBlockService.setBlock($scope.blockId, showDiplomas);      
                } else {
                    CollapseBlockService.setBlock($scope.blockId, false);    
                }
            });
        }
    };
}]).

/**
 *  Render emphasized subjects
 */
directive('kiEmphasizedSubjects', ['CollapseBlockService', function(CollapseBlockService) {
    return {
        restrict: 'A',
        require: '^kiCollapseBlock',
        templateUrl: 'templates/emphasizedSubjects.html',
        controller: function($scope) {
            $scope.$watch('content', function(value) {
                if (value) {
                    $scope.emphasizedSubjects = value;
                    var showSubjects = value && value.length > 0 ? true : false;
                    CollapseBlockService.setBlock($scope.blockId, showSubjects);      
                } else {
                    CollapseBlockService.setBlock($scope.blockId, false);    
                }
            });
        }
    };
}]).

/**
 *  Render avergae limit
 */
directive('kiAverageLimit', ['CollapseBlockService', function(CollapseBlockService) {
    return {
        restrict: 'A',
        require: '^kiCollapseBlock',
        templateUrl: 'templates/averageLimit.html',
        controller: function($scope) {
            $scope.$watch('content', function(value) {
                if (value) {
                    $scope.lowestAcceptedAverage = value;
                    var showAverage = value ? true : false;
                    CollapseBlockService.setBlock($scope.blockId, showAverage);      
                } else {
                    CollapseBlockService.setBlock($scope.blockId, false);    
                }
            });
        }
    };
}]).

/**
 *  Render emphasized subjects
 */
directive('kiLanguageSelection', ['CollapseBlockService', function(CollapseBlockService) {
    return {
        restrict: 'A',
        require: '^kiCollapseBlock',
        templateUrl: 'templates/languageSelection.html',
        controller: function($scope) {
            $scope.$watch('content', function(value) {
                if (value) {
                    $scope.languageSelection = value;
                    var showLanguages = value && value.length > 0 ? true : false;
                    CollapseBlockService.setBlock($scope.blockId, showLanguages);
                } else {
                    CollapseBlockService.setBlock($scope.blockId, false);
                }
            });
        }
    };
}]).

directive('kiExams', ['CollapseBlockService', function(CollapseBlockService) {
    return {
        restrict: 'A',
        require: '^kiCollapseBlock',
        templateUrl: 'templates/exams.html',
        controller: function($scope) {
            $scope.$watch('content', function(value) {
                if (value) {
                    $scope.exams = value;
                    var showExams = value && value.length > 0 ? true : false;
                    CollapseBlockService.setBlock($scope.blockId, showExams);
                } else {
                    CollapseBlockService.setBlock($scope.blockId, false);
                }
            });
        }
    };
}]).

directive('kiAdditionalProof', ['CollapseBlockService', function(CollapseBlockService) {
    return {
        restrict: 'A',
        require: '^kiCollapseBlock',
        templateUrl: 'templates/additionalProof.html',
        controller: function($scope) {
            $scope.$watch('content', function(value) {
                if (value) {
                    $scope.additionalProof = value;
                    var showAdditionalProof = value ? true : false;
                    CollapseBlockService.setBlock($scope.blockId, showAdditionalProof);
                } else {
                    CollapseBlockService.setBlock($scope.blockId, false);
                }
            });
        }
    };
}]).

directive('kiScores', ['TranslationService', function(TranslationService) {
    return {
        restrict: 'A',
        template: '<p data-ng-show="scores">{{scores}}</p>',
        scope: {
            scoreElement: '=scoreElement',
            typename: '=typename'
        },
        link: function(scope, element, attrs) {
            if (scope.scoreElement) {
                if (scope.scoreElement.lowestScore 
                    || scope.scoreElement.highestScore
                    || scope.scoreElement.lowestAcceptedScore) {
                    scope.scores = TranslationService.getTranslation(scope.typename + '-scores', {min: scope.scoreElement.lowestScore, max: scope.scoreElement.highestScore, threshold: scope.scoreElement.lowestAcceptedScore});
                }
            }
        }
    };
}]).

directive('kiAttachments', ['CollapseBlockService', function(CollapseBlockService) {
    return {
        restrict: 'A',
        require: '^kiCollapseBlock',
        templateUrl: 'templates/attachments.html',
        link: function($scope, element, attrs) {
            $scope.$watch('content', function(value) {
                if (value) {
                    $scope.attachments = value;
                    var showAttachments = value && value.length > 0 ? true : false;
                    CollapseBlockService.setBlock($scope.blockId, showAttachments);
                } else {
                    CollapseBlockService.setBlock($scope.blockId, false);
                }
            });
        }
    };
}]).

/**
 *  Render organization social links
 */
directive('kiSocialLinks', function() {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/socialLinks.html',
        link: function(scope, element, attrs) {
            scope.anchor = attrs.anchor;

            scope.$watch('provider', function(data) {
                if (data) {
                    scope.showOrganization = (data.learningEnvironment ||
                        data.accessibility) ? true : false;
                }
            });
        }
    };
}).

/**
 *  Renders higher education major selection block
 */
directive('kiMajorSelection', ['CollapseBlockService', function(CollapseBlockService) {
    return {
        restrict: 'A',
        require: '^kiCollapseBlock',
        templateUrl: 'templates/majorSelection.html',
        controller: function($rootScope, $scope) {
            $rootScope.$watch('translationLanguage', function(value) {
                $scope.translationLanguage = value;
            });
            
            $scope.$watch('content', function(value) {
                if (value) {
                   $scope.textContent = value[0];
                   $scope.children = value[1];
                   var showMajorSelection = value && value[0] ? true : false;
                   CollapseBlockService.setBlock($scope.blockId, showMajorSelection);      
                } else {
                    CollapseBlockService.setBlock($scope.blockId, false); 
                }
            });
        }
    };
}]).

/**
 *  Renders study plan block
 */
directive('kiStudyPlan', ['CollapseBlockService', function(CollapseBlockService) {
    return {
        restrict: 'A',
        require: '^kiCollapseBlock',
        templateUrl: 'templates/studyPlan.html',
        controller: function($scope) {
            $scope.$watch('content', function(value) {
                if (value) {
                    $scope.studyPlan = value;
                    var showStudyPlan = value ? true : false;
                    CollapseBlockService.setBlock($scope.blockId, showStudyPlan);
                } else {
                    CollapseBlockService.setBlock($scope.blockId, false);
                }
            });
        }
    };
}]).

/*
 *  Renders structure of studies block
 */
directive('kiStructureOfStudies', ['CollapseBlockService', function(CollapseBlockService) {
    return {
        restrict: 'A',
        require: '^kiCollapseBlock',
        templateUrl: 'templates/structureOfStudies.html',
        controller: function($scope) {
            $scope.$watch('content', function(value) {
                if (value) {
                    $scope.textContent = value[0];
                    $scope.image = value[1];
                    var showStructure = value && (value[0] || value[1]) ? true : false;
                    CollapseBlockService.setBlock($scope.blockId, showStructure);
                } else {
                    CollapseBlockService.setBlock($scope.blockId, false);
                }
            });
        }
    };
}]);