package io.golos.cyber_android.ui.screens.dashboard.model

import android.content.Intent
import android.net.Uri
import io.golos.cyber_android.ui.screens.dashboard.dto.DeepLinkInfo
import io.golos.cyber_android.ui.screens.dashboard.dto.OpenNotificationInfo
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.domain.dto.notifications.NotificationsStatusDomain
import kotlinx.coroutines.flow.Flow

interface DashboardModel: ModelBase{

    suspend fun getNewNotificationsCounterFlow(): Flow<NotificationsStatusDomain>

    suspend fun updateNewNotificationsCounter()

    suspend fun parseDeepLinkUri(uri: Uri): DeepLinkInfo?

    suspend fun parseOpenNotification(intent: Intent): OpenNotificationInfo?
}