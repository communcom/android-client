package io.golos.cyber_android.services.fcm

import com.google.firebase.iid.FirebaseInstanceId
import io.golos.data.errors.AppError
import io.golos.domain.FcmTokenProvider
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FcmTokenProviderImpl
@Inject
constructor(): FcmTokenProvider {
    override suspend fun provide(): String = suspendCoroutine { continuation ->
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    continuation.resume(task.result!!.token)
                } else {
                    continuation.resumeWithException(AppError.FcmServiceError)
                }
            }
    }
}