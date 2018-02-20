<script type="text/javascript">
var lang = getLanguageFromHost(),
    rgx = new RegExp('\/' + lang + '\/', 'g'),
    res = window.location.href.search(rgx);

function getLanguageFromHost(host) {
    if (!host)
        host = document.location.host;
    var x = host.split('.');
    if (x.length < 2) return 'fi';
    var domain = x[x.length - 2];
    if (domain.indexOf('opintopolku') > -1) {
        return 'fi';
    } else if (domain.indexOf('studieinfo') > -1) {
        return 'sv';
    } else if (domain.indexOf('studyinfo') > -1) {
        return 'en'
    }
    return 'fi'
}
if (res < 0) {
    window.location.href = '${baseUrl}' + lang + '/hakemisto/oppilaitokset';
}
</script>
<script type="text/javascript" src="${baseUrl}raamit/load"></script>
<script>
    jQuery("html").on("oppija-raamit-loaded", function() {
        jQuery("body").show();
    });
</script>
