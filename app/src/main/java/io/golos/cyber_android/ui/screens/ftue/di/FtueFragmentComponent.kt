package io.golos.cyber_android.ui.screens.ftue.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.ftue.view.FtueFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [FtueFragmentModuleBinds::class])
@FragmentScope
interface FtueFragmentComponent {
    @Subcomponent.Builder
    interface Builder {

        fun build(): FtueFragmentComponent
    }

    fun inject(fragment: FtueFragment)
}