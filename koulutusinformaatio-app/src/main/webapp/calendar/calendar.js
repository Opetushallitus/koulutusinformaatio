/**
 *  Application system calendar component.
 *
 *  Requires:
 *  jQuery 1.11
 *  Bootstrap 3.1.1
 *  underscore 1.7.0 (included during initialization if not present)
 *  
 *  Requires 16 column grid for Bootstrap to display properly. See initialization example from index.html.
 */

"use strict";

var ApplicationSystemCalendar = (function() {

    function getLanguageFromHost(host) {
        if (!host)
            host = document.location.host;
        var x = host.split('.');
        if (x.length < 2) return 'fi';
        var domain = x[x.length - 2];
        if (domain.indexOf('opintopolku') > -1) {
            return 'fi';
        } else if (domain.indexOf('studieinfo') > -1) {
            return 'sv';
        } else if (domain.indexOf('studyinfo') > -1) {
            return 'en'
        }
        return 'fi'
    }

    var o = {},
        calendar,
        panel,
        countLisahaut = 0,
        

    calendar = function(options) {

        // throw error if jQuery is not loaded
        if (!window.jQuery) throw "jQuery required";

        // default options for calendar
        var defaultOptions = {
            lang: 'fi',
            selector: '[data-application-system-calendar]',
            deps: {
                stylesheet: '/calendar/css/calendar.css',
                underscore: '/calendar/lib/underscore-min.js'
            },
            calendarResource: '/as/fetchForCalendar'
        };

        // override default options with param opts
        $.extend(true, o, defaultOptions, options);
        o.lang = options.lang ? options.lang : getLanguageFromHost();

        // intitalize language
        ki.i18n.init(o.lang);

        // create container for calendar
        calendar = $(o.selector)
                    .append('<div class="application-system-calendar-container"></div>')
                    .find('.application-system-calendar-container');
        panel = $('<div class="panel-group" id="accordion"></div>');

        // fetch calendar data and parse JSON to calendar view
        var init = function() {
            $.getJSON(o.calendarResource, {uiLang: o.lang}, function(data) {
                var remove = [];
                // split as to periods
                _.each(data, function(item) {
                    if (item.applicationPeriods && item.applicationPeriods.length > 1) {
                        _.each(item.applicationPeriods, function(period) {
                            var clone = _.clone(item);
                            clone.applicationPeriods = [period];
                            clone.nextApplicationPeriodStarts = period.dateRange.startDate;
                            // Set clone as not asOngoing if it's dates do not match
                            clone.asOngoing = (period.dateRange.startDate < new Date() && period.dateRange.endDate > new Date());
                            if(period.dateRange.endDate > new Date()){ // show only upcoming
                                data.push(clone);
                            }
                        });
                        remove.push(item);
                    }
                });

                // remove duplicated items
                data = _.difference(data, remove);
                // group items by month of year
                data = _.groupBy(data, ApplicationSystemCalendar.ApplicationSystemGrouper.group);
                // sort by start date
                data = _.sortBy(data, function(value, key) {
                    return key;
                });

                createCalendar(data, calendar);
            });
        };

        // load given css file
        var loadCss = function(file) {
            $('<link/>', {
                rel: 'stylesheet',
                type: 'text/css',
                href: file
            }).appendTo('head');
        };

        loadCss(o.deps.stylesheet);

        if (!window._) {
            $.getScript(o.deps.underscore, init);
        } else {
            init();
        }
    },

    createCalendar = function(obj, calendar) {
        _.each(obj, function(monthobj, index) {
            countLisahaut = 0;
            var list = $('<ul class="list-unstyled"></ul>');
            var month;

            monthobj = _.sortBy(monthobj, function(month) {
                return ki.Utils.getAsStartDate(month);
            });

            _.each(monthobj, function(item) {
                list.append( createCalendarItem(item) );

                // if item start date is in the past use current time (month)
                var now = new Date();
                var itemStartDate = ki.Utils.getAsStartDate(item) < now ? now : ki.Utils.getAsStartDate(item);
                month = new Date( itemStartDate ).getMonth();
            });
            panel.append( createPanel(ki.i18n.t('month-' + month), list, 'month_' + index) );
        });
        calendar.append(panel);
    },

    createPanel = function(title, content, id) {
        var isCurrentMonth = title === ki.i18n.t('month-' + new Date().getMonth());
        var cssClasses = {a: '', div: ''};
        if (!isCurrentMonth) {
            cssClasses.a = ' collapsed';
        }
        else {
            cssClasses.div = ' in';
        }
        var panel = 
            '<div class="panel panel-default">' + 
                '<a class="panel-heading panel-toggler' + cssClasses.a + '" data-toggle="collapse" data-parent="#accordion" href="#' + id + '">' +
                    '<h4 class="panel-title">' + 
                        title +
                    '</h4>' +
                '</a>' +
                '<div id="' + id + '" class="panel-collapse collapse ' + cssClasses.div + '">' +
                    '<div class="panel-body">' +
                    '</div>' +
            '</div>';

        panel = $(panel);
        panel.find('.panel-body').append(content);
        if(countLisahaut > 0){
            panel.find('.panel-body')
            .append('<a class="showLisahautLink" href="javascript:void(0);" onclick="ki.Utils.showLisahaut();">'
                    +ki.i18n.t('show-lisahaut')+' ('+countLisahaut+')</a>')
        }
        return panel;
    },

    createCalendarItem = function(item) {
        var asStartDate = ki.Utils.getAsStartDate(item),
            asName = ki.Utils.getApplicationSystemName(item),
            listItem = $('<li></li>'),
            row = $('<div class="row"></div>');

        var iconCol = $('<div class="col-xs-2"></div>');
        iconCol.append( createCalendarIconItem( new Date(asStartDate) ));
        row.append( iconCol );

        var infoCol = $('<div class="col-xs-13 col-xs-offset-1"></div>');
        infoCol.append($('<strong/>').text(asName));
        infoCol.append(createApplicationTimeItem(item));
        row.append(infoCol);

        var buttonRow = $('<div class="row"></div>'),
            buttonCol = $('<div class="col-xs-16"></div>');
        buttonCol.append(createApplicationFormButton(item));
        buttonRow.append(buttonCol);

        listItem.addClass(item.varsinainenHaku ? 'varsinainenHaku' : 'notVarsinainenHaku');
        listItem.append(row);
        listItem.append(buttonRow);

        if(!item.varsinainenHaku){
            countLisahaut++;
            listItem.hide();
        }
        return listItem;
    },

    createCalendarIconItem = function(date) {
        var icon = $('<div class="icon-wrapper"></div>');
        icon.append('<div class="icon-day">' + date.getDate() + '</div>');
        icon.append('<div class="icon-month">' + ki.i18n.t('month-' + date.getMonth() + '-abbrv') + '</div>');

        return icon;
    },

    createApplicationTimeItem = function(item) {
        var timeItem;
        if (item && item.applicationPeriods) {
            _.each(item.applicationPeriods, function(period) {
                var start = new Date(period.dateRange.startDate);
                var end = new Date(period.dateRange.endDate);
                timeItem = $('<div></div>');
                timeItem.append('<span>' + ki.Utils.getTimestamp(start) + '</span>');
                timeItem.append('<span>&mdash;</span>');
                timeItem.append('<span>' + ki.Utils.getTimestamp(end) + '</span>');
            });
        }

        return timeItem;
    },

    createApplicationFormButton = function(item) {
        var button = $('<button type="submit" class="btn btn-default pull-right">' + ki.i18n.t('fill-in-form') + '</button>');
        if (!item.asOngoing) {
            button.attr('disabled', 'disabled');
        }
        var action = item.ataruFormKey ?
            "/hakemus/haku/" +  encodeURIComponent(item.id) + "?lang=" + o.lang :
            "/haku-app/lomake/" + encodeURIComponent(item.id);
        var form = $('<form action="' + action + '" target="hakulomake"></form>');
        form.append(button);

        return form;
    };
    
    return {
        calendar: calendar
    };
}());

