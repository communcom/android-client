package io.golos.cyber_android.ui.screens.profile_posts.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.profile_posts.view.ProfilePostsLikedFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [ProfilePostsFragmentModuleBinds::class, ProfilePostsFragmentModule::class])
@FragmentScope
interface ProfilePostsLikedFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: ProfilePostsFragmentModule): Builder
        fun build(): ProfilePostsLikedFragmentComponent
    }

    fun inject(fragment: ProfilePostsLikedFragment)
}