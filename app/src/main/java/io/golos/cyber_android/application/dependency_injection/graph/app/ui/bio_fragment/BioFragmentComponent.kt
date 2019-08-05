package io.golos.cyber_android.application.dependency_injection.graph.app.ui.bio_fragment

import dagger.Subcomponent
import io.golos.cyber_android.ui.shared_fragments.bio.EditProfileBioFragment
import io.golos.cyber_android.ui.shared_fragments.bio.OnboardingBioFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [
    BioFragmentModule::class,
    BioFragmentModuleBinds::class
])
@FragmentScope
interface BioFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: BioFragmentModule): Builder
        fun build(): BioFragmentComponent
    }

    fun inject(fragment: EditProfileBioFragment)
    fun inject(fragment: OnboardingBioFragment)
}