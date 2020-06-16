package io.golos.cyber_android.ui.screens.feed_my.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.feed_my.view.MyFeedFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [MyFeedFragmentModuleBinds::class, MyFeedFragmentModule::class])
@FragmentScope
interface MyFeedFragmentComponent {
    @Subcomponent.Builder
    interface Builder {

        fun build(): MyFeedFragmentComponent
    }

    fun inject(fragment: MyFeedFragment)
}