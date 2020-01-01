package io.golos.cyber_android.ui.screens.profile.di

import dagger.Module
import io.golos.cyber_android.ui.screens.profile_bio.di.ProfileBioFragmentComponent
import io.golos.cyber_android.ui.screens.profile_communities.di.ProfileCommunitiesFragmentComponent
import io.golos.cyber_android.ui.screens.profile_followers.di.ProfileFollowersFragmentComponent
import io.golos.cyber_android.ui.screens.profile_photos.di.ProfilePhotosFragmentComponent
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