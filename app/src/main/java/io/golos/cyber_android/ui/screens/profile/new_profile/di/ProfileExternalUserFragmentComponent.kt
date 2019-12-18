package io.golos.cyber_android.ui.screens.profile.new_profile.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.profile.new_profile.view.ProfileExternalUserFragment
import io.golos.cyber_android.ui.screens.profile_communities.di.ProfileCommunitiesExternalUserFragmentComponent
import io.golos.cyber_android.ui.screens.profile_followers.di.ProfileFollowersExternalUserFragmentComponent
import io.golos.cyber_android.ui.screens.profile_posts.di.ProfilePostsExternalUserFragmentComponent
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [ProfileFragmentModuleBinds::class, ProfileFragmentModule::class, ProfileExternalUserFragmentModuleChild::class])
@FragmentScope
interface ProfileExternalUserFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: ProfileFragmentModule): Builder
        fun build(): ProfileExternalUserFragmentComponent
    }

    val communitiesFragment: ProfileCommunitiesExternalUserFragmentComponent.Builder
    val followersFragment: ProfileFollowersExternalUserFragmentComponent.Builder
    val profilePostsFragment: ProfilePostsExternalUserFragmentComponent.Builder

    fun inject(fragment: ProfileExternalUserFragment)
}