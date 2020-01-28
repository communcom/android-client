package io.golos.domain.dto

import java.util.*

sealed class BaseNotificationDomain (
    open val id: String,
    open val type: NotificationTypeDomain,
    open val isNew: Boolean,
    open val createTime: Date,
    open val lastNotificationTime: String,
    open val user: UserNotificationDomain
)

enum class NotificationTypeDomain {
    MENTION, SUBSCRIBE, UP_VOTE, REPLY
}

data class MentionNotificationDomain(
    override val id: String,
    override val type: NotificationTypeDomain,
    override val isNew: Boolean,
    override val createTime: Date,
    override val lastNotificationTime: String,
    override val user: UserNotificationDomain
) : BaseNotificationDomain(id, type, isNew, createTime, lastNotificationTime, user)

data class ReplyNotificationDomain(
    override val id: String,
    override val type: NotificationTypeDomain,
    override val isNew: Boolean,
    override val createTime: Date,
    override val lastNotificationTime: String,
    override val user: UserNotificationDomain
) : BaseNotificationDomain(id, type, isNew, createTime, lastNotificationTime, user)

data class SubscribeNotificationDomain(
    override val id: String,
    override val type: NotificationTypeDomain,
    override val isNew: Boolean,
    override val createTime: Date,
    override val lastNotificationTime: String,
    override val user: UserNotificationDomain
) : BaseNotificationDomain(id, type, isNew, createTime, lastNotificationTime, user)

data class UpVoteNotificationDomain(
    override val id: String,
    override val type: NotificationTypeDomain,
    override val isNew: Boolean,
    override val createTime: Date,
    override val lastNotificationTime: String,
    override val user: UserNotificationDomain
) : BaseNotificationDomain(id, type, isNew, createTime, lastNotificationTime, user)

data class UserNotificationDomain(val id: UserIdDomain, val name: String?, val avatar: String?)

