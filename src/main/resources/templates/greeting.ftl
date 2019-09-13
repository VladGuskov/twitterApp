<#include "parts/security.ftl">
<#import "parts/common.ftl" as c>

<@c.page>
    <h5>
        <#if name = "guest">
            Привет, Гость
        <#else>
            Привет, ${name}
        </#if>
    </h5>
    <div>
        Это простой клон твиттера
    </div>
    <#if name = "guest">
        <a class="btn btn-primary mt-3" href="/login">Войти</a>
    </#if>
</@c.page>