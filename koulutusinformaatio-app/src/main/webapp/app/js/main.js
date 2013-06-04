var ApplicationBasket = {
    data: [],
    baseUrl: '',

    load: function(baseUrl) {
        this.baseUrl = baseUrl;
        this.build();
    },

    build: function() {
        ApplicationBasket.setTriggers();
        var basketCount = ApplicationBasket.CookieService.getCount();
        $('#appbasket-link').find('span').html('(' + basketCount + ')');
    },

    setTriggers: function() {
        $('#apply-link').off('click');
        $('#apply-link').on('click', function(event) {
            ApplicationBasket.Popup.open();
        });

        $('#appbasket-link').on('basketupdate', function(event) {
            $(this).find('span').html('(' + event.count + ')');
        });
    },

    getAsId: function() {
        // assume only one application system is available
        if (this.data && this.data.length > 0) {
            return this.data[0].applicationSystemId;
        }
    }
};

ApplicationBasket.Popup = {
    maxApplications: 5,
    popupId: 0,

    setTriggers: function() {
        $('.popup-dialog-continue').on('click', function(event) {
            ApplicationBasket.Popup.Form.submit();

        });
    },

    open: function() {
        var basketCookie = ApplicationBasket.CookieService.get();
        var asId = ApplicationBasket.getAsId();

        if (basketCookie.length <= 0) {
            window.location = '/haku-app/lomake/' + asId + '/yhteishaku';
        } else if (basketCookie.length > this.maxApplications) {
            var popupContent = {
                description: i18n.t('application-basket-popup-description-overflow'),
                radios: [
                    {'value': 'toappbasket', 'label': i18n.t('application-basket-popup-go-to-basket')},
                    {'value': 'ignore', 'label': i18n.t('application-basket-popup-ignore')}
                ]
            };

            this.popupId = popover.add(i18n.t('application-basket-popup-title'), this.generateContent(popupContent));
        } else {
            var popupContent = {
                description: i18n.t('application-basket-popup-description'),
                radios: [
                    {'value': 'transfer', 'label': i18n.t('application-basket-popup-transfer')},
                    {'value': 'ignore', 'label': i18n.t('application-basket-popup-ignore')},
                    {'value': 'toappbasket', 'label': i18n.t('application-basket-popup-go-to-basket')},
                ]
            };

            this.popupId = popover.add(i18n.t('application-basket-popup-title'), this.generateContent(popupContent));
        }

        this.setTriggers();
    },

    generateContent: function(content) {
        var container = $('<div>');

        var pElem = $('<p>');
        pElem.html(content.description);

        var formElem = $('<form>', {
            'name': 'appbasket-popup-form',
            'id': 'appbasket-popup-form'
        });

        var list = $('<ul>', {
            'class': 'margin-bottom-2'
        });

        for (var index in content.radios) {
            if (content.radios.hasOwnProperty(index)) {
                var radio = content.radios[index];
                list.append( this.createRadio(radio.value, radio.label) );
            }
        }

        formElem.append(list);
        formElem.append(this.createButton('popup-dialog-close', i18n.t('popup-close')));
        formElem.append(this.createButton('primary float-right popup-dialog-continue popup-dialog-close', i18n.t('popup-continue')));

        container.append(pElem);
        container.append(formElem);

        return container.html();
    },

    createRadio: function(value, label) {
        var inputElem = $('<input>', {
            'type': 'radio',
            'name': 'appbasket-popup-radio',
            'id': 'appbasket-popup-' + value,
            'value': value
        });

        var labelElem = $('<label>', {
            'for': 'appbasket-popup-' + value
        });
        labelElem.html(label);

        var listElem = $('<li>');
        listElem.append(inputElem);
        listElem.append(labelElem);

        return listElem;
    },

    createButton: function(clazz, label) {
        var button = $('<button>', {
            'type': 'button',
            'class': clazz
        });

        button.html('<span><span>' + label + '</span></span>');

        return button;
    }
};

