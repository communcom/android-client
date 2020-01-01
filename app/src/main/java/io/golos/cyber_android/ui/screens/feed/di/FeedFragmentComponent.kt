package io.golos.cyber_android.ui.screens.feed.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.feed.FeedFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [FeedFragmentModuleBinds::class])
@FragmentScope
interface FeedFragmentComponent {

    @Subcomponent.Builder
    interface Builder {
        fun build(): FeedFragmentComponent
    }

    fun inject(fragment: FeedFragment)
}