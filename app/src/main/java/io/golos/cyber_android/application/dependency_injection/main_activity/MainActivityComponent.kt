package io.golos.cyber_android.application.dependency_injection.main_activity

import dagger.Subcomponent
import io.golos.domain.dependency_injection.scopes.ActivityScope

@Subcomponent(modules = [
    MainActivityModule::class,
    MainActivityModuleBinds::class
])
@ActivityScope
interface MainActivityComponent {
}