package io.golos.data.repositories.discussion_creation

import io.golos.commun4j.abi.implementation.comn.gallery.CreatemssgComnGalleryStruct
import io.golos.commun4j.abi.implementation.comn.gallery.DeletemssgComnGalleryStruct
import io.golos.commun4j.abi.implementation.comn.gallery.UpdatemssgComnGalleryStruct
import io.golos.commun4j.sharedmodel.Either
import io.golos.data.api.DiscussionsCreationApi
import io.golos.data.api.TransactionsApi
import io.golos.domain.DispatchersProvider
import io.golos.domain.Logger
import io.golos.domain.entities.DeleteDiscussionResultEntity
import io.golos.domain.entities.DiscussionCreationResultEntity
import io.golos.domain.entities.UpdatePostResultEntity
import io.golos.domain.requestmodel.DiscussionCreateRequest
import io.golos.domain.requestmodel.DiscussionCreationRequestEntity
import io.golos.domain.mappers.CommunToEntityMapper
import io.golos.domain.mappers.EntityToCommunMapper
import javax.inject.Inject

class DiscussionCreationRepositoryImpl
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    toCyberRequestMapper: EntityToCommunMapper<DiscussionCreationRequestEntity, DiscussionCreateRequest>,
    toEntityResultMapper: CommunToEntityMapper<CreatemssgComnGalleryStruct, DiscussionCreationResultEntity>,
    toEntityUpdateResultMapper: CommunToEntityMapper<UpdatemssgComnGalleryStruct, UpdatePostResultEntity>,
    toEntityDeleteResultMapper: CommunToEntityMapper<DeletemssgComnGalleryStruct, DeleteDiscussionResultEntity>,
    discussionsCreationApi: DiscussionsCreationApi,
    transactionsApi: TransactionsApi,
    private val logger: Logger
): DiscussionCreationRepositoryBase(
    dispatchersProvider,
    toCyberRequestMapper,
    toEntityResultMapper,
    toEntityUpdateResultMapper,
    toEntityDeleteResultMapper,
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