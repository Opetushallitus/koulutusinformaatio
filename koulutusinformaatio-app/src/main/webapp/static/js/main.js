var key = 'basket';
var basketCount = $.cookie(key) ? JSON.parse($.cookie(key)).length - 1 : 0;

var LanguageCookie = (function() {
	var key = 'i18next';

    return {
        setLanguage: function(language) {
            $.cookie(key, language, {useLocalStorage: false, path: '/'});
        }
    }
}());

$(document).ready(function() {
	var lang = $('html').attr('lang');
	LanguageCookie.setLanguage(lang);

	$('.search form').on('submit', function(event) {
		event.preventDefault();
		var searchWord = $('input[name="search-field"]').val();
		
		if (lang == 'fi') {
			window.location.href = '../app/#/haku/' + searchWord;
		} else {
			window.location.href = '../../app/#/haku/' + searchWord;
		}
	});

	$('.appbasket-count').html(basketCount);
	$('#search-field-frontpage').focus();
});
