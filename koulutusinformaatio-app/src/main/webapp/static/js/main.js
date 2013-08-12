var key = 'basket';
var basketCount = $.cookie(key) ? JSON.parse($.cookie(key)).length - 1 : 0;

$(document).ready(function() {
	$('.search form').on('submit', function(event) {
		event.preventDefault();
		var searchWord = $('input[name="search-field"]').val();
		var lang = $('input[name="lang"]').val();
		if (lang == 'fi') {
			window.location.href = '../app/#/haku/' + searchWord + '?lang=' + lang;
		} else {
			window.location.href = '../../app/#/haku/' + searchWord + '?lang=' + lang;
		}
	});

	$('.appbasket-count').html(basketCount);
});