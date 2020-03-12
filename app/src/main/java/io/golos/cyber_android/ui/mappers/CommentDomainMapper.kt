package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Comment
import io.golos.domain.dto.CommentDomain

fun CommentDomain.mapToComment(): Comment {
    return Comment(
        contentId = this.contentId.mapToContentId(),
        author = this.author.mapToAuthor(),
        votes = this.votes.mapToVotes(),
        body = this.body,
        childCommentsCount = this.childCommentsCount,
        community = this.community.mapToPostCommunity(),
        meta = this.meta.mapToMeta(),
        parent = this.parent.mapToParentComment(),
        type = this.type,
        isDeleted =  isDeleted,
        isMyComment = isMyComment,
        commentLevel = commentLevel
    )
}