/* Directives */

angular.module('kiApp.directives', []).

/**
 *  Updates the title element of the page.
 */
directive('title', ['$rootScope', function($rootScope) {
    return {
        restrict: 'E',
        link: function(scope, element, attrs) {
            $rootScope.$watch('title', function(value) {
                element.text(value);
            });
        }
    }
}]).

/**
 * Render contact info block
 */
directive('kiRenderContactInfo', function() {
    return {
        restrict: 'E,A',
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
directive('kiRenderInfoCenterAddress', function() {
    return {
        restrict: 'E,A',
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
directive('renderContactPersonInfo', function() {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/contactPersonInfo.html',
        scope: false,
        link: function(scope, element, attrs) {
            scope.$watch('selectedLOI.contactPersons', function(data) {
                if (data && data.length > 0) {
                    scope.showContactPersonInfo = true;
                } else {
                    scope.showContactPersonInfo = false;
                }
            });
        }
    }
}).


/**
 *  Render student benefits block
 */
directive('kiRenderStudentBenefits', function() {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/studentBenefits.html',
        scope: true,
        link: function(scope, element, attrs) {
            scope.anchor = attrs.anchor;

            scope.$watch('provider', function(data) {
                if (data) {
                    scope.showStudentBenefits = (data.livingExpenses ||
                        data.dining ||
                        data.healthcare) ? true : false;
                }
            });
        }
    }
}).

/**
 *  Render general organization information block
 */
directive('kiRenderOrganization', function() {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/organization.html',
        link: function(scope, element, attrs) {
            scope.anchor = attrs.anchor;

            scope.$watch('provider', function(data) {
                if (data) {
                    scope.showOrganization = (data.description ||
                        data.learningEnvironment || data.accessibility) ? true : false;
                }
            });
        }
    }
}).

/**
 *  Render organization image
 */
directive('kiRenderOrganizationImage', function() {
    return function(scope, element, attrs) {
        scope.$watch('providerImage', function(data) {
            if (data && data.pictureEncoded) {
                var imgElem = $('<img>', {
                    src: 'data:image/jpeg;base64,' + data.pictureEncoded,
                    alt: 'Oppilaitoksen kuva'
                });

                $(element).empty();
                element.append(imgElem);
            }
        });
    }

}).

/**
 *  Render professional titles
 */
directive('kiRenderProfessionalTitles', function() {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/professionalTitles.html',
        scope: true,
        link: function(scope, element, attrs) {
            scope.anchor = attrs.anchor;

            scope.$watch('selectedLOI.professionalTitles', function(data) {
                scope.showProfessionalTitles = data ? true : false;

            });
        }
    }
}).

/**
 *  Render diplomas
 */
directive('kiRenderDiploma', function() {
    return {
        restrict: 'A',
        templateUrl: 'templates/diploma.html',
        link: function(scope, element, attrs) {
            scope.$watch('selectedLOI.diplomas', function(data) {
                scope.showDiploma = data ? true : false;
            });
        }
    }
}).

/**
 *  Render emphasized subjects
 */
directive('kiRenderEmphasizedSubjects', function() {
    return {
        restrict: 'A',
        templateUrl: 'templates/emphasizedSubjects.html',
        link: function(scope, element, attrs) {
            scope.$watch('ao.emphasizedSubjects', function(data) {
                scope.showEmphasizedSubjects = data ? true : false;
            });
        }
    }
}).

/**
 *  Render avergae limit
 */
directive('kiRenderAverageLimit', function() {
    return {
        restrict: 'A',
        templateUrl: 'templates/averageLimit.html',
        link: function(scope, element, attrs) {
            scope.$watch('ao.lowestAcceptedAverage', function(data) {
                scope.showAverageLimit = data ? true : false;
            });
        }
    }
}).

/**
 *  Render emphasized subjects
 */
directive('kiRenderLanguageSelection', function() {
    return {
        restrict: 'A',
        templateUrl: 'templates/languageSelection.html',
        link: function(scope, element, attrs) {
            scope.$watch('selectedLOI.languageSelection', function(data) {
                scope.showLanguageSelection = data ? true : false;
            });
        }
    }
}).

directive('kiRenderExams', function() {
    return {
        restrict: 'A',
        templateUrl: 'templates/exams.html',
        //scope: true,
        link: function(scope, element, attrs) {
            scope.$watch('ao.exams', function(data) {
                scope.exams = data;
                //scope.ao.isLukio = UtilityService.isLukio(scope.ao);
            });
        }
    }
}).

directive('kiRenderAdditionalProof', function() {
    return {
        restrict: 'A',
        templateUrl: 'templates/additionalProof.html'
    }
}).

directive('kiRenderScores', function() {
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
                    scope.scores = i18n.t(scope.typename + '-scores', {min: scope.scoreElement.lowestScore, max: scope.scoreElement.highestScore, threshold: scope.scoreElement.lowestAcceptedScore});
                }
            }
        }
    }
}).

