<#include "macros.ftl">
<!DOCTYPE html>
<html>
<#-- <#include "resources.ftl"> -->
<head>
    <#include "head.ftl">
</head>
<body>
<#include "top.ftl">
<#assign providersBaseUrl = "${baseUrl}${lang}/hakemisto/oppilaitokset"/>
    <div id="page">
        <div class="grid16-16">
            <div class="buttongroup margin-bottom-4">
            <#list alphabets as alphabet>
                <#if alphabet == letter>
                    <a href="javascript:void(0);" class="button active">${alphabet}</a>
                <#elseif validCharacters?seq_contains(alphabet)>
                    <a href="${providersBaseUrl}/${alphabet}" class="button">${alphabet}</a>
                <#else>
                    <a href="javascript:void(0);" class="button disabled">${alphabet}</a>
                </#if>
            </#list>
            </div>
            <div class="clear"></div>
            <h1>${provider}</h1>
            <p>
            <#list learningOpportunities as lo>
                <a href="${ngBaseUrl}${lo.type?lower_case}/${lo.id}">${lo.name}</a>
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