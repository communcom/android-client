package io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment

import dagger.Module
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_bio.ProfileBioFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_photos.ProfilePhotosFragmentComponent

@Module(subcomponents = [
    ProfilePhotosFragmentComponent::class,
    ProfileBioFragmentComponent::class
])
class ProfileFragmentModuleChild {
}