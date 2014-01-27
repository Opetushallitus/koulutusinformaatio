<#include "macros.ftl">
<!DOCTYPE html>
<html>
<#-- <#include "resources.ftl"> -->
<head>
    <#include "head.ftl">
</head>
<body>
<#include "top.ftl">
    <div id="page">
        <div class="grid16-16">
            <div class="buttongroup margin-bottom-4">
                <#list alphabets as alphabet>
                    <#if alphabet == letter>
                    <a href="#" class="button active">${alphabet}</a>
                    <#else>
                    <a href="../../${alphabet}" class="button">${alphabet}</a>
                    </#if>
                </#list>
            </div>
            <div class="clear"></div>
            <h1>${provider}</h1>
            <p>
            <#list learningOpportunities as lo>
                <a href="${baseUrl}${lo.type?lower_case}/${lo.id}">${lo.name}</a>
                <br>
            </#list>
            </p>
        </div>
    </div>
    <div class="clear"></div>
    <#include "footer.ftl">
    <#include "scripts.ftl">
</body>
</html>