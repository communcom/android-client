package io.golos.cyber_android.ui.screens.notifications.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.domain.dto.BaseNotificationDomain

interface NotificationsModel : ModelBase {

    suspend fun getNotifications(limit: Int): List<BaseNotificationDomain>
}