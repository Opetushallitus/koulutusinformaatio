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

// load json polyfill if not present
Modernizr.load([
{
    test: window.JSON,
    nope: 'lib/modernizr/json3.min.js'
}
]);
