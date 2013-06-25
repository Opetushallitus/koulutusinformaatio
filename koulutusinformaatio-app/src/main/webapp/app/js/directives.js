/* Directives */

angular.module('kiApp.directives', []).

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

            scope.$watch('parentLO.provider', function(data) {
                if (data) {
                    scope.showContact = (data.visitingAddress ||
                        data.postalAddress ||
                        data.name ||
                        data.email ||
                        data.phone ||
                        data.fax ||
                        data.webPage) ? true : false;
                }

                scope.provider = data;
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

            scope.$watch('parentLO.provider', function(data) {
                if (data) {
                    scope.showStudentBenefits = (data.livingExpenses ||
                        data.dining ||
                        data.healthcare) ? true : false;
                }

                scope.provider = data;
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

            scope.$watch('parentLO.provider', function(data) {
                if (data) {
                    scope.showOrganization = (data.learningEnvironment ||
                        data.accessibility) ? true : false;

                    scope.provider = data;
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
                    'class': 'width-100',
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

            scope.$watch('childLO.professionalTitles', function(data) {
                scope.showProfessionalTitles = data ? true : false;

            });
        }
    }
}).

directive('kiRenderExams', function() {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/exams.html',
        scope: true,
        link: function(scope, element, attrs) {
            scope.$watch('selectedAo.exams', function(data) {
                scope.exams = data;
            });

            scope.rowClass = function(isFirst, isLast) {
                if (isFirst && isLast) {
                    return 'first last';
                } else if (isFirst) {
                    return 'first';
                } else if (isLast) {
                    return 'last';
                } else {
                    return '';
                }
            } 
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

            scope.$watch('parentLO', function(data) {
                if (data && data.provider) {
                    scope.showOrganization = (data.provider.learningEnvironment ||
                        data.provider.accessibility) ? true : false;

                    scope.provider = data.provider;
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
                    element.html(data.replace('@', '(at)'));
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
 directive('kiLocationFilter', function() {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/locationFilter.html',

        link: function(scope, element, attrs) {
            scope.locations = [];

            scope.remove = function(element) {
                scope.locations.splice(scope.locations.indexOf(element), 1);
                scope.change();
            }

            scope.add = function() {
                if (scope.location && scope.locations.indexOf(scope.location) < 0) {
                    scope.locations.push(scope.location);
                    scope.location = '';
                    scope.change();
                }
            }
        }
    };
 }).

/**
 *  Creates and controls language selector for description language
 */
 directive('kiLanguageRibbon', ['$routeParams', function($routeParams) {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/languageRibbon.html',

        link: function(scope, element, attrs) {
            scope.label = i18n.t('description-language-selection');
            scope.isChild = ($routeParams.childId) ? true : false;

            scope.$watch('childLO', function(data) {
                scope.hasMultipleTranslations = scope.childLO && scope.childLO.availableTranslationLanguages.length >= 1;    
            });

            scope.$watch('parentLO', function(data) {
                scope.hasMultipleTranslations = scope.parentLO && scope.parentLO.availableTranslationLanguages.length >= 1;    
            });
        }
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

            scope.$watch('childLO', function(data) {
                if (data && !data.related) {
                    $(element).remove();
                }
            });

            scope.siblingClass = function(sibling) {
                if (sibling.childLOId == $routeParams.childId) {
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
 directive('kiBreadcrumb', ['$location', 'SearchService', function($location, SearchService) {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/breadcrumb.html',
        link: function(scope, element, attrs) {
            var home = 'home';
            var search = i18n.t('breadcrumb-search-results');
            var parent;
            var child;

            scope.$watch('parentLO.name', function(data) {
                parent = data;
                update();
            }, true);

            scope.$watch('childLO.name', function(data) {
                child = data;
                update();
            }, true);

            var update = function() {
                scope.breadcrumbItems = [];
                pushItem({name: home, linkHref: '#/' });
                pushItem({name: search, linkHref: '#/haku/' + SearchService.getTerm() });

                if (scope.parentLO) {
                    pushItem({name: parent, linkHref: '#/tutkinto/' + scope.parentLO.id });
                }

                pushItem({name: child});
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

                    // replace line feed with <br>
                    //content = content.replace(/(\r\n|\n|\r)/g,"<br />");
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
 *  Updates the title element of the page.
 */
directive('kiAppTitle', ['TitleService', function(TitleService) {
    return function(scope, element, attrs) {
        $(element).on('updatetitle', function(e, param) {
            element.html(param);
        });
        //element.html(TitleService.getTitle());
    };
}]).

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
            $(element).empty();
            value = parseInt(value);
            var date = new Date(value);
            element.append(date.getDate() + '.' + (date.getMonth() + 1) + '.' + date.getFullYear());
            if (attrs.precise) {
                element.append(' ' + padWithZero(date.getHours()) + ':' + padWithZero(date.getMinutes()));
            }
        });
    }


}).

directive('kiAsState', function() {
    return function(scope, element, attrs) {
        if (scope.lo.asOngoing) {
            element.html(i18n.t('search-as-ongoing'));
        } else if (scope.lo.nextAs) {
            var ts = new Date(scope.lo.nextAs.startDate);
            element.html(i18n.t('search-as-next') + ' ' + ts.getDate() + '.' + (ts.getMonth() + 1) + '.' + ts.getFullYear());
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
            var start;
            var end;
            attrs.$observe('startDate', function(value) {
                start = value;
                update();
            });

            attrs.$observe('endDate', function(value) {
                end = value;
                update();
            });

            var update = function() {
                if (start && end) {
                    var current = new Date().getTime();
                    if (current < start) {
                        scope.active = "future";
                        scope.timestamp = start;
                    } else if (current > end) {
                        scope.active = "past";
                    } else {
                        scope.active = "present";
                    }
                }
            };
        }
    }
}).

/**
 *  Fetches a trasnlation with the given key and inserts it inside the element
 */
directive('kiI18n', ['TranslationService', function(TranslationService) {
    return function(scope, element, attrs) {
        attrs.$observe('kiI18n', function(value) {
            $(element).empty();
            element.append(TranslationService.getTranslation(value));
        });
    }    
}]);
