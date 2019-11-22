package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.profile_fragment

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.profile.old_profile.OldProfileFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [
    OldProfileFragmentModule::class,
    OldProfileFragmentModuleBinds::class
])
@FragmentScope
interface OldProfileFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: OldProfileFragmentModule): Builder
        fun build(): OldProfileFragmentComponent
    }

    fun inject(fragment: OldProfileFragment)
}