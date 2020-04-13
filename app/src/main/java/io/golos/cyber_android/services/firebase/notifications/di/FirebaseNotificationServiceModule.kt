package io.golos.cyber_android.services.firebase.notifications.di

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import io.golos.cyber_android.services.firebase.notifications.custom_json_adapters.CyberNameJsonAdapter
import io.golos.cyber_android.services.firebase.notifications.custom_json_adapters.CyberSymbolCodeJsonAdapter
import io.golos.cyber_android.services.firebase.notifications.custom_json_adapters.DateJsonAdapter
import io.golos.domain.dependency_injection.Clarification
import javax.inject.Named

@Module
class FirebaseNotificationServiceModule {
    @Provides
    @Named(Clarification.FIREBASE_MESSAGES)
    internal fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(DateJsonAdapter())
            .add(CyberSymbolCodeJsonAdapter())
            .add(CyberNameJsonAdapter())
            .build()
}