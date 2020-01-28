package io.golos.domain.dto

import java.util.*

sealed class NotificationDomain (
    open val id: String,
    open val isNew: Boolean,
    open val createTime: Date,
    open val user: UserNotificationDomain
)

data class MentionNotificationDomain(
    override val id: String,
    override val isNew: Boolean,
    override val createTime: Date,
    override val user: UserNotificationDomain,
    val comment: NotificationCommentDomain
) : NotificationDomain(id, isNew, createTime, user)

data class ReplyNotificationDomain(
    override val id: String,
    override val isNew: Boolean,
    override val createTime: Date,
    override val user: UserNotificationDomain,
    val comment: NotificationCommentDomain
) : NotificationDomain(id, isNew, createTime, user)

data class SubscribeNotificationDomain(
    override val id: String,
    override val isNew: Boolean,
    override val createTime: Date,
    override val user: UserNotificationDomain
) : NotificationDomain(id, isNew, createTime, user)

data class UpVoteNotificationDomain(
    override val id: String,
    override val isNew: Boolean,
    override val createTime: Date,
    override val user: UserNotificationDomain,
    val comment: NotificationCommentDomain
) : NotificationDomain(id, isNew, createTime, user)

data class UserNotificationDomain(val id: UserIdDomain, val name: String?, val avatar: String?)

