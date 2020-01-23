package io.golos.cyber_android.ui.screens.profile.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.UserProfileDomain
import java.io.File

interface ProfileModel: ModelBase {
    val isCurrentUser: Boolean

    val isSubscribed: Boolean

    val isInBlackList: Boolean

    val mutualUsers: List<UserDomain>

    val avatarUrl: String?

    val coverUrl: String?

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

    suspend fun clearBio()

    suspend fun logout()

    suspend fun subscribeUnsubscribe()

    suspend fun moveToBlackList()
}