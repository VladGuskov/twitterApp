<#import "parts/common.ftl" as c>
<#import "parts/login.ftl" as l>

<@c.page>
<a class="mb-1">Добавить нового пользователя</a>
${message?ifExists}
<@l.login "/registration" true />
</@c.page>