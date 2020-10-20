package io.golos.cyber_android.ui.screens.ftue_finish.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.ftue_finish.view.FtueFinishFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [FtueFinishFragmentModuleBinds::class])
@FragmentScope
interface FtueFinishFragmentComponent {
    @Subcomponent.Builder
    interface Builder {

        fun build(): FtueFinishFragmentComponent
    }

    fun inject(fragment: FtueFinishFragment)
}
