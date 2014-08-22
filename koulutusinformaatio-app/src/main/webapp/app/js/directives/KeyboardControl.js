angular.module('kiApp.directives.KeyboardControl', [])

/**
 *  keeps focus within the selected elements when navigating with keyboard
 *
 *  useful in modal windows
 */
.directive('kiKeyboardControl', function() {
    return {
        restrict: 'A',
        link: function($scope, element, attrs) {
            var focusableElementsString ="a[href], area[href], input:not(:disabled), select:not(:disabled), textarea:not(:disabled), button:not(:disabled), iframe, object, embed, *[tabindex], *[contenteditable]";
            var focusableElems = $(element).find(focusableElementsString);
            var currentFocus = $(':focus');
            var currentFocusIndex = 0;

            var setFocusToElement = function(elem) {
                elem.focus();
            }

            var setFocusToNext = function(currentFocusIndex) {
                if (currentFocusIndex < focusableElems.length - 1) {
                    setFocusToElement(focusableElems[currentFocusIndex+1]);
                } else {
                    setFocusToElement(focusableElems[0]);
                }       
            }

            var setFocusToPrevious = function(currentFocusIndex) {
                if (currentFocusIndex > 0) {
                    setFocusToElement(focusableElems[currentFocusIndex-1]);
                } else {
                    setFocusToElement(focusableElems[focusableElems.length-1]);
                }
            }

            $(element).on('keydown', function(event) {
                focusableElems = $(element).find(focusableElementsString);
                currentFocus = $(':focus');
                currentFocusIndex = focusableElems.index(currentFocus);

                if (event.keyCode === 9) {
                    if (event.shiftKey) {
                        setFocusToPrevious(currentFocusIndex);
                    } else {
                        setFocusToNext(currentFocusIndex);
                    }

                    return false;
                }
            });
        }
    }
})