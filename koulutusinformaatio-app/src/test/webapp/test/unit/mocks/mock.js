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


/*
.value('parentLO', {
  "id": "123",
  "name": "Sähkö- ja automaatiotekniikan perustutkinto",
  "provider": {
    "id": "123_provider",
    "name": "Koulutuskeskus Sedu,  Kurikka",
    "applicationSystemIds": [
      "1.2.246.562.5.2013112910480420004764",
      "1.2.246.562.5.2014022711042555034240",
      "1.2.246.562.5.2013080813081926341927",
      "1.2.246.562.29.92175749016",
      "1.2.246.562.29.90697286251"
    ],
    "postalAddress": {
      "streetAddress": "Huovintie 1",
      "streetAddress2": null,
      "postalCode": "61300",
      "postOffice": "KURIKKA"
    },
    "visitingAddress": {
      "streetAddress": "Huovintie 1",
      "streetAddress2": null,
      "postalCode": "61300",
      "postOffice": "KURIKKA"
    },
    "webPage": "http://www.sedu.fi",
    "email": "hakutoimisto@sedu.fi",
    "fax": null,
    "phone": "020 124 6108",
    "description": "<p><strong>Sedu Kurikka</strong> on monialainen ammatillisen koulutuksen kampus, jossa opiskelee yhteensä noin 450 nuorta yhteentoista eri ammattiin (ajoneuvoasentaja, koneistaja ja levyseppähitsaaja, talonrakentaja, sähköasentaja, elektroniikka-asentaja, puuseppä, sisustaja, verhoilija, putkiasentaja, turvallisuusvalvoja ja kokki). </p> <p><strong>Valinnaisiin opintoihin</strong> on kaikilla aloilla mahdollisuus ottaa erilaisia oppilaitoksessa tarjottavia <strong>musiikin opintoja</strong>. Kaikilla aloilla myös mahdollisuus suorittaa kaksi tutkintoa tai lukion aineopintoja yhteistyössä Kurikan lukion kanssa. </p> <p>Opetuspiste sijaitsee Kurikan keskustan ja sen palvelujen välittömässä läheisyydessä. Kurikka on n. 15 000 asukkaan vireä kaupunki, jossa on hyvä asua ja opiskella. Bussiyhteydet naapurikunnista mahdollistavat tarvittaessa päivittäisen kulkemisen kotoa käsin. </p> <p>Oppilaitoksen kanssa samalla tontilla sijaitsee maksuton ja valvottu <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Asuminen\">opiskelija-asuntola </a>80 asukkaalle. Asuntolassa opiskelijoille on iltaisin tarjolla ohjattua vapaa-ajan toimintaa.<strong> </strong></p> <p><strong>Asuntolassa asuvien erityistä tukea tarvitsevien opiskelijoiden on mahdollista hakea <a href=\"http://www.epsospsyk.fi/aake.html%20\">Pikku-AAKEn</a> tuen piiriin.</strong></p> <p>Lisätietoja Kurikan opetuspisteestä <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka\">http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka</a></p> <p> </p> <p><strong>KAKSOISTUTKINTO</strong></p> <p>Voit suorittaa ammatillisen tutkinnon lisäksi myös yo-tutkinnon. Katso lisätietoja</p> <p><a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Hae-opiskelemaan/Hae-opiskelijaksi/Kaksi-tutkintoa\">www.sedu.fi/kaksitutkintoa</a></p> <p> </p> <p><strong>URHEILIJANA HAKEMINEN</strong></p> <p>Urheilupainotteinen ammatillinen perustutkinto</p> <ul> <li><strong>Voit saada 1–3 lisäpistettä urheilullisista saavutuksista, kun haet urheilijalle tarkoitettuun ammatilliseen koulutukseen.</strong> Katso lisätiedot sivulta <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Hae-opiskelemaan/Hae-opiskelijaksi/Urheilupainotteinen-ammatillinen-perustutkinto\">www.sedu.fi/urheilijanhaku</a></li> </ul> <p>Huom! Palauta valmentajan täyttämä lisätietolomake viimeistään 7 vrk haun päättymisen jälkeen osoitteella:</p> <p>Osoite:</p> <p>Koulutuskeskus Sedu, Hakutoimisto, Koulukatu 41, 60100 Seinäjoki</p> <p>Muut yhteystiedot:</p> <p>hakutoimisto@sedu.fi tai p. 020 124 5258</p> <p>Löydät Valmentajatietolomakkeen opintopolku.fi – palvelusta tai Sedun svuilta <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Hae-opiskelemaan/Hae-opiskelijaksi/Urheilupainotteinen-ammatillinen-perustutkinto\">www.sedu.fi/urheilijanhaku</a></p> <p> </p> <p><strong>HARKINTAAN PERUSTUVA VALINTA</strong></p> <ul> <li>Jos haet harkintaan perustuvalla valinnalla, lähetä hakemuksen liitteet hakutoimistoon.</li> </ul> <ul> <li>HUOM! Jokaiseen hakukohteeseen oltava kopioituna omat liitteet. Tulosta mukaan hakemuksesi opintopolusta.</li> </ul> <p>Osoite:</p> <p>Koulutuskeskus Sedu, Hakutoimisto, Koulukatu 41, 60100 Seinäjoki</p> <p>Muut yhteystiedot:</p> <p>hakutoimisto@sedu.fi tai p. 020 124 5258</p> <p>Katso lisätietoja</p> <p><a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Hae-opiskelemaan/Hae-opiskelijaksi/Harkintaan-perustuva-valinta\">www.sedu.fi/harkintaanperustuvavalinta</a></p> <p> </p> <p><strong>ERILLISHAKU</strong></p> <p>Jos olet jo suorittanut jonkun ammatillisen tutkinnon, voit hakea erillishaussa suoraan oppilaitokseen. Hakulomake ja lisätiedot osoitteessa <a href=\"http://www.sedu.fi\">www.sedu.fi</a></p> <p> </p> <p> </p> <p> </p> <p> </p>",
    "healthcare": "<p><a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Tukea-opintoihin/Opiskelijaa-tukevat-palvelut/Opiskelijaterveydenhuolto\">Opiskelijaterveydenhuoltoon</a> kuuluvat terveydenhoitaja-, lääkäri-, hammaslääkäri- ja psykologipalvelut.</p><p>Terveydenhoitajan vastaanotto on joka päivä. Terveydenhoitajan vastaanotolle voit mennä oma-aloitteisesti kaikissa terveyttä ja hyvinvointia koskevissa asioissa. </p><p>Lääkärin, hammaslääkärin ja psykologin vastaanotoille pääsee ajanvarauksella.</p>",
    "accessibility": "<p>Opetuspisteessä toimii <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Opiskelijahuolto-Kurikassa\">opiskelijahuoltoryhmä</a> (OHR), johon kuuluvat terveydenhoitaja, kuraattori, opinto-ohjaajat sekä koulutuspäällikkö. Opiskeluun, vapaa-aikaan, terveyteen tai muihin henkilökohtaisiin asioihin liittyvissä ongelmissa voit aina kääntyä OHR:n jäsenten puoleen.</p> <p><strong>Opintotoimistossa</strong> voit hoitaa opintotukiasiat, koulumatkatukiasiat, opiskelijatodistukset yms. opiskeluun liittyvät asiat.</p> <p>Kurikassa puualalle sekä hotelli-, ravintola- ja cateringalalle haetaan yhteen ryhmään, ja opintojen edetessä ryhmistä muodostetaan erilaisia tiimejä. Jokin tiimeistä on suunniteltu erityistä tukea tarvitseville ja/tai työvaltaisesti opiskeleville pienryhmäläisille. Katso lisää <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Erityisopetus\">erityisen tuen tarjoamisesta</a>. Muissa ryhmissä on tarvittaessa tarjolla erityisopettajien tukea. Opettajien apuna toimii joillakin aloilla ammattiohjaaja. Opetuspisteessä toimii Maijan Paja, jossa tarvittaessa opiskelijat voivat suorittaa opintoja (mm. rästikursseja ja ATTO-opintoja) erityisopettajan pienryhmäohjauksessa.</p> <p>Opiskelijat saavat äänensä kuuluville kaksi kertaa lukuvuodessa kokoontuvissa alakohtaisissa opiskelijahuoltoryhmissä, ja lisäksi opetuspisteen opiskelijafoorumi kokoontuu kahdesti vuodessa. Kurikan opetuspisteen opiskelijat ovat edustettuna Sedun <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Iloa-opintoihin/Opiskelijakunta\">FOKUS-ryhmässä</a>. Koulutetut tutor-opiskelijat toimivat aktiivisesti opetuspisteen esittely- ja markkinointitehtävissä.</p> <p>Oppilaitoksessa toimii maanantai-iltaisin palloilukerho ja kaikkina iltoina on käytettävissä opetuspisteen oma hyvin varustettu kuntosali. Asuntolassa järjestetään erilaista vapaa-ajan ja harrastetoimintaa iltaisin. Lisäksi Kurikan kaupungissa on tarjolla laaja kirjo erilaisia <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Vapaa-ajan-toiminta%20\">vapaa-ajan aktiviteetteja</a>.  </p> <p> </p>",
    "learningEnvironment": "<p>Teorialuokissa ja työsaleissa on ajantasaiset koneet, laitteet ja opetusvälineistö. Opetuspisteessä on kuusi atk-luokkaa. Lähiopetuksen tukena on käytössä Moodle-oppimisympäristö ja työssäoppimisessa eTaitava -järjestelmä. </p><p>Opiskelijat tekevät runsaasti asiakas- ja tilaustöitä, jotka opettavat yrittäjämäistä asennetta ja valmentavat työelämään.  Opiskelijat voivat lisäksi suorittaa opintojaan ja harjoitella yrittäjyyttä osuuskunnassa tai NY-yrityksessä. Katso lisää <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Yrittajyys\">http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Yrittajyys</a> </p><p>Opiskelijoilla on mahdollisuus suorittaa opintoihin sisältyvä työssäoppiminen ulkomailla. Sedu tukee opiskelijaa asumis- ja matkustuskustannuksissa. Katso lisää kansainvälisyydestä <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Kansainvalisyys\">http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Kansainvalisyys</a></p><p>Kaikilla opiskelijoilla Kurikan Sedussa on mahdollisuus sisällyttää opintoihinsa <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Musiikkivalinnaisuus%20\"><strong>musiikin opintoja</strong></a>. Nämä opinnot sisältävät musiikin teorian ja säveltapailun lisäksi henkilökohtaisia soittotunteja ja bändiharjoituksia.</p><p>Elektroniikka-asentajan opinnoissa voit erikoistua esitystekniikkaan ja hankkia erityisosaamista <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Valoa-ja-aanta-oppimaan\">valo-, ääni- ja kuvatekniikkaan </a></p>",
    "dining": "<p>Opiskelijoille tarjotaan koulupäivinä maksuton omassa talossa valmistettu lounas, joka sisältää pääruoan, lämpimät lisäkkeet, tuoresalaatin, ruokajuomat ja leivät sekä useimmiten myös jälkiruoan.</p><p>Aamu- ja iltapäivän kahvitunneilla on ruokalasta ostettavissa edullinen välipala.</p>",
    "livingExpenses": "<p>Opiskelijat ostavat tarvitsemansa oppikirjat ja maksavat osan työvälineistä, jotka jäävät heille itselleen opintojen jälkeen. </p><p>Opiskelijalla tulee olla työnopetuksessa asianmukainen työasu ja työsuojelumääräysten mukaiset jalkineet. Työjalkineet, yhtenäiset haalarit ja muut suojavaatteet hankitaan keskitetysti koulun kautta opintonsa aloittaville opiskelijoille. Työvaatekustannukset vaihtelevat aloittain. </p><p>Sedun opiskelija-asuntolassa asuminen on maksutonta.</p>",
    "living": null,
    "yearClock": null,
    "financingStudies": null,
    "insurances": null,
    "leisureServices": null,
    "social": [
      {
        "name": "facebook",
        "url": "https://www.facebook.com/#!/pages/Koulutuskeskus-Sedu/326925121003?fref=ts"
      },
      {
        "name": "google_plus",
        "url": "https://plus.google.com/u/0/115783246003925085079/posts"
      },
      {
        "name": "muu",
        "url": "http://www.youtube.com/user/KoulutuskeskusSedu"
      }
    ],
    "pictureFound": true,
    "athleteEducation": true,
    "applicationOffice": {
      "name": "Sedu hakutoimisto",
      "phone": "020 124 5258",
      "email": "hakutoimisto@sedu.fi",
      "www": "http://www.sedu.fi",
      "visitingAddress": {
        "streetAddress": "Koulukatu 41",
        "streetAddress2": null,
        "postalCode": "60100",
        "postOffice": "SEINÄJOKI"
      },
      "postalAddress": {
        "streetAddress": "Koulukatu 41",
        "streetAddress2": null,
        "postalCode": "60100",
        "postOffice": "SEINÄJOKI"
      }
    },
    "homeplace": "Kurikka"
  },
  "additionalProviders": [],
  "educationDegree": "32",
  "structure": "<p>Kaikille tutkinnon suorittajille pakolliset osat ovat sähkö- ja automaatiotekniikan perusosaaminen ja sähkö- ja automaatioasennukset. Sähkö- ja automaatiotekniikan koulutusohjelman/osaamisalan sähköasentajan tutkinnossa on lisäksi suoritettava pakollisena osa sähkö- ja energiatekniikka. Sähkö- ja energiatekniikan koulutusohjelman automaatioasentajan tutkinnossa on lisäksi suoritettava pakollisena osa kappaletavara-automaatio tai prosessiautomaatio. Näiden pakollisten tutkinnon osien lisäksi suoritetaan valinnaisia tutkinnon osia, joilla vahvistetaan ammatillista suuntautumista.</p>",
  "structureImageId": "imageId",
  "structureImage": "imageData",
  "accessToFurtherStudies": "<p>Ammatillisista perustutkinnoista sekä ammatti- ja erikoisammattitutkinnoista saa yleisen jatko-opintokelpoisuuden ammattikorkeakouluihin  ja yliopistoihin. Luonteva jatko-opintoväylä on  tekniikan ja liikenteenalan ammattikorkeakoulututkinto, insinööri(AMK). Yliopistossa voi suorittaa esimerkiksi tekniikan kandidaatin ja diplomi-insinöörin tutkinnon. Ammatillisen opettajan  pedagogiset opinnot antavat jatkokoulutusmahdollisuuden ammatillisen opettajan työtehtäviin.</p>",
  "goals": "<p>Sähkö- ja automaatiotekniikan perustutkinnon suorittaneella on sähkö- ja automaatioalan asennus-, huolto- ja kunnossapitotehtävissä tarvittava osaaminen. Suuntautumisensa mukaisesti tutkinnon suorittanut sähköasentaja tekee sähköiseen talotekniikkaan liittyvät sähkö- ja kiinteistöautomaatioasennukset tai sähköverkoston asennukseen, huoltoon ja kunnossapitoon liittyviä tehtäviä. Automaatioasentaja tekee prosessi- tai kappaletavara-automaatioon liittyviä asennus- ja kunnossapitotöitä.</p>",
  "educationDomain": "Tekniikan ja liikenteen ala",
  "stydyDomain": "Sähkö- ja automaatiotekniikka",
  "lois": [
    {
      "id": "1.2.246.562.17.44773411143",
      "selectingDegreeProgram": "<p>Haku <strong>Sähkö- ja automaatiotekniikan perustutkintoon</strong>.<br /><br />Koulutuskeskus Sedussa Kurikassa <strong>Sähkö- ja automaatiotekniikan koulutusohjelma, sähköasentaja</strong></p>",
      "prerequisite": {
        "value": "YO",
        "name": "Lukio ja/tai ylioppilastutkinto",
        "shortName": "YO",
        "description": "Lukio ja/tai ylioppilastutkinto",
        "uri": "pohjakoulutusvaatimustoinenaste_yo"
      },
      "applicationSystems": [
        {
          "id": "1.2.246.562.29.92175749016",
          "name": "Ammatillisen koulutuksen ja lukiokoulutuksen syksyn 2014 yhteishaku",
          "applicationDates": [
            {
              "startDate": 1411534807344,
              "endDate": 1412337614662
            }
          ],
          "applicationOptions": [
            {
              "id": "1.2.246.562.20.61296788732",
              "name": "Sähkö- ja automaatiotekniikan perustutkinto, yo",
              "aoIdentifier": "192",
              "startingQuota": 5,
              "startingQuotaDescription": null,
              "lowestAcceptedScore": 0,
              "lowestAcceptedAverage": 0.0,
              "attachmentDeliveryDeadline": 1412337648569,
              "attachmentDeliveryAddress": {
                "streetAddress": "Sedu hakutoimisto",
                "streetAddress2": "Koulukatu 41",
                "postalCode": "60100",
                "postOffice": "SEINÄJOKI"
              },
              "lastYearApplicantCount": 0,
              "sora": false,
              "educationDegree": "32",
              "teachingLanguages": [
                "FI"
              ],
              "selectionCriteria": "<p>Näin pisteet lasketaan, kun haet lukion jälkeen:</p><h2><strong>Yleinen koulumenestys</strong></h2><ul><li><strong>Saat 1–16 pistettä yleisestä koulumenestyksestä keskiarvon perusteella.</strong> Keskiarvo lasketaan seuraavista oppiaineista: äidinkieli ja kirjallisuus, toinen kotimainen kieli, vieraat kielet, uskonto tai elämänkatsomustieto, historia, yhteiskuntaoppi, matematiikka, fysiikka, kemia, biologia, maantiede, liikunta, terveystieto, musiikki, kuvataide, filosofia ja psykologia.</li></ul><p><strong> </strong></p><table border=\"1\" cellspacing=\"0\" cellpadding=\"10\"><tbody><tr><td valign=\"top\" width=\"381\"><p>Keskiarvo</p></td><td valign=\"top\" width=\"380\"><p>pisteitä</p></td></tr><tr><td width=\"381\"><p>5,50–5,74                           </p></td><td width=\"380\"><p>1</p></td></tr><tr><td width=\"381\"><p>5,75–5,99                           </p></td><td width=\"380\"><p>2</p></td></tr><tr><td width=\"381\"><p>6,00–6,24                     </p></td><td width=\"380\"><p>3</p></td></tr><tr><td width=\"381\"><p>6,25–6,49                           </p></td><td width=\"380\"><p>4</p></td></tr><tr><td width=\"381\"><p>6,50–6,74                           </p></td><td width=\"380\"><p>5</p></td></tr><tr><td width=\"381\"><p>6,75–6,99                           </p></td><td width=\"380\"><p>6</p></td></tr><tr><td width=\"381\"><p>7,00–7,24                           </p></td><td width=\"380\"><p>7</p></td></tr><tr><td width=\"381\"><p>7,25–7,49                           </p></td><td width=\"380\"><p>8</p></td></tr><tr><td width=\"381\"><p>7,50–7,74                           </p></td><td width=\"380\"><p>9</p></td></tr><tr><td width=\"381\"><p>7,75–7,99                           </p></td><td width=\"380\"><p>10</p></td></tr><tr><td width=\"381\"><p>8,00–8,24                           </p></td><td width=\"380\"><p>11</p></td></tr><tr><td width=\"381\"><p>8,25–8,49                           </p></td><td width=\"380\"><p>12</p></td></tr><tr><td width=\"381\"><p>8,50–8,74                           </p></td><td width=\"380\"><p>13</p></td></tr><tr><td width=\"381\"><p>8,75–8,99                           </p></td><td width=\"380\"><p>14</p></td></tr><tr><td width=\"381\"><p>9,00–9,24                           </p></td><td width=\"380\"><p>15</p></td></tr><tr><td width=\"381\"><p>9,25–10,00                         </p></td><td width=\"380\"><p>16</p></td></tr></tbody></table><p><strong>Pisteet lasketaan lukion päättötodistuksesta</strong>. Korotetut arvosanat otetaan huomioon, jos korotuksesta on erillinen, virallinen todistus.</p><p>Huomaathan, että suomalaisesta ylioppilastutkinnosta ei saa pisteitä haettaessa ammatilliseen koulutukseen.<strong>  </strong></p><p>Jos olet saanut todistuksen International Baccalaureate-tutkinnosta (IB), Eurooppalaisesta ylioppilastutkinnosta, European Baccalaureate (EB) tai Reifeprüfung-tutkinnosta (RP) arvosanat muunnetaan vastaamaan lukion päättötodistusta:</p><p> </p><table border=\"1\" cellspacing=\"0\" cellpadding=\"10\"><tbody><tr><td valign=\"top\" width=\"407\"><p>1)</p></td><td valign=\"top\" width=\"407\"><p> </p></td></tr><tr><td width=\"407\"><p>EB-tutkinto                       </p></td><td width=\"407\"><p>Lukio</p></td></tr><tr><td width=\"407\"><p>9-10                                  </p></td><td width=\"407\"><p>10</p></td></tr><tr><td width=\"407\"><p>8                                   </p></td><td width=\"407\"><p>9</p></td></tr><tr><td width=\"407\"><p>7                                      </p></td><td width=\"407\"><p>8</p></td></tr><tr><td width=\"407\"><p>6                                    </p></td><td width=\"407\"><p>7</p></td></tr><tr><td width=\"407\"><p>5                                    </p></td><td width=\"407\"><p>6</p></td></tr><tr><td width=\"407\"><p>3-4                                </p></td><td width=\"407\"><p>5</p></td></tr><tr><td width=\"407\"><p>2)</p></td><td width=\"407\"><p> </p></td></tr><tr><td width=\"407\"><p>IB-tutkinto                        </p></td><td width=\"407\"><p>Lukio</p></td></tr><tr><td width=\"407\"><p>7 (Excellent)                   </p></td><td width=\"407\"><p>10</p></td></tr><tr><td width=\"407\"><p>6 (Very good)                  </p></td><td width=\"407\"><p>9</p></td></tr><tr><td width=\"407\"><p>5 (Good)                         </p></td><td width=\"407\"><p>8</p></td></tr><tr><td width=\"407\"><p>4 (Satisfactory)                </p></td><td width=\"407\"><p>7</p></td></tr><tr><td width=\"407\"><p>3 (Mediocre)                    </p></td><td width=\"407\"><p>6</p></td></tr><tr><td width=\"407\"><p>2 (Poor)                          </p></td><td width=\"407\"><p>5</p></td></tr><tr><td width=\"407\"><p>3)</p></td><td width=\"407\"><p> </p></td></tr><tr><td width=\"407\"><p>RP-tutkinto                       </p></td><td width=\"407\"><p>Lukio</p></td></tr><tr><td width=\"407\"><p>13–15 pistettä                  </p></td><td width=\"407\"><p>10</p></td></tr><tr><td width=\"407\"><p>10–12 pistettä</p></td><td width=\"407\"><p>9</p></td></tr><tr><td width=\"407\"><p>7–9 pistettä</p></td><td width=\"407\"><p>8</p></td></tr><tr><td width=\"407\"><p>5–6 pistettä</p></td><td width=\"407\"><p>7</p></td></tr><tr><td width=\"407\"><p>3–4 pistettä                    </p></td><td width=\"407\"><p>6</p></td></tr><tr><td width=\"407\"><p>1–2 pistettä</p></td><td width=\"407\"><p>5</p></td></tr></tbody></table><p><strong> </strong></p><p>Reifeprüfung-tutkinnon keskiarvo muunnetaan siten, että kokonaispistemäärä jaetaan 60:llä ja muunnetaan taulukon mukaan.</p><p>Korotetut arvosanat otetaan huomioon, jos korotuksesta on erillinen, virallinen todistus.</p><h2><strong>Työkokemus</strong></h2><ul><li><strong>Saat 1–3 pistettä työkokemuksesta tai osallistumisesta työpajatoimintaan tai työharjoitteluun / työkokeiluun</strong>.</li></ul><p>Työkokemukseksi hyväksytään työsuhteessa saatu työkokemus, jonka olet hankkinut peruskoulun jälkeen tai täytettyäsi 16 vuotta. Osa-aikatyössä 150 tunnin mittainen saman työnantajan palveluksessa tehty työ vastaa yhden kuukauden työkokemusta.</p><p>Työharjoittelu / työkokeilu tarkoittaa harjoittelua, johon työ- ja elinkeinotoimisto on sinut ohjannut.</p><p>Oppisopimuskoulutuksesta luetaan työkokemukseksi todistuksissa mainittu työkokemuksen määrä.</p><p>Hakemuksessa otetaan huomioon työkokemus, joka on saatu ennen hakuajan päättymistä. Jos sinulla esimerkiksi on työsopimus ajalle 1.6.-31.12.2013 ja hakuaika on 25.9.-4.10. , niin työkokemukseksi lasketaan ajanjakso 1.6. -4.10. Saat työkokemuksesta yhden pisteen.</p><p> </p><table border=\"1\" cellspacing=\"0\" cellpadding=\"10\"><tbody><tr><td valign=\"top\" width=\"407\"><p>Työkokemuksen pituus</p></td><td valign=\"top\" width=\"407\"><p>pisteitä</p></td></tr><tr><td width=\"407\"><p>3 kk–alle 6 kk                     </p></td><td width=\"407\"><p>1</p></td></tr><tr><td width=\"407\"><p>6 kk–alle 12 kk                   </p></td><td width=\"407\"><p>2</p></td></tr><tr><td width=\"407\"><p>12 kk–                                </p></td><td width=\"407\"><p>3</p></td></tr></tbody></table><h2><strong><br /> Pääsy- ja soveltuvuuskokeet</strong></h2><p>Kaikki hakijat saavat kutsun kokeisiin, jos koulutuksessa järjestetään pääsy- ja soveltuvuuskoe.  Koe voi muodostua eri osista. Oppilaitokset tiedottavat kokeen eri vaiheista. </p><p>Jos olet hakenut useaan koulutukseen, sinut kutsutaan kokeeseen ylimpään hakutoiveeseen, jossa on koe. Koetuloksesi huomioidaan kuitenkin kaikissa samaan valintakoeryhmään kuuluvissa koulutuksissa.</p><p>Valintakoeryhmä tarkoittaa sitä, että monella oppilaitoksella on yksi valintakoe. Silloin yksi koe riittää kaikkiin saman ryhmän koulutuksiin.</p><ul><li><strong>Voit saada 1–10 pistettä hyväksytystä pääsy- ja soveltuvuuskokeesta.</strong> Hylätyn pääsykoetuloksen saanutta hakijaa ei voida valita koulutukseen.</li></ul><ul><li>Huomaathan, että kaikissa koulutuksissa ei ole pääsy- ja soveltuvuuskokeita. Tiedot pääsykokeesta löytyvät tämän sivun alaosasta.</li></ul><ul><li>Pääsykoetulosta käytetään kaikissa vastaavissa koulutuksissa, joihin olet hakenut. Jos olet hakenut useille eri aloille, on mahdollista, että saat kutsun useisiin kokeisiin.</li></ul><h2><strong><br /> Muut pisteet</strong></h2><ul><li><strong>Saat 2 pistettä ammatillisesta koulutuksesta, jonka olet hakiessasi merkinnyt 1. hakutoiveeksi.</strong></li></ul><ul><li><strong>Saat 2 pistettä, jos hakemaasi ammatilliseen koulutukseen ensisijaisesti hakeneista alle 30 prosenttia on samaa sukupuolta kuin sinä.<br /> <br /> </strong></li><li><strong>Voit saada 1–3 lisäpistettä urheilullisista saavutuksista, jos haet urheilijalle tarkoitettuun ammatilliseen koulutukseen.</strong></li></ul><p> </p><h2><strong>Tasapistetilanne</strong></h2><p>Saman pistemäärän saaneet hakijat asetetaan järjestykseen seuraavien perusteiden mukaisesti:</p><ol><li>hakutoivejärjestys</li><li>mahdollinen  pääsy- tai soveltuvuuskokeesta saatava pistemäärä</li><li>yleinen koulumenestys</li></ol><p> Jos pistemäärä on tämän jälkeen edelleen sama, opiskelijat valitaan satunnaisjärjestyksessä. </p><h2><strong>Kielikokeet</strong></h2><p>Saat kutsun kielikokeisiin, jos äidinkielesi on muu kuin opetuskieli eikä kielitaitoasi ole voitu todentaa muilla tavoin, esimerkiksi alla mainituilla todistuksilla. Kokeilla osoitat, että sinulla on riittävät valmiudet opetuskielen suulliseen ja kirjalliseen käyttämiseen. Kokeet ovat kaikille hakijoille samat.</p><p>Jos sinulla on jokin alla mainituista todistuksista, sinua ei kutsuta kielikokeeseen.</p><p><strong> Opetuskielen taitoasi pidetään riittävänä, jos</strong></p><ul><li>sinulla on perusopetuksen päättötodistus, joka on suoritettu vastaanottavan oppilaitoksen opetuskielellä (suomi, ruotsi tai saame).</li><li>olet suorittanut vähintään arvosanalla 7 perusopetuksessa toisen kotimaisen A-kielen oppimäärän. Kielen on oltava vastaanottavan oppilaitoksen opetuskieli.</li><li>olet suorittanut vähintään arvosanalla 7 perusopetuksen suomi tai ruotsi toisena kielenä oppimäärän. Kielen on oltava vastaanottavan oppilaitoksen opetuskieli.</li><li>olet suorittanut lukion oppimäärän tai ylioppilastutkinnon vastaanottavan oppilaitoksen opetuskielellä.</li><li>olet suorittanut lukiokoulutuksen koko oppimäärän jossakin seuraavista äidinkieli ja kirjallisuus-oppiaineen oppimääristä: suomi äidinkielenä, ruotsi äidinkielenä, saame äidinkielenä, suomi toisena kielenä, ruotsi toisena kielenä, suomi saamenkielisille, suomi viittomakielisille. Kielen on oltava vastaanottavan oppilaitoksen opetuskieli.</li><li>olet suorittanut lukiokoulutuksen toisen kotimaisen kielen koko oppimäärän. Kielen on oltava vastaanottavan oppilaitoksen opetuskieli.</li><li>olet suorittanut yleisten kielitutkintojen suomen tai ruotsin kielen tutkinnon kaikki osakokeet vähintään taitotasolla 3. Tai olet suorittanut Valtionhallinnon kielitutkintojen suomen tai ruotsin kielen suullisen ja kirjallisen taidon tutkinnon vähintään taitotasolla tyydyttävä.</li></ul><p>Oppilaitos voi jättää hakijan valitsematta ammatilliseen peruskoulutukseen, jos hänellä ei ole valmiutta opetuskielen suulliseen ja kirjalliseen käyttämiseen sekä ymmärtämiseen.</p><h2><strong>Harkintaan perustuva valinta </strong></h2><p>Oppilaitos voi ottaa opiskelijoita koulutuksiin harkinnan perusteella. Tällöin valinta tehdään valintapistemääristä riippumatta. Yhteen hakukohteeseen voidaan ottaa tällä tavoin enintään 30 prosenttia opiskelijoista. Harkintaan perustuvan valinnan syitä ovat:</p><ul><li>oppimisvaikeudet</li><li>sosiaaliset syyt</li><li>koulutodistusten puuttuminen tai todistusten vertailuvaikeudet.</li></ul><p>Koulutustarpeesi ja edellytyksesi suoriutua opinnoista arvioidaan ja otetaan huomioon, kun valintapistemäärästä poiketaan.</p><p>Huomaathan, että aiempi joustava valinta -menettely ei ole enää käytössä.</p>",
              "soraDescription": null,
              "prerequisite": {
                "value": "YO",
                "name": "Lukio ja/tai ylioppilastutkinto",
                "shortName": "YO",
                "description": "Lukio ja/tai ylioppilastutkinto",
                "uri": "pohjakoulutusvaatimustoinenaste_yo"
              },
              "exams": null,
              "childRefs": [
                {
                  "id": "1.2.246.562.17.83379247477",
                  "losId": "1.2.246.562.5.2013061010185704541592_1.2.246.562.10.85442777687",
                  "name": "Sähkö- ja automaatiotekniikka, sähköasentaja",
                  "qualification": "Sähköasentaja",
                  "prerequisite": {
                    "value": "YO",
                    "name": "Lukio ja/tai ylioppilastutkinto",
                    "shortName": "YO",
                    "description": "Lukio ja/tai ylioppilastutkinto",
                    "uri": "pohjakoulutusvaatimustoinenaste_yo"
                  }
                }
              ],
              "higherEdLOSRefs": [],
              "provider": {
                "id": "1.2.246.562.10.85442777687",
                "name": "Koulutuskeskus Sedu,  Kurikka",
                "applicationSystemIds": [
                  "1.2.246.562.5.2013112910480420004764",
                  "1.2.246.562.5.2014022711042555034240",
                  "1.2.246.562.5.2013080813081926341927",
                  "1.2.246.562.29.92175749016",
                  "1.2.246.562.29.90697286251"
                ],
                "postalAddress": {
                  "streetAddress": "Huovintie 1",
                  "streetAddress2": null,
                  "postalCode": "61300",
                  "postOffice": "KURIKKA"
                },
                "visitingAddress": {
                  "streetAddress": "Huovintie 1",
                  "streetAddress2": null,
                  "postalCode": "61300",
                  "postOffice": "KURIKKA"
                },
                "webPage": "http://www.sedu.fi",
                "email": "hakutoimisto@sedu.fi",
                "fax": null,
                "phone": "020 124 6108",
                "description": "<p><strong>Sedu Kurikka</strong> on monialainen ammatillisen koulutuksen kampus, jossa opiskelee yhteensä noin 450 nuorta yhteentoista eri ammattiin (ajoneuvoasentaja, koneistaja ja levyseppähitsaaja, talonrakentaja, sähköasentaja, elektroniikka-asentaja, puuseppä, sisustaja, verhoilija, putkiasentaja, turvallisuusvalvoja ja kokki). </p> <p><strong>Valinnaisiin opintoihin</strong> on kaikilla aloilla mahdollisuus ottaa erilaisia oppilaitoksessa tarjottavia <strong>musiikin opintoja</strong>. Kaikilla aloilla myös mahdollisuus suorittaa kaksi tutkintoa tai lukion aineopintoja yhteistyössä Kurikan lukion kanssa. </p> <p>Opetuspiste sijaitsee Kurikan keskustan ja sen palvelujen välittömässä läheisyydessä. Kurikka on n. 15 000 asukkaan vireä kaupunki, jossa on hyvä asua ja opiskella. Bussiyhteydet naapurikunnista mahdollistavat tarvittaessa päivittäisen kulkemisen kotoa käsin. </p> <p>Oppilaitoksen kanssa samalla tontilla sijaitsee maksuton ja valvottu <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Asuminen\">opiskelija-asuntola </a>80 asukkaalle. Asuntolassa opiskelijoille on iltaisin tarjolla ohjattua vapaa-ajan toimintaa.<strong> </strong></p> <p><strong>Asuntolassa asuvien erityistä tukea tarvitsevien opiskelijoiden on mahdollista hakea <a href=\"http://www.epsospsyk.fi/aake.html%20\">Pikku-AAKEn</a> tuen piiriin.</strong></p> <p>Lisätietoja Kurikan opetuspisteestä <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka\">http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka</a></p> <p> </p> <p><strong>KAKSOISTUTKINTO</strong></p> <p>Voit suorittaa ammatillisen tutkinnon lisäksi myös yo-tutkinnon. Katso lisätietoja</p> <p><a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Hae-opiskelemaan/Hae-opiskelijaksi/Kaksi-tutkintoa\">www.sedu.fi/kaksitutkintoa</a></p> <p> </p> <p><strong>URHEILIJANA HAKEMINEN</strong></p> <p>Urheilupainotteinen ammatillinen perustutkinto</p> <ul> <li><strong>Voit saada 1–3 lisäpistettä urheilullisista saavutuksista, kun haet urheilijalle tarkoitettuun ammatilliseen koulutukseen.</strong> Katso lisätiedot sivulta <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Hae-opiskelemaan/Hae-opiskelijaksi/Urheilupainotteinen-ammatillinen-perustutkinto\">www.sedu.fi/urheilijanhaku</a></li> </ul> <p>Huom! Palauta valmentajan täyttämä lisätietolomake viimeistään 7 vrk haun päättymisen jälkeen osoitteella:</p> <p>Osoite:</p> <p>Koulutuskeskus Sedu, Hakutoimisto, Koulukatu 41, 60100 Seinäjoki</p> <p>Muut yhteystiedot:</p> <p>hakutoimisto@sedu.fi tai p. 020 124 5258</p> <p>Löydät Valmentajatietolomakkeen opintopolku.fi – palvelusta tai Sedun svuilta <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Hae-opiskelemaan/Hae-opiskelijaksi/Urheilupainotteinen-ammatillinen-perustutkinto\">www.sedu.fi/urheilijanhaku</a></p> <p> </p> <p><strong>HARKINTAAN PERUSTUVA VALINTA</strong></p> <ul> <li>Jos haet harkintaan perustuvalla valinnalla, lähetä hakemuksen liitteet hakutoimistoon.</li> </ul> <ul> <li>HUOM! Jokaiseen hakukohteeseen oltava kopioituna omat liitteet. Tulosta mukaan hakemuksesi opintopolusta.</li> </ul> <p>Osoite:</p> <p>Koulutuskeskus Sedu, Hakutoimisto, Koulukatu 41, 60100 Seinäjoki</p> <p>Muut yhteystiedot:</p> <p>hakutoimisto@sedu.fi tai p. 020 124 5258</p> <p>Katso lisätietoja</p> <p><a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Hae-opiskelemaan/Hae-opiskelijaksi/Harkintaan-perustuva-valinta\">www.sedu.fi/harkintaanperustuvavalinta</a></p> <p> </p> <p><strong>ERILLISHAKU</strong></p> <p>Jos olet jo suorittanut jonkun ammatillisen tutkinnon, voit hakea erillishaussa suoraan oppilaitokseen. Hakulomake ja lisätiedot osoitteessa <a href=\"http://www.sedu.fi\">www.sedu.fi</a></p> <p> </p> <p> </p> <p> </p> <p> </p>",
                "healthcare": "<p><a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Tukea-opintoihin/Opiskelijaa-tukevat-palvelut/Opiskelijaterveydenhuolto\">Opiskelijaterveydenhuoltoon</a> kuuluvat terveydenhoitaja-, lääkäri-, hammaslääkäri- ja psykologipalvelut.</p><p>Terveydenhoitajan vastaanotto on joka päivä. Terveydenhoitajan vastaanotolle voit mennä oma-aloitteisesti kaikissa terveyttä ja hyvinvointia koskevissa asioissa. </p><p>Lääkärin, hammaslääkärin ja psykologin vastaanotoille pääsee ajanvarauksella.</p>",
                "accessibility": "<p>Opetuspisteessä toimii <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Opiskelijahuolto-Kurikassa\">opiskelijahuoltoryhmä</a> (OHR), johon kuuluvat terveydenhoitaja, kuraattori, opinto-ohjaajat sekä koulutuspäällikkö. Opiskeluun, vapaa-aikaan, terveyteen tai muihin henkilökohtaisiin asioihin liittyvissä ongelmissa voit aina kääntyä OHR:n jäsenten puoleen.</p> <p><strong>Opintotoimistossa</strong> voit hoitaa opintotukiasiat, koulumatkatukiasiat, opiskelijatodistukset yms. opiskeluun liittyvät asiat.</p> <p>Kurikassa puualalle sekä hotelli-, ravintola- ja cateringalalle haetaan yhteen ryhmään, ja opintojen edetessä ryhmistä muodostetaan erilaisia tiimejä. Jokin tiimeistä on suunniteltu erityistä tukea tarvitseville ja/tai työvaltaisesti opiskeleville pienryhmäläisille. Katso lisää <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Erityisopetus\">erityisen tuen tarjoamisesta</a>. Muissa ryhmissä on tarvittaessa tarjolla erityisopettajien tukea. Opettajien apuna toimii joillakin aloilla ammattiohjaaja. Opetuspisteessä toimii Maijan Paja, jossa tarvittaessa opiskelijat voivat suorittaa opintoja (mm. rästikursseja ja ATTO-opintoja) erityisopettajan pienryhmäohjauksessa.</p> <p>Opiskelijat saavat äänensä kuuluville kaksi kertaa lukuvuodessa kokoontuvissa alakohtaisissa opiskelijahuoltoryhmissä, ja lisäksi opetuspisteen opiskelijafoorumi kokoontuu kahdesti vuodessa. Kurikan opetuspisteen opiskelijat ovat edustettuna Sedun <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Iloa-opintoihin/Opiskelijakunta\">FOKUS-ryhmässä</a>. Koulutetut tutor-opiskelijat toimivat aktiivisesti opetuspisteen esittely- ja markkinointitehtävissä.</p> <p>Oppilaitoksessa toimii maanantai-iltaisin palloilukerho ja kaikkina iltoina on käytettävissä opetuspisteen oma hyvin varustettu kuntosali. Asuntolassa järjestetään erilaista vapaa-ajan ja harrastetoimintaa iltaisin. Lisäksi Kurikan kaupungissa on tarjolla laaja kirjo erilaisia <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Vapaa-ajan-toiminta%20\">vapaa-ajan aktiviteetteja</a>.  </p> <p> </p>",
                "learningEnvironment": "<p>Teorialuokissa ja työsaleissa on ajantasaiset koneet, laitteet ja opetusvälineistö. Opetuspisteessä on kuusi atk-luokkaa. Lähiopetuksen tukena on käytössä Moodle-oppimisympäristö ja työssäoppimisessa eTaitava -järjestelmä. </p><p>Opiskelijat tekevät runsaasti asiakas- ja tilaustöitä, jotka opettavat yrittäjämäistä asennetta ja valmentavat työelämään.  Opiskelijat voivat lisäksi suorittaa opintojaan ja harjoitella yrittäjyyttä osuuskunnassa tai NY-yrityksessä. Katso lisää <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Yrittajyys\">http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Yrittajyys</a> </p><p>Opiskelijoilla on mahdollisuus suorittaa opintoihin sisältyvä työssäoppiminen ulkomailla. Sedu tukee opiskelijaa asumis- ja matkustuskustannuksissa. Katso lisää kansainvälisyydestä <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Kansainvalisyys\">http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Kansainvalisyys</a></p><p>Kaikilla opiskelijoilla Kurikan Sedussa on mahdollisuus sisällyttää opintoihinsa <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Musiikkivalinnaisuus%20\"><strong>musiikin opintoja</strong></a>. Nämä opinnot sisältävät musiikin teorian ja säveltapailun lisäksi henkilökohtaisia soittotunteja ja bändiharjoituksia.</p><p>Elektroniikka-asentajan opinnoissa voit erikoistua esitystekniikkaan ja hankkia erityisosaamista <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Valoa-ja-aanta-oppimaan\">valo-, ääni- ja kuvatekniikkaan </a></p>",
                "dining": "<p>Opiskelijoille tarjotaan koulupäivinä maksuton omassa talossa valmistettu lounas, joka sisältää pääruoan, lämpimät lisäkkeet, tuoresalaatin, ruokajuomat ja leivät sekä useimmiten myös jälkiruoan.</p><p>Aamu- ja iltapäivän kahvitunneilla on ruokalasta ostettavissa edullinen välipala.</p>",
                "livingExpenses": "<p>Opiskelijat ostavat tarvitsemansa oppikirjat ja maksavat osan työvälineistä, jotka jäävät heille itselleen opintojen jälkeen. </p><p>Opiskelijalla tulee olla työnopetuksessa asianmukainen työasu ja työsuojelumääräysten mukaiset jalkineet. Työjalkineet, yhtenäiset haalarit ja muut suojavaatteet hankitaan keskitetysti koulun kautta opintonsa aloittaville opiskelijoille. Työvaatekustannukset vaihtelevat aloittain. </p><p>Sedun opiskelija-asuntolassa asuminen on maksutonta.</p>",
                "living": null,
                "yearClock": null,
                "financingStudies": null,
                "insurances": null,
                "leisureServices": null,
                "social": [
                  {
                    "name": "facebook",
                    "url": "https://www.facebook.com/#!/pages/Koulutuskeskus-Sedu/326925121003?fref=ts"
                  },
                  {
                    "name": "google_plus",
                    "url": "https://plus.google.com/u/0/115783246003925085079/posts"
                  },
                  {
                    "name": "muu",
                    "url": "http://www.youtube.com/user/KoulutuskeskusSedu"
                  }
                ],
                "pictureFound": true,
                "athleteEducation": true,
                "applicationOffice": {
                  "name": "Sedu hakutoimisto",
                  "phone": "020 124 5258",
                  "email": "hakutoimisto@sedu.fi",
                  "www": "http://www.sedu.fi",
                  "visitingAddress": {
                    "streetAddress": "Koulukatu 41",
                    "streetAddress2": null,
                    "postalCode": "60100",
                    "postOffice": "SEINÄJOKI"
                  },
                  "postalAddress": {
                    "streetAddress": "Koulukatu 41",
                    "streetAddress2": null,
                    "postalCode": "60100",
                    "postOffice": "SEINÄJOKI"
                  }
                },
                "homeplace": "Kurikka"
              },
              "specificApplicationDates": false,
              "applicationStartDate": 1411534807344,
              "applicationEndDate": 1412337614662,
              "applicationPeriodName": "HAKUAIKA",
              "canBeApplied": false,
              "nextApplicationPeriodStarts": null,
              "requiredBaseEducations": [
                "9",
                "7",
                "0"
              ],
              "attachments": null,
              "emphasizedSubjects": null,
              "additionalInfo": "<p><strong>HARKINTAAN PERUSTUVA VALINTA</strong></p><p>Jos haet harkintaan perustuvassa valinnassa, toimita liitteet osoitteeseen</p><p>Sedu hakutoimisto, Koulukatu 41, 60100 Seinäjoki</p><p>HUOM! Jokaiseen hakukohteeseen oltava kopioituna omat liitteet. Tulosta mukaan hakemuksesi opintopolusta.</p><p><strong>ERILLISHAKU</strong></p><p>Ammatillisen tutkinnon aiemmin suorittaneet voivat hakea erillishaussa suoraa oppilaitokseen. Katso ohjeet ja hakulomake www.sedu.fi --&gt; hae opiskelijaksi.</p>",
              "additionalProof": null,
              "overallScoreLimit": null,
              "kaksoistutkinto": true,
              "athleteEducation": false,
              "vocational": true,
              "educationCodeUri": "koulutus_351407",
              "status": null,
              "eligibilityDescription": null,
              "type": "TUTKINTO",
              "educationTypeUri": "et3",
              "hakuaikaId": null,
              "organizationGroups": [],
              "kotitalous": false
            }
          ],
          "asOngoing": false,
          "nextApplicationPeriodStarts": null,
          "status": null,
          "applicationFormLink": null,
          "hakutapa": "01",
          "hakutyyppi": "01"
        }
      ],
      "availableTranslationLanguages": [
        {
          "value": "FI",
          "name": "suomi",
          "shortName": "suomi",
          "description": "suomi",
          "uri": "kieli_fi"
        }
      ]
    },
    {
      "id": "1.2.246.562.5.47892284042",
      "selectingDegreeProgram": "<p><strong>Haku Sähkö- ja automaatiotekniikan perustutkintoon.</strong></p><p>Koulutuskeskus Sedussa Kurikassa <strong>Sähkö- ja automaatiotekniikan osaamisala, sähköasentaja</strong></p>",
      "prerequisite": {
        "value": "PK",
        "name": "Peruskoulu",
        "shortName": "PK",
        "description": "Peruskoulu",
        "uri": "pohjakoulutusvaatimustoinenaste_pk"
      },
      "applicationSystems": [
        {
          "id": "1.2.246.562.29.90697286251",
          "name": "Ammatillisen koulutuksen ja lukiokoulutuksen kevään 2015 yhteishaku",
          "applicationDates": [
            {
              "startDate": 1424757600000,
              "endDate": 1426597220518
            }
          ],
          "applicationOptions": [
            {
              "id": "1.2.246.562.20.88613156095",
              "name": "Sähkö- ja automaatiotekniikan perustutkinto, pk",
              "aoIdentifier": "191",
              "startingQuota": 20,
              "startingQuotaDescription": null,
              "lowestAcceptedScore": 0,
              "lowestAcceptedAverage": 0.0,
              "attachmentDeliveryDeadline": 1426597220518,
              "attachmentDeliveryAddress": {
                "streetAddress": "Sedu hakutoimisto",
                "streetAddress2": "Koulukatu 41",
                "postalCode": "60100",
                "postOffice": "SEINÄJOKI"
              },
              "lastYearApplicantCount": 0,
              "sora": false,
              "educationDegree": "32",
              "teachingLanguages": [
                "FI"
              ],
              "selectionCriteria": "<p>Näin pisteet lasketaan, kun haet peruskoulun jälkeen:</p><h2>Pisteitä ensi kertaa hakeville ja peruskoulun jälkeisiä valmistavia opintoja suorittaneille</h2><ul><li><strong>Saat 8 pistettä, jos sinulla ei ole opiskelupaikkaa ammatilliseen perustutkintoon johtavassa koulutuksessa tai lukiokoulutuksessa<br /></strong><strong> </strong></li><li><strong>Saat 6 pistettä, jos olet suorittanut perusopetuksen oppimäärän samana vuonna kun haet<br /></strong><strong> </strong></li><li><strong>Saat 6 pistettä, jos olet suorittanut jonkun seuraavista: </strong></li><ul><li>kymppiluokan (vähintään 1100 tunnin laajuisen lisäopetuksen)</li><li>ammattistartin (vähintään 20 opintoviikon laajuisen ammatilliseen peruskoulutukseen ohjaavan ja valmistavan koulutuksen)<strong> </strong></li><li>valmentavan ja kuntouttavan opetuksen ja ohjauksen</li><li>maahanmuuttajien ammatilliseen peruskoulutukseen valmistavan koulutuksen</li><li>kotitalousopetuksen / talouskoulun (ei ammatillisena peruskoulutuksena suoritetun)</li><li>kansanopiston vähintään lukuvuoden mittaisen linjan.<br /><strong> </strong></li></ul><li><strong>Saat 2 pistettä ammatillisesta koulutuksesta, jonka olet hakiessasi merkinnyt 1. hakutoiveeksi.<br /></strong></li></ul><h2>Pisteitä sukupuolen perusteella </h2><ul><li><strong>Saat 2 pistettä, jos hakemaasi ammatilliseen koulutukseen ensisijaisesti hakeneista alle 30 prosenttia on samaa sukupuolta kuin sinä.</strong></li></ul><h2>Pisteitä yleisestä koulumenestyksestä</h2><p>Saat 1–16 pistettä yleisestä koulumenestyksestä keskiarvon perusteella. Valinnassa lasketaan keskiarvo seuraavista oppiaineista: äidinkieli ja kirjallisuus, toinen kotimainen kieli, vieraat kielet, uskonto tai elämänkatsomustieto, historia, yhteiskuntaoppi, matematiikka, fysiikka, kemia, biologia, maantieto, liikunta, terveystieto, musiikki, kuvataide, käsityö, kotitalous.<br /> <br />Jos sinulla on arvosana useammasta samaan yhteiseen oppiaineeseen kuuluvasta vähintään kahden vuosiviikkotunnin valinnaisaineesta, lasketaan valinnassa ensin niiden keskiarvo. Pisteesi yleisestä koulumenestyksestä lasketaan siis yhteisen aineen arvosanan ja siihen kuuluvan valinnaisaineen keskiarvosta tai valinnaisaineiden keskiarvosta.</p><p>Yleisen koulumenestyksen pisteet lasketaan peruskoulun päättötodistuksesta. Korotetut arvosanat otetaan huomioon, jos sinulla on korotuksesta todistus.</p><table border=\"1\" cellspacing=\"0\" cellpadding=\"5\" align=\"center\"><tbody><tr><td valign=\"top\" width=\"381\"><p><strong>Keskiarvo</strong></p></td><td valign=\"top\" width=\"380\"><p><strong>Pisteitä</strong></p></td></tr><tr><td valign=\"top\" width=\"381\"><p>5,50–5,74     </p></td><td valign=\"top\" width=\"380\"><p>1</p></td></tr><tr><td valign=\"top\" width=\"381\"><p>5,75<strong>–</strong>5,99      </p></td><td valign=\"top\" width=\"380\"><p>2</p></td></tr><tr><td width=\"381\"><p>6,00<strong>–</strong>6,24                           </p></td><td width=\"380\"><p>3</p></td></tr><tr><td width=\"381\"><p>6,25<strong>–</strong>6,49                           </p></td><td width=\"380\"><p>4</p></td></tr><tr><td width=\"381\"><p>6,50<strong>–</strong>6,74                           </p></td><td width=\"380\"><p>5</p></td></tr><tr><td width=\"381\"><p>6,75<strong>–</strong>6,99                           </p></td><td width=\"380\"><p>6</p></td></tr><tr><td width=\"381\"><p>7,00<strong>–</strong>7,24                           </p></td><td width=\"380\"><p>7</p></td></tr><tr><td width=\"381\"><p>7,25<strong>–</strong>7,49                           </p></td><td width=\"380\"><p>8</p></td></tr><tr><td width=\"381\"><p>7,50<strong>–</strong>7,74                           </p></td><td width=\"380\"><p>9</p></td></tr><tr><td width=\"381\"><p>7,75<strong>–</strong>7,99                           </p></td><td width=\"380\"><p>10</p></td></tr><tr><td width=\"381\"><p>8,00<strong>–</strong>8,24                           </p></td><td width=\"380\"><p>11</p></td></tr><tr><td width=\"381\"><p>8,25<strong>–</strong>8,49                           </p></td><td width=\"380\"><p>12</p></td></tr><tr><td width=\"381\"><p>8,50<strong>–</strong>8,74                           </p></td><td width=\"380\"><p>13</p></td></tr><tr><td width=\"381\"><p>8,75<strong>–</strong>8,99                           </p></td><td width=\"380\"><p>14</p></td></tr><tr><td width=\"381\"><p>9,00<strong>–</strong>9,24                           </p></td><td width=\"380\"><p>15</p></td></tr><tr><td width=\"381\"><p>9,25<strong>–</strong>10,00                         </p></td><td width=\"380\"><p>16</p></td></tr></tbody></table><p> </p><h2><strong>Pisteitä painotettavista arvosanoista</strong></h2><p>Saat 1–8 pistettä painotettavien arvosanojen keskiarvosta. Kaikilla koulutusaloilla otetaan huomioon perusopetuksen päättötodistuksen arvosanat liikunnassa, kuvataiteessa, käsityössä, kotitaloudessa ja musiikissa. Kolmesta parhaasta aineesta lasketaan valinnassa niiden keskiarvo.</p><p>Jos sinulla on arvosana useammasta samaan yhteiseen oppiaineeseen kuuluvasta vähintään kahden vuosiviikkotunnin valinnaisaineesta, lasketaan valinnassa ensin niiden keskiarvo. Pisteesi painotettavista arvosanoista lasketaan siis yhteisen aineen arvosanan ja siihen kuuluvan valinnaisaineen keskiarvosta tai valinnaisaineiden keskiarvosta.</p><p> </p><table border=\"1\" cellspacing=\"0\" cellpadding=\"5\" align=\"center\"><tbody><tr><td valign=\"top\" width=\"407\"><p><strong>Keskiarvo</strong></p></td><td valign=\"top\" width=\"407\"><p><strong>Pisteitä</strong></p></td></tr><tr><td width=\"407\"><p>6,00–6,49                           </p></td><td width=\"407\"><p>1</p></td></tr><tr><td width=\"407\"><p>6,50–6,99                           </p></td><td width=\"407\"><p>2</p></td></tr><tr><td width=\"407\"><p>7,00–7,49                           </p></td><td width=\"407\"><p>3</p></td></tr><tr><td width=\"407\"><p>7,50–7,99                           </p></td><td width=\"407\"><p>4</p></td></tr><tr><td width=\"407\"><p>8,00–8,49                           </p></td><td width=\"407\"><p>5</p></td></tr><tr><td width=\"407\"><p>8,50–8,99                           </p></td><td width=\"407\"><p>6</p></td></tr><tr><td width=\"407\"><p>9,00–9,49                           </p></td><td width=\"407\"><p>7</p></td></tr><tr><td width=\"407\"><p>9,50–10,00                         </p></td><td width=\"407\"><p>8</p></td></tr></tbody></table><p> </p><p>Jos olet suorittanut perusopetuksen oppimäärän aikuiskoulutuksena, voidaan ottaa huomioon perusopetuksen erotodistuksen arvosanat seuraavissa aineissa: liikunta, kuvataide, käsityö, kotitalous ja musiikki, jollei näitä arvosanoja ole päättötodistuksessa.</p><p> </p><h2><strong>Pisteitä työkokemuksesta</strong></h2><ul><li><strong>Saat 1–3 pistettä työkokemuksesta tai osallistumisesta työpajatoimintaan tai työharjoitteluun / työkokeiluun</strong>.</li></ul><p>Työkokemukseksi hyväksytään työsuhteessa saatu työkokemus, jonka olet hankkinut peruskoulun jälkeen tai täytettyäsi 16 vuotta. Osa-aikatyössä 150 tunnin mittainen saman työnantajan palveluksessa tehty työ vastaa yhden kuukauden työkokemusta. </p><p>Työharjoittelu / työkokeilu tarkoittaa harjoittelua, johon työ- ja elinkeinotoimisto on sinut ohjannut. </p><p>Oppisopimuskoulutuksesta luetaan työkokemukseksi todistuksissa mainittu työkokemuksen määrä. </p><p>Hakemuksessa otetaan huomioon työkokemus, joka on saatu ennen hakuajan päättymistä. Jos sinulla esimerkiksi on työsopimus ajalle 1.6.-31.12.2013 ja hakuaika on 25.9.-4.10. , niin työkokemukseksi lasketaan ajanjakso 1.6. -4.10. Saat työkokemuksesta yhden pisteen.</p><p> </p><table border=\"1\" cellspacing=\"0\" cellpadding=\"10\" align=\"center\"><tbody><tr><td valign=\"top\" width=\"407\"><p><strong>Työkokemuksen pituus</strong></p></td><td valign=\"top\" width=\"407\"><p>Pisteitä</p></td></tr><tr><td width=\"407\"><p>3 kk–alle 6 kk                     </p></td><td width=\"407\"><p>1</p></td></tr><tr><td width=\"407\"><p>6 kk–alle 12 kk                   </p></td><td width=\"407\"><p>2</p></td></tr><tr><td width=\"407\"><p>12 kk–                                </p></td><td width=\"407\"><p>3</p></td></tr></tbody></table><p> </p><h2><strong>Lisäpisteet urheilullisista saavutuksista</strong> </h2><ul><li><strong>Voit saada 1–3 lisäpistettä urheilullisista saavutuksista, jos haet urheilijalle tarkoitettuun ammatilliseen koulutukseen.</strong><strong> <br /></strong></li></ul><h2><strong>Pääsy- ja soveltuvuuskokeet</strong><strong> </strong></h2><p>Kaikki hakijat saavat kutsun kokeisiin, jos koulutuksessa järjestetään pääsy- ja soveltuvuuskoe.  Koe voi muodostua eri osista. Oppilaitokset tiedottavat kokeen eri vaiheista.  </p><p>Jos olet hakenut useaan koulutukseen, sinut kutsutaan kokeeseen ylimpään hakutoiveeseen, jossa on koe. Koetuloksesi huomioidaan kuitenkin kaikissa samaan valintakoeryhmään kuuluvissa koulutuksissa.</p><p>Valintakoeryhmä tarkoittaa sitä, että monella oppilaitoksella on yksi valintakoe. Silloin yksi koe riittää kaikkiin saman ryhmän koulutuksiin. </p><ul><li><strong>Voit saada 1–10 pistettä hyväksytystä pääsy- ja soveltuvuuskokeesta.</strong> Hylätyn pääsykoetuloksen saanutta hakijaa ei voida valita koulutukseen. </li><li>Huomaathan, että kaikissa koulutuksissa ei ole pääsy- ja soveltuvuuskokeita. Tiedot pääsykokeesta löytyvät tämän sivun alaosasta.</li><li>Pääsykoetulosta käytetään kaikissa vastaavissa koulutuksissa, joihin olet hakenut. Jos olet hakenut useille eri aloille, on mahdollista, että saat kutsun useisiin kokeisiin.</li></ul><h2><strong>Tasapistetilanne</strong></h2><p>Saman pistemäärän saaneet hakijat asetetaan järjestykseen seuraavien perusteiden mukaisesti:</p><ol><li>hakutoivejärjestys</li><li>mahdollinen  pääsy- tai soveltuvuuskokeesta saatava pistemäärä</li><li>yleinen koulumenestys</li><li>painotettavat arvosanat.</li></ol><p>Jos pistemäärä on tämän jälkeen edelleen sama, opiskelijat valitaan satunnaisjärjestyksessä.<br /> </p><h2><strong>Kielikokeet</strong></h2><p>Saat kutsun kielikokeisiin, jos äidinkielesi on muu kuin opetuskieli eikä kielitaitoasi ole voitu todentaa muilla tavoin, esimerkiksi alla mainituilla todistuksilla. Kokeilla osoitat, että sinulla on riittävät valmiudet opetuskielen suulliseen ja kirjalliseen käyttämiseen. Kokeet ovat kaikille hakijoille samat.</p><p>Jos sinulla on jokin alla mainituista todistuksista, sinua ei kutsuta kielikokeeseen.</p><p><strong> Opetuskielen taitoasi pidetään riittävänä, jos</strong></p><ul><li>sinulla on perusopetuksen päättötodistus, joka on suoritettu vastaanottavan oppilaitoksen opetuskielellä (suomi, ruotsi tai saame).</li><li>olet suorittanut vähintään arvosanalla 7 perusopetuksessa toisen kotimaisen A-kielen oppimäärän. Kielen on oltava vastaanottavan oppilaitoksen opetuskieli.</li><li>olet suorittanut vähintään arvosanalla 7 perusopetuksen suomi tai ruotsi toisena kielenä oppimäärän. Kielen on oltava vastaanottavan oppilaitoksen opetuskieli.</li><li>olet suorittanut lukion oppimäärän tai ylioppilastutkinnon vastaanottavan oppilaitoksen opetuskielellä.</li><li>olet suorittanut lukiokoulutuksen koko oppimäärän jossakin seuraavista äidinkieli ja kirjallisuus-oppiaineen oppimääristä: suomi äidinkielenä, ruotsi äidinkielenä, saame äidinkielenä, suomi toisena kielenä, ruotsi toisena kielenä, suomi saamenkielisille, suomi viittomakielisille. Kielen on oltava vastaanottavan oppilaitoksen opetuskieli.</li><li>olet suorittanut lukiokoulutuksen toisen kotimaisen kielen koko oppimäärän. Kielen on oltava vastaanottavan oppilaitoksen opetuskieli.</li><li>olet suorittanut yleisten kielitutkintojen suomen tai ruotsin kielen tutkinnon kaikki osakokeet vähintään taitotasolla 3. Tai olet suorittanut Valtionhallinnon kielitutkintojen suomen tai ruotsin kielen suullisen ja kirjallisen taidon tutkinnon vähintään taitotasolla tyydyttävä.</li></ul><p>Oppilaitos voi jättää hakijan valitsematta ammatilliseen peruskoulutukseen, jos hänellä ei ole valmiutta opetuskielen suulliseen ja kirjalliseen käyttämiseen sekä ymmärtämiseen.</p><h2><strong>Harkintaan perustuva valinta </strong></h2><p>Oppilaitos voi ottaa opiskelijoita koulutuksiin harkinnan perusteella. Tällöin valinta tehdään valintapistemääristä riippumatta. Yhteen hakukohteeseen voidaan ottaa tällä tavoin enintään 30 prosenttia opiskelijoista. Harkintaan perustuvan valinnan syitä ovat:</p><ul><li>oppimisvaikeudet</li><li>sosiaaliset syyt</li><li>koulutodistusten puuttuminen tai todistusten vertailuvaikeudet. </li></ul><p>Koulutustarpeesi ja edellytyksesi suoriutua opinnoista arvioidaan ja otetaan huomioon, kun valintapistemäärästä poiketaan.</p><p>Huomaathan, että aiempi joustava valinta -menettely ei ole enää käytössä.</p>",
              "soraDescription": null,
              "prerequisite": {
                "value": "PK",
                "name": "Peruskoulu",
                "shortName": "PK",
                "description": "Peruskoulu",
                "uri": "pohjakoulutusvaatimustoinenaste_pk"
              },
              "exams": null,
              "childRefs": [
                {
                  "id": "1.2.246.562.17.70202706886",
                  "losId": "1.2.246.562.5.2013061010185704541592_1.2.246.562.10.85442777687",
                  "name": "Sähkö- ja automaatiotekniikka, sähköasentaja",
                  "qualification": "Sähköasentaja",
                  "prerequisite": {
                    "value": "PK",
                    "name": "Peruskoulu",
                    "shortName": "PK",
                    "description": "Peruskoulu",
                    "uri": "pohjakoulutusvaatimustoinenaste_pk"
                  }
                }
              ],
              "higherEdLOSRefs": [],
              "provider": {
                "id": "1.2.246.562.10.85442777687",
                "name": "Koulutuskeskus Sedu,  Kurikka",
                "applicationSystemIds": [
                  "1.2.246.562.5.2013112910480420004764",
                  "1.2.246.562.5.2014022711042555034240",
                  "1.2.246.562.5.2013080813081926341927",
                  "1.2.246.562.29.92175749016",
                  "1.2.246.562.29.90697286251"
                ],
                "postalAddress": {
                  "streetAddress": "Huovintie 1",
                  "streetAddress2": null,
                  "postalCode": "61300",
                  "postOffice": "KURIKKA"
                },
                "visitingAddress": {
                  "streetAddress": "Huovintie 1",
                  "streetAddress2": null,
                  "postalCode": "61300",
                  "postOffice": "KURIKKA"
                },
                "webPage": "http://www.sedu.fi",
                "email": "hakutoimisto@sedu.fi",
                "fax": null,
                "phone": "020 124 6108",
                "description": "<p><strong>Sedu Kurikka</strong> on monialainen ammatillisen koulutuksen kampus, jossa opiskelee yhteensä noin 450 nuorta yhteentoista eri ammattiin (ajoneuvoasentaja, koneistaja ja levyseppähitsaaja, talonrakentaja, sähköasentaja, elektroniikka-asentaja, puuseppä, sisustaja, verhoilija, putkiasentaja, turvallisuusvalvoja ja kokki). </p> <p><strong>Valinnaisiin opintoihin</strong> on kaikilla aloilla mahdollisuus ottaa erilaisia oppilaitoksessa tarjottavia <strong>musiikin opintoja</strong>. Kaikilla aloilla myös mahdollisuus suorittaa kaksi tutkintoa tai lukion aineopintoja yhteistyössä Kurikan lukion kanssa. </p> <p>Opetuspiste sijaitsee Kurikan keskustan ja sen palvelujen välittömässä läheisyydessä. Kurikka on n. 15 000 asukkaan vireä kaupunki, jossa on hyvä asua ja opiskella. Bussiyhteydet naapurikunnista mahdollistavat tarvittaessa päivittäisen kulkemisen kotoa käsin. </p> <p>Oppilaitoksen kanssa samalla tontilla sijaitsee maksuton ja valvottu <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Asuminen\">opiskelija-asuntola </a>80 asukkaalle. Asuntolassa opiskelijoille on iltaisin tarjolla ohjattua vapaa-ajan toimintaa.<strong> </strong></p> <p><strong>Asuntolassa asuvien erityistä tukea tarvitsevien opiskelijoiden on mahdollista hakea <a href=\"http://www.epsospsyk.fi/aake.html%20\">Pikku-AAKEn</a> tuen piiriin.</strong></p> <p>Lisätietoja Kurikan opetuspisteestä <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka\">http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka</a></p> <p> </p> <p><strong>KAKSOISTUTKINTO</strong></p> <p>Voit suorittaa ammatillisen tutkinnon lisäksi myös yo-tutkinnon. Katso lisätietoja</p> <p><a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Hae-opiskelemaan/Hae-opiskelijaksi/Kaksi-tutkintoa\">www.sedu.fi/kaksitutkintoa</a></p> <p> </p> <p><strong>URHEILIJANA HAKEMINEN</strong></p> <p>Urheilupainotteinen ammatillinen perustutkinto</p> <ul> <li><strong>Voit saada 1–3 lisäpistettä urheilullisista saavutuksista, kun haet urheilijalle tarkoitettuun ammatilliseen koulutukseen.</strong> Katso lisätiedot sivulta <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Hae-opiskelemaan/Hae-opiskelijaksi/Urheilupainotteinen-ammatillinen-perustutkinto\">www.sedu.fi/urheilijanhaku</a></li> </ul> <p>Huom! Palauta valmentajan täyttämä lisätietolomake viimeistään 7 vrk haun päättymisen jälkeen osoitteella:</p> <p>Osoite:</p> <p>Koulutuskeskus Sedu, Hakutoimisto, Koulukatu 41, 60100 Seinäjoki</p> <p>Muut yhteystiedot:</p> <p>hakutoimisto@sedu.fi tai p. 020 124 5258</p> <p>Löydät Valmentajatietolomakkeen opintopolku.fi – palvelusta tai Sedun svuilta <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Hae-opiskelemaan/Hae-opiskelijaksi/Urheilupainotteinen-ammatillinen-perustutkinto\">www.sedu.fi/urheilijanhaku</a></p> <p> </p> <p><strong>HARKINTAAN PERUSTUVA VALINTA</strong></p> <ul> <li>Jos haet harkintaan perustuvalla valinnalla, lähetä hakemuksen liitteet hakutoimistoon.</li> </ul> <ul> <li>HUOM! Jokaiseen hakukohteeseen oltava kopioituna omat liitteet. Tulosta mukaan hakemuksesi opintopolusta.</li> </ul> <p>Osoite:</p> <p>Koulutuskeskus Sedu, Hakutoimisto, Koulukatu 41, 60100 Seinäjoki</p> <p>Muut yhteystiedot:</p> <p>hakutoimisto@sedu.fi tai p. 020 124 5258</p> <p>Katso lisätietoja</p> <p><a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Hae-opiskelemaan/Hae-opiskelijaksi/Harkintaan-perustuva-valinta\">www.sedu.fi/harkintaanperustuvavalinta</a></p> <p> </p> <p><strong>ERILLISHAKU</strong></p> <p>Jos olet jo suorittanut jonkun ammatillisen tutkinnon, voit hakea erillishaussa suoraan oppilaitokseen. Hakulomake ja lisätiedot osoitteessa <a href=\"http://www.sedu.fi\">www.sedu.fi</a></p> <p> </p> <p> </p> <p> </p> <p> </p>",
                "healthcare": "<p><a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Tukea-opintoihin/Opiskelijaa-tukevat-palvelut/Opiskelijaterveydenhuolto\">Opiskelijaterveydenhuoltoon</a> kuuluvat terveydenhoitaja-, lääkäri-, hammaslääkäri- ja psykologipalvelut.</p><p>Terveydenhoitajan vastaanotto on joka päivä. Terveydenhoitajan vastaanotolle voit mennä oma-aloitteisesti kaikissa terveyttä ja hyvinvointia koskevissa asioissa. </p><p>Lääkärin, hammaslääkärin ja psykologin vastaanotoille pääsee ajanvarauksella.</p>",
                "accessibility": "<p>Opetuspisteessä toimii <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Opiskelijahuolto-Kurikassa\">opiskelijahuoltoryhmä</a> (OHR), johon kuuluvat terveydenhoitaja, kuraattori, opinto-ohjaajat sekä koulutuspäällikkö. Opiskeluun, vapaa-aikaan, terveyteen tai muihin henkilökohtaisiin asioihin liittyvissä ongelmissa voit aina kääntyä OHR:n jäsenten puoleen.</p> <p><strong>Opintotoimistossa</strong> voit hoitaa opintotukiasiat, koulumatkatukiasiat, opiskelijatodistukset yms. opiskeluun liittyvät asiat.</p> <p>Kurikassa puualalle sekä hotelli-, ravintola- ja cateringalalle haetaan yhteen ryhmään, ja opintojen edetessä ryhmistä muodostetaan erilaisia tiimejä. Jokin tiimeistä on suunniteltu erityistä tukea tarvitseville ja/tai työvaltaisesti opiskeleville pienryhmäläisille. Katso lisää <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Erityisopetus\">erityisen tuen tarjoamisesta</a>. Muissa ryhmissä on tarvittaessa tarjolla erityisopettajien tukea. Opettajien apuna toimii joillakin aloilla ammattiohjaaja. Opetuspisteessä toimii Maijan Paja, jossa tarvittaessa opiskelijat voivat suorittaa opintoja (mm. rästikursseja ja ATTO-opintoja) erityisopettajan pienryhmäohjauksessa.</p> <p>Opiskelijat saavat äänensä kuuluville kaksi kertaa lukuvuodessa kokoontuvissa alakohtaisissa opiskelijahuoltoryhmissä, ja lisäksi opetuspisteen opiskelijafoorumi kokoontuu kahdesti vuodessa. Kurikan opetuspisteen opiskelijat ovat edustettuna Sedun <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Iloa-opintoihin/Opiskelijakunta\">FOKUS-ryhmässä</a>. Koulutetut tutor-opiskelijat toimivat aktiivisesti opetuspisteen esittely- ja markkinointitehtävissä.</p> <p>Oppilaitoksessa toimii maanantai-iltaisin palloilukerho ja kaikkina iltoina on käytettävissä opetuspisteen oma hyvin varustettu kuntosali. Asuntolassa järjestetään erilaista vapaa-ajan ja harrastetoimintaa iltaisin. Lisäksi Kurikan kaupungissa on tarjolla laaja kirjo erilaisia <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Vapaa-ajan-toiminta%20\">vapaa-ajan aktiviteetteja</a>.  </p> <p> </p>",
                "learningEnvironment": "<p>Teorialuokissa ja työsaleissa on ajantasaiset koneet, laitteet ja opetusvälineistö. Opetuspisteessä on kuusi atk-luokkaa. Lähiopetuksen tukena on käytössä Moodle-oppimisympäristö ja työssäoppimisessa eTaitava -järjestelmä. </p><p>Opiskelijat tekevät runsaasti asiakas- ja tilaustöitä, jotka opettavat yrittäjämäistä asennetta ja valmentavat työelämään.  Opiskelijat voivat lisäksi suorittaa opintojaan ja harjoitella yrittäjyyttä osuuskunnassa tai NY-yrityksessä. Katso lisää <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Yrittajyys\">http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Yrittajyys</a> </p><p>Opiskelijoilla on mahdollisuus suorittaa opintoihin sisältyvä työssäoppiminen ulkomailla. Sedu tukee opiskelijaa asumis- ja matkustuskustannuksissa. Katso lisää kansainvälisyydestä <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Kansainvalisyys\">http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Kansainvalisyys</a></p><p>Kaikilla opiskelijoilla Kurikan Sedussa on mahdollisuus sisällyttää opintoihinsa <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Musiikkivalinnaisuus%20\"><strong>musiikin opintoja</strong></a>. Nämä opinnot sisältävät musiikin teorian ja säveltapailun lisäksi henkilökohtaisia soittotunteja ja bändiharjoituksia.</p><p>Elektroniikka-asentajan opinnoissa voit erikoistua esitystekniikkaan ja hankkia erityisosaamista <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Valoa-ja-aanta-oppimaan\">valo-, ääni- ja kuvatekniikkaan </a></p>",
                "dining": "<p>Opiskelijoille tarjotaan koulupäivinä maksuton omassa talossa valmistettu lounas, joka sisältää pääruoan, lämpimät lisäkkeet, tuoresalaatin, ruokajuomat ja leivät sekä useimmiten myös jälkiruoan.</p><p>Aamu- ja iltapäivän kahvitunneilla on ruokalasta ostettavissa edullinen välipala.</p>",
                "livingExpenses": "<p>Opiskelijat ostavat tarvitsemansa oppikirjat ja maksavat osan työvälineistä, jotka jäävät heille itselleen opintojen jälkeen. </p><p>Opiskelijalla tulee olla työnopetuksessa asianmukainen työasu ja työsuojelumääräysten mukaiset jalkineet. Työjalkineet, yhtenäiset haalarit ja muut suojavaatteet hankitaan keskitetysti koulun kautta opintonsa aloittaville opiskelijoille. Työvaatekustannukset vaihtelevat aloittain. </p><p>Sedun opiskelija-asuntolassa asuminen on maksutonta.</p>",
                "living": null,
                "yearClock": null,
                "financingStudies": null,
                "insurances": null,
                "leisureServices": null,
                "social": [
                  {
                    "name": "facebook",
                    "url": "https://www.facebook.com/#!/pages/Koulutuskeskus-Sedu/326925121003?fref=ts"
                  },
                  {
                    "name": "google_plus",
                    "url": "https://plus.google.com/u/0/115783246003925085079/posts"
                  },
                  {
                    "name": "muu",
                    "url": "http://www.youtube.com/user/KoulutuskeskusSedu"
                  }
                ],
                "pictureFound": true,
                "athleteEducation": true,
                "applicationOffice": {
                  "name": "Sedu hakutoimisto",
                  "phone": "020 124 5258",
                  "email": "hakutoimisto@sedu.fi",
                  "www": "http://www.sedu.fi",
                  "visitingAddress": {
                    "streetAddress": "Koulukatu 41",
                    "streetAddress2": null,
                    "postalCode": "60100",
                    "postOffice": "SEINÄJOKI"
                  },
                  "postalAddress": {
                    "streetAddress": "Koulukatu 41",
                    "streetAddress2": null,
                    "postalCode": "60100",
                    "postOffice": "SEINÄJOKI"
                  }
                },
                "homeplace": "Kurikka"
              },
              "specificApplicationDates": false,
              "applicationStartDate": 1424757600000,
              "applicationEndDate": 1426597220518,
              "applicationPeriodName": "",
              "canBeApplied": false,
              "nextApplicationPeriodStarts": 1424757600000,
              "requiredBaseEducations": [
                "3",
                "2",
                "6",
                "1",
                "7",
                "0"
              ],
              "attachments": null,
              "emphasizedSubjects": null,
              "additionalInfo": "<p><strong>HARKINTAAN PERUSTUVA VALINTA</strong></p><ul><li>Jos haet harkintaan perustuvalla valinnalla, lähetä hakemuksen liitteet hakutoimistoon.</li><li>HUOM! Jokaiseen hakukohteeseen oltava kopioituna omat liitteet. Tulosta mukaan hakemuksesi opintopolusta.</li></ul><p>Osoite:</p><p>Sedu hakutoimisto, Koulukatu 41, 60100 Seinäjoki</p><p> </p><p>Muut yhteystiedot:</p><p><a href=\"mailto:hakutoimisto@sedu.fi\">hakutoimisto@sedu.fi</a> tai p. 040 830 2275</p><p>Katso lisätietoja</p><p><a href=\"http://www.sedu.fi/harkintaanperustuvavalinta\">www.sedu.fi/harkintaanperustuvavalinta</a></p><p> </p><p><strong>ERILLISHAKU</strong><strong></strong></p><p>Jos olet jo suorittanut ammatillisen tutkinnon, ammattitutkinnon tai korkeakoulututkinnon, voit hakea erillishaussa suoraa oppilaitokseen. Hakulomake ja lisätiedot osoitteessa <a href=\"http://www.sedu.fi\">www.sedu.fi</a></p><p> </p><p><strong>KAKSOISTUTKINTO</strong></p><p>Voit suorittaa ammatillisen tutkinnon lisäksi myös yo-tutkinnon. Katso lisätietoja</p><p><a href=\"http://www.sedu.fi/kaksitutkintoa\">www.sedu.fi/kaksitutkintoa</a></p><p> </p><p><strong>URHEILIJANA HAKEMINEN</strong></p><p>Urheilupainotteinen ammatillinen perustutkinto</p><ul><li><strong>Voit saada 1–3 lisäpistettä urheilullisista saavutuksista, kun haet urheilijalle tarkoitettuun ammatilliseen koulutukseen.</strong> Katso lisätiedot sivulta <a href=\"http://www.sedu.fi/urheilijanhaku\">www.sedu.fi/urheilijanhaku</a></li></ul><p> </p><p>Huom! Palauta valmentajan täyttämä lisätietolomake viimeistään 7 vrk haun päättymisen jälkeen osoitteella:</p><p>Sedu hakutoimisto, Koulukatu 41, 60100 Seinäjoki</p><p> </p><p>Muut yhteystiedot:</p><p><a href=\"mailto:hakutoimisto@sedu.fi\">hakutoimisto@sedu.fi</a> tai p. 040 830 2275</p><p> </p><p><strong>Löydät valmentajatietolomakkeen Sedun sivuilta <a href=\"http://www.sedu.fi/urheilijanhaku\">www.sedu.fi/urheilijanhaku</a></strong></p>",
              "additionalProof": null,
              "overallScoreLimit": null,
              "kaksoistutkinto": true,
              "athleteEducation": false,
              "vocational": true,
              "educationCodeUri": "koulutus_351407",
              "status": null,
              "eligibilityDescription": null,
              "type": "TUTKINTO",
              "educationTypeUri": "et3",
              "hakuaikaId": null,
              "organizationGroups": [],
              "kotitalous": false
            }
          ],
          "asOngoing": false,
          "nextApplicationPeriodStarts": 1424757600000,
          "status": null,
          "applicationFormLink": null,
          "hakutapa": "01",
          "hakutyyppi": "01"
        },
        {
          "id": "1.2.246.562.29.92175749016",
          "name": "Ammatillisen koulutuksen ja lukiokoulutuksen syksyn 2014 yhteishaku",
          "applicationDates": [
            {
              "startDate": 1411534807344,
              "endDate": 1412337614662
            }
          ],
          "applicationOptions": [
            {
              "id": "1.2.246.562.20.46340914756",
              "name": "Sähkö- ja automaatiotekniikan perustutkinto, pk",
              "aoIdentifier": "191",
              "startingQuota": 5,
              "startingQuotaDescription": null,
              "lowestAcceptedScore": 0,
              "lowestAcceptedAverage": 0.0,
              "attachmentDeliveryDeadline": 1412337648569,
              "attachmentDeliveryAddress": {
                "streetAddress": "Sedu hakutomisto",
                "streetAddress2": "Koulukatu 41",
                "postalCode": "60100",
                "postOffice": "SEINÄJOKI"
              },
              "lastYearApplicantCount": 0,
              "sora": false,
              "educationDegree": "32",
              "teachingLanguages": [
                "FI"
              ],
              "selectionCriteria": "<p>Näin pisteet lasketaan, kun haet peruskoulun jälkeen:</p><h2>Pisteitä ensi kertaa hakeville ja peruskoulun jälkeisiä valmistavia opintoja suorittaneille</h2><ul><li><strong>Saat 8 pistettä, jos sinulla ei ole opiskelupaikkaa ammatilliseen perustutkintoon johtavassa koulutuksessa tai lukiokoulutuksessa<br /></strong><strong> </strong></li><li><strong>Saat 6 pistettä, jos olet suorittanut perusopetuksen oppimäärän samana vuonna kun haet<br /></strong><strong> </strong></li><li><strong>Saat 6 pistettä, jos olet suorittanut jonkun seuraavista: </strong></li><ul><li>kymppiluokan (vähintään 1100 tunnin laajuisen lisäopetuksen)</li><li>ammattistartin (vähintään 20 opintoviikon laajuisen ammatilliseen peruskoulutukseen ohjaavan ja valmistavan koulutuksen)<strong> </strong></li><li>valmentavan ja kuntouttavan opetuksen ja ohjauksen</li><li>maahanmuuttajien ammatilliseen peruskoulutukseen valmistavan koulutuksen</li><li>kotitalousopetuksen / talouskoulun (ei ammatillisena peruskoulutuksena suoritetun)</li><li>kansanopiston vähintään lukuvuoden mittaisen linjan.<br /><strong> </strong></li></ul><li><strong>Saat 2 pistettä ammatillisesta koulutuksesta, jonka olet hakiessasi merkinnyt 1. hakutoiveeksi.<br /></strong></li></ul><h2>Pisteitä sukupuolen perusteella </h2><ul><li><strong>Saat 2 pistettä, jos hakemaasi ammatilliseen koulutukseen ensisijaisesti hakeneista alle 30 prosenttia on samaa sukupuolta kuin sinä.</strong></li></ul><h2>Pisteitä yleisestä koulumenestyksestä</h2><p>Saat 1–16 pistettä yleisestä koulumenestyksestä keskiarvon perusteella. Valinnassa lasketaan keskiarvo seuraavista oppiaineista: äidinkieli ja kirjallisuus, toinen kotimainen kieli, vieraat kielet, uskonto tai elämänkatsomustieto, historia, yhteiskuntaoppi, matematiikka, fysiikka, kemia, biologia, maantieto, liikunta, terveystieto, musiikki, kuvataide, käsityö, kotitalous.<br /> <br />Jos sinulla on arvosana useammasta samaan yhteiseen oppiaineeseen kuuluvasta vähintään kahden vuosiviikkotunnin valinnaisaineesta, lasketaan valinnassa ensin niiden keskiarvo. Pisteesi yleisestä koulumenestyksestä lasketaan siis yhteisen aineen arvosanan ja siihen kuuluvan valinnaisaineen keskiarvosta tai valinnaisaineiden keskiarvosta.</p><p>Yleisen koulumenestyksen pisteet lasketaan peruskoulun päättötodistuksesta. Korotetut arvosanat otetaan huomioon, jos sinulla on korotuksesta todistus.</p><table border=\"1\" cellspacing=\"0\" cellpadding=\"5\" align=\"center\"><tbody><tr><td valign=\"top\" width=\"381\"><p><strong>Keskiarvo</strong></p></td><td valign=\"top\" width=\"380\"><p><strong>Pisteitä</strong></p></td></tr><tr><td valign=\"top\" width=\"381\"><p>5,50–5,74     </p></td><td valign=\"top\" width=\"380\"><p>1</p></td></tr><tr><td valign=\"top\" width=\"381\"><p>5,75<strong>–</strong>5,99      </p></td><td valign=\"top\" width=\"380\"><p>2</p></td></tr><tr><td width=\"381\"><p>6,00<strong>–</strong>6,24                           </p></td><td width=\"380\"><p>3</p></td></tr><tr><td width=\"381\"><p>6,25<strong>–</strong>6,49                           </p></td><td width=\"380\"><p>4</p></td></tr><tr><td width=\"381\"><p>6,50<strong>–</strong>6,74                           </p></td><td width=\"380\"><p>5</p></td></tr><tr><td width=\"381\"><p>6,75<strong>–</strong>6,99                           </p></td><td width=\"380\"><p>6</p></td></tr><tr><td width=\"381\"><p>7,00<strong>–</strong>7,24                           </p></td><td width=\"380\"><p>7</p></td></tr><tr><td width=\"381\"><p>7,25<strong>–</strong>7,49                           </p></td><td width=\"380\"><p>8</p></td></tr><tr><td width=\"381\"><p>7,50<strong>–</strong>7,74                           </p></td><td width=\"380\"><p>9</p></td></tr><tr><td width=\"381\"><p>7,75<strong>–</strong>7,99                           </p></td><td width=\"380\"><p>10</p></td></tr><tr><td width=\"381\"><p>8,00<strong>–</strong>8,24                           </p></td><td width=\"380\"><p>11</p></td></tr><tr><td width=\"381\"><p>8,25<strong>–</strong>8,49                           </p></td><td width=\"380\"><p>12</p></td></tr><tr><td width=\"381\"><p>8,50<strong>–</strong>8,74                           </p></td><td width=\"380\"><p>13</p></td></tr><tr><td width=\"381\"><p>8,75<strong>–</strong>8,99                           </p></td><td width=\"380\"><p>14</p></td></tr><tr><td width=\"381\"><p>9,00<strong>–</strong>9,24                           </p></td><td width=\"380\"><p>15</p></td></tr><tr><td width=\"381\"><p>9,25<strong>–</strong>10,00                         </p></td><td width=\"380\"><p>16</p></td></tr></tbody></table><p> </p><h2><strong>Pisteitä painotettavista arvosanoista</strong></h2><p>Saat 1–8 pistettä painotettavien arvosanojen keskiarvosta. Kaikilla koulutusaloilla otetaan huomioon perusopetuksen päättötodistuksen arvosanat liikunnassa, kuvataiteessa, käsityössä, kotitaloudessa ja musiikissa. Kolmesta parhaasta aineesta lasketaan valinnassa niiden keskiarvo.</p><p>Jos sinulla on arvosana useammasta samaan yhteiseen oppiaineeseen kuuluvasta vähintään kahden vuosiviikkotunnin valinnaisaineesta, lasketaan valinnassa ensin niiden keskiarvo. Pisteesi painotettavista arvosanoista lasketaan siis yhteisen aineen arvosanan ja siihen kuuluvan valinnaisaineen keskiarvosta tai valinnaisaineiden keskiarvosta.</p><p> </p><table border=\"1\" cellspacing=\"0\" cellpadding=\"5\" align=\"center\"><tbody><tr><td valign=\"top\" width=\"407\"><p><strong>Keskiarvo</strong></p></td><td valign=\"top\" width=\"407\"><p><strong>Pisteitä</strong></p></td></tr><tr><td width=\"407\"><p>6,00–6,49                           </p></td><td width=\"407\"><p>1</p></td></tr><tr><td width=\"407\"><p>6,50–6,99                           </p></td><td width=\"407\"><p>2</p></td></tr><tr><td width=\"407\"><p>7,00–7,49                           </p></td><td width=\"407\"><p>3</p></td></tr><tr><td width=\"407\"><p>7,50–7,99                           </p></td><td width=\"407\"><p>4</p></td></tr><tr><td width=\"407\"><p>8,00–8,49                           </p></td><td width=\"407\"><p>5</p></td></tr><tr><td width=\"407\"><p>8,50–8,99                           </p></td><td width=\"407\"><p>6</p></td></tr><tr><td width=\"407\"><p>9,00–9,49                           </p></td><td width=\"407\"><p>7</p></td></tr><tr><td width=\"407\"><p>9,50–10,00                         </p></td><td width=\"407\"><p>8</p></td></tr></tbody></table><p> </p><p>Jos olet suorittanut perusopetuksen oppimäärän aikuiskoulutuksena, voidaan ottaa huomioon perusopetuksen erotodistuksen arvosanat seuraavissa aineissa: liikunta, kuvataide, käsityö, kotitalous ja musiikki, jollei näitä arvosanoja ole päättötodistuksessa.</p><p> </p><h2><strong>Pisteitä työkokemuksesta</strong></h2><ul><li><strong>Saat 1–3 pistettä työkokemuksesta tai osallistumisesta työpajatoimintaan tai työharjoitteluun / työkokeiluun</strong>.</li></ul><p>Työkokemukseksi hyväksytään työsuhteessa saatu työkokemus, jonka olet hankkinut peruskoulun jälkeen tai täytettyäsi 16 vuotta. Osa-aikatyössä 150 tunnin mittainen saman työnantajan palveluksessa tehty työ vastaa yhden kuukauden työkokemusta. </p><p>Työharjoittelu / työkokeilu tarkoittaa harjoittelua, johon työ- ja elinkeinotoimisto on sinut ohjannut. </p><p>Oppisopimuskoulutuksesta luetaan työkokemukseksi todistuksissa mainittu työkokemuksen määrä. </p><p>Hakemuksessa otetaan huomioon työkokemus, joka on saatu ennen hakuajan päättymistä. Jos sinulla esimerkiksi on työsopimus ajalle 1.6.-31.12.2013 ja hakuaika on 25.9.-4.10. , niin työkokemukseksi lasketaan ajanjakso 1.6. -4.10. Saat työkokemuksesta yhden pisteen.</p><p> </p><table border=\"1\" cellspacing=\"0\" cellpadding=\"10\" align=\"center\"><tbody><tr><td valign=\"top\" width=\"407\"><p><strong>Työkokemuksen pituus</strong></p></td><td valign=\"top\" width=\"407\"><p>Pisteitä</p></td></tr><tr><td width=\"407\"><p>3 kk–alle 6 kk                     </p></td><td width=\"407\"><p>1</p></td></tr><tr><td width=\"407\"><p>6 kk–alle 12 kk                   </p></td><td width=\"407\"><p>2</p></td></tr><tr><td width=\"407\"><p>12 kk–                                </p></td><td width=\"407\"><p>3</p></td></tr></tbody></table><p> </p><h2><strong>Lisäpisteet urheilullisista saavutuksista</strong> </h2><ul><li><strong>Voit saada 1–3 lisäpistettä urheilullisista saavutuksista, jos haet urheilijalle tarkoitettuun ammatilliseen koulutukseen.</strong><strong> <br /></strong></li></ul><h2><strong>Pääsy- ja soveltuvuuskokeet</strong><strong> </strong></h2><p>Kaikki hakijat saavat kutsun kokeisiin, jos koulutuksessa järjestetään pääsy- ja soveltuvuuskoe.  Koe voi muodostua eri osista. Oppilaitokset tiedottavat kokeen eri vaiheista.  </p><p>Jos olet hakenut useaan koulutukseen, sinut kutsutaan kokeeseen ylimpään hakutoiveeseen, jossa on koe. Koetuloksesi huomioidaan kuitenkin kaikissa samaan valintakoeryhmään kuuluvissa koulutuksissa.</p><p>Valintakoeryhmä tarkoittaa sitä, että monella oppilaitoksella on yksi valintakoe. Silloin yksi koe riittää kaikkiin saman ryhmän koulutuksiin. </p><ul><li><strong>Voit saada 1–10 pistettä hyväksytystä pääsy- ja soveltuvuuskokeesta.</strong> Hylätyn pääsykoetuloksen saanutta hakijaa ei voida valita koulutukseen. </li><li>Huomaathan, että kaikissa koulutuksissa ei ole pääsy- ja soveltuvuuskokeita. Tiedot pääsykokeesta löytyvät tämän sivun alaosasta.</li><li>Pääsykoetulosta käytetään kaikissa vastaavissa koulutuksissa, joihin olet hakenut. Jos olet hakenut useille eri aloille, on mahdollista, että saat kutsun useisiin kokeisiin.</li></ul><h2><strong>Tasapistetilanne</strong></h2><p>Saman pistemäärän saaneet hakijat asetetaan järjestykseen seuraavien perusteiden mukaisesti:</p><ol><li>hakutoivejärjestys</li><li>mahdollinen  pääsy- tai soveltuvuuskokeesta saatava pistemäärä</li><li>yleinen koulumenestys</li><li>painotettavat arvosanat.</li></ol><p>Jos pistemäärä on tämän jälkeen edelleen sama, opiskelijat valitaan satunnaisjärjestyksessä.<br /> </p><h2><strong>Kielikokeet</strong></h2><p>Saat kutsun kielikokeisiin, jos äidinkielesi on muu kuin opetuskieli eikä kielitaitoasi ole voitu todentaa muilla tavoin, esimerkiksi alla mainituilla todistuksilla. Kokeilla osoitat, että sinulla on riittävät valmiudet opetuskielen suulliseen ja kirjalliseen käyttämiseen. Kokeet ovat kaikille hakijoille samat.</p><p>Jos sinulla on jokin alla mainituista todistuksista, sinua ei kutsuta kielikokeeseen.</p><p><strong> Opetuskielen taitoasi pidetään riittävänä, jos</strong></p><ul><li>sinulla on perusopetuksen päättötodistus, joka on suoritettu vastaanottavan oppilaitoksen opetuskielellä (suomi, ruotsi tai saame).</li><li>olet suorittanut vähintään arvosanalla 7 perusopetuksessa toisen kotimaisen A-kielen oppimäärän. Kielen on oltava vastaanottavan oppilaitoksen opetuskieli.</li><li>olet suorittanut vähintään arvosanalla 7 perusopetuksen suomi tai ruotsi toisena kielenä oppimäärän. Kielen on oltava vastaanottavan oppilaitoksen opetuskieli.</li><li>olet suorittanut lukion oppimäärän tai ylioppilastutkinnon vastaanottavan oppilaitoksen opetuskielellä.</li><li>olet suorittanut lukiokoulutuksen koko oppimäärän jossakin seuraavista äidinkieli ja kirjallisuus-oppiaineen oppimääristä: suomi äidinkielenä, ruotsi äidinkielenä, saame äidinkielenä, suomi toisena kielenä, ruotsi toisena kielenä, suomi saamenkielisille, suomi viittomakielisille. Kielen on oltava vastaanottavan oppilaitoksen opetuskieli.</li><li>olet suorittanut lukiokoulutuksen toisen kotimaisen kielen koko oppimäärän. Kielen on oltava vastaanottavan oppilaitoksen opetuskieli.</li><li>olet suorittanut yleisten kielitutkintojen suomen tai ruotsin kielen tutkinnon kaikki osakokeet vähintään taitotasolla 3. Tai olet suorittanut Valtionhallinnon kielitutkintojen suomen tai ruotsin kielen suullisen ja kirjallisen taidon tutkinnon vähintään taitotasolla tyydyttävä.</li></ul><p>Oppilaitos voi jättää hakijan valitsematta ammatilliseen peruskoulutukseen, jos hänellä ei ole valmiutta opetuskielen suulliseen ja kirjalliseen käyttämiseen sekä ymmärtämiseen.</p><h2><strong>Harkintaan perustuva valinta </strong></h2><p>Oppilaitos voi ottaa opiskelijoita koulutuksiin harkinnan perusteella. Tällöin valinta tehdään valintapistemääristä riippumatta. Yhteen hakukohteeseen voidaan ottaa tällä tavoin enintään 30 prosenttia opiskelijoista. Harkintaan perustuvan valinnan syitä ovat:</p><ul><li>oppimisvaikeudet</li><li>sosiaaliset syyt</li><li>koulutodistusten puuttuminen tai todistusten vertailuvaikeudet. </li></ul><p>Koulutustarpeesi ja edellytyksesi suoriutua opinnoista arvioidaan ja otetaan huomioon, kun valintapistemäärästä poiketaan.</p><p>Huomaathan, että aiempi joustava valinta -menettely ei ole enää käytössä.</p>",
              "soraDescription": null,
              "prerequisite": {
                "value": "PK",
                "name": "Peruskoulu",
                "shortName": "PK",
                "description": "Peruskoulu",
                "uri": "pohjakoulutusvaatimustoinenaste_pk"
              },
              "exams": null,
              "childRefs": [
                {
                  "id": "1.2.246.562.17.70800560057",
                  "losId": "1.2.246.562.5.2013061010185704541592_1.2.246.562.10.85442777687",
                  "name": "Sähkö- ja automaatiotekniikka, sähköasentaja",
                  "qualification": "Sähköasentaja",
                  "prerequisite": {
                    "value": "PK",
                    "name": "Peruskoulu",
                    "shortName": "PK",
                    "description": "Peruskoulu",
                    "uri": "pohjakoulutusvaatimustoinenaste_pk"
                  }
                }
              ],
              "higherEdLOSRefs": [],
              "provider": {
                "id": "1.2.246.562.10.85442777687",
                "name": "Koulutuskeskus Sedu,  Kurikka",
                "applicationSystemIds": [
                  "1.2.246.562.5.2013112910480420004764",
                  "1.2.246.562.5.2014022711042555034240",
                  "1.2.246.562.5.2013080813081926341927",
                  "1.2.246.562.29.92175749016",
                  "1.2.246.562.29.90697286251"
                ],
                "postalAddress": {
                  "streetAddress": "Huovintie 1",
                  "streetAddress2": null,
                  "postalCode": "61300",
                  "postOffice": "KURIKKA"
                },
                "visitingAddress": {
                  "streetAddress": "Huovintie 1",
                  "streetAddress2": null,
                  "postalCode": "61300",
                  "postOffice": "KURIKKA"
                },
                "webPage": "http://www.sedu.fi",
                "email": "hakutoimisto@sedu.fi",
                "fax": null,
                "phone": "020 124 6108",
                "description": "<p><strong>Sedu Kurikka</strong> on monialainen ammatillisen koulutuksen kampus, jossa opiskelee yhteensä noin 450 nuorta yhteentoista eri ammattiin (ajoneuvoasentaja, koneistaja ja levyseppähitsaaja, talonrakentaja, sähköasentaja, elektroniikka-asentaja, puuseppä, sisustaja, verhoilija, putkiasentaja, turvallisuusvalvoja ja kokki). </p> <p><strong>Valinnaisiin opintoihin</strong> on kaikilla aloilla mahdollisuus ottaa erilaisia oppilaitoksessa tarjottavia <strong>musiikin opintoja</strong>. Kaikilla aloilla myös mahdollisuus suorittaa kaksi tutkintoa tai lukion aineopintoja yhteistyössä Kurikan lukion kanssa. </p> <p>Opetuspiste sijaitsee Kurikan keskustan ja sen palvelujen välittömässä läheisyydessä. Kurikka on n. 15 000 asukkaan vireä kaupunki, jossa on hyvä asua ja opiskella. Bussiyhteydet naapurikunnista mahdollistavat tarvittaessa päivittäisen kulkemisen kotoa käsin. </p> <p>Oppilaitoksen kanssa samalla tontilla sijaitsee maksuton ja valvottu <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Asuminen\">opiskelija-asuntola </a>80 asukkaalle. Asuntolassa opiskelijoille on iltaisin tarjolla ohjattua vapaa-ajan toimintaa.<strong> </strong></p> <p><strong>Asuntolassa asuvien erityistä tukea tarvitsevien opiskelijoiden on mahdollista hakea <a href=\"http://www.epsospsyk.fi/aake.html%20\">Pikku-AAKEn</a> tuen piiriin.</strong></p> <p>Lisätietoja Kurikan opetuspisteestä <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka\">http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka</a></p> <p> </p> <p><strong>KAKSOISTUTKINTO</strong></p> <p>Voit suorittaa ammatillisen tutkinnon lisäksi myös yo-tutkinnon. Katso lisätietoja</p> <p><a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Hae-opiskelemaan/Hae-opiskelijaksi/Kaksi-tutkintoa\">www.sedu.fi/kaksitutkintoa</a></p> <p> </p> <p><strong>URHEILIJANA HAKEMINEN</strong></p> <p>Urheilupainotteinen ammatillinen perustutkinto</p> <ul> <li><strong>Voit saada 1–3 lisäpistettä urheilullisista saavutuksista, kun haet urheilijalle tarkoitettuun ammatilliseen koulutukseen.</strong> Katso lisätiedot sivulta <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Hae-opiskelemaan/Hae-opiskelijaksi/Urheilupainotteinen-ammatillinen-perustutkinto\">www.sedu.fi/urheilijanhaku</a></li> </ul> <p>Huom! Palauta valmentajan täyttämä lisätietolomake viimeistään 7 vrk haun päättymisen jälkeen osoitteella:</p> <p>Osoite:</p> <p>Koulutuskeskus Sedu, Hakutoimisto, Koulukatu 41, 60100 Seinäjoki</p> <p>Muut yhteystiedot:</p> <p>hakutoimisto@sedu.fi tai p. 020 124 5258</p> <p>Löydät Valmentajatietolomakkeen opintopolku.fi – palvelusta tai Sedun svuilta <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Hae-opiskelemaan/Hae-opiskelijaksi/Urheilupainotteinen-ammatillinen-perustutkinto\">www.sedu.fi/urheilijanhaku</a></p> <p> </p> <p><strong>HARKINTAAN PERUSTUVA VALINTA</strong></p> <ul> <li>Jos haet harkintaan perustuvalla valinnalla, lähetä hakemuksen liitteet hakutoimistoon.</li> </ul> <ul> <li>HUOM! Jokaiseen hakukohteeseen oltava kopioituna omat liitteet. Tulosta mukaan hakemuksesi opintopolusta.</li> </ul> <p>Osoite:</p> <p>Koulutuskeskus Sedu, Hakutoimisto, Koulukatu 41, 60100 Seinäjoki</p> <p>Muut yhteystiedot:</p> <p>hakutoimisto@sedu.fi tai p. 020 124 5258</p> <p>Katso lisätietoja</p> <p><a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Hae-opiskelemaan/Hae-opiskelijaksi/Harkintaan-perustuva-valinta\">www.sedu.fi/harkintaanperustuvavalinta</a></p> <p> </p> <p><strong>ERILLISHAKU</strong></p> <p>Jos olet jo suorittanut jonkun ammatillisen tutkinnon, voit hakea erillishaussa suoraan oppilaitokseen. Hakulomake ja lisätiedot osoitteessa <a href=\"http://www.sedu.fi\">www.sedu.fi</a></p> <p> </p> <p> </p> <p> </p> <p> </p>",
                "healthcare": "<p><a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Tukea-opintoihin/Opiskelijaa-tukevat-palvelut/Opiskelijaterveydenhuolto\">Opiskelijaterveydenhuoltoon</a> kuuluvat terveydenhoitaja-, lääkäri-, hammaslääkäri- ja psykologipalvelut.</p><p>Terveydenhoitajan vastaanotto on joka päivä. Terveydenhoitajan vastaanotolle voit mennä oma-aloitteisesti kaikissa terveyttä ja hyvinvointia koskevissa asioissa. </p><p>Lääkärin, hammaslääkärin ja psykologin vastaanotoille pääsee ajanvarauksella.</p>",
                "accessibility": "<p>Opetuspisteessä toimii <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Opiskelijahuolto-Kurikassa\">opiskelijahuoltoryhmä</a> (OHR), johon kuuluvat terveydenhoitaja, kuraattori, opinto-ohjaajat sekä koulutuspäällikkö. Opiskeluun, vapaa-aikaan, terveyteen tai muihin henkilökohtaisiin asioihin liittyvissä ongelmissa voit aina kääntyä OHR:n jäsenten puoleen.</p> <p><strong>Opintotoimistossa</strong> voit hoitaa opintotukiasiat, koulumatkatukiasiat, opiskelijatodistukset yms. opiskeluun liittyvät asiat.</p> <p>Kurikassa puualalle sekä hotelli-, ravintola- ja cateringalalle haetaan yhteen ryhmään, ja opintojen edetessä ryhmistä muodostetaan erilaisia tiimejä. Jokin tiimeistä on suunniteltu erityistä tukea tarvitseville ja/tai työvaltaisesti opiskeleville pienryhmäläisille. Katso lisää <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Erityisopetus\">erityisen tuen tarjoamisesta</a>. Muissa ryhmissä on tarvittaessa tarjolla erityisopettajien tukea. Opettajien apuna toimii joillakin aloilla ammattiohjaaja. Opetuspisteessä toimii Maijan Paja, jossa tarvittaessa opiskelijat voivat suorittaa opintoja (mm. rästikursseja ja ATTO-opintoja) erityisopettajan pienryhmäohjauksessa.</p> <p>Opiskelijat saavat äänensä kuuluville kaksi kertaa lukuvuodessa kokoontuvissa alakohtaisissa opiskelijahuoltoryhmissä, ja lisäksi opetuspisteen opiskelijafoorumi kokoontuu kahdesti vuodessa. Kurikan opetuspisteen opiskelijat ovat edustettuna Sedun <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Iloa-opintoihin/Opiskelijakunta\">FOKUS-ryhmässä</a>. Koulutetut tutor-opiskelijat toimivat aktiivisesti opetuspisteen esittely- ja markkinointitehtävissä.</p> <p>Oppilaitoksessa toimii maanantai-iltaisin palloilukerho ja kaikkina iltoina on käytettävissä opetuspisteen oma hyvin varustettu kuntosali. Asuntolassa järjestetään erilaista vapaa-ajan ja harrastetoimintaa iltaisin. Lisäksi Kurikan kaupungissa on tarjolla laaja kirjo erilaisia <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Vapaa-ajan-toiminta%20\">vapaa-ajan aktiviteetteja</a>.  </p> <p> </p>",
                "learningEnvironment": "<p>Teorialuokissa ja työsaleissa on ajantasaiset koneet, laitteet ja opetusvälineistö. Opetuspisteessä on kuusi atk-luokkaa. Lähiopetuksen tukena on käytössä Moodle-oppimisympäristö ja työssäoppimisessa eTaitava -järjestelmä. </p><p>Opiskelijat tekevät runsaasti asiakas- ja tilaustöitä, jotka opettavat yrittäjämäistä asennetta ja valmentavat työelämään.  Opiskelijat voivat lisäksi suorittaa opintojaan ja harjoitella yrittäjyyttä osuuskunnassa tai NY-yrityksessä. Katso lisää <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Yrittajyys\">http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Yrittajyys</a> </p><p>Opiskelijoilla on mahdollisuus suorittaa opintoihin sisältyvä työssäoppiminen ulkomailla. Sedu tukee opiskelijaa asumis- ja matkustuskustannuksissa. Katso lisää kansainvälisyydestä <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Kansainvalisyys\">http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Kansainvalisyys</a></p><p>Kaikilla opiskelijoilla Kurikan Sedussa on mahdollisuus sisällyttää opintoihinsa <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Musiikkivalinnaisuus%20\"><strong>musiikin opintoja</strong></a>. Nämä opinnot sisältävät musiikin teorian ja säveltapailun lisäksi henkilökohtaisia soittotunteja ja bändiharjoituksia.</p><p>Elektroniikka-asentajan opinnoissa voit erikoistua esitystekniikkaan ja hankkia erityisosaamista <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Valoa-ja-aanta-oppimaan\">valo-, ääni- ja kuvatekniikkaan </a></p>",
                "dining": "<p>Opiskelijoille tarjotaan koulupäivinä maksuton omassa talossa valmistettu lounas, joka sisältää pääruoan, lämpimät lisäkkeet, tuoresalaatin, ruokajuomat ja leivät sekä useimmiten myös jälkiruoan.</p><p>Aamu- ja iltapäivän kahvitunneilla on ruokalasta ostettavissa edullinen välipala.</p>",
                "livingExpenses": "<p>Opiskelijat ostavat tarvitsemansa oppikirjat ja maksavat osan työvälineistä, jotka jäävät heille itselleen opintojen jälkeen. </p><p>Opiskelijalla tulee olla työnopetuksessa asianmukainen työasu ja työsuojelumääräysten mukaiset jalkineet. Työjalkineet, yhtenäiset haalarit ja muut suojavaatteet hankitaan keskitetysti koulun kautta opintonsa aloittaville opiskelijoille. Työvaatekustannukset vaihtelevat aloittain. </p><p>Sedun opiskelija-asuntolassa asuminen on maksutonta.</p>",
                "living": null,
                "yearClock": null,
                "financingStudies": null,
                "insurances": null,
                "leisureServices": null,
                "social": [
                  {
                    "name": "facebook",
                    "url": "https://www.facebook.com/#!/pages/Koulutuskeskus-Sedu/326925121003?fref=ts"
                  },
                  {
                    "name": "google_plus",
                    "url": "https://plus.google.com/u/0/115783246003925085079/posts"
                  },
                  {
                    "name": "muu",
                    "url": "http://www.youtube.com/user/KoulutuskeskusSedu"
                  }
                ],
                "pictureFound": true,
                "athleteEducation": true,
                "applicationOffice": {
                  "name": "Sedu hakutoimisto",
                  "phone": "020 124 5258",
                  "email": "hakutoimisto@sedu.fi",
                  "www": "http://www.sedu.fi",
                  "visitingAddress": {
                    "streetAddress": "Koulukatu 41",
                    "streetAddress2": null,
                    "postalCode": "60100",
                    "postOffice": "SEINÄJOKI"
                  },
                  "postalAddress": {
                    "streetAddress": "Koulukatu 41",
                    "streetAddress2": null,
                    "postalCode": "60100",
                    "postOffice": "SEINÄJOKI"
                  }
                },
                "homeplace": "Kurikka"
              },
              "specificApplicationDates": false,
              "applicationStartDate": 1411534807344,
              "applicationEndDate": 1412337614662,
              "applicationPeriodName": "HAKUAIKA",
              "canBeApplied": false,
              "nextApplicationPeriodStarts": null,
              "requiredBaseEducations": [
                "3",
                "2",
                "6",
                "1",
                "7",
                "0"
              ],
              "attachments": null,
              "emphasizedSubjects": null,
              "additionalInfo": "<p><strong>HARKINTAAN PERUSTUVA VALINTA</strong></p><p>Jos haet harkintaan perustuvassa valinnassa, toimita liitteet osoitteeseen</p><p>Sedu hakutoimisto, Koulukatu 41, 60100 Seinäjoki</p><p>HUOM! Jokaiseen hakukohteeseen oltava kopioituna omat liitteet. Tulosta mukaan hakemuksesi opintopolusta.</p><p><strong>ERILLISHAKU</strong></p><p>Ammatillisen tutkinnon aiemmin suorittaneet voivat hakea erillishaussa suoraa oppilaitokseen. Katso ohjeet ja hakulomake www.sedu.fi --&gt; hae opiskelijaksi.</p>",
              "additionalProof": null,
              "overallScoreLimit": null,
              "kaksoistutkinto": true,
              "athleteEducation": false,
              "vocational": true,
              "educationCodeUri": "koulutus_351407",
              "status": null,
              "eligibilityDescription": null,
              "type": "TUTKINTO",
              "educationTypeUri": "et3",
              "hakuaikaId": null,
              "organizationGroups": [],
              "kotitalous": false
            }
          ],
          "asOngoing": false,
          "nextApplicationPeriodStarts": null,
          "status": null,
          "applicationFormLink": null,
          "hakutapa": "01",
          "hakutyyppi": "01"
        },
        {
          "id": "1.2.246.562.5.2013080813081926341927",
          "name": "Ammatillisen koulutuksen ja lukiokoulutuksen kevään 2014 yhteishaku",
          "applicationDates": [
            {
              "startDate": 1393221600000,
              "endDate": 1394802000000
            }
          ],
          "applicationOptions": [
            {
              "id": "1.2.246.562.5.41153698216",
              "name": "Sähkö- ja automaatiotekniikan perustutkinto, pk",
              "aoIdentifier": "191",
              "startingQuota": 20,
              "startingQuotaDescription": null,
              "lowestAcceptedScore": 0,
              "lowestAcceptedAverage": 0.0,
              "attachmentDeliveryDeadline": 1394802000000,
              "attachmentDeliveryAddress": {
                "streetAddress": "Huovintie 1",
                "streetAddress2": null,
                "postalCode": "61300",
                "postOffice": "KURIKKA"
              },
              "lastYearApplicantCount": 0,
              "sora": false,
              "educationDegree": "32",
              "teachingLanguages": [
                "FI"
              ],
              "selectionCriteria": "<p>Näin pisteet lasketaan, kun haet peruskoulun jälkeen:</p><h2>Pisteitä ensi kertaa hakeville ja peruskoulun jälkeisiä valmistavia opintoja suorittaneille</h2><ul><li><strong>Saat 8 pistettä, jos sinulla ei ole opiskelupaikkaa ammatilliseen perustutkintoon johtavassa koulutuksessa tai lukiokoulutuksessa<br /></strong><strong> </strong></li><li><strong>Saat 6 pistettä, jos olet suorittanut perusopetuksen oppimäärän samana vuonna kun haet<br /></strong><strong> </strong></li><li><strong>Saat 6 pistettä, jos olet suorittanut jonkun seuraavista: </strong></li><ul><li>kymppiluokan (vähintään 1100 tunnin laajuisen lisäopetuksen)</li><li>ammattistartin (vähintään 20 opintoviikon laajuisen ammatilliseen peruskoulutukseen ohjaavan ja valmistavan koulutuksen)<strong> </strong></li><li>valmentavan ja kuntouttavan opetuksen ja ohjauksen</li><li>maahanmuuttajien ammatilliseen peruskoulutukseen valmistavan koulutuksen</li><li>kotitalousopetuksen / talouskoulun (ei ammatillisena peruskoulutuksena suoritetun)</li><li>kansanopiston vähintään lukuvuoden mittaisen linjan.<br /><strong> </strong></li></ul><li><strong>Saat 2 pistettä ammatillisesta koulutuksesta, jonka olet hakiessasi merkinnyt 1. hakutoiveeksi.<br /></strong></li></ul><h2>Pisteitä sukupuolen perusteella </h2><ul><li><strong>Saat 2 pistettä, jos hakemaasi ammatilliseen koulutukseen ensisijaisesti hakeneista alle 30 prosenttia on samaa sukupuolta kuin sinä.</strong></li></ul><h2>Pisteitä yleisestä koulumenestyksestä</h2><p>Saat 1–16 pistettä yleisestä koulumenestyksestä keskiarvon perusteella. Valinnassa lasketaan keskiarvo seuraavista oppiaineista: äidinkieli ja kirjallisuus, toinen kotimainen kieli, vieraat kielet, uskonto tai elämänkatsomustieto, historia, yhteiskuntaoppi, matematiikka, fysiikka, kemia, biologia, maantieto, liikunta, terveystieto, musiikki, kuvataide, käsityö, kotitalous.<br /> <br />Jos sinulla on arvosana useammasta samaan yhteiseen oppiaineeseen kuuluvasta vähintään kahden vuosiviikkotunnin valinnaisaineesta, lasketaan valinnassa ensin niiden keskiarvo. Pisteesi yleisestä koulumenestyksestä lasketaan siis yhteisen aineen arvosanan ja siihen kuuluvan valinnaisaineen keskiarvosta tai valinnaisaineiden keskiarvosta.</p><p>Yleisen koulumenestyksen pisteet lasketaan peruskoulun päättötodistuksesta. Korotetut arvosanat otetaan huomioon, jos sinulla on korotuksesta todistus.</p><table border=\"1\" cellspacing=\"0\" cellpadding=\"5\" align=\"center\"><tbody><tr><td valign=\"top\" width=\"381\"><p><strong>Keskiarvo</strong></p></td><td valign=\"top\" width=\"380\"><p><strong>Pisteitä</strong></p></td></tr><tr><td valign=\"top\" width=\"381\"><p>5,50–5,74     </p></td><td valign=\"top\" width=\"380\"><p>1</p></td></tr><tr><td valign=\"top\" width=\"381\"><p>5,75<strong>–</strong>5,99      </p></td><td valign=\"top\" width=\"380\"><p>2</p></td></tr><tr><td width=\"381\"><p>6,00<strong>–</strong>6,24                           </p></td><td width=\"380\"><p>3</p></td></tr><tr><td width=\"381\"><p>6,25<strong>–</strong>6,49                           </p></td><td width=\"380\"><p>4</p></td></tr><tr><td width=\"381\"><p>6,50<strong>–</strong>6,74                           </p></td><td width=\"380\"><p>5</p></td></tr><tr><td width=\"381\"><p>6,75<strong>–</strong>6,99                           </p></td><td width=\"380\"><p>6</p></td></tr><tr><td width=\"381\"><p>7,00<strong>–</strong>7,24                           </p></td><td width=\"380\"><p>7</p></td></tr><tr><td width=\"381\"><p>7,25<strong>–</strong>7,49                           </p></td><td width=\"380\"><p>8</p></td></tr><tr><td width=\"381\"><p>7,50<strong>–</strong>7,74                           </p></td><td width=\"380\"><p>9</p></td></tr><tr><td width=\"381\"><p>7,75<strong>–</strong>7,99                           </p></td><td width=\"380\"><p>10</p></td></tr><tr><td width=\"381\"><p>8,00<strong>–</strong>8,24                           </p></td><td width=\"380\"><p>11</p></td></tr><tr><td width=\"381\"><p>8,25<strong>–</strong>8,49                           </p></td><td width=\"380\"><p>12</p></td></tr><tr><td width=\"381\"><p>8,50<strong>–</strong>8,74                           </p></td><td width=\"380\"><p>13</p></td></tr><tr><td width=\"381\"><p>8,75<strong>–</strong>8,99                           </p></td><td width=\"380\"><p>14</p></td></tr><tr><td width=\"381\"><p>9,00<strong>–</strong>9,24                           </p></td><td width=\"380\"><p>15</p></td></tr><tr><td width=\"381\"><p>9,25<strong>–</strong>10,00                         </p></td><td width=\"380\"><p>16</p></td></tr></tbody></table><p> </p><h2><strong>Pisteitä painotettavista arvosanoista</strong></h2><p>Saat 1–8 pistettä painotettavien arvosanojen keskiarvosta. Kaikilla koulutusaloilla otetaan huomioon perusopetuksen päättötodistuksen arvosanat liikunnassa, kuvataiteessa, käsityössä, kotitaloudessa ja musiikissa. Kolmesta parhaasta aineesta lasketaan valinnassa niiden keskiarvo.</p><p>Jos sinulla on arvosana useammasta samaan yhteiseen oppiaineeseen kuuluvasta vähintään kahden vuosiviikkotunnin valinnaisaineesta, lasketaan valinnassa ensin niiden keskiarvo. Pisteesi painotettavista arvosanoista lasketaan siis yhteisen aineen arvosanan ja siihen kuuluvan valinnaisaineen keskiarvosta tai valinnaisaineiden keskiarvosta.</p><p> </p><table border=\"1\" cellspacing=\"0\" cellpadding=\"5\" align=\"center\"><tbody><tr><td valign=\"top\" width=\"407\"><p><strong>Keskiarvo</strong></p></td><td valign=\"top\" width=\"407\"><p><strong>Pisteitä</strong></p></td></tr><tr><td width=\"407\"><p>6,00–6,49                           </p></td><td width=\"407\"><p>1</p></td></tr><tr><td width=\"407\"><p>6,50–6,99                           </p></td><td width=\"407\"><p>2</p></td></tr><tr><td width=\"407\"><p>7,00–7,49                           </p></td><td width=\"407\"><p>3</p></td></tr><tr><td width=\"407\"><p>7,50–7,99                           </p></td><td width=\"407\"><p>4</p></td></tr><tr><td width=\"407\"><p>8,00–8,49                           </p></td><td width=\"407\"><p>5</p></td></tr><tr><td width=\"407\"><p>8,50–8,99                           </p></td><td width=\"407\"><p>6</p></td></tr><tr><td width=\"407\"><p>9,00–9,49                           </p></td><td width=\"407\"><p>7</p></td></tr><tr><td width=\"407\"><p>9,50–10,00                         </p></td><td width=\"407\"><p>8</p></td></tr></tbody></table><p> </p><p>Jos olet suorittanut perusopetuksen oppimäärän aikuiskoulutuksena, voidaan ottaa huomioon perusopetuksen erotodistuksen arvosanat seuraavissa aineissa: liikunta, kuvataide, käsityö, kotitalous ja musiikki, jollei näitä arvosanoja ole päättötodistuksessa.</p><p> </p><h2><strong>Pisteitä työkokemuksesta</strong></h2><ul><li><strong>Saat 1–3 pistettä työkokemuksesta tai osallistumisesta työpajatoimintaan tai työharjoitteluun / työkokeiluun</strong>.</li></ul><p>Työkokemukseksi hyväksytään työsuhteessa saatu työkokemus, jonka olet hankkinut peruskoulun jälkeen tai täytettyäsi 16 vuotta. Osa-aikatyössä 150 tunnin mittainen saman työnantajan palveluksessa tehty työ vastaa yhden kuukauden työkokemusta. </p><p>Työharjoittelu / työkokeilu tarkoittaa harjoittelua, johon työ- ja elinkeinotoimisto on sinut ohjannut. </p><p>Oppisopimuskoulutuksesta luetaan työkokemukseksi todistuksissa mainittu työkokemuksen määrä. </p><p>Hakemuksessa otetaan huomioon työkokemus, joka on saatu ennen hakuajan päättymistä. Jos sinulla esimerkiksi on työsopimus ajalle 1.6.-31.12.2013 ja hakuaika on 25.9.-4.10. , niin työkokemukseksi lasketaan ajanjakso 1.6. -4.10. Saat työkokemuksesta yhden pisteen.</p><p> </p><table border=\"1\" cellspacing=\"0\" cellpadding=\"10\" align=\"center\"><tbody><tr><td valign=\"top\" width=\"407\"><p><strong>Työkokemuksen pituus</strong></p></td><td valign=\"top\" width=\"407\"><p>Pisteitä</p></td></tr><tr><td width=\"407\"><p>3 kk–alle 6 kk                     </p></td><td width=\"407\"><p>1</p></td></tr><tr><td width=\"407\"><p>6 kk–alle 12 kk                   </p></td><td width=\"407\"><p>2</p></td></tr><tr><td width=\"407\"><p>12 kk–                                </p></td><td width=\"407\"><p>3</p></td></tr></tbody></table><p> </p><h2><strong>Lisäpisteet urheilullisista saavutuksista</strong> </h2><ul><li><strong>Voit saada 1–3 lisäpistettä urheilullisista saavutuksista, jos haet urheilijalle tarkoitettuun ammatilliseen koulutukseen.</strong><strong> <br /></strong></li></ul><h2><strong>Pääsy- ja soveltuvuuskokeet</strong><strong> </strong></h2><p>Kaikki hakijat saavat kutsun kokeisiin, jos koulutuksessa järjestetään pääsy- ja soveltuvuuskoe.  Koe voi muodostua eri osista. Oppilaitokset tiedottavat kokeen eri vaiheista.  </p><p>Jos olet hakenut useaan koulutukseen, sinut kutsutaan kokeeseen ylimpään hakutoiveeseen, jossa on koe. Koetuloksesi huomioidaan kuitenkin kaikissa samaan valintakoeryhmään kuuluvissa koulutuksissa.</p><p>Valintakoeryhmä tarkoittaa sitä, että monella oppilaitoksella on yksi valintakoe. Silloin yksi koe riittää kaikkiin saman ryhmän koulutuksiin. </p><ul><li><strong>Voit saada 1–10 pistettä hyväksytystä pääsy- ja soveltuvuuskokeesta.</strong> Hylätyn pääsykoetuloksen saanutta hakijaa ei voida valita koulutukseen. </li><li>Huomaathan, että kaikissa koulutuksissa ei ole pääsy- ja soveltuvuuskokeita. Tiedot pääsykokeesta löytyvät tämän sivun alaosasta.</li><li>Pääsykoetulosta käytetään kaikissa vastaavissa koulutuksissa, joihin olet hakenut. Jos olet hakenut useille eri aloille, on mahdollista, että saat kutsun useisiin kokeisiin.</li></ul><h2><strong>Tasapistetilanne</strong></h2><p>Saman pistemäärän saaneet hakijat asetetaan järjestykseen seuraavien perusteiden mukaisesti:</p><ol><li>hakutoivejärjestys</li><li>mahdollinen  pääsy- tai soveltuvuuskokeesta saatava pistemäärä</li><li>yleinen koulumenestys</li><li>painotettavat arvosanat.</li></ol><p>Jos pistemäärä on tämän jälkeen edelleen sama, opiskelijat valitaan satunnaisjärjestyksessä.<br /> </p><h2><strong>Kielikokeet</strong></h2><p>Saat kutsun kielikokeisiin, jos äidinkielesi on muu kuin opetuskieli eikä kielitaitoasi ole voitu todentaa muilla tavoin, esimerkiksi alla mainituilla todistuksilla. Kokeilla osoitat, että sinulla on riittävät valmiudet opetuskielen suulliseen ja kirjalliseen käyttämiseen. Kokeet ovat kaikille hakijoille samat.</p><p>Jos sinulla on jokin alla mainituista todistuksista, sinua ei kutsuta kielikokeeseen.</p><p><strong> Opetuskielen taitoasi pidetään riittävänä, jos</strong></p><ul><li>sinulla on perusopetuksen päättötodistus, joka on suoritettu vastaanottavan oppilaitoksen opetuskielellä (suomi, ruotsi tai saame).</li><li>olet suorittanut vähintään arvosanalla 7 perusopetuksessa toisen kotimaisen A-kielen oppimäärän. Kielen on oltava vastaanottavan oppilaitoksen opetuskieli.</li><li>olet suorittanut vähintään arvosanalla 7 perusopetuksen suomi tai ruotsi toisena kielenä oppimäärän. Kielen on oltava vastaanottavan oppilaitoksen opetuskieli.</li><li>olet suorittanut lukion oppimäärän tai ylioppilastutkinnon vastaanottavan oppilaitoksen opetuskielellä.</li><li>olet suorittanut lukiokoulutuksen koko oppimäärän jossakin seuraavista äidinkieli ja kirjallisuus-oppiaineen oppimääristä: suomi äidinkielenä, ruotsi äidinkielenä, saame äidinkielenä, suomi toisena kielenä, ruotsi toisena kielenä, suomi saamenkielisille, suomi viittomakielisille. Kielen on oltava vastaanottavan oppilaitoksen opetuskieli.</li><li>olet suorittanut lukiokoulutuksen toisen kotimaisen kielen koko oppimäärän. Kielen on oltava vastaanottavan oppilaitoksen opetuskieli.</li><li>olet suorittanut yleisten kielitutkintojen suomen tai ruotsin kielen tutkinnon kaikki osakokeet vähintään taitotasolla 3. Tai olet suorittanut Valtionhallinnon kielitutkintojen suomen tai ruotsin kielen suullisen ja kirjallisen taidon tutkinnon vähintään taitotasolla tyydyttävä.</li></ul><p>Oppilaitos voi jättää hakijan valitsematta ammatilliseen peruskoulutukseen, jos hänellä ei ole valmiutta opetuskielen suulliseen ja kirjalliseen käyttämiseen sekä ymmärtämiseen.</p><h2><strong>Harkintaan perustuva valinta </strong></h2><p>Oppilaitos voi ottaa opiskelijoita koulutuksiin harkinnan perusteella. Tällöin valinta tehdään valintapistemääristä riippumatta. Yhteen hakukohteeseen voidaan ottaa tällä tavoin enintään 30 prosenttia opiskelijoista. Harkintaan perustuvan valinnan syitä ovat:</p><ul><li>oppimisvaikeudet</li><li>sosiaaliset syyt</li><li>koulutodistusten puuttuminen tai todistusten vertailuvaikeudet. </li></ul><p>Koulutustarpeesi ja edellytyksesi suoriutua opinnoista arvioidaan ja otetaan huomioon, kun valintapistemäärästä poiketaan.</p><p>Huomaathan, että aiempi joustava valinta -menettely ei ole enää käytössä.</p>",
              "soraDescription": null,
              "prerequisite": {
                "value": "PK",
                "name": "Peruskoulu",
                "shortName": "PK",
                "description": "Peruskoulu",
                "uri": "pohjakoulutusvaatimustoinenaste_pk"
              },
              "exams": null,
              "childRefs": [
                {
                  "id": "1.2.246.562.5.67003105698",
                  "losId": "1.2.246.562.5.2013061010185704541592_1.2.246.562.10.85442777687",
                  "name": "Sähkö- ja automaatiotekniikka, sähköasentaja",
                  "qualification": "Sähköasentaja",
                  "prerequisite": {
                    "value": "PK",
                    "name": "Peruskoulu",
                    "shortName": "PK",
                    "description": "Peruskoulu",
                    "uri": "pohjakoulutusvaatimustoinenaste_pk"
                  }
                }
              ],
              "higherEdLOSRefs": [],
              "provider": {
                "id": "1.2.246.562.10.85442777687",
                "name": "Koulutuskeskus Sedu,  Kurikka",
                "applicationSystemIds": [
                  "1.2.246.562.5.2013112910480420004764",
                  "1.2.246.562.5.2014022711042555034240",
                  "1.2.246.562.5.2013080813081926341927",
                  "1.2.246.562.29.92175749016",
                  "1.2.246.562.29.90697286251"
                ],
                "postalAddress": {
                  "streetAddress": "Huovintie 1",
                  "streetAddress2": null,
                  "postalCode": "61300",
                  "postOffice": "KURIKKA"
                },
                "visitingAddress": {
                  "streetAddress": "Huovintie 1",
                  "streetAddress2": null,
                  "postalCode": "61300",
                  "postOffice": "KURIKKA"
                },
                "webPage": "http://www.sedu.fi",
                "email": "hakutoimisto@sedu.fi",
                "fax": null,
                "phone": "020 124 6108",
                "description": "<p><strong>Sedu Kurikka</strong> on monialainen ammatillisen koulutuksen kampus, jossa opiskelee yhteensä noin 450 nuorta yhteentoista eri ammattiin (ajoneuvoasentaja, koneistaja ja levyseppähitsaaja, talonrakentaja, sähköasentaja, elektroniikka-asentaja, puuseppä, sisustaja, verhoilija, putkiasentaja, turvallisuusvalvoja ja kokki). </p> <p><strong>Valinnaisiin opintoihin</strong> on kaikilla aloilla mahdollisuus ottaa erilaisia oppilaitoksessa tarjottavia <strong>musiikin opintoja</strong>. Kaikilla aloilla myös mahdollisuus suorittaa kaksi tutkintoa tai lukion aineopintoja yhteistyössä Kurikan lukion kanssa. </p> <p>Opetuspiste sijaitsee Kurikan keskustan ja sen palvelujen välittömässä läheisyydessä. Kurikka on n. 15 000 asukkaan vireä kaupunki, jossa on hyvä asua ja opiskella. Bussiyhteydet naapurikunnista mahdollistavat tarvittaessa päivittäisen kulkemisen kotoa käsin. </p> <p>Oppilaitoksen kanssa samalla tontilla sijaitsee maksuton ja valvottu <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Asuminen\">opiskelija-asuntola </a>80 asukkaalle. Asuntolassa opiskelijoille on iltaisin tarjolla ohjattua vapaa-ajan toimintaa.<strong> </strong></p> <p><strong>Asuntolassa asuvien erityistä tukea tarvitsevien opiskelijoiden on mahdollista hakea <a href=\"http://www.epsospsyk.fi/aake.html%20\">Pikku-AAKEn</a> tuen piiriin.</strong></p> <p>Lisätietoja Kurikan opetuspisteestä <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka\">http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka</a></p> <p> </p> <p><strong>KAKSOISTUTKINTO</strong></p> <p>Voit suorittaa ammatillisen tutkinnon lisäksi myös yo-tutkinnon. Katso lisätietoja</p> <p><a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Hae-opiskelemaan/Hae-opiskelijaksi/Kaksi-tutkintoa\">www.sedu.fi/kaksitutkintoa</a></p> <p> </p> <p><strong>URHEILIJANA HAKEMINEN</strong></p> <p>Urheilupainotteinen ammatillinen perustutkinto</p> <ul> <li><strong>Voit saada 1–3 lisäpistettä urheilullisista saavutuksista, kun haet urheilijalle tarkoitettuun ammatilliseen koulutukseen.</strong> Katso lisätiedot sivulta <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Hae-opiskelemaan/Hae-opiskelijaksi/Urheilupainotteinen-ammatillinen-perustutkinto\">www.sedu.fi/urheilijanhaku</a></li> </ul> <p>Huom! Palauta valmentajan täyttämä lisätietolomake viimeistään 7 vrk haun päättymisen jälkeen osoitteella:</p> <p>Osoite:</p> <p>Koulutuskeskus Sedu, Hakutoimisto, Koulukatu 41, 60100 Seinäjoki</p> <p>Muut yhteystiedot:</p> <p>hakutoimisto@sedu.fi tai p. 020 124 5258</p> <p>Löydät Valmentajatietolomakkeen opintopolku.fi – palvelusta tai Sedun svuilta <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Hae-opiskelemaan/Hae-opiskelijaksi/Urheilupainotteinen-ammatillinen-perustutkinto\">www.sedu.fi/urheilijanhaku</a></p> <p> </p> <p><strong>HARKINTAAN PERUSTUVA VALINTA</strong></p> <ul> <li>Jos haet harkintaan perustuvalla valinnalla, lähetä hakemuksen liitteet hakutoimistoon.</li> </ul> <ul> <li>HUOM! Jokaiseen hakukohteeseen oltava kopioituna omat liitteet. Tulosta mukaan hakemuksesi opintopolusta.</li> </ul> <p>Osoite:</p> <p>Koulutuskeskus Sedu, Hakutoimisto, Koulukatu 41, 60100 Seinäjoki</p> <p>Muut yhteystiedot:</p> <p>hakutoimisto@sedu.fi tai p. 020 124 5258</p> <p>Katso lisätietoja</p> <p><a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Hae-opiskelemaan/Hae-opiskelijaksi/Harkintaan-perustuva-valinta\">www.sedu.fi/harkintaanperustuvavalinta</a></p> <p> </p> <p><strong>ERILLISHAKU</strong></p> <p>Jos olet jo suorittanut jonkun ammatillisen tutkinnon, voit hakea erillishaussa suoraan oppilaitokseen. Hakulomake ja lisätiedot osoitteessa <a href=\"http://www.sedu.fi\">www.sedu.fi</a></p> <p> </p> <p> </p> <p> </p> <p> </p>",
                "healthcare": "<p><a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Tukea-opintoihin/Opiskelijaa-tukevat-palvelut/Opiskelijaterveydenhuolto\">Opiskelijaterveydenhuoltoon</a> kuuluvat terveydenhoitaja-, lääkäri-, hammaslääkäri- ja psykologipalvelut.</p><p>Terveydenhoitajan vastaanotto on joka päivä. Terveydenhoitajan vastaanotolle voit mennä oma-aloitteisesti kaikissa terveyttä ja hyvinvointia koskevissa asioissa. </p><p>Lääkärin, hammaslääkärin ja psykologin vastaanotoille pääsee ajanvarauksella.</p>",
                "accessibility": "<p>Opetuspisteessä toimii <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Opiskelijahuolto-Kurikassa\">opiskelijahuoltoryhmä</a> (OHR), johon kuuluvat terveydenhoitaja, kuraattori, opinto-ohjaajat sekä koulutuspäällikkö. Opiskeluun, vapaa-aikaan, terveyteen tai muihin henkilökohtaisiin asioihin liittyvissä ongelmissa voit aina kääntyä OHR:n jäsenten puoleen.</p> <p><strong>Opintotoimistossa</strong> voit hoitaa opintotukiasiat, koulumatkatukiasiat, opiskelijatodistukset yms. opiskeluun liittyvät asiat.</p> <p>Kurikassa puualalle sekä hotelli-, ravintola- ja cateringalalle haetaan yhteen ryhmään, ja opintojen edetessä ryhmistä muodostetaan erilaisia tiimejä. Jokin tiimeistä on suunniteltu erityistä tukea tarvitseville ja/tai työvaltaisesti opiskeleville pienryhmäläisille. Katso lisää <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Erityisopetus\">erityisen tuen tarjoamisesta</a>. Muissa ryhmissä on tarvittaessa tarjolla erityisopettajien tukea. Opettajien apuna toimii joillakin aloilla ammattiohjaaja. Opetuspisteessä toimii Maijan Paja, jossa tarvittaessa opiskelijat voivat suorittaa opintoja (mm. rästikursseja ja ATTO-opintoja) erityisopettajan pienryhmäohjauksessa.</p> <p>Opiskelijat saavat äänensä kuuluville kaksi kertaa lukuvuodessa kokoontuvissa alakohtaisissa opiskelijahuoltoryhmissä, ja lisäksi opetuspisteen opiskelijafoorumi kokoontuu kahdesti vuodessa. Kurikan opetuspisteen opiskelijat ovat edustettuna Sedun <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Iloa-opintoihin/Opiskelijakunta\">FOKUS-ryhmässä</a>. Koulutetut tutor-opiskelijat toimivat aktiivisesti opetuspisteen esittely- ja markkinointitehtävissä.</p> <p>Oppilaitoksessa toimii maanantai-iltaisin palloilukerho ja kaikkina iltoina on käytettävissä opetuspisteen oma hyvin varustettu kuntosali. Asuntolassa järjestetään erilaista vapaa-ajan ja harrastetoimintaa iltaisin. Lisäksi Kurikan kaupungissa on tarjolla laaja kirjo erilaisia <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Vapaa-ajan-toiminta%20\">vapaa-ajan aktiviteetteja</a>.  </p> <p> </p>",
                "learningEnvironment": "<p>Teorialuokissa ja työsaleissa on ajantasaiset koneet, laitteet ja opetusvälineistö. Opetuspisteessä on kuusi atk-luokkaa. Lähiopetuksen tukena on käytössä Moodle-oppimisympäristö ja työssäoppimisessa eTaitava -järjestelmä. </p><p>Opiskelijat tekevät runsaasti asiakas- ja tilaustöitä, jotka opettavat yrittäjämäistä asennetta ja valmentavat työelämään.  Opiskelijat voivat lisäksi suorittaa opintojaan ja harjoitella yrittäjyyttä osuuskunnassa tai NY-yrityksessä. Katso lisää <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Yrittajyys\">http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Yrittajyys</a> </p><p>Opiskelijoilla on mahdollisuus suorittaa opintoihin sisältyvä työssäoppiminen ulkomailla. Sedu tukee opiskelijaa asumis- ja matkustuskustannuksissa. Katso lisää kansainvälisyydestä <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Kansainvalisyys\">http://www.sedu.fi/Koulutuskeskus-Sedu/Opiskelu/Kansainvalisyys</a></p><p>Kaikilla opiskelijoilla Kurikan Sedussa on mahdollisuus sisällyttää opintoihinsa <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Musiikkivalinnaisuus%20\"><strong>musiikin opintoja</strong></a>. Nämä opinnot sisältävät musiikin teorian ja säveltapailun lisäksi henkilökohtaisia soittotunteja ja bändiharjoituksia.</p><p>Elektroniikka-asentajan opinnoissa voit erikoistua esitystekniikkaan ja hankkia erityisosaamista <a href=\"http://www.sedu.fi/Koulutuskeskus-Sedu/Paikkakuntavalikko/Kurikka/Valoa-ja-aanta-oppimaan\">valo-, ääni- ja kuvatekniikkaan </a></p>",
                "dining": "<p>Opiskelijoille tarjotaan koulupäivinä maksuton omassa talossa valmistettu lounas, joka sisältää pääruoan, lämpimät lisäkkeet, tuoresalaatin, ruokajuomat ja leivät sekä useimmiten myös jälkiruoan.</p><p>Aamu- ja iltapäivän kahvitunneilla on ruokalasta ostettavissa edullinen välipala.</p>",
                "livingExpenses": "<p>Opiskelijat ostavat tarvitsemansa oppikirjat ja maksavat osan työvälineistä, jotka jäävät heille itselleen opintojen jälkeen. </p><p>Opiskelijalla tulee olla työnopetuksessa asianmukainen työasu ja työsuojelumääräysten mukaiset jalkineet. Työjalkineet, yhtenäiset haalarit ja muut suojavaatteet hankitaan keskitetysti koulun kautta opintonsa aloittaville opiskelijoille. Työvaatekustannukset vaihtelevat aloittain. </p><p>Sedun opiskelija-asuntolassa asuminen on maksutonta.</p>",
                "living": null,
                "yearClock": null,
                "financingStudies": null,
                "insurances": null,
                "leisureServices": null,
                "social": [
                  {
                    "name": "facebook",
                    "url": "https://www.facebook.com/#!/pages/Koulutuskeskus-Sedu/326925121003?fref=ts"
                  },
                  {
                    "name": "google_plus",
                    "url": "https://plus.google.com/u/0/115783246003925085079/posts"
                  },
                  {
                    "name": "muu",
                    "url": "http://www.youtube.com/user/KoulutuskeskusSedu"
                  }
                ],
                "pictureFound": true,
                "athleteEducation": true,
                "applicationOffice": {
                  "name": "Sedu hakutoimisto",
                  "phone": "020 124 5258",
                  "email": "hakutoimisto@sedu.fi",
                  "www": "http://www.sedu.fi",
                  "visitingAddress": {
                    "streetAddress": "Koulukatu 41",
                    "streetAddress2": null,
                    "postalCode": "60100",
                    "postOffice": "SEINÄJOKI"
                  },
                  "postalAddress": {
                    "streetAddress": "Koulukatu 41",
                    "streetAddress2": null,
                    "postalCode": "60100",
                    "postOffice": "SEINÄJOKI"
                  }
                },
                "homeplace": "Kurikka"
              },
              "specificApplicationDates": false,
              "applicationStartDate": 1393221600000,
              "applicationEndDate": 1394802000000,
              "applicationPeriodName": "",
              "canBeApplied": false,
              "nextApplicationPeriodStarts": null,
              "requiredBaseEducations": [
                "3",
                "2",
                "6",
                "1",
                "7",
                "0"
              ],
              "attachments": null,
              "emphasizedSubjects": null,
              "additionalInfo": null,
              "additionalProof": null,
              "overallScoreLimit": null,
              "kaksoistutkinto": true,
              "athleteEducation": false,
              "vocational": true,
              "educationCodeUri": "koulutus_351407",
              "status": null,
              "eligibilityDescription": null,
              "type": "TUTKINTO",
              "educationTypeUri": "et3",
              "hakuaikaId": null,
              "organizationGroups": [],
              "kotitalous": false
            }
          ],
          "asOngoing": false,
          "nextApplicationPeriodStarts": null,
          "status": null,
          "applicationFormLink": null,
          "hakutapa": "01",
          "hakutyyppi": "01"
        }
      ],
      "availableTranslationLanguages": [
        {
          "value": "FI",
          "name": "suomi",
          "shortName": "suomi",
          "description": "suomi",
          "uri": "kieli_fi"
        },
        {
          "value": "FI",
          "name": "suomi",
          "shortName": "suomi",
          "description": "suomi",
          "uri": "kieli_fi"
        },
        {
          "value": "FI",
          "name": "suomi",
          "shortName": "suomi",
          "description": "suomi",
          "uri": "kieli_fi"
        }
      ]
    }
  ],
  "creditValue": "120",
  "creditUnit": "ov",
  "translationLanguage": "fi",
  "topics": [
    {
      "value": " 82",
      "name": "Sähkö- ja automaatiotekniikka",
      "shortName": "Sähkö- ja automaatiotekniikka",
      "description": "Sähkö- ja automaatiotekniikka",
      "uri": "teemat_11.aiheet_82"
    }
  ],
  "themes": [
    {
      "value": " 11",
      "name": "Tekniikka ",
      "shortName": "Tekniikka ",
      "description": "Tekniikka ",
      "uri": "teemat_11"
    }
  ]
});
*/