ApplicationBasket.Popup.Form = {

    submit: function() {
        var value = $('#appbasket-popup-form').serializeArray();
        var asId = ApplicationBasket.getAsId();

        for (var i = 0; i < value.length; i++) {
            if (value.hasOwnProperty(i) && value[i].name == 'appbasket-popup-radio') {
                if (value[i].value == 'toappbasket') {
                    window.location = '#/muistilista';
                    popover.hide('appbasket-popup');
                } else if (value[i].value == 'ignore') {
                    window.location = '/haku-app/lomake/' + asId + '/yhteishaku';
                } else if (value[i].value == 'transfer') {
                    ApplicationBasket.DataService.getLOData(function(data) {
                        ApplicationBasket.Popup.Form.gotoApplicationForm(data);
                    });
                        
                }
            }
        }
    },

    gotoApplicationForm: function(data) {
        var inputProviderName, inputProviderId, inputEducationName, inputEducationId, inputEducationDegree;
        var form = $('<form>', {
            'method': 'post'
        });
        form.css('display', 'none');

        for (var i = 0; i < data.length; i++) {
            form.attr('action', '/haku-app/lomake/' + data[i].applicationSystemId + '/yhteishaku');


            for (var j = 0; j < data[i].applicationOptions.length; j++) {
                var ao = data[i].applicationOptions[j];
                

                inputProviderName = $('<input>', {
                    'type': 'hidden',
                    'name': 'preference' + (j+1) + '-Opetuspiste',
                    'value': ao.providerName
                });

                inputProviderId = $('<input>', {
                    'type': 'hidden',
                    'name': 'preference' + (j+1) + '-Opetuspiste-id',
                    'value': ao.providerId
                });

                inputEducationName = $('<input>', {
                    'type': 'hidden',
                    'name': 'preference' + (j+1) + '-Koulutus',
                    'value': ao.name
                });

                inputEducationId = $('<input>', {
                    'type': 'hidden',
                    'name': 'preference' + (j+1) + '-Koulutus-id',
                    'value': ao.id
                });

                inputEducationDegree = $('<input>', {
                    'type': 'hidden',
                    'name': 'preference' + (j+1) + '-educationDegree',
                    'value': ao.educationDegree
                });

                form.append(inputProviderName);
                form.append(inputProviderId);
                form.append(inputEducationName);
                form.append(inputEducationId);
                form.append(inputEducationDegree);
            }
        }

        $('body').append(form);
        form.submit();
    }

};

ApplicationBasket.DataService = {
    getLOData: function(callback) {
        var baseUrl = '';
        var serviceUrl = '../basket/items?';
        var aoIds = ApplicationBasket.CookieService.get();

        if (aoIds && aoIds.length > 0) {
            for (var index in aoIds) {
                if (aoIds.hasOwnProperty(index)) {
                    serviceUrl += '&aoId=' + aoIds[index];
                }
            }
        }

        $.getJSON(baseUrl + serviceUrl, function(data) {
            callback(data);
        });
    }
};

ApplicationBasket.CookieService = {
    key: 'basket',

    get: function() {
        return $.cookie(this.key) ? JSON.parse($.cookie(this.key)) : [];
    },

    set: function(value) {
        value = JSON.stringify(value);
        $.cookie(this.key, value, {useLocalStorage: false, path: '/'});
    },

    getCount: function() {
        return $.cookie(this.key) ? JSON.parse($.cookie(this.key)).length : 0;
    }
};


var tabsMenu = {
    build:function(){
        tabsMenu.load();
        //tabsMenu.setTriggers();
    },

    load:function() {
        if(window.location.hash) {

            hash = window.location.hash.substr(1);
            key = "tabsheet";
            hashparams = hash.split(':');
            id = hashparams[1];
            
            if(hash.indexOf(key) != -1) {
                $('.tabs .tab[data-tabs-id="'+hashparams[1]+'"]').each(function() {

                    group = $(this).attr('data-tabs-group');

                    $('.tabsheet[data-tabs-group="'+group+'"]').hide();
                    $('.tabs .tab[data-tabs-group="'+group+'"]').removeClass('current');
                    $('.tabsheet[data-tabs-group="'+group+'"][data-tabs-id="'+id+'"]').show();
                    $('.tabs .tab[data-tabs-group="'+group+'"][data-tabs-id="'+id+'"]').addClass('current');
                });
            }
        }

        $('.tabs .tab a').click(function(event) {
            event.preventDefault();
        });

        $('.tabs .tab').click(function(event) {
            event.preventDefault();
            group = $(this).attr('data-tabs-group');
            id = $(this).attr('data-tabs-id');

            $('.tabs .tab[data-tabs-group="'+group+'"]').removeClass('current');
            $(this).addClass('current');

            $('.tabsheet[data-tabs-group="'+group+'"]').hide();

            $('.tabsheet[data-tabs-group="'+group+'"][data-tabs-id="'+id+'"]').show();
        });
    }
};

