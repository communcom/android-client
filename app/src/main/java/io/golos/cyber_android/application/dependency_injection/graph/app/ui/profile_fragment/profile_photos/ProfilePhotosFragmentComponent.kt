package io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_photos

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.profile_photos.view.ProfilePhotosFragment
import io.golos.domain.dependency_injection.scopes.SubFragmentScope

@Subcomponent(modules = [ProfilePhotosFragmentModuleBinds::class, ProfilePhotosFragmentModule::class])
@SubFragmentScope
interface ProfilePhotosFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: ProfilePhotosFragmentModule): Builder
        fun build(): ProfilePhotosFragmentComponent
    }

    fun inject(fragment: ProfilePhotosFragment)
}