package io.golos.data.api

import io.golos.cyber4j.model.CyberName
import io.golos.cyber4j.model.TransactionSuccessful
import io.golos.cyber4j.model.VoteResult

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-21.
 */
interface VoteApi {
    fun vote(
        postOrCommentAuthor: CyberName,
        postOrCommentPermlink: String,
        voteStrength: Short
    ): TransactionSuccessful<VoteResult>
}