package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Post
import io.golos.domain.dto.PostDomain

fun PostDomain.mapToPost(): Post {
    return Post(
        author = this.author,
        community = this.community.mapToPostCommunity(),
        contentId = this.contentId.mapToContentId(),
        body = this.body,
        meta = this.meta.mapToMeta(),
        stats = this.stats?.mapToStats(),
        type = this.type,
        shareUrl = this.shareUrl,
        votes = this.votes.mapToVotes(),
        isMyPost = this.isMyPost,
        reward = this.reward,
        donation = this.donation
    )
}
