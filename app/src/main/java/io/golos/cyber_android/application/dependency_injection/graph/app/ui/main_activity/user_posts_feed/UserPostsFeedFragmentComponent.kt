package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.user_posts_feed

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.profile.old_profile.posts.UserPostsFeedFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [
    UserPostsFeedFragmentModule::class,
    UserPostsFeedFragmentModuleBinds::class
])
@FragmentScope
interface UserPostsFeedFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: UserPostsFeedFragmentModule): Builder
        fun build(): UserPostsFeedFragmentComponent
    }

    fun inject(fragment: UserPostsFeedFragment)
}