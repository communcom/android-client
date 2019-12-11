package io.golos.data.dto

import com.squareup.moshi.Json


data class CommunityEntity (
    @Json(name = "community_id")
    val communityId: String,
    @Json(name = "alias")
    val alias: String?,
    @Json(name = "name")
    val name: String,
    @Json(name = "avatar_url")
    val avatarUrl: String?,
    @Json(name = "cover_url")
    val coverUrl: String?,
    @Json(name = "subscribers_count")
    val subscribersCount: Int,
    @Json(name = "posts_count")
    val postsCount: Int,
    @Json(name = "is_subscribed")
    val isSubscribed: Boolean
)