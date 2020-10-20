package io.golos.cyber_android.ui.dto

import io.golos.domain.dto.*
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.ContentBlock

data class Post(
    val title:String,
    val author: UserBriefDomain,
    val community: Community,
    val contentId: ContentIdDomain,
    val body: ContentBlock?,
    val meta: Meta,
    val stats: Stats?,
    val type: String?,
    val shareUrl: String?,
    var votes: Votes,
    val isMyPost: Boolean,
    val reward: RewardPostDomain?,
    val donation: DonationsDomain?,
    var viewCount: Int
) {
    data class Community(
        val alias: String?,
        val communityId: CommunityIdDomain,
        val name: String?,
        val avatarUrl: String?,
        var isSubscribed: Boolean
    )
}
