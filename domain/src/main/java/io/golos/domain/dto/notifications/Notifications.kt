package io.golos.domain.dto.notifications

import io.golos.domain.dto.CommunityDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.UserIdDomain
import java.util.*

sealed class NotificationDomain (
    open val id: String,
    open val isNew: Boolean,
    open val createTime: Date,
    open val user: UserNotificationDomain,
    open val currentUserId: UserIdDomain,
    open val currentUserName: String
)

data class MentionNotificationDomain(
    override val id: String,
    override val isNew: Boolean,
    override val createTime: Date,
    override val user: UserNotificationDomain,
    val comment: NotificationCommentDomain?,
    val post: NotificationPostDomain?,
    override val currentUserId: UserIdDomain,
    override val currentUserName: String
) : NotificationDomain(id, isNew, createTime, user, currentUserId, currentUserName)

data class ReplyNotificationDomain(
    override val id: String,
    override val isNew: Boolean,
    override val createTime: Date,
    override val user: UserNotificationDomain,
    val comment: NotificationCommentDomain,
    override val currentUserId: UserIdDomain,
    override val currentUserName: String
) : NotificationDomain(id, isNew, createTime, user, currentUserId, currentUserName)

data class SubscribeNotificationDomain(
    override val id: String,
    override val isNew: Boolean,
    override val createTime: Date,
    override val user: UserNotificationDomain,
    override val currentUserId: UserIdDomain,
    override val currentUserName: String
) : NotificationDomain(id, isNew, createTime, user, currentUserId, currentUserName)

data class UpVoteNotificationDomain(
    override val id: String,
    override val isNew: Boolean,
    override val createTime: Date,
    override val user: UserNotificationDomain,
    val comment: NotificationCommentDomain?,
    val post: NotificationPostDomain?,
    override val currentUserId: UserIdDomain,
    override val currentUserName: String
) : NotificationDomain(id, isNew, createTime, user, currentUserId, currentUserName)

data class TransferNotificationDomain(
    override val id: String,
    override val isNew: Boolean,
    override val createTime: Date,
    override val user: UserNotificationDomain,      // From

    val receiver: UserIdDomain,

    val amount: Double,
    val pointType: String?,

    val community: CommunityDomain,

    override val currentUserId: UserIdDomain,
    override val currentUserName: String
) : NotificationDomain(id, isNew, createTime, user, currentUserId, currentUserName)

data class RewardNotificationDomain(
    override val id: String,
    override val isNew: Boolean,
    override val createTime: Date,
    override val user: UserNotificationDomain,
    val amount: Double,

    val community: CommunityDomain,

    override val currentUserId: UserIdDomain,
    override val currentUserName: String
) : NotificationDomain(id, isNew, createTime, user, currentUserId, currentUserName)

data class ReferralPurchaseBonusNotificationDomain(
    override val id: String,
    override val isNew: Boolean,
    override val createTime: Date,
    override val user: UserNotificationDomain,  // from

    val amount: Double,
    val pointType: String,
    val userId: UserIdDomain,
    val percent: Int,
    val referral: UserNotificationDomain,

    override val currentUserId: UserIdDomain,
    override val currentUserName: String
) : NotificationDomain(id, isNew, createTime, user, currentUserId, currentUserName)

data class ReferralRegistrationBonusNotificationDomain(
    override val id: String,
    override val isNew: Boolean,
    override val createTime: Date,
    override val user: UserNotificationDomain,  // from

    val amount: Double,
    val pointType: String,
    val userId: UserIdDomain,
    val referral: UserNotificationDomain,

    override val currentUserId: UserIdDomain,
    override val currentUserName: String
) : NotificationDomain(id, isNew, createTime, user, currentUserId, currentUserName)

data class UnsupportedNotificationDomain(
    override val id: String,
    override val isNew: Boolean,
    override val createTime: Date,
    override val user: UserNotificationDomain,

    override val currentUserId: UserIdDomain,
    override val currentUserName: String
) : NotificationDomain(id, isNew, createTime, user, currentUserId, currentUserName)

data class DonationNotificationDomain(
    override val id: String,
    override val isNew: Boolean,
    override val createTime: Date,
    override val user: UserNotificationDomain,      // From

    val postId: ContentIdDomain,
    val postTextBrief: String?,
    val postImageUrl: String?,

    val amount: Double,
    val pointType: String?,

    val community: CommunityDomain,

    override val currentUserId: UserIdDomain,
    override val currentUserName: String
) : NotificationDomain(id, isNew, createTime, user, currentUserId, currentUserName)

data class UserNotificationDomain(val id: UserIdDomain, val name: String?, val avatar: String?)