/*
 *  Group calendar items by start date year and month 
 */
ApplicationSystemCalendar.ApplicationSystemGrouper = (function() {
    var group = function(item) {
        if (!!item.nextApplicationPeriodStarts) {
            var date = new Date(item.nextApplicationPeriodStarts);
        } else {
            var date = new Date(_.first(item.applicationPeriods).dateRange.startDate);
        }

        // if period start date is in the past group it under ongoing month
        var now = new Date();
        date = (date < now) ? now : date;

        // padd one digit months with zero
        var month = ki.Utils.padWithZero(date.getMonth());
        return date.getFullYear() + '' + month;
    }

    return {
        group: group
    };
}());

var ki = ki || {};

/*
 *  A simple i18n object used to translate calendar content
 */
ki.i18n = (function() {
    var i18nResources = {
        fi: {
            'month-0-abbrv': 'Tammi',
            'month-1-abbrv': 'Helmi',
            'month-2-abbrv': 'Maalis',
            'month-3-abbrv': 'Huhti',
            'month-4-abbrv': 'Touko',
            'month-5-abbrv': 'Kesä',
            'month-6-abbrv': 'Heinä',
            'month-7-abbrv': 'Elo',
            'month-8-abbrv': 'Syys',
            'month-9-abbrv': 'Loka',
            'month-10-abbrv': 'Marras',
            'month-11-abbrv': 'Joulu',
            'month-0': 'Tammikuu',
            'month-1': 'Helmikuu',
            'month-2': 'Maaliskuu',
            'month-3': 'Huhtikuu',
            'month-4': 'Toukokuu',
            'month-5': 'Kesäkuu',
            'month-6': 'Heinäkuu',
            'month-7': 'Elokuu',
            'month-8': 'Syyskuu',
            'month-9': 'Lokakuu',
            'month-10': 'Marraskuu',
            'month-11': 'Joulukuu',
            'time-abbrv': 'klo',
            'fill-in-form': 'Täytä hakulomake',
            'show-lisahaut': 'Näytä myös lisähaut'
        },
        sv: {
            'month-0-abbrv': 'Jan',
            'month-1-abbrv': 'Feb',
            'month-2-abbrv': 'Mar',
            'month-3-abbrv': 'Apr',
            'month-4-abbrv': 'Maj',
            'month-5-abbrv': 'Jun',
            'month-6-abbrv': 'Jul',
            'month-7-abbrv': 'Aug',
            'month-8-abbrv': 'Sep',
            'month-9-abbrv': 'Okt',
            'month-10-abbrv': 'Nov',
            'month-11-abbrv': 'Dec',
            'month-0': 'Januari',
            'month-1': 'Februari',
            'month-2': 'Mars',
            'month-3': 'April',
            'month-4': 'Maj',
            'month-5': 'Juni',
            'month-6': 'Juli',
            'month-7': 'Augusti',
            'month-8': 'September',
            'month-9': 'Oktober',
            'month-10': 'November',
            'month-11': 'December',
            'time-abbrv': 'kl',
            'fill-in-form': 'Fyll i ansökan',
            'show-lisahaut': 'Visa även tilläggsansökningarna'
        },
        en: {
            'month-0-abbrv': 'Jan',
            'month-1-abbrv': 'Feb',
            'month-2-abbrv': 'Mar',
            'month-3-abbrv': 'Apr',
            'month-4-abbrv': 'May',
            'month-5-abbrv': 'Jun',
            'month-6-abbrv': 'Jul',
            'month-7-abbrv': 'Aug',
            'month-8-abbrv': 'Sep',
            'month-9-abbrv': 'Oct',
            'month-10-abbrv': 'Nov',
            'month-11-abbrv': 'Dec',
            'month-0': 'January',
            'month-1': 'February',
            'month-2': 'March',
            'month-3': 'April',
            'month-4': 'May',
            'month-5': 'June',
            'month-6': 'July',
            'month-7': 'August',
            'month-8': 'September',
            'month-9': 'October',
            'month-10': 'November',
            'month-11': 'December',
            'time-abbrv': 'at',
            'fill-in-form': 'Fill in application',
            'show-lisahaut': 'Show also additional applications'
        }
    },

    init = function(lang) {
        this.lang = lang;
    },

    translate = function(key) {
        return i18nResources[this.lang][key] ? i18nResources[this.lang][key] : key;
    };

    return {
        init: init,
        t: translate
    };
}());

