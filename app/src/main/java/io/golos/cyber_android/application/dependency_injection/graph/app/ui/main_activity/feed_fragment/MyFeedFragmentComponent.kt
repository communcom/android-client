package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.feed_fragment

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.feed.MyFeedFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [
    MyFeedFragmentModule::class,
    MyFeedFragmentModuleBinds::class
])
@FragmentScope
interface MyFeedFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: MyFeedFragmentModule): Builder
        fun build(): MyFeedFragmentComponent
    }

    fun inject(fragment: MyFeedFragment)
}