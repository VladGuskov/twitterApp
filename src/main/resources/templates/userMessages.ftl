<#import "parts/common.ftl" as c>
<#include "parts/security.ftl">

<@c.page>
    <h3>${userChannel.username}</h3>
        <#if !isCurrentUser>
            <#if isSubscriber>
                <a class="btn btn-info" href="/user/unsubscribe/${userChannel.id}">Отписаться</a>
            <#else>
                <a class="btn btn-info" href="/user/subscribe/${userChannel.id}">Подписаться</a>
            </#if>
        </#if>
    <div class="container my-3">
        <div class="row">
            <div class="col">
                <div class="card">
                    <div class="card-body">
                        <div class="card-title">Подписки</div>
                        <h3 class="card-text">
                            <a href="/user/subscriptions/${userChannel.id}/list">${subscriptionsCount}</a>
                        </h3>
                    </div>
                </div>
            </div>
            <div class="col">
                <div class="card">
                    <div class="card-body">
                        <div class="card-title">Подписчики</div>
                        <h3 class="card-text">
                            <a href="/user/subscribers/${userChannel.id}/list">${subscribersCount}</a>
                        </h3>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <#if isCurrentUser && isAdmin>
        <#include "parts/messageEdit.ftl" />
        <#if message??>
            <a class="btn btn-warning" role="button" href="/user-messages/delete/${message.author.id}?message=${message.id}">Удалить</a>
        </#if>
    </#if>
    <#if isCurrentUser && !isAdmin>
        <#include "parts/messageEdit.ftl" />
        <#if message??>
            <a class="btn btn-warning" role="button" href="/user-messages/delete/${message.author.id}?message=${message.id}">Удалить</a>
        </#if>
    </#if>
    <#if !isCurrentUser && isAdmin>
        <#if message??>
            <div class="card-columns">
                <div class="card my-3" data-id="${message.id}">
                    <#if message.filename??>
                        <img src="/img/${message.filename}" class="card-img-top" />
                    </#if>
                    <div class="m-2">
                        <span>${message.text}</span>
                        <i>#${message.tag}</i>
                    </div>
                    <div class="card-footer text-muted">
                        <a href="/user-messages/${message.author.id}">${message.authorName}</a>
                    </div>
                </div>
            </div>
            <a class="btn btn-warning" role="button" href="/user-messages/delete/${message.author.id}?message=${message.id}">Удалить</a>
        </#if>
    </#if>
    <#include "parts/messageList.ftl" />
</@c.page>