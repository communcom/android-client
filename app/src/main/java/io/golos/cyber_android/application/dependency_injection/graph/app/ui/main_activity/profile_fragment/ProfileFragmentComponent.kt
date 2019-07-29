package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.profile_fragment

import dagger.Subcomponent
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [
    ProfileFragmentModule::class,
    ProfileFragmentModuleBinds::class
])
@FragmentScope
interface ProfileFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: ProfileFragmentModule): Builder
        fun build(): ProfileFragmentComponent
    }
}