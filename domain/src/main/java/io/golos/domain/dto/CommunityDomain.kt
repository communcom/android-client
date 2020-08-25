package io.golos.domain.dto

data class CommunityDomain (
    val communityId: CommunityIdDomain,
    val alias: String?,
    val name: String,
    val avatarUrl: String?,
    val coverUrl: String?,
    val subscribersCount: Int,
    val postsCount: Int,
    val isSubscribed: Boolean,
    val isInBlacklist:Boolean = false
)