package io.golos.cyber_android.application.dependency_injection.profile

import dagger.Subcomponent
import io.golos.domain.dependency_injection.scopes.ActivityScope

@Subcomponent(modules = [
    ProfileModule::class,
    ProfileModuleBinds::class
])
@ActivityScope
interface ProfileComponent {
}