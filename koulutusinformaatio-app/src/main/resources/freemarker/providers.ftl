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
                    <a href="javascript:void(0);" class="button active">${alphabet}</a>
                <#elseif validCharacters?seq_contains(alphabet)>
                    <a href="${alphabet}" class="button">${alphabet}</a>
                <#else>
                    <a href="javascript:void(0);" class="button disabled">${alphabet}</a>
                </#if>
            </#list>
            </div>
            <div class="clear"></div>

            <p>
            <#list providers as provider>
                <a href="${letter}/${provider.id}/koulutukset">${provider.name}</a>
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