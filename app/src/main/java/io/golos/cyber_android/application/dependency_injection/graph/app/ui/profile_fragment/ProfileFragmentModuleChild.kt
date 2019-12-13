package io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment

import dagger.Module
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_bio.ProfileBioFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_communities.ProfileCommunitiesFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_followers.ProfileFollowersFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_photos.ProfilePhotosFragmentComponent
import io.golos.cyber_android.ui.screens.profile_black_list.di.ProfileBlackListFragmentComponent
import io.golos.cyber_android.ui.screens.profile_posts.di.ProfilePostsLikedFragmentComponent

@Module(subcomponents = [
    ProfilePhotosFragmentComponent::class,
    ProfileBioFragmentComponent::class,
    ProfileCommunitiesFragmentComponent::class,
    ProfileFollowersFragmentComponent::class,
    ProfileBlackListFragmentComponent::class
])
class ProfileFragmentModuleChild {
}