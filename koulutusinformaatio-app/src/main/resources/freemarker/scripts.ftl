<script type="text/javascript">
var lang = getLanguageFromHost(),
    rgx = new RegExp('\/' + lang + '\/', 'g'),
    res = window.location.href.search(rgx);

function getLanguageFromHost() {
    var x = window.location.host.split('.')
    if (x.length < 2)
        return 'fi'
    switch (x[x.length - 2]) {
        case 'opintopolku': return 'fi'
        case 'studieinfo': return 'sv'
        case 'studyinfo': return 'en'
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
