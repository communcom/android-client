package io.golos.domain.rules

import io.golos.domain.requestmodel.CommentFeedUpdateRequest
import io.golos.domain.requestmodel.PostFeedUpdateRequest

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-21.
 */
class FeedUpdateApprover : RequestApprover<PostFeedUpdateRequest> {
    override fun approve(param: PostFeedUpdateRequest): Boolean {
        return param.pageKey != CyberFeedToEntityMapper.feedEndMark
    }
}

class CommentUpdateApprover : RequestApprover<CommentFeedUpdateRequest> {
    override fun approve(param: CommentFeedUpdateRequest): Boolean {
        return param.pageKey != CyberCommentsToEntityMapper.feedEndMark
    }
}