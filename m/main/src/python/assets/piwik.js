var siteDomain = document.domain;
var piwikSiteId = 2;
if (siteDomain == 'opintopolku.fi') {
    piwikSiteId = 4;
} else if (siteDomain == 'virkailija.opintopolku.fi') {
    piwikSiteId = 3;
} else if (siteDomain == 'testi.opintopolku.fi') {
    piwikSiteId = 1;
} else if (siteDomain == 'testi.virkailija.opintopolku.fi') {
    piwikSiteId = 5;
}

var _paq = _paq || [];
_paq.push(["setDocumentTitle", document.domain + "/" + document.title]);
_paq.push(["trackPageView"]);
_paq.push(["enableLinkTracking"]);

(function () {
    var u = "https://analytiikka.opintopolku.fi/piwik/";
    _paq.push(["setTrackerUrl", u + "piwik.php"]);
    _paq.push(["setSiteId", piwikSiteId]);
    var d = document, g = d.createElement("script"), s = d.getElementsByTagName("script")[0];
    g.type = "text/javascript";
    g.defer = true;
    g.async = true;
    g.src = u + "piwik.js";
    s.parentNode.insertBefore(g, s);
})();