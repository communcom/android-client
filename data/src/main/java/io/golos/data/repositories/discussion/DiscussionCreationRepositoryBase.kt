package io.golos.data.repositories.discussion

import io.golos.commun4j.abi.implementation.c.gallery.CreateCGalleryStruct
import io.golos.commun4j.abi.implementation.c.gallery.RemoveCGalleryStruct
import io.golos.commun4j.abi.implementation.c.gallery.UpdateCGalleryStruct
import io.golos.data.api.discussions.DiscussionsApi
import io.golos.data.api.transactions.TransactionsApi
import io.golos.domain.entities.DiscussionCreationResultEntity
import io.golos.domain.mappers.discussion_creation.request.DiscussionCreationRequestMapper
import io.golos.domain.mappers.discussion_creation.result.DiscussionCreateResultToEntityMapper
import io.golos.domain.mappers.discussion_creation.result.DiscussionDeleteResultToEntityMapper
import io.golos.domain.mappers.discussion_creation.result.DiscussionUpdateResultToEntityMapper
import io.golos.domain.requestmodel.*

abstract class DiscussionCreationRepositoryBase(
    private val discussionsCreationApi: DiscussionsApi,
    private val transactionsApi: TransactionsApi
) {
    protected fun createOrUpdateDiscussion(params: DiscussionCreationRequestEntity): DiscussionCreationResultEntity {
            val request = DiscussionCreationRequestMapper.map(params)
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
                    request.title,
                    request.body,
                    request.tags,
                    request.communityId,
                    request.metadata,
                    request.beneficiaries,
                    request.vestPayment,
                    request.tokenProp
                )
                is UpdatePostRequest -> discussionsCreationApi.updatePost(
                    request.postPermlink,
                    request.title,
                    request.body,
                    request.tags,
                    request.metadata
                )
                is DeleteDiscussionRequest -> discussionsCreationApi.deletePost(request.permlink)
            }

            transactionsApi.waitForTransaction(apiAnswer.first.transaction_id)

            return when (request) {
                is UpdatePostRequest -> DiscussionUpdateResultToEntityMapper.map(apiAnswer.second as UpdateCGalleryStruct)
                is DeleteDiscussionRequest -> DiscussionDeleteResultToEntityMapper.map(apiAnswer.second as RemoveCGalleryStruct)
                else -> DiscussionCreateResultToEntityMapper.map(apiAnswer.second as CreateCGalleryStruct)
            }
    }
}