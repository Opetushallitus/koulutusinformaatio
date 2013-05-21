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

/*
var dropDownMenu = {
    // initiate dropDownMenus
    build: function() {
        dropDownMenu.load();
        dropDownMenu.setTriggers();
    },
    // hide (display: none) ul dropdown navigations
    load: function() {
        $('.navigation > li > ul').hide();
        $('.sub-dropdown > ul').hide();
    },
    
    // set listener for dropdown navigations
    setTriggers: function() {

        $('.navigation > li').hover(navigationMouseOver, navigationMouseOut);
        $('.sub-dropdown').hover(dropdownMouseOver, dropdownMouseOut);

        // bring dropdown navigation visible on mouseover
        function navigationMouseOver() {
            if( $(this).children().filter('ul').length !== 0 ) {
                $(this).children().filter('ul').fadeIn(200);
            }
        }

        // hide dropdown navigation on mouseout
        function navigationMouseOut() {
            if( $(this).children().filter('ul').length !== 0 ) {
                $(this).children().filter('ul').fadeOut(200);
            }
        }

        //bring sub-dropdown navigation visible on mouseover
        function dropdownMouseOver() {
            $(this).children().filter('ul').fadeIn(200);
        }

        //hide sub-dropdown navigation on mouseout
        function dropdownMouseOut() {
            $(this).children().filter('ul').fadeOut(200);
        }
    }
};
*/

// load json polyfill if not present
Modernizr.load([
    {
        test: window.JSON,
        nope: 'lib/modernizr/json3.min.js'
    }
]);
