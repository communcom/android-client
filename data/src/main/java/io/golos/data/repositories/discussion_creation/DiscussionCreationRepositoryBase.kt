package io.golos.data.repositories.discussion_creation

import io.golos.commun4j.abi.implementation.comn.gallery.CreatemssgComnGalleryStruct
import io.golos.commun4j.abi.implementation.comn.gallery.DeletemssgComnGalleryStruct
import io.golos.commun4j.abi.implementation.comn.gallery.UpdatemssgComnGalleryStruct
import io.golos.data.api.DiscussionsCreationApi
import io.golos.data.api.TransactionsApi
import io.golos.domain.DispatchersProvider
import io.golos.domain.entities.DeleteDiscussionResultEntity
import io.golos.domain.entities.DiscussionCreationResultEntity
import io.golos.domain.entities.UpdatePostResultEntity
import io.golos.domain.requestmodel.*
import io.golos.domain.mappers.CommunToEntityMapper
import io.golos.domain.mappers.EntityToCommunMapper
import kotlinx.coroutines.withContext

abstract class DiscussionCreationRepositoryBase(
    private val dispatchersProvider: DispatchersProvider,
    private val toCyberRequestMapper: EntityToCommunMapper<DiscussionCreationRequestEntity, DiscussionCreateRequest>,
    private val toEntityResultMapper: CommunToEntityMapper<CreatemssgComnGalleryStruct, DiscussionCreationResultEntity>,
    private val toEntityUpdateResultMapper: CommunToEntityMapper<UpdatemssgComnGalleryStruct, UpdatePostResultEntity>,
    private val toEntityDeleteResultMapper: CommunToEntityMapper<DeletemssgComnGalleryStruct, DeleteDiscussionResultEntity>,
    private val discussionsCreationApi: DiscussionsCreationApi,
    private val transactionsApi: TransactionsApi
) {
    protected suspend fun createOrUpdateDiscussion(params: DiscussionCreationRequestEntity): DiscussionCreationResultEntity =
        withContext(dispatchersProvider.ioDispatcher) {
            val request = toCyberRequestMapper.map(params)
            val apiAnswer = when (request) {
                is CreateCommentRequest -> discussionsCreationApi.createComment(
                    request.body,
                    request.parentAccount,
                    request.parentPermlink,
                    request.category,
                    request.metadata,
                    request.beneficiaries,
                    request.vestPayment,
                    request.tokenProp
                )
                is CreatePostRequest -> discussionsCreationApi.createPost(
                    request.title, request.body, request.tags,
                    request.metadata, request.beneficiaries, request.vestPayment, request.tokenProp
                )
                is UpdatePostRequest -> discussionsCreationApi.updatePost(
                    request.postPermlink, request.title, request.body,
                    request.tags, request.metadata
                )
                is DeleteDiscussionRequest -> discussionsCreationApi.deletePostOrComment(request.permlink)
            }

            transactionsApi.waitForTransaction(apiAnswer.first.transaction_id)

            when (request) {
                is UpdatePostRequest -> toEntityUpdateResultMapper.map(apiAnswer.second as UpdatemssgComnGalleryStruct)
                is DeleteDiscussionRequest -> toEntityDeleteResultMapper.map(apiAnswer.second as DeletemssgComnGalleryStruct)
                else -> toEntityResultMapper.map(apiAnswer.second as CreatemssgComnGalleryStruct)
            }
        }
}