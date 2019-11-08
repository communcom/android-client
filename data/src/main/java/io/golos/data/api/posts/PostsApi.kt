package io.golos.data.api.posts

import io.golos.commun4j.model.FeedSortByType
import io.golos.commun4j.model.FeedTimeFrame
import io.golos.commun4j.model.FeedType
import io.golos.commun4j.model.GetDiscussionsResult
import io.golos.commun4j.sharedmodel.CyberName

interface PostsApi {

    suspend fun getPosts(
        userId: CyberName?,
        communityId: String?,
        communityAlias: String?,
        allowNsfw: Boolean?,
        type: FeedType?,
        sortBy: FeedSortByType?,
        frameTime: FeedTimeFrame?,
        offset: Int
    ): GetDiscussionsResult
}