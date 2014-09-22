<#include "macros.ftl">
<!DOCTYPE html>
<html>
<#-- <#include "resources.ftl"> -->
<head>
<#include "head.ftl">
</head>
<body>
<#assign providersBaseUrl = "${baseUrl}${lang}/hakemisto/oppilaitokset"/>
<#assign educationBaseUrl = "${baseUrl}${lang}/hakemisto/koulutukset"/>
<div class="app-container">
    <div class="container">
        <div class="row margin-vertical-default">
            <div class="col-xs-16">
                <div class="btn-group">
                <#list alphabets as alphabet>
                    <#if alphabet == letter>
                        <a href="javascript:void(0);" class="btn btn-primary active">${alphabet}</a>
                    <#elseif validCharacters?seq_contains(alphabet)>
                        <a href="${providersBaseUrl}/${alphabet}" class="btn btn-primary">${alphabet}</a>
                    <#else>
                        <a href="javascript:void(0);" class="btn btn-primary disabled">${alphabet}</a>
                    </#if>
                </#list>
                </div>
            </div>
        </div>

        <div class="row margin-vertical-default">
            <div class="col-xs-16">
            <#list providerTypes as providerType>

                <#if providerType.value == "99">
                    <#assign providerName><@msg "provider.type.other"/></#assign>
                <#else>
                        <#assign providerName = providerType.name>
                </#if>

                <#if providerType.value == selectedProviderType>
                    ${providerName}
                <#else>
                    <a href="${providersBaseUrl}/${letter}/${providerType.value}">${providerName}</a>
                </#if>
                <#if providerType_has_next> | </#if>

            </#list>
            </div>
        </div>

        <div class="row margin-vertical-default">
            <div class="col-xs-16">
                <ul class="inside">
                <#list providers as provider>
                    <li><a href="${educationBaseUrl}/${provider.id}">${provider.name}</a></li>
                </#list>
                </ul>
            </div>
        </div>
    </div>
</div>
<#include "scripts.ftl">
</body>
</html>