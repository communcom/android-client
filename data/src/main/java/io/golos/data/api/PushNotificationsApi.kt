package io.golos.data.api

import io.golos.cyber4j.services.model.ResultOk

interface PushNotificationsApi {
    fun subscribeOnMobilePushNotifications(deviceId: String, fcmToken: String): ResultOk

    fun unSubscribeOnNotifications(deviceId: String, fcmToken: String): ResultOk
}