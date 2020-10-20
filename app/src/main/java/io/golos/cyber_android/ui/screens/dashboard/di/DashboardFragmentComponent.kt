package io.golos.cyber_android.ui.screens.dashboard.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.dashboard.view.DashboardFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [DashboardFragmentModuleBinds::class])
@FragmentScope
interface DashboardFragmentComponent {
    @Subcomponent.Builder
    interface Builder {

        fun build(): DashboardFragmentComponent
    }

    fun inject(fragment: DashboardFragment)
}