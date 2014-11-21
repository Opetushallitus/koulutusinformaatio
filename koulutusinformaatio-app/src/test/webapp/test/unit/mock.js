var i18n = {
	map: {
		'existing-key': 'translation value',
		'title-front-page': 'Etusivu'
	},
	
	t: function(key, options) {
		return this.map[key] || key;
	},

	setLng: function() {},
	
	init: function() {}
};

// avoid page reloads durign tests
mockWindow = {
	location: {
		reload: function() {}
	}
};
