package io.golos.cyber_android.ui.screens.profile_posts.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.profile_posts.view.ProfilePostsFragment
import io.golos.domain.dependency_injection.scopes.SubFragmentScope

@Subcomponent(modules = [ProfilePostsFragmentModuleBinds::class, ProfilePostsFragmentModule::class])
@SubFragmentScope
interface ProfilePostsFragmentComponent {

    @Subcomponent.Builder
    interface Builder {
        fun init(module: ProfilePostsFragmentModule): Builder
        fun build(): ProfilePostsFragmentComponent
    }

    fun inject(fragment: ProfilePostsFragment)
}