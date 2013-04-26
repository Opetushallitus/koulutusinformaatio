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
}