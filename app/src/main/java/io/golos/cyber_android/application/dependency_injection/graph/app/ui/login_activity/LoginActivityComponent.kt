package io.golos.cyber_android.application.dependency_injection.graph.app.ui.login_activity

import dagger.Subcomponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.login_activity.on_boarding.OnBoardingFragmentComponent
import io.golos.domain.dependency_injection.scopes.ActivityScope

@Subcomponent(modules = [
    LoginActivityModule::class,
    LoginActivityModuleBinds::class
])
@ActivityScope
interface LoginActivityComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): LoginActivityComponent
    }

    val onBoardingFragmentComponent: OnBoardingFragmentComponent.Builder
}