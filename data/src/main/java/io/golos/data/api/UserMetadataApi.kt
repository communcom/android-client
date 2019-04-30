package io.golos.data.api

import io.golos.cyber4j.model.CyberName
import io.golos.cyber4j.model.ProfileMetadataUpdateResult
import io.golos.cyber4j.model.UserMetadata

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-30.
 */
interface UserMetadataApi {
    fun setUserMetadata(
        type: String? = null,
        app: String? = null,
        email: String? = null,
        phone: String? = null,
        facebook: String? = null,
        instagram: String? = null,
        telegram: String? = null,
        vk: String? = null,
        website: String? = null,
        first_name: String? = null,
        last_name: String? = null,
        name: String? = null,
        birthDate: String? = null,
        gender: String? = null,
        location: String? = null,
        city: String? = null,
        about: String? = null,
        occupation: String? = null,
        iCan: String? = null,
        lookingFor: String? = null,
        businessCategory: String? = null,
        backgroundImage: String? = null,
        coverImage: String? = null,
        profileImage: String? = null,
        userImage: String? = null,
        icoAddress: String? = null,
        targetDate: String? = null,
        targetPlan: String? = null,
        targetPointA: String? = null,
        targetPointB: String? = null
    ): ProfileMetadataUpdateResult

    fun getUserMetadata(user: CyberName): UserMetadata
}