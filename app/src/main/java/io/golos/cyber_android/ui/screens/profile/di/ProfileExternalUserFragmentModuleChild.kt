package io.golos.cyber_android.ui.screens.profile.di

import dagger.Module
import io.golos.cyber_android.ui.screens.profile_communities.di.ProfileCommunitiesExternalUserFragmentComponent
import io.golos.cyber_android.ui.screens.profile_followers.di.ProfileFollowersExternalUserFragmentComponent
import io.golos.cyber_android.ui.screens.profile_posts.di.ProfilePostsExternalUserFragmentComponent

@Module(subcomponents = [
    ProfileCommunitiesExternalUserFragmentComponent::class,
    ProfileFollowersExternalUserFragmentComponent::class,
    ProfilePostsExternalUserFragmentComponent::class
])
class ProfileExternalUserFragmentModuleChild