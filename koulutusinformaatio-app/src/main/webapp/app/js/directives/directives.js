"use strict";

/* Directives */

angular.module('kiApp.directives',
    [
        'kiApp.directives.FacetTree',
        'kiApp.directives.KeyboardControl',
        'kiApp.directives.SelectAreaDialog',
        'kiApp.directives.FacetTitle',
        'kiApp.directives.TextBlocks',
        'kiApp.directives.ContentBlocks',
        'kiApp.directives.AppBasket',
        'kiApp.directives.AjaxLoader',
        'kiApp.directives.ThemeIcon']).

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
    };
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
    };
}]).

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
    };
}).

/**
 *  Multiple emails in one field
 */
directive('kiMultipleEmails', function () {
    return {
        require: 'ngModel',
        link: function(scope, element, attrs, ctrl ) {
            var emailRegex = "[a-z0-9!#$%&'*+/=?^_`{|}~.-]+@[a-z0-9-]+(\\.[a-z0-9-]+)+";
            var separators = "[,;\\s]+";
            var emailsRegex = new RegExp("^(" + emailRegex + separators + ")*(" + emailRegex + ")$");
            ctrl.$parsers.unshift(function(viewValue) {
                if (emailsRegex.test(viewValue)) {
                    ctrl.$setValidity('multipleEmails', true);
                    return $.map(viewValue.split(new RegExp(separators)), $.trim);
                } else {
                    ctrl.$setValidity('multipleEmails', false);
                    return undefined;
                }
            });
        }
    };
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
    };
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
            };

            scope.getLocations = function($viewValue) {
                return SearchLocationService.query($viewValue);
            };
            scope.arialabel = TranslationService.getTranslation('location-filter-aria-label');
            scope.placeholder = TranslationService.getTranslation('location-filter-placeholder');
        }
    };
 }]).

/**
 *  Creates and controls language selector for description language
 */
 directive('kiLanguageRibbon', [function() {
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
            };
        }
    };
 }]).

/**
 *  Creates and controls the link "ribbon" of sibling LOs in child view
 */
  directive('kiSiblingRibbon', ['$routeParams', function($routeParams) {
    return {
        restrict: 'A',
        templateUrl: function(el, attrs) {
            var template = attrs.template || 'siblings';
            return 'templates/' + template + '.html';
        },
        scope: {
            siblings: '='
        },
        controller: function($scope) {
            $scope.siblingClass = function(sibling) {
                if (sibling.losId == $routeParams.id || sibling.id == $routeParams.id) {
                    return 'disabled';
                }

                return '';
            };
        }
    };
}]).

/**
 *  Creates and controls the link "ribbon" of sibling LOs in child view
 */
  directive('kiChildRibbon', [function() {
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
                        child.url = (scope.type == 'korkeakoulu' || scope.type == 'ammatillinenaikuiskoulutus') ? '#!/' + scope.type + '/' : '#!/koulutus/';
                        child.url += child.id;
                        child.url += (child.prerequisite && child.prerequisite.value) ? '?prerequisite=' + child.prerequisite.value : '';
                    });
                }
            });
        }
    };
}]).

/**
 *  Creates and controls the breadcrumb
 */
directive('kiBreadcrumb', ['SearchService', 'Config', 'FilterService', 'TranslationService', function(SearchService, Config, FilterService, TranslationService) {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/breadcrumb.html',
        scope: {
            parent: '=',
            lo: '=',
            provider: '=',
            loType: '=',
            kiBreadcrumb: '@'
        },
        link: function($scope, element, attrs) {
            var home = 'home',
                root = TranslationService.getTranslation('breadcrumb-search-results'),
                goToTooltip = TranslationService.getTranslation('breadcrumb-go-to-page') + ' ',
                homeTooltip = TranslationService.getTranslation('tooltip:to-frontpage'),
                parent,
                lo,
                provider;

            $scope.$watch('parent', function(data) {
                if (data && $scope.loType !== 'lukio' && $scope.loType !== 'erityisopetus') {
                    parent = {
                        name: data.name,
                        linkHref: '#!/tutkinto/' + data.id,
                        tooltip: goToTooltip + data.name
                    };
                }
                update();
            }, true);

            $scope.$watch('lo', function(data) {
                if (data) {
                    if ($scope.loType === 'lukio') {
                        lo = {
                            name: data.provider.name + ', ' + data.name
                        }
                    } else {
                        lo = {
                            name: data.name
                        }
                    }
                }
                update();
            }, true);

            $scope.$watch('provider', function(data) {
                if (data) {
                    provider = {
                        name: data.name
                    }
                }
                update();
            }, true);


            $scope.$watch('kiBreadcrumb', function(data) {
                root = {
                    name: TranslationService.getTranslation(data),
                    linkHref: '#!/haku/' + SearchService.getTerm() + '?' + FilterService.getParams(),
                    tooltip: goToTooltip + root
                }
                update();
            });


            var update = function() {
                $scope.breadcrumbItems = [];
                pushItem({name: home, linkHref: Config.get('frontpageUrl'), tooltip: homeTooltip });
                pushItem(root);
                pushItem(parent);
                pushItem(lo);
                pushItem(provider);
            };

            var pushItem = function(item) {
                if (item) {
                    $scope.breadcrumbItems.push(item);
                }
            };
        }
    };
}]).

