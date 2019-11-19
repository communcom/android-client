package io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.profile.new_profile.view.ProfileFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [ProfileFragmentModuleBinds::class, ProfileFragmentModule::class])
@FragmentScope
interface ProfileFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: ProfileFragmentModule): Builder
        fun build(): ProfileFragmentComponent
    }

    fun inject(fragment: ProfileFragment)
}