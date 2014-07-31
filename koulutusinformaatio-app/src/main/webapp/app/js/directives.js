/* Directives */

angular.module('kiApp.directives', 
    [
        'kiApp.Navigation',
        'kiApp.FacetTree',
        'kiApp.KeyboardControl',
        'kiApp.SelectAreaDialog',
        'kiApp.FacetTitle',
        'kiApp.directives.kiBlocks',
        'kiApp.directives.AppBasket']).

/**
 *  Updates the title element of the page.
 */
directive('title', ['$rootScope', function($rootScope) {
    return {
        restrict: 'E',
        link: function(scope, element, attrs) {
            $rootScope.$watch('title', function(value) {
                document.title = value;
            });
        }
    }
}]).

directive('meta', ['$rootScope', function($rootScope) {
    return {
        restrict: 'E',
        link: function(scope, element, attrs) {
            if (attrs.name === 'description') {
                $rootScope.$watch('description', function(value) {
                    element.attr('content', value);
                });
            }
        }
    }
}]).

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
    }
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
    }
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
    }
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
    }
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
    }
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
    }

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
    }
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
    }
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
    }
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
    }
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
    }
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
    }
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
    }
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
    }
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
    }
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
    }
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
    }
}).

/**
 *  Render email (@ replaced with (at))
 */
directive('kiEmail', function() {
    return {
        restrict: 'E,A',
        link: function(scope, element, attrs) {
            attrs.$observe('kiEmail', function(data) {
                if (data) {
                    element.text(data.replace('@', '(at)'));
                }
            });
        }
    }
}).

/**
 *  Change relative link to absolute link
 */
directive('kiAbsoluteLink', function() {
    return {
        restrict: 'E,A',
        link: function(scope, element, attrs) {
            attrs.$observe('kiAbsoluteLink', function(data) {
                if (data.search(':\/\/') > -1) {
                    element.attr('href', data);
                } else {
                    element.attr('href', 'http://' + data);
                }
            });
        }
    }
}).

/**
 *  Creates and controls the location filter element
 */
 directive('kiLocationFilter', ['SearchLocationService', 'TranslationService', function(SearchLocationService, TranslationService) {
    return {
        restrict: 'A',
        templateUrl: 'templates/locationFilter.html',
        scope: false,
        link: function(scope, element, attrs) {

            scope.add = function() {
                scope.setFilteredLocations([scope.location]);
                scope.location = '';
                scope.change();
                return false;
            }

            scope.getLocations = function($viewValue) {
                return SearchLocationService.query($viewValue);
            }

            scope.placeholder = TranslationService.getTranslation('location-filter-placeholder');
        }
    };
 }]).

/**
 *  Creates and controls language selector for description language
 */
 directive('kiLanguageRibbon', ['$routeParams', function($routeParams) {
    return {
        restrict: 'A',
        templateUrl: 'templates/languageRibbon.html',
        scope: {
            languages: '=',
            changeLanguage: '&'
        },

        link: function(scope, element, attrs) {
            scope.$watch('languages', function(data) {
                scope.hasMultipleTranslations = (data && data.length >= 1) ? true : false;
            });

            var callback = scope.changeLanguage();

            scope.changeLanguage = function(lang) {
                callback(lang);
            }
        }
    };
 }]).

 /**
 *  Creates and controls prerequisite selection
 */
 directive('kiPrerequisiteSelectionRibbon', ['$routeParams', function($routeParams) {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/prerequisiteRibbon.html'
    };
 }]).

/**
 *  Creates and controls the link "ribbon" of sibling LOs in child view
 */
  directive('kiSiblingRibbon', ['$location', '$routeParams', function($location, $routeParams) {
    return {
        restrict: 'A',
        templateUrl: 'templates/siblings.html',
        scope: {
            siblings: '='
        },
        controller: function($scope) {
            $scope.siblingClass = function(sibling) {
                if (sibling.losId == $routeParams.id) {
                    return 'disabled';
                } else {
                    return '';
                }
            }
        }
    }
}]).

/**
 *  Creates and controls the link "ribbon" of sibling LOs in child view
 */
  directive('kiChildRibbon', ['$location', '$routeParams', function($location, $routeParams) {
    return {
        restrict: 'A',
        templateUrl: 'templates/childRibbon.html',
        scope: {
            children: '=children',
            type: '=type',
            lang: '=lang'
        },
        link: function(scope, element, attrs) {
            scope.$watch('children', function() {
                if (scope.type) {
                    angular.forEach(scope.children, function(child, key) {
                        child.url = scope.type == 'korkeakoulu' ? '#!/' + scope.type + '/' : '#!/koulutusohjelma/';
                        child.url += scope.type == 'korkeakoulu' ? child.id : child.losId;
                        child.url += (child.prerequisite && child.prerequisite.value) ? '?prerequisite=' + child.prerequisite.value : '';
                    });
                }
            });
        }
    }
}]).

