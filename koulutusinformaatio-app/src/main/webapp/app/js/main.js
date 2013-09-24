var tabsMenu = {
    build:function(selectedTab){
        tabsMenu.load(selectedTab);
    },

    load:function(selectedTab) {
        if(selectedTab) {
            $('.tabs .tab[data-tabs-id="'+selectedTab+'"]').each(function() {

                group = $(this).attr('data-tabs-group');

                $('.tabsheet[data-tabs-group="'+group+'"]').hide();
                $('.tabs .tab[data-tabs-group="'+group+'"]').removeClass('current');
                $('.tabsheet[data-tabs-group="'+group+'"][data-tabs-id="'+selectedTab+'"]').show();
                $('.tabs .tab[data-tabs-group="'+group+'"][data-tabs-id="'+selectedTab+'"]').addClass('current');
            });
        }

        $('.tabs .tab a').click(function(event) {
            event.preventDefault();
            return false;
        });

        $('.tabs .tab').click(function(event) {
            event.preventDefault();
            group = $(this).attr('data-tabs-group');
            id = $(this).attr('data-tabs-id');

            $('.tabs .tab[data-tabs-group="'+group+'"]').removeClass('current');
            $(this).addClass('current');

            $('.tabsheet[data-tabs-group="'+group+'"]').hide();

            $('.tabsheet[data-tabs-group="'+group+'"][data-tabs-id="'+id+'"]').show();
            return false;
        });
    }
};


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

$.getScript("/static/js/oph-banner.js");
