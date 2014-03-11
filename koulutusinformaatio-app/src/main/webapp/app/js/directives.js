/* Directives */

angular.module('kiApp.directives', ['kiApp.Navigation', 'angularTreeview']).

/**
 *  Updates the title element of the page.
 */
directive('title', ['$rootScope', function($rootScope) {
    return {
        restrict: 'E',
        link: function(scope, element, attrs) {
            $rootScope.$watch('title', function(value) {
                document.title = value;
                //element.text(value);
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
        scope: {
            contactPersons: '=content'
        },
        link: function(scope, element, attrs) {

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
        restrict: 'A',
        templateUrl: 'templates/professionalTitles.html',
        scope: {
            title: '@title',
            content: '=content'
        },
        link: function(scope, element, attrs) {
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

directive('kiRenderScores', ['TranslationService', function(TranslationService) {
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
 directive('kiLocationFilter', ['SearchLocationService', 'TranslationService', function(SearchLocationService, TranslationService) {
    return {
        restrict: 'A',
        templateUrl: 'templates/locationFilter.html',
        scope: false,
        link: function(scope, element, attrs) {

            scope.add = function() {
                if (!scope.$parent.locations) {
                    scope.$parent.locations = [];
                }

                if (scope.location && scope.$parent.locations.indexOf(scope.location) < 0) {
                    scope.$parent.locations.push(scope.location);
                    scope.location = '';
                    scope.change();
                    return false;
                }
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
        restrict: 'A',
        templateUrl: 'templates/children.html',
        scope: {
            children: '=children',
            type: '=type'
        },
        link: function(scope, element, attrs) {
            scope.$watch('children', function() {
                if (scope.type) {
                    angular.forEach(scope.children, function(child, key) {
                        child.url = scope.type == 'korkeakoulu' ? '#!/' + scope.type + '/' : '#!/koulutusohjelma/';
                        child.url += scope.type == 'korkeakoulu' ? child.id : child.losId;
                        child.url += scope.prerequisite ? '#' + scope.prerequisite : '';
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
            var root = TranslationService.getTranslation('breadcrumb-search-results');
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
                pushItem({name: root, linkHref: '#!/haku/' + SearchService.getTerm() + '?' + FilterService.getParams() });

                if (scope.parent && (scope.loType != 'lukio' && scope.loType != 'erityisopetus')) { // TODO: do not compare to loType
                    pushItem({name: parent, linkHref: '#!/tutkinto/' + scope.parent.id });
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
directive('renderTextBlock', ['TranslationService', function(TranslationService) {
    return function(scope, element, attrs) {

            var title;
            var content;

            attrs.$observe('content', function(value) {
                content = value;
                title = TranslationService.getTranslationByTeachingLanguage(attrs.title);
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
}]).

directive('renderExtendableTextBlock', ['TranslationService', function(TranslationService) {
    return {
        restrict: 'A',
        templateUrl: 'templates/extendableTextBlock.html',
        scope: {
            title: '@title',
            content: '@content'
        },
        link: function(scope, element, attrs) {
            var contentElement = $(element).find('.extendable-content');
            var contentHeight;


            scope.$watch(function() { return contentElement.is(':visible') }, function(value) {
                contentHeight = contentElement.get(0).offsetHeight;
                if (contentHeight > 200) {
                    scope.state = 'closed';
                    contentElement.css('height', 200);
                    contentElement.css('overflow', 'hidden');
                }
            });

            scope.toggleShow = function() {
                if (scope.state == 'closed') {
                    //contentElement.css('overflow', 'visible');
                    contentElement.css('height', 'auto');
                    scope.state = 'open'; 
                } else {
                    contentElement.css('height', 200);
                    //contentElement.css('overflow', 'hidden');
                    scope.state = 'closed'; 
                }
            }
        }
    }
}]).

/**
 *  Renders higher education major selection block
 */
directive('kiRenderMajorSelection', function() {
    return {
        restrict: 'A',
        templateUrl: 'templates/majorSelection.html',
        scope: {
            content: '=',
            title: '@',
            children: '='
        }
    }
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
                element.addClass('label vih');
                element.text(TranslationService.getTranslation('label-as-ongoing'));
            } else {
                element.addClass('label har');
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
directive('kiRenderApplicationStatusLabel', function() {
    return {
        restrict: 'A',
        template: '<span data-ng-switch="active">' +
                    '<span data-ng-switch-when="future"><span data-ki-i18n="application-system-active-future" data-lang="{{lang}}"></span> <span data-ki-timestamp="{{timestamp}}"></span></span>' +
                    '<span data-ng-switch-when="past" data-ki-i18n="application-system-active-past" data-lang="{{lang}}"></span>' +
                    '<span data-ng-switch-when="present"data-ki-i18n="application-system-active-present" data-lang="{{lang}}"></span>' +
                '</span>',
        scope: {
            applicationSystem: '=as',
            applicationOption: '=ao',
            lang: '@lang'
        },
        link: function(scope, element, attrs) {
            var as = scope.applicationSystem;
            var ao = scope.applicationOption;

            if (ao && ao.specificApplicationDates) {
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
            status: '=kiPreviewStatusLabel'
        },
        link: function($scope, element, attrs) {
            var statusPublished = 'JULKAISTU';
            var statusReady = 'VALMIS';
            var statusDraft = 'LUONNOS';

            if ($scope.status == statusPublished || $scope.status == statusReady) {
                element.addClass('label vih');
            } else {
                element.addClass('label sin');
            }

            var labelText = TranslationService.getTranslation($scope.status);
            element.html(labelText);
        }
    }
}]).

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
 *  Render application option index for ao tab
 */
directive('kiApplicationOptionIndex', [ function() {
    return {
        restrict: 'A',
        templateUrl: 'templates/applicationOptionIndex.html',
        scope: {
            lo: '=lo'
        },
        controller: function($scope) {
            // scrolls to an anchor on page
            $scope.scrollToAnchor = function(id) {
                id = id.replace(/\./g,"\\.");
                $('html, body').scrollTop($('#' + id).offset().top);
                return false;
            };
            

            $scope.$watch('lo', function(value) {
                var length = 0;

                if ($scope.lo && $scope.lo.applicationSystems) {
                    angular.forEach($scope.lo.applicationSystems, function(as, askey) {
                        length += as.applicationOptions.length;
                    });
                }

                $scope.showIndex = length > 1 ? true : false;
            });
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
}]);
