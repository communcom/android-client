package io.golos.data.api.posts

import io.golos.commun4j.Commun4j
import io.golos.commun4j.model.FeedSortByType
import io.golos.commun4j.model.FeedTimeFrame
import io.golos.commun4j.model.FeedType
import io.golos.commun4j.model.GetDiscussionsResult
import io.golos.commun4j.sharedmodel.CyberName
import javax.inject.Inject

class PostsApiImpl @Inject constructor(private val commun4j: Commun4j): PostsApi {

    override suspend fun getPosts(
        userId: CyberName?,
        communityId: String?,
        communityAlias: String?,
        allowNsfw: Boolean?,
        type: FeedType?,
        sortBy: FeedSortByType?,
        frameTime: FeedTimeFrame?,
        offset: Int
    ): GetDiscussionsResult {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}