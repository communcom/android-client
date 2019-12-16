package io.golos.cyber_android.ui.screens.profile.new_profile.model

import dagger.Lazy
import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.screens.profile.new_profile.model.logout.LogoutUseCase
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.dto.UserProfileDomain
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.domain.use_cases.user.UsersRepository
import timber.log.Timber
import java.io.File
import java.lang.Exception
import javax.inject.Inject

class ProfileModelImpl
@Inject
constructor(
    private val profileUserId: UserIdDomain,
    private val currentUserRepository: CurrentUserRepository,
    private val  usersRepository: UsersRepository,
    private val logout: Lazy<LogoutUseCase>
) : ModelBaseImpl(),
    ProfileModel {

    private var isSubscriptionInProgress = false

    override val isCurrentUser: Boolean
        get() = profileUserId == currentUserRepository.userId

    override val isSubscription: Boolean
        get() = userProfile.isSubscription

    private lateinit var userProfile: UserProfileDomain

    override val mutualUsers: List<UserDomain>
        get() = userProfile.commonFriends

    override val avatarUrl: String?
        get() = userProfile.avatarUrl

    override val coverUrl: String?
        get() = userProfile.coverUrl

    override suspend fun loadProfileInfo(): UserProfileDomain {
        userProfile = usersRepository.getUserProfile(profileUserId)
        return userProfile
    }

    /**
     * @return url of an avatar
     */
    override suspend fun sendAvatar(avatarFile: File): String = usersRepository.updateAvatar(avatarFile)

    /**
     * @return url of a cover
     */
    override suspend fun sendCover(coverFile: File): String = usersRepository.updateCover(coverFile)

    override suspend fun sendBio(text: String) = usersRepository.updateBio(text)

    override suspend fun clearAvatar() = usersRepository.clearAvatar()

    override suspend fun clearCover() = usersRepository.clearCover()

    override suspend fun clearBio() = usersRepository.clearBio()

    override suspend fun logout() = logout.get().logout()

    override fun restartApp() = logout.get().restartApp()

    override suspend fun subscribeUnsubscribe() {
        if(isSubscriptionInProgress) {
            return
        }

        isSubscriptionInProgress = true

        try {
            if(userProfile.isSubscription) {
                usersRepository.unsubscribeToFollower(userProfile.userId)
            } else {
                usersRepository.subscribeToFollower(userProfile.userId)
            }
            userProfile = userProfile.copy(isSubscription = !userProfile.isSubscription)
        } catch (ex: Exception) {
            Timber.e(ex)
            throw ex
        } finally {
            isSubscriptionInProgress = false
        }
    }
}