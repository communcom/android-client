package io.golos.cyber_android.ui.screens.community_page.dto

data class CommunityInfo(
    val name:String,
    val avatarUrl:String?,
    val subscribersCount:Int,
    val postsCount:Int,
    val reportCount:Int,
    val proposalCount:Int
)