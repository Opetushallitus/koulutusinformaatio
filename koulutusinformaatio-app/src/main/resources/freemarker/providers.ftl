<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8 ">


</head>
<body>
<#list alphabets as alphabet>
    <#if alphabet == letter>
        ${alphabet}
    <#else>
        <a href="${alphabet}">${alphabet}</a>
    </#if>
    <#if alphabet_has_next>/</#if>
</#list>

<br>
<#list providers as provider>
    <a href="${provider.id}/koulutukset">${provider.name}</a>
    <br>
</#list>
</body>

</html>