directive('kiRenderAttachments', function() {
    return {
        restrict: 'A',
        templateUrl: 'templates/attachments.html',
        link: function(scope, element, attrs) {
            scope.$watch('ao.attachments', function(data) {
                scope.showAttachments = data ? true : false;
                scope.attachments = data;
            });
        }
    }
}).

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
 directive('kiLocationFilter', ['SearchLocationService', function(SearchLocationService) {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/locationFilter.html',
        scope: false,
        link: function(scope, element, attrs) {

            scope.remove = function(element) {
                scope.locations.splice(scope.locations.indexOf(element), 1);
                scope.change();
                return false;
            }
    
            scope.add = function() {
                if (!scope.locations) {
                    scope.locations = [];
                }

                if (scope.location && scope.locations.indexOf(scope.location) < 0) {
                    scope.locations.push(scope.location);
                    scope.location = '';
                    scope.change();
                    return false;
                }
            }

            scope.getLocations = function($viewValue) {
                return SearchLocationService.query($viewValue);
            }

            scope.placeholder = i18n.t('location-filter-placeholder');
        }
    };
 }]).

/**
 *  Creates and controls language selector for description language
 */
 directive('kiLanguageRibbon', ['$routeParams', function($routeParams) {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/languageRibbon.html',

        link: function(scope, element, attrs) {
            var type = $routeParams.loType;
            scope.isChild = (type === 'koulutusohjelma' || type == 'lukio') ? true : false; // TODO: do not use loType directly

            scope.$watch('lo', function(data) {
                scope.hasMultipleTranslations = (data&& data.availableTranslationLanguages && data.availableTranslationLanguages.length >= 1) ? true : false;
            });
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
        restrict: 'E,A',
        templateUrl: 'templates/siblings.html',
        link: function(scope, element, attrs) {

            scope.$watch('selectedAs.children', function(data) {
                if (data && data.length <= 1) {
                    $(element).remove();
                }
            });

            scope.siblingClass = function(sibling) {
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
        restrict: 'E,A',
        templateUrl: 'templates/children.html',
        link: function(scope, element, attrs) {

            scope.$watch('selectedParentLOI', function(data) {
                if (data && !data.children) {
                    $(element).remove();
                }
            });

            scope.siblingClass = function(sibling) {
                if (sibling.childLOId == $routeParams.id) {
                    return 'disabled';
                } else {
                    return '';
                }
            }
        }
    }
}]).


/**
 *  Creates and controls the breadcrumb
 */
 directive('kiBreadcrumb', ['$location', 'SearchService', 'Config', 'LanguageService', 'FilterService', function($location, SearchService, Config, LanguageService, FilterService) {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/breadcrumb.html',
        link: function(scope, element, attrs) {
            var home = 'home';
            var root = i18n.t('breadcrumb-search-results');
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
                root = i18n.t(data);
                update();
            });

            var update = function() {
                scope.breadcrumbItems = [];
                pushItem({name: home, linkHref: Config.get('frontpageUrl') });
                pushItem({name: root, linkHref: '#/haku/' + SearchService.getTerm() + '?' + FilterService.getParams() });

                if (scope.parent && scope.loType != 'lukio') { // TODO: do not compare to loType
                    pushItem({name: parent, linkHref: '#/tutkinto/' + scope.parent.id });
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
 *  Renders a text block with title. If no content exists the whole text block gets removed. 
 */
directive('renderTextBlock', function() {
    return function(scope, element, attrs) {

            var title;
            var content;

            attrs.$observe('title', function(value) {
                if (value) {
                    title = i18n.t(value);
                }
                update();
            });

            attrs.$observe('content', function(value) {
                content = value;
                update();
            });

            var update = function() {
                $(element).empty();
                if (content) {
                    if (title) {
                        var titleElement = createTitleElement(title, attrs.anchor, attrs.level);
                        element.append(titleElement);
                    }

                    element.append(content);
                }
            };

            var createTitleElement = function(text, anchortag, level) {
                var idAttr = anchortag ? 'id="' + anchortag + '"' : '';
                if (level) {
                    return $('<h' + level + ' ' + idAttr + '>' + text + '</h' + level + '>');
                } else {
                    return $('<h2 ' + idAttr + '>' + text + '</h2>');
                }
            };
        };
}).

/**
 *  Renders study plan block
 */
directive('renderStudyPlan', function() {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/studyPlan.html',
        scope: false
    }
}).

/**
 *  Creates a human readable date from timestamp
 */
directive('kiTimestamp', function() {
    var padWithZero = function(number) {
        number = number.toString();
        if (number.length <= 1) {
            return "0" + number;
        } else {
            return number;
        }
    }

    return function(scope, element, attrs) {
        attrs.$observe('kiTimestamp', function(value) {
            if (value) {
                $(element).empty();
                value = parseInt(value);
                var date = new Date(value);
                element.append(date.getDate() + '.' + (date.getMonth() + 1) + '.' + date.getFullYear());
                if (attrs.precise) {
                    element.append(' ' + padWithZero(date.getHours()) + ':' + padWithZero(date.getMinutes()));
                }
            }
        });
    }
}).

/**
 *  Render application system state as label
 */
directive('kiAsStateLabel', ['UtilityService', function(UtilityService) {
    
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
                element.addClass('label vih');
                element.text(i18n.t('label-as-ongoing'));
            } else {
                element.addClass('label har');
                element.text(i18n.t('label-as-not-ongoing'));
            }
        })
    }
}]).

/**
 *  Render application system state for search result view
 */
directive('kiAsState', function() {
    return function(scope, element, attrs) {
        if (scope.lo.asOngoing) {
            element.text(i18n.t('search-as-ongoing'));
        } else if (scope.lo.nextApplicationPeriodStarts) {
            var ts = new Date(scope.lo.nextApplicationPeriodStarts);
            element.text(i18n.t('search-as-next') + ' ' + ts.getDate() + '.' + (ts.getMonth() + 1) + '.' + ts.getFullYear());
        }
    }


}).

/**
 *  Render application system status
 */
directive('kiRenderApplicationSystemActive', function() {
    return {
        restrict: 'E,A',
        template: '<span data-ng-switch="active">' +
                    '<span data-ng-switch-when="future"><span data-ki-i18n="application-system-active-future"></span> <span data-ki-timestamp="{{timestamp}}"></span></span>' +
                    '<span data-ng-switch-when="past" data-ki-i18n="application-system-active-past"></span>' +
                    '<span data-ng-switch-when="present"data-ki-i18n="application-system-active-present"></span>' +
                '</span>',
        link: function(scope, element, attrs) {
            var as;
            scope.$watch('as', function(data) {
                as = data;
                update();
            });

            var update = function() {
                if (as) {
                    if (as.asOngoing) {
                        scope.active = "present";
                    } else if (as.nextApplicationPeriodStarts) {
                        scope.active = "future";
                        scope.timestamp = as.nextApplicationPeriodStarts;
                    } else {
                        scope.active = "past";
                    }
                }
            };
        }
    }
}).

/**
 *  Render application option status
 */
directive('kiRenderApplicationOptionActive', function() {
    return {
        restrict: 'E,A',
        template: '<span data-ng-switch="active">' +
                    '<span data-ng-switch-when="future"><span data-ki-i18n="application-system-active-future"></span> <span data-ki-timestamp="{{timestamp}}"></span></span>' +
                    '<span data-ng-switch-when="past" data-ki-i18n="application-system-active-past"></span>' +
                    '<span data-ng-switch-when="present"data-ki-i18n="application-system-active-present"></span>' +
                '</span>',
        link: function(scope, element, attrs) {
            var ao;
            scope.$watch('ao', function(data) {
                ao = data;
                update();
            });

            var update = function() {
                if (ao) {
                    if (ao.canBeApplied) {
                        scope.active = "present";
                    } else if (ao.nextApplicationPeriodStarts) {
                        scope.active = "future";
                        scope.timestamp = ao.nextApplicationPeriodStarts;
                    } else {
                        scope.active = "past";
                    }
                }
            };
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
        attrs.$observe('kiI18n', function(value) {
            $(element).empty();
            var translation = TranslationService.getTranslation(value);
            if (attrs.showColon) {
                translation += ':';
            }

            element.append(translation);
        });
    }    
}]);
