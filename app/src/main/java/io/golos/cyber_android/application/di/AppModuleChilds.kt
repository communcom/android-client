package io.golos.cyber_android.application.di

import dagger.Module
import io.golos.cyber_android.services.firebase.notifications.di.FirebaseNotificationServiceComponent
import io.golos.cyber_android.ui.di.UIComponent

@Module(subcomponents = [
    UIComponent::class,
    FirebaseNotificationServiceComponent::class
])
class AppModuleChilds