<#include "macros.ftl">
<!DOCTYPE html>
<html>
<head>
    <#include "head.ftl">
</head>
<body>
<#assign providersBaseUrl = "${baseUrl}${lang}/hakemisto/oppilaitokset"/>
<div class="app-container">
    <div class="container">
        <div class="row">
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
        <div class="row">
            <div class="col-xs-16">
                <h1>${provider}</h1>
            </div>
        </div>
        <div class="row">
            <div class="col-xs-16">
                <ul class="inside">
                <#list learningOpportunities as lo>
                    <li><a href="${ngBaseUrl}${lo.type?lower_case}/${lo.id}">${lo.name}</a></li>
                </#list>
                </ul>
            </div>
        </div>
    </div>
</div>
<#include "scripts.ftl">
</body>
</html>