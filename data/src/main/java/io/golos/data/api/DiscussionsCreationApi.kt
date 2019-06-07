package io.golos.data.api

import io.golos.cyber4j.model.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-02.
 */
interface DiscussionsCreationApi {

    fun createComment(
        body: String,
        parentAccount: CyberName,
        parentPermlink: String,
        category: List<Tag>,
        metadata: DiscussionCreateMetadata,
        beneficiaries: List<Beneficiary> = emptyList(),
        vestPayment: Boolean = true,
        tokenProp: Long = 0L
    ): Pair<TransactionSuccessful<CreateDiscussionResult>, CreateDiscussionResult>

    fun createPost(
        title: String,
        body: String,
        tags: List<Tag>,
        metadata: DiscussionCreateMetadata,
        beneficiaries: List<Beneficiary> = emptyList(),
        vestPayment: Boolean = true,
        tokenProp: Long = 0L
    ): Pair<TransactionSuccessful<CreateDiscussionResult>, CreateDiscussionResult>

    fun updatePost(postPermlink: String,
                   newTitle: String,
                   newBody: String,
                   newTags: List<Tag>,
                   newJsonMetadata: DiscussionCreateMetadata
    ): Pair<TransactionSuccessful<UpdateDiscussionResult>, UpdateDiscussionResult>

    fun deletePostOrComment(postOrCommentPermlink: String):
            Pair<TransactionSuccessful<DeleteResult>, DeleteResult>
}