<!DOCTYPE html>
<html>
<head>
    <#include "head.ftl">
</head>
<body>
<#include lang + "/top.ftl">
    <div id="page">
        <div class="grid16-16">
            <div class="buttongroup margin-bottom-4">
            <#list alphabets as alphabet>
                <#if alphabet == letter>
                    <a href="#" class="button active">${alphabet}</a>
                <#else>
                    <a href="${alphabet}" class="button">${alphabet}</a>
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
</body>
</html>