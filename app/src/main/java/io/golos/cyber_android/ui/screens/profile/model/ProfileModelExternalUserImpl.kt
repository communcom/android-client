package io.golos.cyber_android.ui.screens.profile.model

import io.golos.cyber_android.ui.screens.profile.model.logout.LogoutUseCase
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.domain.repositories.UsersRepository
import javax.inject.Inject
import dagger.Lazy
import io.golos.cyber_android.ui.screens.profile.model.notifications_settings.NotificationsSettingsFacade
import io.golos.cyber_android.ui.screens.wallet_shared.balance_calculator.BalanceCalculator
import io.golos.data.persistence.key_value_storage.storages.shared_preferences.SharedPreferencesStorage
import io.golos.data.repositories.wallet.WalletRepository
import io.golos.domain.dto.CommunityDomain
import io.golos.domain.repositories.GlobalSettingsRepository
import io.golos.domain.use_cases.community.CommunitiesRepository

class ProfileModelExternalUserImpl
@Inject
constructor(
    profileUserId: UserIdDomain,
    currentUserRepository: CurrentUserRepository,
    usersRepository: UsersRepository,
    communityRepository: CommunitiesRepository,
    walletRepository: WalletRepository,
    logout: Lazy<LogoutUseCase>,
    balanceCalculator: BalanceCalculator,
    sharedPreferencesStorage: SharedPreferencesStorage,
    notificationSettings: NotificationsSettingsFacade
) : ProfileModelImpl(
    profileUserId,
    currentUserRepository,
    usersRepository,
    communityRepository,
    walletRepository,
    logout,
    balanceCalculator,
    sharedPreferencesStorage,
    notificationSettings
), ProfileModel {
    override val isBalanceVisible: Boolean
        get() = false

    override suspend fun getHighlightCommunities(): List<CommunityDomain> = userProfile.highlightCommunities

    override suspend fun getTotalBalance(): Double = 0.0
}