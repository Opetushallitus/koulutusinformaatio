<script src="${baseUrl}app/lib/jquery.enhanced.cookie.js" type="text/javascript"></script>
<script type="text/javascript">
var cfg = {useLocalStorage: false, path: "/" },
    lang = jQuery.cookie('i18next', cfg),
    rgx = new RegExp('\/' + lang + '\/', 'g'),
    res = window.location.href.search(rgx);

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
