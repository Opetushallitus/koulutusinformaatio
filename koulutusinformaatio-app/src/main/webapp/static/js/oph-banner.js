var ophBannerRendered = false;

function showOphBanner() {
    if (ophBannerRendered) return;
    var oph_host = location.host.split(':')[0];
    var bannerText;
    if (oph_host.indexOf('koulutus') == 0) bannerText = 'koulutus';
    else if (oph_host.indexOf('testi') == 0) bannerText = 'QA';
    else if (oph_host.indexOf('xtest-') == 0) bannerText = 'Kielistudio';
    else if (oph_host.indexOf('test-') == 0) bannerText = 'Reppu';
    else if (oph_host.indexOf('itest-') == 0) bannerText = 'Luokka';
    else if (oph_host.indexOf('localhost') == 0) bannerText = oph_host;
    if (bannerText) {
        var $target = $("header #home-link, #site #sitecontent .content h1").first();
        if ($target.length) {
            var oph_header = "<span style='padding-left:20px;color: #ff0000;text-transform: uppercase;font-weight: bold; font-size: larger'>" + bannerText + "</span>";
            $target.after(oph_header);
            ophBannerRendered = true;
        }
    }
}

$(document).ready(function(){
    showOphBanner();
    setTimeout(showOphBanner, 100); // make sure works also after rendering angular template
    setTimeout(showOphBanner, 1000);
});
