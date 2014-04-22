angular.module('kiApp.Navigation', []).

/**
 *  Updates the title element of the page.
 */
directive('kiNavigation', function() {
    return {
        restrict: 'A',
        template: '<ul data-tree data-val="navigation" data-level="1"></ul>',
        controller: function($scope, NavigationService) {
            NavigationService.query().then(function(result) {
                $scope.navigation = result.nav;
            });
        }
    }
}).

directive('kiSkipNavigation', function() {
    return {
        restrict: 'A',
        template: '<div id="skip-link"><a href="javascript:void(0)" data-ki-i18n="skip-to-content" data-ng-click="skipNavigation()"></a></div>',
        controller: function($scope, $location, $anchorScroll) {
            $scope.skipNavigation = function() {
                $location.hash('page');
                $anchorScroll();
                angular.element('#search-field').focus();
            }
        }
    }
}).

directive('tree', function ($compile, $rootScope) {
return {
    restrict: 'A',
    terminal: true,
    replace: true,
    scope: {
        val: '=',
        level: '='
    },
    controller: function($scope, Config) {
        $scope.links = {
            textversion: Config.get('textVersionUrl')
        };
    },
    link: function (scope, element, attrs) {

        scope.$watch('val', function(value) {
            if (value) {
                update();
            }
        });

        var update = function() {
            var template = '';
            if (angular.isArray(scope.val)) {
                template +=
                    '<ul class="level-{{level}}';
                if (attrs.level == 1 ) {
                    template += ' menubar root-level" role="menubar" id="nav" >'
                        + '<li class="menu-parent" data-ng-repeat="item in val" title="{{item.title}}" role="menuitem" aria-haspopup="true" tabindex="0">'
                        + '<a data-ng-href="{{item.link}}" tabindex="-1">{{item.title}}</a>' ;
                } else {
                    template += ' menu" role="menu" aria-hidden="true" style="display: none;" >'
                        + '<li class="menu-item" data-ng-repeat="item in val" role="menuitem" tabIndex="-1" >'
                        + '<a data-ng-href="{{item.link}}" tabIndex="-1" >{{item.title}}</a>';
                }
                template += '<ul data-tree data-val="item.subnav"  data-level="level + 1"></ul>';
                template += '</li>';

                if (attrs.level == 1 && !$rootScope.isStudyInfo) {
                    template += '<li class="float-right"><a data-ng-href="{{links.textversion}}" class="action-link"><span data-ki-i18n="link-to-accessible-version"></span></a></li></ul>';
                } else {
                    template += '</ul>';
                }

            }

            var newElement = angular.element(template);
            $compile(newElement)(scope);
            element.replaceWith(newElement);

        };

        element.ready(function(){
            navigationMenubar('#nav');
        });

        /*
         * navigationMenubar() alustaa navigoinnin
         * navigointi on sidottu html ul elementtiin #nav id:llä
         *
         * @param(id string) id on sidottu navigaation juuri html ul elementtiin
         **/
        function navigationMenubar(id){
            this.$id = $(id);

            this.$rootItems = this.$id.children('li'); // jQuerry array of all root-level menu items

            this.$items = this.$id.find('.menu-item');//.not('.separator'); // jQuery array of menu items

            this.$parents = this.$id.find('.menu-parent'); // jQuery array of menu items

            this.$allItems = this.$parents.add(this.$items); // jQuery array of all menu items

            this.$activeItem = null; // jQuery object of the menu item with focus
            //vertikalinen navigointi on asetettu pois päältä
            this.vmenu = false;
            this.bChildOpen = false; // true if child menu is open

            this.keys = {
                tab:    9,
                enter:  13,
                esc:    27,
                space:  32,
                left:   37,
                up:     38,
                right:  39,
                down:   40
            };

            // bind event handlers
            bindHandlers();
        };

        /*
         * bindHandlers() lisää tapahtuman käsittelijät
         * navigaatio menuun
         * */
        function bindHandlers() {

            ///////// bind mouse event handlers //////////
            // bind a handler for the menu items
            this.$items.mouseenter(function(e) {
                $(this).addClass('menu-hover');
                return true;
            });

            // bind a mouseout handler for the menu items
            this.$items.mouseout(function(e) {
                $(this).removeClass('menu-hover');
                return true;
            });

            // bind a mouseenter handler for the menu parents
            this.$parents.mouseenter(function(e) {
                return handleMouseEnter($(this), e);
            });

            // bind a mouseleave handler
            this.$parents.mouseleave(function(e) {
                return handleMouseLeave($(this), e);
            });

            // bind a click handler
            this.$allItems.click(function(e) {
                return handleClick($(this), e);
            });

            //////////// bind key event handlers //////////////////
            // bind a keydown handler
            this.$allItems.keydown(function(e) {
                return handleKeyDown($(this), e);
            });

            // bind a keypress handler
            this.$allItems.keypress(function(e) {
                return handleKeyPress($(this), e);
            });

            // bind a focus handler
            this.$allItems.focus(function(e) {
                return handleFocus($(this), e);
            });

            // bind a blur handler
            this.$allItems.blur(function(e) {
                return handleBlur($(this), e);
            });

            // bind a document click handler
            $(document).click(function(e) {
                return handleDocumentClick(e);
            });

        } // end bindHandlers()

        /*
         * handleMouseEnter() process mouseover tapahtumaa päämenussa menun
         * @param($item object) jQuery object joka laukaisee tapahtuman
         * @param(e object) tapahtuma objekti
         * @return(boolean) return true
         * */
        function handleMouseEnter($item, e) {

            // add hover style
            $item.addClass('menu-hover');
            //sulkee sen menun jossa on focus
            this.$allItems.removeClass('menu-focus');
            this.$allItems.find('ul').not('.root-level').hide().attr('aria-hidden','true');
            // expand the first level submenu
            if ($item.attr('aria-haspopup') == 'true') {
                $item.children('ul').show().attr('aria-hidden', 'false');
                this.bChildOpen = true;
            }
            //e.stopPropagation();
            return true;

        } // end handleMouseEnter()

        /*
         * handleMouseLeave() prosessoin mouseout tapahtumaa päämenussa
         *
         * @param($menu object) on jQuery objekti joka laukaisee tapahtuman
         * @param(e object) tapahtuma objekti
         * @return(boolean) palautaa boolen true/false
         * */
        function handleMouseLeave($menu, e) {

            var $active = $menu.find('.menu-focus');

            $active = $active.add($menu.find('.menu-focus'));

            // Remove hover style
            $menu.removeClass('menu-hover');

            // if any item in the child menu has focus, move focus to the root item
            if ($active.length > 0) {

                this.bChildOpen = false;

                // remove the focus style from the active item
                $active.removeClass('menu-focus');

                // store the active item
                this.$activeItem = $menu;

                // cannot hide items with focus -- move focus to root item
                $menu.focus();
            }

            // hide the child menu
            $menu.children('ul').hide().attr('aria-hidden', 'true');

            //e.stopPropagation();
            return true;

        } // end handleMouseLeave()

        /*
         * handleClick() prosessoi click tapahtumia päämenussa
         *
         * @param($item object) jQuery objekti joka laukaisee tapahtuman
         * @parem(e object) tapahtuma objekti
         * @return(boolea) palautta boolean true/false
         * */
        function handleClick($item, e) {

            var $parentUL = $item.parent();

            if ($parentUL.is('.root-level')) {
                // open the child menu if it is closed
                $item.children('ul').first().show().attr('aria-hidden', 'false');
                this.bChildOpen = true;
            }
            else {
                // remove hover and focus styling
                this.$allItems.removeClass('menu-hover menu-focus');

                // close the menu
                this.$id.find('ul').not('.root-level').hide().attr('aria-hidden','true');
            }

            // if menu item triggers some behavior other than going to a link,
            // would stop propagation and return false
            // e.stopPropagation();
            // return false;
            return true;

        } // end handleClick()

        /*
         * handleFocus() processoin focus tapahtumia menulle
         *
         * @param($item object) jQuery objekti joka laukaisee tapahtuman
         * @param(e object) tapahtuma objekti
         * @return(boolean) palauttaa boolen true/false
         * */
        function handleFocus($item, e) {
            // if activeItem is null, we are getting focus from outside the menu. Store
            // the item that triggered the event
            if (this.$activeItem == null) {
                this.$activeItem = $item;
            }
            else if ($item[0] != this.$activeItem[0]) {
                return true;
            }
            // get the set of jquery objects for all the parent items of the active item
            var $parentItems = this.$activeItem.parentsUntil('div').filter('li');

            // remove focus styling from all other menu items
            this.$allItems.removeClass('menu-focus');
            // remove hove styling form all menus if mouse is over during keyboard navigation
            this.$allItems.removeClass('menu-hover');
            // add styling to the active item
            this.$activeItem.addClass('menu-focus');

            if (this.$activeItem.hasClass('menu-parent')) {
                // for parent items, add .menu-focus directly to the list item
                this.$activeItem.addClass('menu-focus');
            }
            else {
                // for sub-menu items, add .menu-focus to the anchor
                this.$activeItem.find('a').addClass('menu-focus');
            }
            // add styling to all parent items.
            $parentItems.addClass('menu-focus');

            if (this.vmenu == true) {
                // if the bChildOpen is true, open the active item's child menu (if applicable)
                if (this.bChildOpen == true) {

                    var $itemUL = $item.parent();

                    // if the itemUL is a root-level menu and item is a parent item,
                    // show the child menu.
                    if ($itemUL.is('.root-level') && ($item.attr('aria-haspopup') == 'true')) {
                        $item.children('ul').show().attr('aria-hidden', 'false');
                    }
                }
                else {
                    this.vmenu = false;
                }
            }

            return true;

        } // end handleFocus()

        /*
         * hanleBlur() prosessoi fokusen poistumista
         * menu itemistä
         * @param($item object) on jQuery item objecti joka laukaisee tapahtuman
         * @param(e object) tapahtuman käsittelijä objekti
         * @return(boolean) palauttaa true
         * */
        function handleBlur($item, e) {
            $item.removeClass('menu-focus');
            return true;
        } // end handleBlur()

        /*
         * handleKeyDown prosessoi keydown tapahtumia menussa
         *
         * @param($item object) on jQuery item objecti joka laukaisee tapahtuman
         * @param(e object) tapahtuman käsittelijä objekti
         * @return(boolean) palauttaa boolean true/false
         * */
        function handleKeyDown($item, e) {

            if (e.altKey || e.ctrlKey) {
                // Modifier key pressed: Do not process
                return true;
            }

            switch(e.keyCode) {
                case this.keys.tab: {
                    // hide all menu items and update their aria attributes
                    this.$id.find('ul').hide().attr('aria-hidden', 'true');

                    // remove focus styling from all menu items
                    this.$allItems.removeClass('menu-focus');

                    this.$activeItem = null;
                    this.bChildOpen == false;

                    break;
                }
                case this.keys.esc: {

                    var $itemUL = $item.parent();

                    if ($itemUL.is('.root-level')) {
                        // hide the child menu and update the aria attributes
                        $item.children('ul').first().hide().attr('aria-hidden', 'true');
                    }
                    else {

                        // move up one level
                        this.$activeItem = $itemUL.parent();

                        // reset the childOpen flag
                        this.bChildOpen = false;

                        // set focus on the new item
                        this.$activeItem.focus();

                        // hide the active menu and update the aria attributes
                        $itemUL.hide().attr('aria-hidden', 'true');
                    }

                    e.stopPropagation();
                    return false;
                }
                case this.keys.enter:
                case this.keys.space: {

                    window.location = $item.find('a').attr('href');
                    e.stopPropagation();
                    return false;
                }

                case this.keys.left: {

                    if (this.vmenu == true && $itemUL.is('.root-level')) {
                        // If this is a vertical menu and the root-level is active, move
                        // to the previous item in the menu
                        this.$activeItem = moveUp($item);
                    }
                    else {
                        this.$activeItem = moveToPrevious($item);
                    }

                    this.$activeItem.focus();

                    e.stopPropagation();
                    return false;
                }
                case this.keys.right: {

                    if (this.vmenu == true && $itemUL.is('.root-level')) {
                        // If this is a vertical menu and the root-level is active, move
                        // to the next item in the menu
                        this.$activeItem = moveDown($item);
                    }
                    else {
                        this.$activeItem = moveToNext($item);
                    }

                    this.$activeItem.focus();

                    e.stopPropagation();
                    return false;
                }
                case this.keys.up: {

                    if (this.vmenu == true && $itemUL.is('.root-level')) {
                        // If this is a vertical menu and the root-level is active, move
                        // to the previous root-level menu
                        this.$activeItem = moveToPrevious($item);
                    }
                    else {
                        this.$activeItem = moveUp($item);
                    }

                    this.$activeItem.focus();

                    e.stopPropagation();
                    return false;
                }
                case this.keys.down: {

                    if (this.vmenu == true && $itemUL.is('.root-level')) {
                        // If this is a vertical menu and the root-level is active, move
                        // to the next root-level menu
                        this.$activeItem = moveToNext($item);
                    }
                    else {
                        this.$activeItem = moveDown($item);
                    }
                    this.$activeItem.focus();

                    e.stopPropagation();
                    return false;
                }
            } // end switch
            return true;

        } // end handleKeyDown()

        /*
         * moveToNext() liikutaan menussa sen seuraavalle tasolle, joka voi olla seuraava
         * juuri tason menu tai vanhempi menun lapsi. Jos juuri taso on aktiiven taso, valikossa
         * tämä funktion looppaa sen ensimmäiseen tasoon
         *
         * jos menu on horisonttaalinen, ensimmäinen lapsi elementti valitusta menusta on valittuna
         *
         * @param($item object) on aktivoitu menu itemi
         * @return(object) palauttaa seuraava itemin mihin siirrytään tai liikkuminen ei mahdollista
         * */
        function moveToNext($item) {

            var $itemUL = $item.parent(); // $item's containing menu
            var $menuItems = $itemUL.children('li'); // the items in the currently active menu
            var menuNum = $menuItems.length; // the number of items in the active menu
            var menuIndex = $menuItems.index($item); // the items index in its menu
            var $newItem = null;
            var $newItemUL = null;

            if ($itemUL.is('.root-level')) {
                // this is the root level move to next sibling. This will require closing
                // the current child menu and opening the new one.

                if (menuIndex < menuNum-1) { // not the last root menu
                    $newItem = $item.next();
                }
                else { // wrap to first item
                    $newItem = $menuItems.first();
                }

                // close the current child menu (if applicable)
                if ($item.attr('aria-haspopup') == 'true') {

                    var $childMenu = $item.children('ul').first();

                    if ($childMenu.attr('aria-hidden') == 'false') {
                        // hide the child and update aria attributes accordingly
                        $childMenu.hide().attr('aria-hidden', 'true');
                        this.bChildOpen = true;
                    }
                }

                // remove the focus styling from the current menu
                $item.removeClass('menu-focus');

                // open the new child menu (if applicable)
                if (($newItem.attr('aria-haspopup') == 'true') && (this.bChildOpen == true)) {

                    var $childMenu = $newItem.children('ul').first();

                    // open the child and update aria attributes accordingly
                    $childMenu.show().attr('aria-hidden', 'false');

                    if (!this.vmenu) {
                        // select the first item in the child menu
                        $newItem = $childMenu.children('li').first();
                    }

                }
            }
            else {
                // this is not the root level. If there is a child menu to be moved into, do that;
                // otherwise, move to the next root-level menu if there is one
                if ($item.attr('aria-haspopup') == 'true') {

                    var $childMenu = $item.children('ul').first();

                    $newItem = $childMenu.children('li').first();

                    // show the child menu and update its aria attributes
                    $childMenu.show().attr('aria-hidden', 'false');
                    this.bChildOpen = true;
                }
                else {
                    // at deepest level, move to the next root-level menu

                    if (this.vmenu == true) {
                        // do nothing
                        return $item;
                    }

                    var $parentMenus = null;
                    var $rootItem = null;

                    // get list of all parent menus for item, up to the root level
                    $parentMenus = $item.parentsUntil('div').filter('ul').not('.root-level');

                    // hide the current menu and update its aria attributes accordingly
                    $parentMenus.hide().attr('aria-hidden', 'true');

                    // remove the focus styling from the active menu
                    $parentMenus.find('li').removeClass('menu-focus');
                    $parentMenus.last().parent().removeClass('menu-focus');

                    $rootItem = $parentMenus.last().parent(); // the containing root for the menu

                    menuIndex = this.$rootItems.index($rootItem);

                    // if this is not the last root menu item, move to the next one
                    if (menuIndex < this.$rootItems.length-1) {
                        $newItem = $rootItem.next();
                    }
                    else { // loop
                        $newItem = this.$rootItems.first();
                    }

                    if ($newItem.attr('aria-haspopup') == 'true') {
                        var $childMenu = $newItem.children('ul').first();

                        $newItem = $childMenu.children('li').first();

                        // show the child menu and update it's aria attributes
                        $childMenu.show().attr('aria-hidden', 'false');
                        this.bChildOpen = true;
                    }
                }
            }

            return $newItem;
        }

        /*
         * moveToPrevious() liikutaan menussa edelliseen kohtaan
         * tämä voi voi olla edellinen juuri tason menu tai vanhemman lapsi menu.
         * Jos ollaan juuri tasolla ja aktiivinen valinta on ensimmäisenä menussa
         * tämä funktion looppaa valikon viimeiseen itemiin.
         *
         * Jos valikko on horisontaalinen, juuri valittu lapsi elementti on valittu
         *
         * @param($item object) on valittu menu
         * @return(object) palautta itemin mihin liikkutaan tai liikkuminen ei ole mahdollista
         * */
        function moveToPrevious($item) {

            var $itemUL = $item.parent(); // $item's containing menu
            var $menuItems = $itemUL.children('li'); // the items in the currently active menu
            var menuNum = $menuItems.length; // the number of items in the active menu
            var menuIndex = $menuItems.index($item); // the items index in its menu
            var $newItem = null;
            var $newItemUL = null;

            if ($itemUL.is('.root-level')) {
                // this is the root level move to previous sibling. This will require closing
                // the current child menu and opening the new one.

                if (menuIndex > 0) { // not the first root menu
                    $newItem = $item.prev();
                }
                else { // wrap to last item
                    $newItem = $menuItems.last();
                }

                // close the current child menu (if applicable)
                if ($item.attr('aria-haspopup') == 'true') {

                    var $childMenu = $item.children('ul').first();

                    if ($childMenu.attr('aria-hidden') == 'false') {
                        // hide the child and update aria attributes accordingly
                        $childMenu.hide().attr('aria-hidden', 'true');
                        this.bChildOpen = true;
                    }
                }

                // remove the focus styling from the current menu
                $item.removeClass('menu-focus');

                // open the new child menu (if applicable)
                if (($newItem.attr('aria-haspopup') == 'true') && this.bChildOpen == true) {

                    var $childMenu = $newItem.children('ul').first();

                    // open the child and update aria attributes accordingly
                    $childMenu.show().attr('aria-hidden', 'false');

                    if (!this.vmenu) {
                        // select the first item in the child menu
                        $newItem = $childMenu.children('li').first();
                    }
                }
            }
            else {
                // this is not the root level. If there is a parent menu that is not the
                // root menu, move up one level; otherwise, move to first item of the previous
                // root menu.

                var $parentLI = $itemUL.parent();
                var $parentUL = $parentLI.parent();

                // if this is a vertical menu or is not the first child menu
                // of the root-level menu, move up one level.
                if (this.vmenu == true || !$parentUL.is('.root-level')) {

                    $newItem = $itemUL.parent();

                    // hide the active menu and update aria-hidden
                    $itemUL.hide().attr('aria-hidden', 'true');

                    // remove the focus highlight from the $item
                    $item.removeClass('menu-focus');

                    if (this.vmenu == true) {
                        // set a flag so the focus handler does't reopen the menu
                        this.bChildOpen = false;
                    }
                }
                else { // move to previous root-level menu

                    // hide the current menu and update the aria attributes accordingly
                    $itemUL.hide().attr('aria-hidden', 'true');

                    // remove the focus styling from the active menu
                    $item.removeClass('menu-focus');
                    $parentLI.removeClass('menu-focus');

                    menuIndex = this.$rootItems.index($parentLI);

                    if (menuIndex > 0) {
                        // move to the previous root-level menu
                        $newItem = $parentLI.prev();
                    }
                    else { // loop to last root-level menu
                        $newItem = this.$rootItems.last();
                    }

                    // add the focus styling to the new menu
                    $newItem.addClass('menu-focus');

                    if ($newItem.attr('aria-haspopup') == 'true') {
                        var $childMenu = $newItem.children('ul').first();

                        // show the child menu and update it's aria attributes
                        $childMenu.show().attr('aria-hidden', 'false');
                        this.bChildOpen = true;

                        $newItem = $childMenu.children('li').first();
                    }
                }
            }
            return $newItem;
        }

        /*
         * moveDown() valitaan seuraava valinta valikosta
         * jos aktiivien valinta on viimeisenä valikossa tämä funktion looppaa
         * valikon ensimmäiseen valintaan
         *
         * $param($item object) aktiivinen valinta
         *
         * $param(startChr char) [valinnainen] startChar in kirjasin jolle yritetään
         * löytää vastaava pari valikon valinta otsikoista. Jos vastaavuus löytyy kohditus siirtyy
         * kyseiseen valintaan valikon alusta
         *
         * $return(object) palauttaa seuraavan valinnan mihin liikutaan tai liikkuminen ei ole mahdollista
         * */
        function moveDown($item, startChr) {
            var $itemUL = $item.parent(); // $item's containing menu
            var $menuItems = $itemUL.children('li').not('.separator'); // the items in the currently active menu
            var menuNum = $menuItems.length; // the number of items in the active menu
            var menuIndex = $menuItems.index($item); // the items index in its menu
            var $newItem = null;
            var $newItemUL = null;

            if ($itemUL.is('.root-level')) { // this is the root level menu

                if ($item.attr('aria-haspopup') != 'true') {
                    // No child menu to move to
                    return $item;
                }

                // Move to the first item in the child menu
                $newItemUL = $item.children('ul').first();
                $newItem = $newItemUL.children('li').first();

                // make sure the child menu is visible
                $newItemUL.show().attr('aria-hidden', 'false');
                this.bChildOpen = true;

                return $newItem;
            }

            // if $item is not the last item in its menu, move to the next item. If startChr is specified, move
            // to the next item with a title that begins with that character.
            //
            if (startChr) {

                var bMatch = false;
                var curNdx = menuIndex+1;

                // check if the active item was the last one on the list
                if (curNdx == menuNum) {
                    curNdx = 0;
                }

                // Iterate through the menu items (starting from the current item and wrapping) until a match is found
                // or the loop returns to the current menu item
                while (curNdx != menuIndex)  {

                    // Use the first of the two following lines if menu does not contain anchor tags.
                    // Otherwise use the second
                    // var titleChr = $menuItems.eq(curNdx).html().charAt(0);
                    var titleChr = $menuItems.eq(curNdx).find('a').html().charAt(0);

                    if (titleChr.toLowerCase() == startChr) {
                        bMatch = true;
                        break;
                    }

                    curNdx = curNdx+1;

                    if (curNdx == menuNum) {
                        // reached the end of the list, start again at the beginning
                        curNdx = 0;
                    }
                }

                if (bMatch == true) {
                    $newItem = $menuItems.eq(curNdx);

                    // remove the focus styling from the current item
                    $item.removeClass('menu-focus');

                    return $newItem
                }
                else {
                    return $item;
                }
            }
            else {
                if (menuIndex < menuNum-1) {
                    $newItem = $menuItems.eq(menuIndex+1);
                }
                else {
                    $newItem = $menuItems.first();
                }
            }

            // remove the focus styling from the current item
            $item.removeClass('menu-focus');

            return $newItem;
        }

        /*
         * moveUp() valitsee seuraavan menu kohdan menusta.
         * jos aktiivinen kohta on ensimmäisenä menussa, tämä funktion
         * siirtää aktiivisuuden menun viimeiseen kohtaan
         * @param($item object) on aktiivinen menun kohta
         * @return(object) palauttaa seuraavan menun kohdan, johon siirrytään
         * */
        function moveUp($item) {

            var $itemUL = $item.parent(); // $item's containing menu
            var $menuItems = $itemUL.children('li').not('.separator'); // the items in the currently active menu
            var menuNum = $menuItems.length; // the number of items in the active menu
            var menuIndex = $menuItems.index($item); // the items index in its menu
            var $newItem = null;
            var $newItemUL = null;

            if ($itemUL.is('.root-level')) { // this is the root level menu

                if ($item.attr('aria-haspopup') != 'true') {
                    // No child menu to move to
                    return $item;
                }

                // Move to the first item in the child menu
                $newItemUL = $item.children('ul').last();
                $newItem = $newItemUL.children('li').last();

                // make sure the child menu is visible
                $newItemUL.show().attr('aria-hidden', 'false');
                this.bChildOpen = true;

                return $newItem;
            }

            // if $item is not the first item in its menu, move to the previous item
            if (menuIndex > 0) {

                $newItem = $menuItems.eq(menuIndex-1);
            }
            else {
                // loop to top of menu
                $newItem = $menuItems.last();
            }

            // remove the focus styling from the current item
            $item.removeClass('menu-focus');

            return $newItem;
        }


        /*
         * handleKeyPress() prosessoin keydown tapahtumaa menulle
         *
         * Opera selain suorittaa joitain ikkuna komentoja keypress tapahtumina
         * eikä keydown tapahtumina kuten Firefox, Safari ja IE.
         * Tämä tapahtuman käsittelija käyttää keypresses tapahtumia liikuttaessa
         * menussa näppäimistöllä
         *
         * @param($item object) on jQuery objekti joka laukaisee tapahtuman
         * @param(e object) tapahtuman objekti
         * @return(boolean) palautaa boolean arvon true/false
         * */
        function handleKeyPress($item, e) {
            if (e.altKey || e.ctrlKey || e.shiftKey) {
                // Modifier key pressed: Do not process
                return true;
            }

            switch(e.keyCode) {
                case this.keys.tab: {
                    return true;
                }
                case this.keys.esc:
                case this.keys.enter:
                case this.keys.space:
                case this.keys.up:
                case this.keys.down:
                case this.keys.left:
                case this.keys.right: {

                    e.stopPropagation();
                    return false;
                }
                default : {
                    var chr = String.fromCharCode(e.which);

                    this.$activeItem = moveDown($item, chr);
                    this.$activeItem.focus();

                    e.stopPropagation();
                    return false;
                }
            } // end switch
            return true;

        } // end handleKeyPress()

        /*
         * handleDocumentClick() prosessoi documentin click eventtiä
         * sulkeakseen avoimen menun kun käyttäjä klikkaa valikon ulkopuolelle
         * @param(e object) on tapahtuma objekti
         * @return(boolen) palautaa true
         * */
        function handleDocumentClick(e) {

            // get a list of all child menus
            var $childMenus = this.$id.find('ul').not('.root-level');

            // hide the child menus
            $childMenus.hide().attr('aria-hidden', 'true');

            this.$allItems.removeClass('menu-focus');

            this.$activeItem = null;

            // allow the event to propagate
            return true;

        } // end handleDocumentClick()

    }
}
});