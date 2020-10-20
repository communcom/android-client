package io.golos.domain.rules

import io.golos.domain.mappers.CyberCommentsToEntityMapperImpl
import io.golos.domain.mappers.CyberFeedToEntityMapperImpl
import io.golos.domain.requestmodel.CommentFeedUpdateRequest
import io.golos.domain.requestmodel.PostFeedUpdateRequest
import javax.inject.Inject

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-21.
 */
class FeedUpdateApprover
@Inject
constructor() : RequestApprover<PostFeedUpdateRequest> {
    override fun approve(param: PostFeedUpdateRequest): Boolean {
        return param.pageKey != CyberFeedToEntityMapperImpl.feedEndMark
    }
}

class CommentUpdateApprover
@Inject
constructor() : RequestApprover<CommentFeedUpdateRequest> {
    override fun approve(param: CommentFeedUpdateRequest): Boolean {
        return param.pageKey != CyberCommentsToEntityMapperImpl.feedEndMark
    }
}