package io.golos.data.api

import io.golos.cyber4j.services.model.ResultOk
import io.golos.cyber4j.sharedmodel.CyberName

interface PushNotificationsApi {
    fun subscribeOnMobilePushNotifications(deviceId: String, fcmToken: String): ResultOk

    fun unSubscribeOnNotifications(userId: CyberName, deviceId: String): ResultOk
}