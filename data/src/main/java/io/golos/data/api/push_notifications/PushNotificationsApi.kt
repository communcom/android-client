package io.golos.data.api.push_notifications

import io.golos.commun4j.services.model.ResultOk
import io.golos.commun4j.sharedmodel.CyberName

interface PushNotificationsApi {
    fun subscribeOnMobilePushNotifications(deviceId: String, fcmToken: String): ResultOk

    fun unSubscribeOnNotifications(userId: CyberName, deviceId: String): ResultOk
}