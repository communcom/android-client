package io.golos.cyber_android.ui.screens.profile.model

import dagger.Lazy
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.screens.profile.model.logout.LogoutUseCase
import io.golos.data.repositories.wallet.WalletRepository
import io.golos.domain.dto.CommunityDomain
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.dto.UserProfileDomain
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.domain.repositories.UsersRepository
import io.golos.domain.use_cases.community.CommunitiesRepository
import timber.log.Timber
import java.io.File
import javax.inject.Inject

open class ProfileModelImpl
@Inject
constructor(
    private val profileUserId: UserIdDomain,
    private val currentUserRepository: CurrentUserRepository,
    private val usersRepository: UsersRepository,
    private val communityRepository: CommunitiesRepository,
    private val walletRepository: WalletRepository,
    private val logout: Lazy<LogoutUseCase>
) : ModelBaseImpl(),
    ProfileModel {

    private var isSubscribingInProgress = false
    private var isBlockingInProgress = false

    override val isCurrentUser: Boolean
        get() = profileUserId == currentUserRepository.userId

    override val isSubscribed: Boolean
        get() = userProfile.isSubscribed

    override val isInBlackList: Boolean
        get() = userProfile.isInBlacklist

    override val isBalanceVisible: Boolean
        get() = true

    protected lateinit var userProfile: UserProfileDomain

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

    override suspend fun getHighlightCommunities(): List<CommunityDomain> =
        communityRepository.getUserCommunities(currentUserRepository.userId, 0, 10)

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

    override suspend fun subscribeUnsubscribe() {
        if(isSubscribingInProgress) {
            return
        }

        isSubscribingInProgress = true

        try {
            if(userProfile.isSubscribed) {
                usersRepository.unsubscribeToFollower(userProfile.userId)
            } else {
                usersRepository.subscribeToFollower(userProfile.userId)
            }
            userProfile = userProfile.copy(isSubscribed = !userProfile.isSubscribed)
        } catch (ex: Exception) {
            Timber.e(ex)
            throw ex
        } finally {
            isSubscribingInProgress = false
        }
    }

    override suspend fun moveToBlackList() {
        if(isBlockingInProgress) {
            return
        }

        isBlockingInProgress = true

        try {
            if(userProfile.isInBlacklist) {
                usersRepository.moveUserFromBlackList(profileUserId)
            } else {
                usersRepository.moveUserToBlackList(profileUserId)
            }
            userProfile = userProfile.copy(isInBlacklist = !userProfile.isInBlacklist)
        } catch (ex: Exception) {
            Timber.e(ex)
            throw ex
        } finally {
            isBlockingInProgress = false
        }
    }

    override suspend fun getTotalBalance(): Double = walletRepository.getTotalBalanceInCommuns()
}