package io.golos.domain.rules

import io.golos.domain.model.PostFeedUpdateRequest

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-21.
 */
class FeedUpdateApprover : RequestApprover<PostFeedUpdateRequest> {
    override fun approve(param: PostFeedUpdateRequest): Boolean {
        return param.pageKey != CyberFeedToEntityMapper.feedEndMark
    }
}