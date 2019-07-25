package io.golos.cyber_android.application.dependency_injection.profile

import dagger.Subcomponent
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [
    ProfileModule::class,
    ProfileModuleBinds::class
])
@FragmentScope
interface ProfileComponent {
}