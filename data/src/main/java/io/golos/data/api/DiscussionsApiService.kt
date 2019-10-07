package io.golos.data.api

import androidx.annotation.WorkerThread
import io.golos.commun4j.model.CyberDiscussion
import io.golos.commun4j.model.CyberDiscussionRaw
import io.golos.commun4j.model.DiscussionsResult
import io.golos.commun4j.model.GetDiscussionsResultRaw
import io.golos.commun4j.services.model.FeedSort
import io.golos.commun4j.services.model.FeedTimeFrame
import io.golos.commun4j.sharedmodel.CyberName

/**
 * Created by yuri yurivladdurain@gmail.com on 11/03/2019.
 */
interface PostsApiService {
    @WorkerThread
    fun getCommunityPosts(
        communityId: String,
        limit: Int,
        sort: FeedSort,
        timeFrame: FeedTimeFrame,
        sequenceKey: String? = null,
        tags: List<String>? = null
    ): GetDiscussionsResultRaw

    @WorkerThread
    fun getPost(
        user: CyberName,
        permlink: String
    ): CyberDiscussionRaw

    @WorkerThread
    fun getUserSubscriptions(
        userId: String,
        limit: Int,
        sort: FeedSort,
        sequenceKey: String? = null
    ): GetDiscussionsResultRaw

    @WorkerThread
    fun getUserPost(
        userId: String,
        limit: Int,
        sort: FeedSort,
        sequenceKey: String? = null
    ): GetDiscussionsResultRaw
}

interface CommentsApiService {
    @WorkerThread
    fun getCommentsOfPost(
        user: CyberName,
        permlink: String,
        limit: Int,
        sort: FeedSort,
        sequenceKey: String? = null
    ): GetDiscussionsResultRaw

    @WorkerThread
    fun getComment(
        user: CyberName,
        permlink: String
    ): CyberDiscussionRaw
}