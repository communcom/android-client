package io.golos.cyber_android.application.di

import dagger.Component
import io.golos.cyber_android.application.App
import io.golos.cyber_android.services.firebase.notifications.di.FirebaseNotificationServiceComponent
import io.golos.cyber_android.services.post_view.di.RecordPostViewServiceComponent
import io.golos.cyber_android.ui.di.UIComponent
import io.golos.domain.dependency_injection.scopes.ApplicationScope

@Component(modules = [
    AppModule::class,
    AppModuleBinds::class,
    AppModuleChilds::class
])
@ApplicationScope
interface AppComponent {
    val ui: UIComponent.Builder
    val firebaseNotification: FirebaseNotificationServiceComponent.Builder
    val recordPostViewService: RecordPostViewServiceComponent.Builder

    fun inject(app: App)
}