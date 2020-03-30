package io.golos.cyber_android.ui.screens.notifications.view.list.items

import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.UserIdDomain
import java.util.*

data class RewardNotificationItem(
    override val version: Long,
    override val id: Long,
    override val isFirstItem: Boolean,
    override val isLastItem: Boolean,

    override val notificationId: String,
    override val createTime: Date,
    override val isNew: Boolean,

    override val userId: String,
    override val userName: String?,
    override val userAvatar: String?,

    val amount: Double,

    val communityId: CommunityIdDomain,
    val communityAlias: String,
    val communityName: String,
    val communityAvatarUrl: String?,

    val currentUserId: UserIdDomain,
    val currentUserName: String
) :
    BaseNotificationItem(
        version,
        id,
        isFirstItem,
        isLastItem,
        notificationId,
        createTime,
        isNew,
        userId,
        userName,
        userAvatar
    ), VersionedListItem