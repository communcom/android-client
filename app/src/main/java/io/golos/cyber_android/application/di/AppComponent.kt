package io.golos.cyber_android.application.di

import io.golos.domain.dependency_injection.scopes.ApplicationScope
import dagger.Component
import io.golos.cyber_android.application.App
import io.golos.cyber_android.services.firebase.di.FirebaseNotificationServiceComponent
import io.golos.cyber_android.ui.di.UIComponent

@Component(modules = [
    AppModule::class,
    AppModuleBinds::class,
    AppModuleChilds::class
])
@ApplicationScope
interface AppComponent {
    val ui: UIComponent.Builder
    val firebaseNotification: FirebaseNotificationServiceComponent.Builder

    fun inject(app: App)
}