package io.golos.cyber_android.ui.screens.profile.new_profile.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.domain.dto.UserProfileDomain
import java.io.File

interface ProfileModel: ModelBase {
    suspend fun loadProfileInfo(): UserProfileDomain

    /**
     * @return url of a cover
     */
    suspend fun sendAvatar(avatarFile: File): String

    /**
     * @return url of an avatar
     */
    suspend fun sendCover(coverFile: File): String

    suspend fun sendBio(text: String)

    suspend fun clearAvatar()

    suspend fun clearCover()
}