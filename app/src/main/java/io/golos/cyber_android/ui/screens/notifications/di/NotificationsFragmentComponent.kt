package io.golos.cyber_android.ui.screens.notifications.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.notifications.view.NotificationsFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [NotificationsFragmentModuleBinds::class])
@FragmentScope
interface NotificationsFragmentComponent {
    @Subcomponent.Builder
    interface Builder {

        fun build(): NotificationsFragmentComponent
    }

    fun inject(fragment: NotificationsFragment)
}