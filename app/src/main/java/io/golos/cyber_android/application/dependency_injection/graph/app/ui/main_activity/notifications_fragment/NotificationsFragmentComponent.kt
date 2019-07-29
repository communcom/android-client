package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.notifications_fragment

import dagger.Subcomponent
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [
    NotificationsFragmentModule::class,
    NotificationsFragmentModuleBinds::class
])
@FragmentScope
interface NotificationsFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): NotificationsFragmentComponent
    }
}