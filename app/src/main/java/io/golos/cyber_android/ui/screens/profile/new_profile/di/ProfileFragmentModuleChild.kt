package io.golos.cyber_android.ui.screens.profile.new_profile.di

import dagger.Module
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_bio.ProfileBioFragmentComponent
import io.golos.cyber_android.ui.screens.profile_communities.di.ProfileCommunitiesFragmentComponent
import io.golos.cyber_android.ui.screens.profile_followers.di.ProfileFollowersFragmentComponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_photos.ProfilePhotosFragmentComponent
import io.golos.cyber_android.ui.screens.profile_black_list.di.ProfileBlackListFragmentComponent
import io.golos.cyber_android.ui.screens.profile_posts.di.ProfilePostsFragmentComponent
import io.golos.cyber_android.ui.screens.profile_posts.di.ProfilePostsLikedFragmentComponent

@Module(subcomponents = [
    ProfilePhotosFragmentComponent::class,
    ProfileBioFragmentComponent::class,
    ProfileCommunitiesFragmentComponent::class,
    ProfileFollowersFragmentComponent::class,
    ProfileBlackListFragmentComponent::class,
    ProfilePostsFragmentComponent::class,
    ProfilePostsLikedFragmentComponent::class
])
class ProfileFragmentModuleChild