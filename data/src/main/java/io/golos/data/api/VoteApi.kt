package io.golos.data.api

import io.golos.cyber4j.abi.implementation.publish.VotePublishStruct
import io.golos.cyber4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.cyber4j.sharedmodel.CyberName

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-21.
 */
interface VoteApi {
    fun vote(
        postOrCommentAuthor: CyberName,
        postOrCommentPermlink: String,
        voteStrength: Short
    ): TransactionCommitted<VotePublishStruct>
}