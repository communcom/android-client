package io.golos.cyber_android.services.firebase.di

import dagger.Subcomponent
import io.golos.cyber_android.services.firebase.FirebaseNotificationService
import io.golos.domain.dependency_injection.scopes.ServiceScope

@Subcomponent(modules = [FirebaseNotificationServiceModule::class])
@ServiceScope
interface FirebaseNotificationServiceComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): FirebaseNotificationServiceComponent
    }

    fun inject(service: FirebaseNotificationService)
}