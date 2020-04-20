package io.golos.cyber_android.ui.screens.notifications.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.data.repositories.wallet.WalletRepository
import io.golos.domain.dto.notifications.NotificationsPageDomain
import io.golos.domain.dto.notifications.NotificationsStatusDomain
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.domain.repositories.NotificationsRepository
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject


class NotificationsModelImpl
@Inject constructor(
    private val notificationsRepository: NotificationsRepository,
    private val currentUserRepository: CurrentUserRepository,
    private val walletRepository: WalletRepository
) : NotificationsModel,
    ModelBaseImpl() {

    override suspend fun geNewNotificationsCounterFlow(): Flow<NotificationsStatusDomain> = notificationsRepository.getNewNotificationsCounterFlow()

    override suspend fun getBalance(): List<WalletCommunityBalanceRecordDomain> = walletRepository.getBalance()

    override suspend fun getCurrentUser(): UserDomain {
        return UserDomain(currentUserRepository.userId,
            currentUserRepository.userName,
            currentUserRepository.userAvatarUrl,
            null,
            null,
            false)
    }

    override suspend fun markAllNotificationAsViewed(untilDate: Date) {
        notificationsRepository.markAllNotificationAsViewed(untilDate)
    }

    override suspend fun getNewNotificationsCounter(): Int = notificationsRepository.getNewNotificationsCounter()

    override suspend fun getNotifications(pageKey: String?, limit: Int): NotificationsPageDomain {
        return notificationsRepository.getNotifications(pageKey, limit)
    }
}