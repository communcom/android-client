package io.golos.cyber_android.ui.screens.app_start.welcome.activity.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.app_start.welcome.activity.view.WelcomeActivity
import io.golos.cyber_android.ui.screens.app_start.welcome.welcome_fragment.WelcomeFragment
import io.golos.domain.dependency_injection.scopes.ActivityScope

@Subcomponent(modules = [
    WelcomeActivityModuleBinds::class
])
@ActivityScope
interface WelcomeActivityComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): WelcomeActivityComponent
    }

    fun inject(activity: WelcomeActivity)
    fun inject(fragment: WelcomeFragment)
}