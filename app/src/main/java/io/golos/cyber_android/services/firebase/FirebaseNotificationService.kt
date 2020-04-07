package io.golos.cyber_android.services.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import timber.log.Timber

class FirebaseNotificationService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Timber.tag("FIREBASE_TOKEN").d(token)
    }
}