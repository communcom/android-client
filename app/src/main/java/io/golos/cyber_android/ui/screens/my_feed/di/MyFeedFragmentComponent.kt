package io.golos.cyber_android.ui.screens.my_feed.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.my_feed.view.MyFeedFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [MyFeedFragmentModuleBinds::class])
@FragmentScope
interface MyFeedFragmentComponent {
    @Subcomponent.Builder
    interface Builder {

        fun build(): MyFeedFragmentComponent
    }

    fun inject(fragment: MyFeedFragment)
}