package io.golos.cyber_android.ui.screens.profile.model

import dagger.Lazy
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.screens.profile.model.logout.LogoutUseCase
import io.golos.cyber_android.ui.screens.profile.model.notifications_settings.NotificationsSettingsFacade
import io.golos.cyber_android.ui.screens.wallet.data.enums.Currencies
import io.golos.cyber_android.ui.screens.wallet.model.WalletModelImpl
import io.golos.cyber_android.ui.screens.wallet_shared.balance_calculator.BalanceCalculator
import io.golos.data.persistence.key_value_storage.storages.shared_preferences.SharedPreferencesStorage
import io.golos.data.repositories.wallet.WalletRepository
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.dto.*
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.domain.repositories.GlobalSettingsRepository
import io.golos.domain.repositories.UsersRepository
import io.golos.domain.use_cases.community.CommunitiesRepository
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import java.io.File
import javax.inject.Inject

open class ProfileModelImpl
@Inject
constructor(
    private var profileUserId: UserIdDomain,
    private val currentUserRepository: CurrentUserRepository,
    private val usersRepository: UsersRepository,
    private val communityRepository: CommunitiesRepository,
    private val walletRepository: WalletRepository,
    private val logout: Lazy<LogoutUseCase>,
    private val balanceCalculator: BalanceCalculator,
    private val keyValueStorageFacade: KeyValueStorageFacade,
    override val notificationSettings: NotificationsSettingsFacade,
    private val globalSettingsRepository: GlobalSettingsRepository
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

    override val isBalanceUpdated: Flow<Boolean?>
        get() = globalSettingsRepository.isBalanceUpdated

    override var balanceData: List<WalletCommunityBalanceRecordDomain> = listOf()

    override suspend fun clearBalanceUpdateLastCallback() {
        globalSettingsRepository.notifyBalanceUpdate(null)
    }

    override suspend fun loadProfileInfo(): UserProfileDomain {
        userProfile = usersRepository.getUserProfile(profileUserId)

        // It's dirty trick - in case if we pass user's name in "profileUserId" instead of user's id
        // It's possible if we move to a profile by link to user in a post
        profileUserId = userProfile.userId

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

    override suspend fun getTotalBalance(): Double {
        balanceData = walletRepository.getBalance()
        return balanceCalculator.getTotalBalance(balanceData) * getCurrency().coefficient
    }

    override fun getCurrency(): Currencies {
        return keyValueStorageFacade.getCurrencyCoefficient()?.let { Currencies.getCurrency(it) }
            ?: Currencies.COMMUN
    }
}