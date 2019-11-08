package io.golos.data.repositories

import io.golos.commun4j.model.FeedSortByType
import io.golos.commun4j.model.FeedTimeFrame
import io.golos.commun4j.model.FeedType
import io.golos.commun4j.utils.toCyberName
import io.golos.data.api.posts.PostsApi
import io.golos.domain.entities.PostDomain
import io.golos.domain.entities.PostsConfigurationDomain
import io.golos.domain.interactors.posts.PostsRepository
import javax.inject.Inject

class PostsRepositoryImpl @Inject constructor(private val postsApi: PostsApi): PostsRepository {

    override suspend fun getPosts(postsConfigurationDomain: PostsConfigurationDomain): List<PostDomain> {
        val discussionsResult = postsApi.getPosts(
            postsConfigurationDomain.userId.toCyberName(),
            postsConfigurationDomain.communityId,
            postsConfigurationDomain.communityAlias,
            postsConfigurationDomain.allowNsfw,
            FeedType.COMMUNITY,
            FeedSortByType.TIME,
            FeedTimeFrame.ALL,
            postsConfigurationDomain.offset
        )
        val discussion = discussionsResult.
        return arrayListOf()
    }
}