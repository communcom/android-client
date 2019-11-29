package io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment

import dagger.Subcomponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_bio.ProfileBioFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_communities.ProfileCommunitiesFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_photos.ProfilePhotosFragmentComponent
import io.golos.cyber_android.ui.screens.profile.new_profile.view.ProfileFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [ProfileFragmentModuleBinds::class, ProfileFragmentModule::class, ProfileFragmentModuleChild::class])
@FragmentScope
interface ProfileFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: ProfileFragmentModule): Builder
        fun build(): ProfileFragmentComponent
    }

    val photosFragment: ProfilePhotosFragmentComponent.Builder
    val bioFragment: ProfileBioFragmentComponent.Builder
    val communitiesFragment: ProfileCommunitiesFragmentComponent.Builder

    fun inject(fragment: ProfileFragment)
}