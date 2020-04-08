package io.golos.cyber_android.services.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.Lazy
import io.golos.cyber_android.application.App
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.dto.FcmTokenStateDomain
import io.golos.utils.id.IdUtil
import timber.log.Timber
import java.util.concurrent.Executors
import javax.inject.Inject

class FirebaseNotificationService : FirebaseMessagingService() {
    private lateinit var injectionKey: String

    @Inject
    internal lateinit var keyValueStorage: Lazy<KeyValueStorageFacade>

    override fun onCreate() {
        super.onCreate()

        injectionKey = IdUtil.generateStringId()
        App.injections.get<FirebaseNotificationServiceComponent>(injectionKey).inject(this)
    }

    override fun onNewToken(token: String) {
        Timber.tag("FIREBASE_TOKEN").d("Token: $token")
        Executors.newSingleThreadExecutor().execute {
            keyValueStorage.get().saveFcmToken(FcmTokenStateDomain(false, token))
            Timber.tag("FIREBASE_TOKEN").d("Token: saved")
        }
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        Timber.tag("FIREBASE_MESSAGE").d("Message received")
        super.onMessageReceived(p0)
    }

    override fun onDestroy() {
        super.onDestroy()
        App.injections.release<FirebaseNotificationServiceComponent>(injectionKey)
    }
}