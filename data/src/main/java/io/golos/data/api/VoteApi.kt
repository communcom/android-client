package io.golos.data.api

import com.memtrip.eos.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.abi.implementation.publish.VotePublishStruct
import io.golos.sharedmodel.CyberName

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