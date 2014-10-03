/**
 *  Application system calendar component.
 *
 *  Requires:
 *  jQuery 1.11
 *  underscore 1.7.0
 *  Bootstrap 3.1.1
 */

"use strict";

var ApplicationSystemCalendar = (function() {

    var selector,
        calendar,
        panel,
        lang,
        stylesheet = '/calendar/css/calendar.css',
        deps = {
            underscore: '/calendar/lib/underscore-min.js'
        },
        calendarResource = '/as/fetchForCalendar',

    calendar = function(selector, language) {

        if (!window.jQuery) throw "jQuery required";

        selector = selector;
        lang = language || 'fi';
        ki.i18n.init(lang);
        calendar = $(selector)
                    .append('<div class="application-system-calendar-container"></div>')
                    .find('.application-system-calendar-container');
        panel = $('<div class="panel-group" id="accordion"></div>');

        var init = function() {
            $.getJSON(calendarResource, {uiLang: lang}, function(data) {
                var remove = [];
                _.each(data, function(item) {
                    if (item.applicationPeriods && item.applicationPeriods.length > 1) {
                        _.each(item.applicationPeriods, function(period) {
                            var clone = _.clone(item);
                            clone.applicationPeriods = [period];
                            clone.nextApplicationPeriodStarts = period.dateRange.startDate;
                            data.push(clone);
                        });
                        remove.push(item);
                    }
                });

                // remove duplicated item
                data = _.difference(data, remove);
                data = _.groupBy(data, ApplicationSystemCalendar.ApplicationSystemGrouper.group);
                data = _.sortBy(data, function(value, key) {
                    return key;
                });
                createCalendar(data);
            });
        };

        var loadCss = function(file) {
            $('<link/>', {
                rel: 'stylesheet',
                type: 'text/css',
                href: file
            }).appendTo('head');
        };

        loadCss(stylesheet);

        if (!window._) {
            $.getScript(deps.underscore, init);
        } else {
            init();
        }
    },

    createCalendar = function(obj) {
        _.each(obj, function(monthobj, index) {
            var list = $('<ul class="list-unstyled"></ul>');
            var month;

            monthobj = _.sortBy(monthobj, function(month) {
                return ki.Utils.getAsStartDate(month);
            });

            _.each(monthobj, function(item) {
                list.append( createCalendarItem(item) );
                month = new Date( ki.Utils.getAsStartDate(item) ).getMonth();
            });

            panel.append( createPanel(ki.i18n.t('month-' + month), list, 'month_' + index) );
        });

        calendar.append(panel);
        calendar.show();
    },

    createPanel = function(title, content, id) {
        var panel = 
            '<div class="panel panel-default">' + 
                '<div class="panel-heading panel-toggler" data-toggle="collapse" data-parent="#accordion" href="#' + id + '">' + 
                    '<h4 class="panel-title">' + 
                        title +
                    '</h4>' +
                '</div>' +
                '<div id="' + id + '" class="panel-collapse collapse">' +
                    '<div class="panel-body">' +
                    '</div>' +
            '</div>';

        panel = $(panel);
        panel.find('.panel-body').append(content);
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
        infoCol.append('<strong>' + asName + '</strong>');
        infoCol.append(createApplicationTimeItem(item));
        row.append(infoCol);

        var buttonRow = $('<div class="row"></div>'),
            buttonCol = $('<div class="col-xs-16"></div>');
        buttonCol.append(createApplicationFormButton(item));
        buttonRow.append(buttonCol);

        listItem.append(row);
        listItem.append(buttonRow);

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
        var form = $('<form action="/haku-app/lomake/' + item.id + '"></form>');
        form.append(button);

        return form;
    };

    
    return {
        calendar: calendar
    };
}());

ApplicationSystemCalendar.ApplicationSystemGrouper = (function() {
    var group = function(item) {
        if (!!item.nextApplicationPeriodStarts) {
            var date = new Date(item.nextApplicationPeriodStarts);
        } else {
            var date = new Date(_.first(item.applicationPeriods).dateRange.startDate);
        }

        return date.getFullYear() + '' + date.getMonth();
    }

    return {
        group: group
    };
}());

var ki = ki || {};

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
            'fill-in-form': 'Täytä hakulomake'
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
            'fill-in-form': 'Fyll i ansökan'
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
            'fill-in-form': 'Fill in application'
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

ki.Utils = (function() {
    var isInThePast = function(date) {
        var current = new Date().getTime();
        if (date) {
            return date.endDate < current;
        }

        return true;
    },

    getAsStartDate = function(as) {
        if (as && as.applicationPeriods && as.applicationPeriods.length > 0) {
            var period = _.first(as.applicationPeriods);
            return period.dateRange ? period.dateRange.startDate : undefined;
        }
    },

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

    padWithZero = function(number) {
        number = number.toString();
        if (number.length <= 1) {
            return "0" + number;
        } else {
            return number;
        }
    };

    return {
        isInThePast: isInThePast,
        getAsStartDate: getAsStartDate,
        getApplicationSystemName: getApplicationSystemName,
        getTimestamp: getTimestamp,
        padWithZero: padWithZero
    };
}());