package io.golos.domain.dto

data class CommunityDomain (
    val communityId: String,
    val name: String,
    var logo: String?,
    val followersCount: Long,
    val postsCount: Long,
    val isSubscribed: Boolean
)