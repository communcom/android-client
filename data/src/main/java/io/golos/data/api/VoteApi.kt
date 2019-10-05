package io.golos.data.api

import io.golos.commun4j.abi.implementation.comn.gallery.VoteComnGalleryStruct
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.commun4j.sharedmodel.CyberName

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-21.
 */
interface VoteApi {
    fun vote(
        postOrCommentAuthor: CyberName,
        postOrCommentPermlink: String,
        voteStrength: Short
    ): TransactionCommitted<VoteComnGalleryStruct>
}