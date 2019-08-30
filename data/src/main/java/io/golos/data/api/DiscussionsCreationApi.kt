package io.golos.data.api

import io.golos.cyber4j.abi.implementation.publish.CreatemssgPublishStruct
import io.golos.cyber4j.abi.implementation.publish.DeletemssgPublishStruct
import io.golos.cyber4j.abi.implementation.publish.UpdatemssgPublishStruct
import io.golos.cyber4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.cyber4j.model.*
import io.golos.cyber4j.sharedmodel.CyberName

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
    ): Pair<TransactionCommitted<CreatemssgPublishStruct>, CreatemssgPublishStruct>

    fun createPost(
        title: String,
        body: String,
        tags: List<Tag>,
        metadata: DiscussionCreateMetadata,
        beneficiaries: List<Beneficiary> = emptyList(),
        vestPayment: Boolean = true,
        tokenProp: Long = 0L
    ): Pair<TransactionCommitted<CreatemssgPublishStruct>, CreatemssgPublishStruct>

    fun updatePost(postPermlink: String,
                   newTitle: String,
                   newBody: String,
                   newTags: List<Tag>,
                   newJsonMetadata: DiscussionCreateMetadata
    ): Pair<TransactionCommitted<UpdatemssgPublishStruct>, UpdatemssgPublishStruct>

    fun deletePostOrComment(postOrCommentPermlink: String):
            Pair<TransactionCommitted<DeletemssgPublishStruct>, DeletemssgPublishStruct>
}