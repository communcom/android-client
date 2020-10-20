package io.golos.data.api.user_metadata

import io.golos.commun4j.abi.implementation.c.social.PinCSocialStruct
import io.golos.commun4j.abi.implementation.c.social.UpdatemetaCSocialStruct
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.domain.commun_entities.GetProfileResultExt
import io.golos.domain.dto.UserIdDomain
import io.golos.commun4j.utils.Pair as CommunPair

interface UserMetadataApi {
    fun setUserMetadata(
        about: String? = null,
        coverImage: String? = null,
        profileImage: String? = null
    ): TransactionCommitted<UpdatemetaCSocialStruct>

    fun getUserMetadata(user: UserIdDomain): GetProfileResultExt

    fun pin(user: CyberName): CommunPair<TransactionCommitted<PinCSocialStruct>, PinCSocialStruct>

    fun unPin(user: CyberName): CommunPair<TransactionCommitted<PinCSocialStruct>, PinCSocialStruct>
}