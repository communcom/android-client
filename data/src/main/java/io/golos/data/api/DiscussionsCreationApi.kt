package io.golos.data.api

import io.golos.cyber4j.model.CreateDiscussionResult
import io.golos.cyber4j.model.CyberName
import io.golos.cyber4j.model.DiscussionCreateMetadata
import io.golos.cyber4j.model.Tag

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-02.
 */
interface DiscussionsCreationApi {

    fun createComment(
        body: String,
        parentAccount: CyberName,
        parentPermlink: String,
        parentDiscussionRefBlockNum: Long,
        category: List<Tag>,
        metadata: DiscussionCreateMetadata,
        beneficiaries: List<io.golos.cyber4j.model.Beneficiary> = emptyList(),
        vestPayment: Boolean = true,
        tokenProp: Long = 0L
    ): CreateDiscussionResult

    fun createPost(
        title: String,
        body: String,
        tags: List<io.golos.cyber4j.model.Tag>,
        metadata: DiscussionCreateMetadata,
        beneficiaries: List<io.golos.cyber4j.model.Beneficiary> = emptyList(),
        vestPayment: Boolean = true,
        tokenProp: Long = 0L
    ): CreateDiscussionResult
}