package io.golos.data.api.user_metadata

import io.golos.commun4j.abi.implementation.comn.social.PinComnSocialStruct
import io.golos.commun4j.abi.implementation.comn.social.UpdatemetaComnSocialStruct
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.domain.commun_entities.GetProfileResultExt
import io.golos.commun4j.utils.Pair as CommunPair

interface UserMetadataApi {
    fun setUserMetadata(
        about: String? = null,
        coverImage: String? = null,
        profileImage: String? = null
    ): TransactionCommitted<UpdatemetaComnSocialStruct>

    fun getUserMetadata(user: CyberName): GetProfileResultExt

    fun pin(user: CyberName): CommunPair<TransactionCommitted<PinComnSocialStruct>, PinComnSocialStruct>

    fun unPin(user: CyberName): CommunPair<TransactionCommitted<PinComnSocialStruct>, PinComnSocialStruct>
}