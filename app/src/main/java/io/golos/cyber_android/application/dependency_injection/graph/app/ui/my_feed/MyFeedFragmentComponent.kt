package io.golos.cyber_android.application.dependency_injection.graph.app.ui.my_feed

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.myfeed.view.MyFeedFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [MyFeedFragmentModuleBinds::class, MyFeedFragmentModule::class])
@FragmentScope
interface MyFeedFragmentComponent {
    @Subcomponent.Builder
    interface Builder {

        fun postsListFragmentModule(module: MyFeedFragmentModule): Builder

        fun build(): MyFeedFragmentComponent
    }

    fun inject(fragment: MyFeedFragment)
}