/**
 *  Creates a human readable date from timestamp
 */
directive('kiTimestamp', ['TranslationService', 'UtilityService', function(TranslationService, UtilityService) {
    return function(scope, element, attrs) {
        attrs.$observe('kiTimestamp', function(value) {
            if (value) {
                var date = UtilityService.convertTimestampToCurrentTime(parseInt(value));
                $(element).empty();
                element.append(date.getDate() + '.' + (date.getMonth() + 1) + '.' + date.getFullYear());
                element.append(' ' + TranslationService.getTranslation('time-abbreviation') + ' ' + UtilityService.padWithZero(date.getHours()) + ':' + UtilityService.padWithZero(date.getMinutes()));
            }
        });
    };
}]).

/*
 *  Parses a time interval from start and end timestamps
 */
directive('kiTimeInterval', ['UtilityService', 'TranslationService', function(UtilityService, TranslationService) {
    var isSameDay = function(start, end) {
        if (start.getFullYear() !== end.getFullYear()) {
            return false;
        } else if (start.getMonth() !== end.getMonth()) {
            return false;
        } else if (start.getDate() !== end.getDate()) {
            return false;
        } else {
            return true;
        }
    };

    return {
        restrict: 'A',
        scope: {
            startTs: '=',
            endTs: '=',
            showTime: '='
        },
        link: function($scope, element, attrs) {
            var start = UtilityService.convertTimestampToCurrentTime($scope.startTs);
            var end = UtilityService.convertTimestampToCurrentTime($scope.endTs);

            // do not repeat date information if both timestamp are in same day
            if (isSameDay(start, end)) {
                element.append(start.getDate() + '.' + (start.getMonth() + 1) + '.' + start.getFullYear());

                // show hours and minutes only if requested
                if ($scope.showTime) {
                    element.append(' ' + TranslationService.getTranslation('time-abbreviation') + ' ' + UtilityService.padWithZero(start.getHours()) + ':' + UtilityService.padWithZero(start.getMinutes()));
                    element.append(' - ');
                    element.append(UtilityService.padWithZero(end.getHours()) + ':' + UtilityService.padWithZero(end.getMinutes()));
                }
            } else {
                element.append(start.getDate() + '.' + (start.getMonth() + 1) + '.' + start.getFullYear());
                // show hours and minutes only if requested
                if ($scope.showTime) {
                    element.append(' ' + TranslationService.getTranslation('time-abbreviation') + ' ' + UtilityService.padWithZero(start.getHours()) + ':' + UtilityService.padWithZero(start.getMinutes()));
                }
                element.append(' - ');
                element.append(end.getDate() + '.' + (end.getMonth() + 1) + '.' + end.getFullYear());
                // show hours and minutes only if requested
                if ($scope.showTime) {
                    element.append(' ' + TranslationService.getTranslation('time-abbreviation') + ' ' + UtilityService.padWithZero(end.getHours()) + ':' + UtilityService.padWithZero(end.getMinutes()));
                }
            }
        }
    };
}]).

/**
 *  Render application system state as label
 */
