package io.golos.cyber_android.services.firebase

import dagger.Subcomponent
import io.golos.domain.dependency_injection.scopes.ServiceScope

@Subcomponent
@ServiceScope
interface FirebaseNotificationServiceComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): FirebaseNotificationServiceComponent
    }

    fun inject(service: FirebaseNotificationService)
}