/**
 *  Creates and controls the breadcrumb
 */
 directive('kiBreadcrumb', ['$location', 'SearchService', 'Config', 'LanguageService', 'FilterService', 'TranslationService', function($location, SearchService, Config, LanguageService, FilterService, TranslationService) {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/breadcrumb.html',
        link: function(scope, element, attrs) {
            var home = 'home';
            var root =TranslationService.getTranslation('breadcrumb-search-results');
            var goToTooltip=TranslationService.getTranslation('breadcrumb-go-to-page')+' ';
            var homeTooltip = TranslationService.getTranslation('tooltip:to-frontpage');
            var parent;
            var child;
            var provider;

            scope.$watch('parent.name', function(data) {
                parent = data;
                update();
            }, true);

            scope.$watch('lo.name', function(data) {
                child = data;
                update();
            }, true);

            scope.$watch('provider.name', function(data) {
                provider = data;
                update();
            }, true);

            attrs.$observe('kiBreadcrumb', function(data) {
                root = TranslationService.getTranslation(data);
                update();
            });

            var update = function() {
                scope.breadcrumbItems = [];
                pushItem({name: home, linkHref: Config.get('frontpageUrl'), tooltip: homeTooltip });
                pushItem({name: root, linkHref: '#!/haku/' + SearchService.getTerm() + '?' + FilterService.getParams(), tooltip: goToTooltip + root });

                if (scope.parent && (scope.loType != 'lukio' && scope.loType != 'erityisopetus')) { // TODO: do not compare to loType
                    pushItem({name: parent, linkHref: '#!/tutkinto/' + scope.parent.id, tooltip: goToTooltip + parent });
                }

                if (scope.loType == 'lukio') { // TODO: do not compare to loType
                    pushItem({name: provider + ', ' + child});
                } else {
                    pushItem({name: child});
                }
            };

            var pushItem = function(item) {
                if (item.name) {
                    scope.breadcrumbItems.push(item);
                }
            };
        }
    };
}]).

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
    }
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
    }
}]).

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
    }
}]).


/**
 *  Creates a human readable date from timestamp
 */
directive('kiTimestamp', ['UtilityService', function(UtilityService) {
    return function(scope, element, attrs) {
        attrs.$observe('kiTimestamp', function(value) {
            if (value) {
                $(element).empty();
                value = parseInt(value);
                var date = new Date(value);
                element.append(date.getDate() + '.' + (date.getMonth() + 1) + '.' + date.getFullYear());
                if (attrs.precise) {
                    element.append(' ' + UtilityService.padWithZero(date.getHours()) + ':' + UtilityService.padWithZero(date.getMinutes()));
                }
            }
        });
    }
}]).

directive('kiTimeInterval', ['UtilityService', 'TranslationService', function(UtilityService, TranslationService) {
    var isSameDay = function(start, end) {
        if (start.getFullYear() != end.getFullYear()) {
            return false;
        } else if (start.getMonth() != end.getMonth()) {
            return false;
        } else if (start.getDate() != end.getDate()) {
            return false;
        } else {
            return true;
        }
    };

    return {
        restrict: 'A',
        scope: {
            examEvent: '='
        },
        link: function(scope, element, attrs) {
            var start = new Date(scope.examEvent.start);
            var end = new Date(scope.examEvent.end);

            if (isSameDay(start, end)) {
                element.append(start.getDate() + '.' + (start.getMonth() + 1) + '.' + start.getFullYear());
                element.append(' ' + TranslationService.getTranslation('time-abbreviation') + ' ' + UtilityService.padWithZero(start.getHours()) + ':' + UtilityService.padWithZero(start.getMinutes()));
            } else {
                element.append(start.getDate() + '.' + (start.getMonth() + 1) + '.' + start.getFullYear());
                element.append(' - ');
                element.append(end.getDate() + '.' + (end.getMonth() + 1) + '.' + end.getFullYear());
            }
        }
    }
}]).

/**
 *  Render application system state as label
 */
directive('kiAsStateLabel', ['UtilityService', 'TranslationService', function(UtilityService, TranslationService) {
    
    var isAsOngoing = function(as) {
        var result = false;
        if (UtilityService.isLisahaku(as)) {
            angular.forEach(as.applicationOptions, function(value, key) {
                if (value.canBeApplied) {
                    result = true;
                }          
            });
        } else if (as.asOngoing) {
            result = true;
        }

        return result;
    };

    return function(scope, element, attrs) {
        scope.$watch('loi', function(data) {
            var isOngoing = false;

            if (data && data.applicationSystems) {
                for (var asIndex in data.applicationSystems) {
                    if (data.applicationSystems.hasOwnProperty(asIndex)) {
                        var as = data.applicationSystems[asIndex];
                        if (isAsOngoing(as)) {
                            isOngoing = true;
                            break; 
                        }
                    }
                }
            }

            if (isOngoing) {
                element.addClass('label label-success');
                element.text(TranslationService.getTranslation('label-as-ongoing'));
            } else {
                element.addClass('label label-default');
                element.text(TranslationService.getTranslation('label-as-not-ongoing'));
            }
        })
    }
}]).

/**
 *  Render application system state for search result view
 */
