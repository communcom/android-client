package io.golos.data.api

import io.golos.cyber4j.model.CyberName
import io.golos.cyber4j.model.ProfileMetadataUpdateResult
import io.golos.cyber4j.model.TransactionSuccessful
import io.golos.cyber4j.services.model.UserMetadataResult

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-30.
 */
interface UserMetadataApi {
    fun setUserMetadata(
        about: String? = null,
        coverImage: String? = null,
        profileImage: String? = null
    ): TransactionSuccessful<ProfileMetadataUpdateResult>

    fun getUserMetadata(user: CyberName): UserMetadataResult
}