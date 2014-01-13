<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8 ">

</head>
<body>
${provider}
<#list learningOpportunities as lo>
    <a href="${baseUrl}/${lo.type?lower_case}/${lo.id}">${lo.name}</a>
    <br>
</#list>

</body>

</html>