directive('kiAsStateLabel', ['UtilityService', 'TranslationService', function(UtilityService, TranslationService) {

    var isAsOngoing = function(as) {
        var result = false;
        if (UtilityService.isLisahaku(as)) {
            angular.forEach(as.applicationOptions, function(value) {
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
        });
    };
}]).

/**
 *  Render application system state for search result view
 */
directive('kiAsState', ['TranslationService', 'UtilityService', function(TranslationService, UtilityService) {
    return {
    	templateUrl: 'templates/asState.html',
    	controller: function($scope) {

    		if ($scope.lo.nextApplicationPeriodStarts && $scope.lo.nextApplicationPeriodStarts.length > 0) {
    			var parsedPeriods = [];
    			for (var i = 0; i < $scope.lo.nextApplicationPeriodStarts.length; ++i) {
    				var ts = new Date($scope.lo.nextApplicationPeriodStarts[i]);
    				var parsedPeriod = ts.getDate() +
            			'.' + (ts.getMonth() + 1) +
            			'.' + ts.getFullYear() +
            			' ' + TranslationService.getTranslation('time-abbreviation') +
            			' ' + UtilityService.padWithZero(ts.getHours()) +
            			':' + UtilityService.padWithZero(ts.getMinutes());
    				if (parsedPeriods.indexOf(parsedPeriod) < 0) {
    					parsedPeriods.push(parsedPeriod);
    				}

    			}
    			$scope.lo.parsedAppPeriods = parsedPeriods;
    		}

            $scope.isPeriodsForthcoming = function() {
            	return !$scope.lo.asOngoing && $scope.lo.nextApplicationPeriodStarts && $scope.lo.nextApplicationPeriodStarts.length > 0;
            }
        }
    };
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
                    '<span data-ng-switch-when="present" data-ki-i18n="application-system-active-present"></span>' +
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
                    // BUG-1892 : Loop & check if options can really be applied now.
                    angular.forEach(as.applicationOptions, function(value) {
                        if (value.canBeApplied) {
                            scope.active = "present";
                        }
                    });
                } else if (as.nextApplicationPeriodStarts) {
                    scope.active = "future";
                    scope.timestamp = as.nextApplicationPeriodStarts;
                } else {
                    scope.active = "past";
                }
            }
        }
    };
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
    };
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
            label: '@',
            periodName: '=',
            asenddates: '='
        },
        controller: function($scope) {
            $scope.smallerDate = $scope.enddate;
            $scope.isJatkuva = function () {
                // code for jatkuva haku is 03
                return $scope.hakutapa === '03';
            };

            if($scope.asenddates) {
                var asEndDateThatIncludesAoDate = _.find($scope.asenddates, function (daterange) {
                    return (daterange.startDate < $scope.enddate && daterange.endDate > $scope.enddate)
                });
                var asEndDate = asEndDateThatIncludesAoDate ? asEndDateThatIncludesAoDate.endDate : $scope.asenddates[0].enddate;
                $scope.smallerDate = ($scope.enddate > asEndDate) ? asEndDate : $scope.enddate;
            }
        }
    };
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
    };
}]).

/**
 *  Fetches a trasnlation with the given key and inserts it inside the element
 */
directive('kiI18n', ['$sanitize', 'TranslationService', function($sanitize, TranslationService) {
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
                key = key.replace(/\./g, ''); // remove . chars from key
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

                translation = $sanitize(translation);
                element.append(translation);
            }
        }
    };
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
    };
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
}).
/**
 * Kuvaukseen voi olla syötettynä rikkinäistä html:ää. Tällöin näytetään teksti rikkinäisenä.
 * https://github.com/shaunbowe/ngBindHtmlIfSafe
 */
directive("bindHtmlIfSafe", ['$compile', '$sce', function ($compile, $sce) {
    return function (scope, element, attrs) {
        scope.$watch(
            function (scope) {
                return scope.$eval(attrs.bindHtmlIfSafe);
            },
            function (value) {
                if(value) {
                    var sanitizedHtml = null;
                    try {
                        sanitizedHtml = $sce.getTrustedHtml(value);
                    } catch (ignore) {}

                    if (sanitizedHtml != null) {
                        element.html(sanitizedHtml);
                    } else {
                        console.error("Passing through invalid html. Url: " + window.location + " html: " + value);
                        element.text(value);
                    }

                    $compile(element.contents())(scope);
                }
            }
        );
    }
}]);