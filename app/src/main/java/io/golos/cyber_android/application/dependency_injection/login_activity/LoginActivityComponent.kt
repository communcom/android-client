package io.golos.cyber_android.application.dependency_injection.login_activity

import dagger.Subcomponent
import io.golos.domain.dependency_injection.scopes.ActivityScope

@Subcomponent(modules = [
    LoginActivityModule::class,
    LoginActivityModuleBinds::class
])
@ActivityScope
interface LoginActivityComponent {

}