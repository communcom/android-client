package io.golos.data

import androidx.annotation.WorkerThread
import io.golos.cyber4j.model.CyberDiscussion
import io.golos.cyber4j.model.CyberName
import io.golos.cyber4j.model.DiscussionTimeSort
import io.golos.cyber4j.model.DiscussionsResult

/**
 * Created by yuri yurivladdurain@gmail.com on 11/03/2019.
 */
interface PostsApiService {
    @WorkerThread
    fun getCommunityPosts(
        communityId: String,
        limit: Int,
        sort: DiscussionTimeSort,
        sequenceKey: String? = null
    ): DiscussionsResult

    @WorkerThread
    fun getPost(
        user: CyberName,
        permlink: String,
        refBlockNum: Long
    ): CyberDiscussion
}

interface CommentsApiService {
    @WorkerThread
    fun getCommentsOfPost(
        user: CyberName,
        permlink: String,
        refBlockNum: Long,
        limit: Int,
        sort: DiscussionTimeSort,
        sequenceKey: String? = null
    ): DiscussionsResult

    @WorkerThread
    fun getComment(
        user: CyberName,
        permlink: String,
        refBlockNum: Long
    ): CyberDiscussion
}