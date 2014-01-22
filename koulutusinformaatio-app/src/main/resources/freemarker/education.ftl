<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8 ">

</head>
<body>

<#list alphabets as alphabet>
    <#if alphabet == letter>
    ${alphabet}
    <#else>
    <a href="../../${alphabet}">${alphabet}</a>
    </#if>
    <#if alphabet_has_next>/</#if>
</#list>

<br>
<br>

${provider}

<br>
<br>
<#list learningOpportunities as lo>
    <a href="${baseUrl}${lo.type?lower_case}/${lo.id}">${lo.name}</a>
    <br>
</#list>

</body>

</html>