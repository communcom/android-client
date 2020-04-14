package io.golos.cyber_android.ui.screens.dashboard.model

import android.content.Intent
import android.net.Uri
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.screens.dashboard.dto.DeepLinkInfo
import io.golos.cyber_android.ui.screens.dashboard.dto.OpenNotificationInfo
import io.golos.cyber_android.ui.screens.dashboard.model.deep_links.DeepLinksParser
import io.golos.cyber_android.ui.shared.utils.IntentConstants
import io.golos.data.repositories.wallet.WalletRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.NotificationsStatusDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.repositories.NotificationsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DashboardModelImpl
@Inject
constructor(
    private val notificationsRepository: NotificationsRepository,
    private val deepLinksParser: DeepLinksParser,
    private val walletRepository: WalletRepository,
    private val dispatchersProvider: DispatchersProvider
): DashboardModel {

    override suspend fun getNewNotificationsCounterFlow(): Flow<NotificationsStatusDomain> = notificationsRepository.getNewNotificationsCounterFlow()

    override suspend fun updateNewNotificationsCounter() = notificationsRepository.updateNewNotificationsCounter()

    override suspend fun parseDeepLinkUri(uri: Uri): DeepLinkInfo? = deepLinksParser.parse(uri)

    override suspend fun parseOpenNotification(intent: Intent): OpenNotificationInfo? =
        intent.takeIf { it.action == IntentConstants.ACTION_OPEN_NOTIFICATION }
            ?.let {
                it.getParcelableExtra<ContentId>(IntentConstants.POST_CONTENT_ID)?.let { contentId ->
                    OpenNotificationInfo.OpenPost(contentId)
                }
                ?: it.getParcelableExtra<UserIdDomain>(IntentConstants.USER_ID)?.let { userId ->
                    OpenNotificationInfo.OpenProfile(userId)
                }
                ?: withContext(dispatchersProvider.ioDispatcher) {
                    OpenNotificationInfo.OpenWallet(walletRepository.getBalance())
                }
            }
}