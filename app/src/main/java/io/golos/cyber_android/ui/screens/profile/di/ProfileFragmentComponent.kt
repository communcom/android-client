package io.golos.cyber_android.ui.screens.profile.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.profile_bio.di.ProfileBioFragmentComponent
import io.golos.cyber_android.ui.screens.profile_communities.di.ProfileCommunitiesFragmentComponent
import io.golos.cyber_android.ui.screens.profile_followers.di.ProfileFollowersFragmentComponent
import io.golos.cyber_android.ui.screens.profile_photos.di.ProfilePhotosFragmentComponent
import io.golos.cyber_android.ui.screens.profile.view.ProfileFragment
import io.golos.cyber_android.ui.screens.profile_black_list.di.ProfileBlackListFragmentComponent
import io.golos.cyber_android.ui.screens.profile_posts.di.ProfilePostsFragmentComponent
import io.golos.cyber_android.ui.screens.profile_posts.di.ProfilePostsLikedFragmentComponent
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
    val followersFragment: ProfileFollowersFragmentComponent.Builder
    val blackListFragment: ProfileBlackListFragmentComponent.Builder
    val profilePostsFragment: ProfilePostsFragmentComponent.Builder
    val likedFragment: ProfilePostsLikedFragmentComponent.Builder

    fun inject(fragment: ProfileFragment)
}