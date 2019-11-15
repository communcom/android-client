package io.golos.cyber_android.application.dependency_injection.graph.app.ui.feed

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.main_activity.feed.FeedFragment
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