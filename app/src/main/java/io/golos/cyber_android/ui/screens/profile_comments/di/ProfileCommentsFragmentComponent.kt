package io.golos.cyber_android.ui.screens.profile_comments.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.profile_comments.view.ProfileCommentsFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [ProfileCommentsFragmentModuleBinds::class, ProfileCommentsModule::class])
@FragmentScope
interface ProfileCommentsFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: ProfileCommentsModule): Builder

        fun build(): ProfileCommentsFragmentComponent
    }

    fun inject(fragment: ProfileCommentsFragment)
}