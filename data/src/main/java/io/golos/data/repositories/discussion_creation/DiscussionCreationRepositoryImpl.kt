package io.golos.data.repositories.discussion_creation

import io.golos.commun4j.sharedmodel.Either
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
    
    override suspend fun createOrUpdate(params: DiscussionCreationRequestEntity): Either<DiscussionCreationResultEntity, Throwable> =
        try {
            Either.Success<DiscussionCreationResultEntity, Throwable>(createOrUpdateDiscussion(params))
        } catch (ex: Exception) {
            logger.log(ex)
            Either.Failure<DiscussionCreationResultEntity, Throwable>(ex)
        }
}