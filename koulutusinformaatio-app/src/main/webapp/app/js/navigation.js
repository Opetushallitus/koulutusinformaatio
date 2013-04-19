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