/*
var dropDownMenu = {
    // initiate dropDownMenus
    build: function() {
        dropDownMenu.setTriggers();
    },
        
    // set listener for dropdown navigations
    setTriggers: function() {

        $('.navigation > li').hover(navigationMouseOver, navigationMouseOut);

        // bring dropdown navigation visible on mouseover
        function navigationMouseOver() {
            $(this).css('background-color', 'white');
            $(this).children().filter('span').css('color', '#333');
            $(this).children().filter('a').css('color', '#333');
            $(this).children().filter('ul').fadeIn(100);
        }

        // hide dropdown navigation on mouseout
        function navigationMouseOut() {
            $(this).css('background-color', '#06526b');
            $(this).children().filter('span').css('color', 'white');
            $(this).children().filter('a').css('color', 'white');
            $(this).children().filter('ul').fadeOut(100);
        }
    }
};
*/

    //dropDownMenu.build();

    var popover = {
        handlers : {
            openPopovers : 0,
            autoGenCount : 0
        },
        build:function(){
            popover.set.triggers();
        },
        add:function(title, content){
            // Popover auto-generated id
            id = 'poag'+popover.handlers.autoGenCount; 
            popover.handlers.autoGenCount++;

            popover_close = '<span class="popup-dialog-close">&#8203;</span>';

            html =  '<div class="popup-dialog-wrapper generated" id="'+id+'" style="z-index:'+(popover.handlers.autoGenCount+1000)+';">';
            html +=     popover_close;
            html +=     '<div class="popup-dialog">';
            html +=         popover_close;
            html +=         '<div class="popup-dialog-header">';
            html +=             '<h3>' + title + '</h3>';
            html +=         '</div>';
            html +=         '<div class="popup-dialog-content">';
            html +=             content;
            html +=         '</div>';
            html +=     '</div>';
            html += '</div>';
        
            $('#overlay').append(html);
        
            $('#' + id).show();
            popover.handlers.openPopovers++;
            popover.set.overlay();
            popover.set.size($('#'+id+' .popup-dialog'));
            popover.set.position($('#'+id+' .popup-dialog'));

            return id;
        },
        hide:function(id){
            if($('#'+id).length != 0)
            {
                $('#'+id).hide();
                popover.handlers.openPopovers--;
                popover.set.overlay();
            }
        },
        remove:function(id){
            /*
            if(target.length != 0 && $(target).length != 0)
            {
                $(target).closest('.popup-dialog-wrapper').remove(); // Alternatively .detach()
                popover.handlers.openPopovers--;
                popover.set.overlay();
            }*/
            $('#' + id).remove();
            popover.handlers.openPopovers--;
            popover.set.overlay();
        },
        show:function(id){
            if($('#'+id).length != 0)
            {
                $('#'+id).show();
                popover.handlers.openPopovers++;
                popover.set.overlay();
                popover.set.size($('#'+id+' .popup-dialog'));
                popover.set.position($('#'+id+' .popup-dialog'));
            }
        },
        set : {
            active:function(){
                $('#overlay .popup-dialog-wrapper').addClass('inactive').last().removeClass('inactive');
            },
            overlay:function(){
            
                // Show overlay if 1 or more popovers are open/visible
                // Hide overlay if no popovers are open/visible
                if(popover.handlers.openPopovers > 0)
                {
                    $('#overlay').show();
                    
                    popover.set.active();
                }
                else
                {
                    $('#overlay').hide();
                }
            },
            position:function(target){
            
                // Target the actual popover-window
                if($(target).hasClass('.popup-dialog-wrapper'))
                {
                    target = $(target).find('.popup-dialog');
                }
            
                // Get window height and position from top
                //window = $(window);
                window_top = $(window).scrollTop();
                window_height = $(window).height();
                
                // Get wrapper position from top
                wrapper_top = $('#viewport').scrollTop();
                popover_height = $(target).outerHeight(true);
                
                // Center popover if it fits in the window
                if (popover_height < window_height)
                {
                    offset = (window_height-popover_height)/2;
                }
                else
                {
                    offset = 0;
                }
                // Determine popover position
                popover_position = window_top+offset-wrapper_top;
                // console.log(window_top+"+"+offset+"-"+wrapper_top+"="+popover_position);
                target.css({'top':popover_position+'px'});
                
            },
            size:function(target){
                
                // Target the actual popover-window
                if($(target).hasClass('.popup-dialog-wrapper'))
                {
                    target = $(target).find('.popup-dialog');
                }
                
                content_width = $(target).find('.popup-dialog-content').width();
                content_outerwidth = $(target).find('.popup-dialog-content').outerWidth(true);
                content_padding = content_outerwidth-content_width;

                // Content area has minimum width
                if (content_outerwidth < 460)
                {
                    content_width = 460-content_padding;
                }
                
                popover_width = content_width-content_padding;
                
                $(target).find('.popup-dialog-content').css({'width':content_width+'px'});
                $(target).css({'width':popover_width+'px'});
                
            },
            triggers:function(){
            
                // Remove or hide popover from closing links
                $('body').on('click', '.popup-dialog-wrapper .popup-dialog-close', function(){
                    id = $(this).closest('.popup-dialog-wrapper').attr('id');
                    popover.hide(id);
                    target = $(this).closest('.popup-dialog-wrapper').remove(); //.find('.popup-dialog');
                });
                
                // Generate new popover
                $('body').on('click', '[data-po-add]', function(event){
                    event.preventDefault();
                    popover.add();

                });
                
                // Show already existing popover with id
                $('body').on('click', '[data-po-show]', function(event){
                    event.preventDefault();
                    id = $(this).attr('data-po-show');
                    popover.show(id);
                });
                
                // Hide already existing popover with id
                $('body').on('click', '[data-po-hide]', function(event){
                    event.preventDefault();
                    id = $(this).attr('data-po-hide');
                    popover.hide(id);
                });
            }
        }
    }

// load json polyfill if not present
Modernizr.load([
{
    test: window.JSON,
    nope: 'lib/modernizr/json3.min.js'
}
]);
