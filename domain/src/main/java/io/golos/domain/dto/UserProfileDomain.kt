package io.golos.domain.dto

import java.util.*

data class UserProfileDomain(
    val userId: UserIdDomain,
    val coverUrl: String?,
    val avatarUrl: String?,
    val bio: String?,
    val name: String,
    val joinDate: Date,
    val followersCount: Int,
    val followingsCount: Int,
    val communitiesSubscribedCount: Int,
    val highlightCommunities: List<CommunityDomain>,
    val commonFriends: List<UserDomain>,

    /**
     * By current user
     */
    val isBlocked: Boolean,

    /**
     * Of current user
     */
    val isInBlacklist: Boolean,

    /**
     * To current user
     */
    val isSubscribed: Boolean,

    /**
     * Is a current user subscribed to the user
     */
    val isSubscription: Boolean
)