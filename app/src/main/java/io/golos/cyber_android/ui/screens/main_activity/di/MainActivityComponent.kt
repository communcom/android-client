package io.golos.cyber_android.ui.screens.main_activity.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.communities_list.di.CommunitiesListFragmentComponent
import io.golos.cyber_android.ui.screens.communities_list.di.CommunitiesListFragmentTabComponent
import io.golos.cyber_android.ui.screens.main_activity.MainActivity
import io.golos.domain.dependency_injection.scopes.ActivityScope

@Subcomponent(
    modules = [
        MainActivityModuleChilds::class,
        MainActivityModuleBinds::class
    ]
)
@ActivityScope
interface MainActivityComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): MainActivityComponent
    }

    val communitiesFragmentComponent: CommunitiesListFragmentComponent.Builder
    val communitiesFragmentTabComponent: CommunitiesListFragmentTabComponent.Builder

    fun inject(activity: MainActivity)
}