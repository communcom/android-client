package io.golos.data.api.push_notifications

import io.golos.commun4j.Commun4j
import io.golos.commun4j.services.model.ResultOk
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.data.api.Commun4jApiBase
import io.golos.domain.repositories.CurrentUserRepositoryRead
import javax.inject.Inject

class PushNotificationsApiImpl
@Inject
constructor(
    commun4j: Commun4j,
    currentUserRepository: CurrentUserRepositoryRead
) : Commun4jApiBase(commun4j, currentUserRepository), PushNotificationsApi {

    override fun subscribeOnMobilePushNotifications(deviceId: String, fcmToken: String): ResultOk {
        //commun4j.subscribeOnMobilePushNotifications(deviceId, fcmToken, AppName.GLS).getOrThrow()
        return ResultOk("")
    }

    override fun unSubscribeOnNotifications(userId: CyberName, deviceId: String): ResultOk {
        //commun4j.unSubscribeOnNotifications(userId, deviceId, AppName.GLS).getOrThrow()
        return ResultOk("")
    }
}