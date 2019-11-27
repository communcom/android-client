package io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_bio

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.profile_bio.view.ProfileBioFragment
import io.golos.domain.dependency_injection.scopes.SubFragmentScope

@Subcomponent(modules = [ProfileBioFragmentModuleBinds::class, ProfileBioFragmentModule::class])
@SubFragmentScope
interface ProfileBioFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: ProfileBioFragmentModule): Builder
        fun build(): ProfileBioFragmentComponent
    }

    fun inject(fragment: ProfileBioFragment)
}