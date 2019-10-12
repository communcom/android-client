package io.golos.data.repositories.discussion

import io.golos.commun4j.sharedmodel.CyberName
import io.golos.data.api.discussions.DiscussionsApi
import io.golos.data.api.transactions.TransactionsApi
import io.golos.domain.DispatchersProvider
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.entities.DiscussionCreationResultEntity
import io.golos.domain.entities.DiscussionRelatedEntities
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.mappers.CyberPostToEntityMapper
import io.golos.domain.mappers.PostEntitiesToModelMapper
import io.golos.domain.requestmodel.DiscussionCreationRequestEntity
import javax.inject.Inject

class DiscussionRepositoryImpl
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    private val discussionsApi: DiscussionsApi,
    private val postToEntityMapper: CyberPostToEntityMapper,
    private val postToModelMapper: PostEntitiesToModelMapper,
    transactionsApi: TransactionsApi
): DiscussionCreationRepositoryBase(
    dispatchersProvider,
    discussionsApi,
    transactionsApi
), DiscussionRepository {
    override suspend fun createOrUpdate(params: DiscussionCreationRequestEntity): DiscussionCreationResultEntity =
        createOrUpdateDiscussion(params)

    override suspend fun getPost(user: CyberName, permlink: Permlink): PostModel =
        discussionsApi.getPost(user, permlink)
            .let { rawPost -> postToEntityMapper.map(rawPost) }
            .let { postEntity -> postToModelMapper.map(postEntity) }
}