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
})

.value('parentLO', {
	"id": "123",
	"name": "lo name",
	"provider": {
		"id": "123_provider",
		"name": "provider name",
		"pictureFound": "true"
	},
	"structureImageId": "structure_image_id",
	"additionalProviders": [
		{
			"id": "additional_provider_id",
			"pictureFound": "true"
		}
	],
	"lois": [
		{
	      	"id": "loi_yo_id",
	      	"prerequisite": {
	        	"value": "YO",
	        	"description": "Lukio ja/tai ylioppilastutkinto"
	     	},
	      	"applicationSystems": [
	        	{
	          		"id": "as_yo_id",
	          		"name": "as_yo_name",
	          		"applicationDates": [
	            		{
	              			"startDate": 1411534807344,
	              			"endDate": 1412337614662
	            		}
					],
	        		"applicationOptions": [
	          			{
			              	"id": "ao_yo_id",
			              	"name": "ao_yo_name",
			              	"prerequisite": {
			        			"value": "YO"
			      			},
	          			}
	          		]
	          	}
			]
		},
		{
	      	"id": "loi_pk_id",
	      	"prerequisite": {
	        	"value": "PK",
	        	"description": "Peruskoulu"
	     	},
	      	"applicationSystems": [
	        	{
	          		"id": "as_pk_id",
	          		"name": "as_pk_name",
	          		"applicationDates": [
	            		{
	              			"startDate": 1411534807344,
	              			"endDate": 1412337614662
	            		}
					],
	        		"applicationOptions": [
	          			{
			              	"id": "ao_pk_id",
			              	"name": "ao_pk_name",
			              	"prerequisite": {
			        			"value": "PK"
			      			},
	          			}
	          		]
	          	}
			]
		}
	]
})

.value('appbasketData', [{"applicationSystemId":"1.2.246.562.29.95390561488","applicationSystemName":"Korkeakoulujen yhteishaku kevät 2015","maxApplicationOptions":6,"applicationFormLink":null,"applicationDates":[{"startDate":1426572039988,"endDate":1428580845519}],"applicationOptions":[{"id":"1.2.246.562.20.672318451810","name":"Sosiaalitieteiden tutkinto-ohjelma","educationDegree":"koulutusasteoph2002_72","sora":false,"teachingLanguages":["FI"],"providerName":"Tampereen yliopisto, Yhteiskunta- ja kulttuuritieteiden yksikkö","providerId":"1.2.246.562.10.79281552269","providerLocation":"TAMPERE","qualification":null,"baseEducationRequirement":null,"parent":{"id":"1.2.246.562.17.43308410422","name":"Sosiaalitieteiden tutkinto-ohjelma"},"losRefs":[{"id":"1.2.246.562.17.43308410422","asIds":null,"name":"Sosiaalitieteiden tutkinto-ohjelma","qualifications":["Yhteiskuntatieteiden kandidaatti","Yhteiskuntatieteiden maisteri"],"prerequisite":null,"provider":"Tampereen yliopisto, Yhteiskunta- ja kulttuuritieteiden yksikkö","fieldOfExpertise":null}],"children":[],"attachmentDeliveryDeadline":null,"attachments":null,"exams":[{"type":"Kirjallinen valintakoe","description":"<p>Tiistai 2.6.2015 klo 12.00–16.00</p>\n<p>Kalevantie 4, yliopiston päärakennus</p>\n<h2><strong>Valintakoevaatimukset</strong></h2>\n<p>Valintakoe muodostuu kirjallisuuskokeesta ja aineistokokeesta. Kirjallisuuskokeen vastaukset arvostellaan kaikilta kokeeseen osallistuneilta. Aineistokoe arvostellaan vain kirjallisuuskokeessa parhaiten suoriutuneelta kolmannekselta (1/3), kuitenkin vähintään 150 hakijalta. Valintakokeen lopullinen pistemäärä on kirjallisuuskokeen ja aineistokokeen yhteenlaskettu pistemäärä.</p>\n<p><b>Kirjallisuuskoe</b></p>\n<p>Kirjallisuuskokeen ennakkomateriaalina ovat suomenkieliset sosiaalitieteelliset artikkelit. <b>Artikkelit julkaistaan 9.4.2015 klo 9 Tampereen yliopiston verkkosivuilla osoitteessa <i><a href=\"http://www.uta.fi/yky/opiskelijaksi/opiskelijavalinta/sosiaalitieteiden_valintakoemateriaalit.html\" target=\"_blank\">www.uta.fi/yky/opiskelijaksi/opiskelijavalinta/sosiaalitieteiden_valintakoemateriaalit.html</a></i>. </b>Artikkeleihin perustuvissa kirjallisissa tehtävissä arvioidaan hakijan kykyä ymmärtää ja tulkita tieteellisiä tekstejä sekä hakijan taitoa kirjoittaa loogisesti etenevää, hyvin jäsenneltyä ja oikeakielistä tekstiä. Kirjallisuuskokeen maksimipistemäärä on 30 pistettä.</p>\n<p><b>Aineistokoe</b></p>\n<p>Valintakokeessa jaettavaan materiaaliin perustuvissa kirjallisissa tehtävissä arvioidaan hakijan kykyä analysoida ja tulkita yhteiskunnallisia ilmiöitä sekä soveltaa kirjallisuusosion tarjoamia näkökulmia aineiston tulkinnassa. Aineistokoe edellyttää ennakkomateriaalin tietojen soveltamista. Aineistokokeen maksimipistemäärä on 30 pistettä.</p>","examEvents":[{"start":1433235640579,"end":1433250056438,"description":"","address":{"streetAddress":"Kalevantie 4","streetAddress2":null,"postalCode":"33100","postOffice":"TAMPERE"},"timeIncluded":true}],"scoreLimit":null}],"athleteEducation":false,"aoIdentifier":null,"kaksoistutkinto":false,"vocational":false,"educationCodeUri":"koulutus_733299","type":"korkeakoulu","educationTypeUri":null,"prerequisite":null,"applicationDates":[],"canBeApplied":false,"nextApplicationPeriodStarts":null,"hakutapaUri":null,"applicationFormLink":null,"asId":null,"asName":null,"kotitalous":false,"hakuaikaId":"220207","higherEducation":true}],"asOngoing":false,"nextApplicationPeriodStarts":1426572039988}])

