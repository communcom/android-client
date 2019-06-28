package io.golos.data.api

import com.memtrip.eos.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.abi.implementation.social.PinSocialStruct
import io.golos.abi.implementation.social.UpdatemetaSocialStruct
import io.golos.cyber4j.services.model.UserMetadataResult
import io.golos.sharedmodel.CyberName

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-30.
 */
interface UserMetadataApi {
    fun setUserMetadata(
        about: String? = null,
        coverImage: String? = null,
        profileImage: String? = null
    ): TransactionCommitted<UpdatemetaSocialStruct>

    fun getUserMetadata(user: CyberName): UserMetadataResult

    fun pin(user: CyberName): Pair<TransactionCommitted<PinSocialStruct>, PinSocialStruct>

    fun unPin(user: CyberName): Pair<TransactionCommitted<PinSocialStruct>, PinSocialStruct>
}