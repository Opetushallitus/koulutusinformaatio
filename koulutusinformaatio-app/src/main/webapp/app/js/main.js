var ApplicationBasket = {
    data: {},

    load: function() {
        this.build();
        //setTriggers();
    },

    build: function() {
        var baseUrl = '';
        var serviceUrl = 'mock/ao.json';
        ApplicationBasket.DataService.getLOData(baseUrl + serviceUrl, function(data) {
            ApplicationBasket.data = data;
            ApplicationBasket.setTriggers();
        });
    },

    setTriggers: function() {
        $('#application-basket-link').on('click', function(event) {
            ApplicationBasket.Popup.open();
        });

        $('.popover-continue').on('click', function(event) {
            ApplicationBasket.Popup.Form.submit();
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

    load: function() {

    },

    build: function() {

    },

    open: function() {
        var basketCookie = ApplicationBasket.CookieService.get();
        var asId = ApplicationBasket.getAsId();

        if (basketCookie.length <= 0) {
            window.location = '/haku-app/lomake/' + asId + '/yhteishaku';
        } else if (basketCookie.length > this.maxApplications) {
            popover.show('appbasket-overflow-popup');
        } else {
            popover.show('appbasket-popup');
        }

        //popover.add('title', 'content');
    }
};

ApplicationBasket.Popup.Form = {

    submit: function() {
        var value = $('#application-basket-popup-form').serializeArray();
        var asId = ApplicationBasket.getAsId();

        for (var i = 0; i < value.length; i++) {
            if (value.hasOwnProperty(i)) {
                if (value[i].name == 'appbasket-popup-radio') {
                    if (value[i].value == 'goto') {
                        window.location = '#/muistilista';
                        popover.hide('appbasket-popup');
                    } else if (value[i].value == 'ignore') {
                        window.location = '/haku-app/lomake/' + asId + '/yhteishaku';
                    } else if (value[i].value == 'pick') {
                        this.gotoApplicationForm();
                    }
                }
            }
        }
    },

    gotoApplicationForm: function() {
        var inputProviderName, inputProviderId, inputEducationName, inputEducationId, inputEducationDegree;
        var form = $('<form>', {
            'method': 'post'
        });

        var appData = ApplicationBasket.data;
        for (var i = 0; i < appData.length; i++) {
            form.attr('action', '/haku-app/lomake/' + appData[i].applicationSystemId + '/yhteishaku');


            for (var j = 0; j < appData[i].applicationOptions.length; j++) {
                var ao = appData[i].applicationOptions[j];
                

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
    getLOData: function(url, callback) {
        $.getJSON(url, function(data) {
            callback(data);
        });
    }
};

ApplicationBasket.CookieService = {
    key: 'basket',

    get: function() {
        //console.log($.cookie(this.key));
        return $.cookie(this.key) ? JSON.parse($.cookie(this.key)) : [];
    },

    set: function(value) {
        value = JSON.stringify(value);
        $.cookie(this.key, value, {useLocalStorage: false, path: '/'});
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

    /*
    setTriggers:function(){
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
    */
};

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
        
            popover_close = '<span class="popover-close">&#8203;</span>';
            
            
            /* @todo: Handle "alert" type, where popover must be closed via dedicated button/link with possible callback.
            if (params.type == 'alert')
            {
                if (params.close == false)
                {
                    popover_close = '';
                }
            }
            */
            //content = 'test <a class="popovertest" data-po-add="new" href="#">Test</a>';
            //title = 'Laatikon otsikko';
        
            html =  '<div class="popover-wrapper generated" id="'+id+'" style="z-index:'+(popover.handlers.autoGenCount*100)+';">';
            html +=     popover_close;
            html +=     '<div class="popover">';
            html +=         popover_close;
            html +=         '<div class="popover-header">';
            html +=             title;
            html +=         '</div>';
            html +=         '<div class="popover-content">';
            html +=             content;
            html +=         '</div>';
            html +=     '</div>';
            html += '</div>';
        
            $('#overlay').append(html);
        
            popover.handlers.openPopovers++;
            popover.set.overlay();
            popover.set.size($('#'+id+' .popover'));
            popover.set.position($('#'+id+' .popover'));
        },
        hide:function(id){
            if($('#'+id).length != 0)
            {
                $('#'+id).hide();
                popover.handlers.openPopovers--;
                popover.set.overlay();
            }
        },
        remove:function(target){
            if(target.length != 0 && $(target).length != 0)
            {
                $(target).closest('.popover-wrapper').remove(); // Alternatively .detach()
                popover.handlers.openPopovers--;
                popover.set.overlay();
            }
        },
        show:function(id){
            if($('#'+id).length != 0)
            {
                $('#'+id).show();
                popover.handlers.openPopovers++;
                popover.set.overlay();
                popover.set.size($('#'+id+' .popover'));
                popover.set.position($('#'+id+' .popover'));
            }
        },
        set : {
            active:function(){
                $('#overlay .popover-wrapper').addClass('inactive').last().removeClass('inactive');
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
                if($(target).hasClass('.popover-wrapper'))
                {
                    target = $(target).find('.popover');
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
                if($(target).hasClass('.popover-wrapper'))
                {
                    target = $(target).find('.popover');
                }
                
                content_width = $(target).find('.popover-content').width();
                content_outerwidth = $(target).find('.popover-content').outerWidth(true);
                content_padding = content_outerwidth-content_width;

                // Content area has minimum width
                if (content_outerwidth < 460)
                {
                    content_width = 460-content_padding;
                }
                
                popover_width = content_width-content_padding;
                
                console.log(content_width);
                
                $(target).find('.popover-content').css({'width':content_width+'px'});
                $(target).css({'width':popover_width+'px'});
                
            },
            triggers:function(){
            
                // Remove or hide popover from closing links
                $('body').on('click', '.popover-wrapper .popover-close', function(){
                    
                    // If window was generated dynamically remove, else just hide
                    if($(this).closest('.popover-wrapper').hasClass('generated'))
                    {
                        target = $(this).closest('.popover-wrapper').find('.popover');
                        popover.remove(target);
                    }
                    else
                    {
                        id = $(this).closest('.popover-wrapper').attr('id');
                        popover.hide(id);
                    }
                });
                
                // Generate new popover
                $('body').on('click', '[data-po-add]', function(event){
                    event.preventDefault();
                    popover.add();

                });
                
                // Show already existing popover with id
                $('body').on('click', '[data-po-show]', function(event){
                    //console.log($(this).attr('data-po-show'));
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
    
    //popover.build();

// load json polyfill if not present
Modernizr.load([
{
    test: window.JSON,
    nope: 'lib/modernizr/json3.min.js'
}
]);