directive('kiAsState', ['TranslationService', function(TranslationService) {
    return function(scope, element, attrs) {
        if (scope.lo.asOngoing) {
            element.text(TranslationService.getTranslation('search-as-ongoing'));
        } else if (scope.lo.nextApplicationPeriodStarts) {
            var ts = new Date(scope.lo.nextApplicationPeriodStarts);
            element.text(TranslationService.getTranslation('search-as-next') + ' ' + ts.getDate() + '.' + (ts.getMonth() + 1) + '.' + ts.getFullYear());
        }
    }
}]).

/**
 *  Render application status label
 */
directive('kiApplicationStatusLabel', function() {
    return {
        restrict: 'A',
        template: '<span data-ng-switch="active" class="text-muted">' +
                    '<span data-ng-switch-when="future"><span data-ki-i18n="application-system-active-future"></span> <span data-ki-timestamp="{{timestamp}}"></span></span>' +
                    '<span data-ng-switch-when="past" data-ki-i18n="application-system-active-past"></span>' +
                    '<span data-ng-switch-when="present"data-ki-i18n="application-system-active-present"></span>' +
                '</span>',
        scope: {
            applicationSystem: '=as',
            applicationOption: '=ao'
        },
        link: function(scope, element, attrs) {
            var as = scope.applicationSystem;
            var ao = scope.applicationOption;

            if (ao) {
                if (ao.canBeApplied) {
                    scope.active = "present";
                } else if (ao.nextApplicationPeriodStarts) {
                    scope.active = "future";
                    scope.timestamp = ao.nextApplicationPeriodStarts;
                } else {
                    scope.active = "past";
                }
            } else if (as) {
                if (as.asOngoing) {
                    scope.active = "present";
                } else if (as.nextApplicationPeriodStarts) {
                    scope.active = "future";
                    scope.timestamp = as.nextApplicationPeriodStarts;
                } else {
                    scope.active = "past";
                }
            }
        }
    }
}).

/**
 *  Render status label for preview
 */
directive('kiPreviewStatusLabel', ['TranslationService', function(TranslationService) {
    return {
        restrict: 'A',
        scope: {
            status: '=kiPreviewStatusLabel',
            lang: '=lang'
        },
        link: function($scope, element, attrs) {
            var statusPublished = 'JULKAISTU';
            var statusReady = 'VALMIS';
            var statusDraft = 'LUONNOS';

            if ($scope.status == statusPublished || $scope.status == statusReady) {
                element.addClass('label label-success');
            } else {
                element.addClass('label label-info');
            }

            var labelText = TranslationService.getTranslationByLanguage($scope.status, $scope.lang);
            element.html(labelText);
        }
    }
}]).

/**
 *  Render application time
 */
directive('kiAoApplicationTime', function() {
    return {
        restrict: 'A',
        templateUrl: 'templates/aoApplicationTime.html',
        scope : {
            startdate: '=',
            enddate: '=',
            hakutapa: '=',
            label: '@'
        },
        controller: function($scope) {
            $scope.isJatkuva = function() {
                // code for jatkuva haku is 03
                return $scope.hakutapa == '03';
            }
        }
    }
}).

/**
 *  Render application option status
 */
directive('kiBanner', ['$location', function($location) {
    return {
        restrict: 'E,A',
        template: '<span class="banner-text">{{banner}}</span>',
        link: function(scope, element, attrs) {
            var host = $location.host();
            if (host.indexOf('koulutus') == 0) scope.banner = 'koulutus';
            else if (host.indexOf('testi') == 0) scope.banner = 'QA';
            else if (host.indexOf('xtest-') == 0) scope.banner = 'Kielistudio';
            else if (host.indexOf('test-') == 0) scope.banner = 'Reppu';
            else if (host.indexOf('itest-') == 0) scope.banner = 'Luokka';
            else if (host.indexOf('localhost') == 0) scope.banner = host;
        }
    }
}]).

/**
 *  Fetches a trasnlation with the given key and inserts it inside the element
 */
directive('kiI18n', ['TranslationService', function(TranslationService) {
    return function(scope, element, attrs) {
        var key;
        var lang;

        attrs.$observe('kiI18n', function(value) {
            key = value;
            update();
        });

        attrs.$observe('lang', function(value) {
            lang = value;
            update();
        });

        var update = function() {
            if (key) {
                $(element).empty();

                var translation;
                if (lang) {
                    translation = TranslationService.getTranslationByLanguage(key, lang);
                } else {
                    translation = TranslationService.getTranslation(key);
                }
                
                if (attrs.showColon) {
                    translation += ':';
                }

                element.append(translation);
            }
        }
    }    
}]).

/**
 *  Inserts a title attribute to the element using a translation key
 */
directive('kiTitle', ['TranslationService', function(TranslationService) {
    return {
        restrict: 'A',
        scope: false,
        link: function($scope, element, attrs) {
            attrs.$observe('kiTitle', function(value) {
                var translation = TranslationService.getTranslation(value);
                element.attr('title', translation);
            });
        }
    }
}]).

/*
* Set focus to element it is set to.
* */
directive('setFocusHere', function(){
        return{
            restrict: 'A',
            link: function(scope, element){
                element[0].focus();
            }
        };
});
