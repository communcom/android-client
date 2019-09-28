package io.golos.data.repositories.discussion_creation

import io.golos.cyber4j.abi.implementation.gls.publish.CreatemssgGlsPublishStruct
import io.golos.cyber4j.abi.implementation.gls.publish.DeletemssgGlsPublishStruct
import io.golos.cyber4j.abi.implementation.gls.publish.UpdatemssgGlsPublishStruct
import io.golos.data.api.DiscussionsCreationApi
import io.golos.data.api.TransactionsApi
import io.golos.domain.DispatchersProvider
import io.golos.domain.entities.DeleteDiscussionResultEntity
import io.golos.domain.entities.DiscussionCreationResultEntity
import io.golos.domain.entities.UpdatePostResultEntity
import io.golos.domain.requestmodel.*
import io.golos.domain.rules.CyberToEntityMapper
import io.golos.domain.rules.EntityToCyberMapper
import kotlinx.coroutines.withContext

abstract class DiscussionCreationRepositoryBase(
    private val dispatchersProvider: DispatchersProvider,
    private val toCyberRequestMapper: EntityToCyberMapper<DiscussionCreationRequestEntity, DiscussionCreateRequest>,
    private val toEntityResultMapper: CyberToEntityMapper<CreatemssgGlsPublishStruct, DiscussionCreationResultEntity>,
    private val toEntityUpdateResultMapper: CyberToEntityMapper<UpdatemssgGlsPublishStruct, UpdatePostResultEntity>,
    private val toEntityDeleteResultMapper: CyberToEntityMapper<DeletemssgGlsPublishStruct, DeleteDiscussionResultEntity>,
    private val discussionsCreationApi: DiscussionsCreationApi,
    private val transactionsApi: TransactionsApi
) {
    protected suspend fun createOrUpdateDiscussion(params: DiscussionCreationRequestEntity): DiscussionCreationResultEntity =
        withContext(dispatchersProvider.ioDispatcher) {
            val request = toCyberRequestMapper(params)
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
                is UpdatePostRequest -> toEntityUpdateResultMapper(apiAnswer.second as UpdatemssgGlsPublishStruct)
                is DeleteDiscussionRequest -> toEntityDeleteResultMapper(apiAnswer.second as DeletemssgGlsPublishStruct)
                else -> toEntityResultMapper(apiAnswer.second as CreatemssgGlsPublishStruct)
            }
        }
}