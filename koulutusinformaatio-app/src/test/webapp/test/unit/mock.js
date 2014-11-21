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

angular.module('kiMock', [])

.value('themeFacet', {
	topicFacet: { 
		facetValues: [
			{
				valueName: "themeName", 
				valueId: "themeId",
				childValues: [
					{
						valueName: "topicName",
						valueId: "topicId"
					}
				]
			}
		]
	}
});

