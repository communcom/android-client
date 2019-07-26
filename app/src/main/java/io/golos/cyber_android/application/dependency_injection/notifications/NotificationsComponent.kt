package io.golos.cyber_android.application.dependency_injection.notifications

import dagger.Subcomponent
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [
    NotificationsModule::class,
    NotificationsModuleBinds::class
])
@FragmentScope
interface NotificationsComponent {
}