package io.golos.data.repositories.discussion_creation

import io.golos.data.api.discussions.DiscussionsApi
import io.golos.data.api.transactions.TransactionsApi
import io.golos.domain.DispatchersProvider
import io.golos.domain.Logger
import io.golos.domain.entities.DiscussionCreationResultEntity
import io.golos.domain.requestmodel.DiscussionCreationRequestEntity
import javax.inject.Inject

class DiscussionCreationRepositoryImpl
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    discussionsCreationApi: DiscussionsApi,
    transactionsApi: TransactionsApi,
    private val logger: Logger
): DiscussionCreationRepositoryBase(
    dispatchersProvider,
    discussionsCreationApi,
    transactionsApi
), DiscussionCreationRepository {
    
    override suspend fun createOrUpdate(params: DiscussionCreationRequestEntity): DiscussionCreationResultEntity =
        createOrUpdateDiscussion(params)
}