/*
 *  Common utils used by calendar
 */
ki.Utils = (function() {

    // tells if given date is in the past
    var isInThePast = function(date) {
        var current = new Date().getTime();
        if (date) {
            return date.endDate < current;
        }

        return true;
    },

    // get start date of the give application system
    getAsStartDate = function(as) {
        if (as && as.applicationPeriods && as.applicationPeriods.length > 0) {
            var period = _.first(as.applicationPeriods);
            return period.dateRange ? period.dateRange.startDate : undefined;
        }
    },

    // get application system name with period name
    getApplicationSystemName = function(as) {
        if (as) {
            var result = as.name;
            if (as.applicationPeriods && as.applicationPeriods.length > 0) {
                var period = _.first(as.applicationPeriods);
                result += period.name ? (', ' + period.name) : '';
            }

            return result;
        }
    },

    // get date as a human readble timestamp
    getTimestamp = function(date) {
        if (date) {
            return date.getDate() + 
            '.' + 
            (date.getMonth() + 1) + 
            '.' + 
            date.getFullYear() + 
            ' ' + ki.i18n.t('time-abbrv') + ' ' +
            ki.Utils.padWithZero(date.getHours()) + 
            ':' + 
            ki.Utils.padWithZero(date.getMinutes());
        }
    },

    // padds a one digit number with zero (leading)
    padWithZero = function(number) {
        number = number.toString();
        if (number.length <= 1) {
            return "0" + number;
        } else {
            return number;
        }
    },
    
    showLisahaut = function(item) {
        $('.notVarsinainenHaku').show();
        $('.showLisahautLink').hide();
    };

    return {
        isInThePast: isInThePast,
        getAsStartDate: getAsStartDate,
        getApplicationSystemName: getApplicationSystemName,
        getTimestamp: getTimestamp,
        padWithZero: padWithZero,
        showLisahaut: showLisahaut
    };
}());
