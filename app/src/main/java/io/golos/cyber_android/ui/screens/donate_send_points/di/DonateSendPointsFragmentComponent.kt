package io.golos.cyber_android.ui.screens.donate_send_points.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.donate_send_points.view.DonateSendPointsFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [DonateSendPointsFragmentModuleBinds::class, DonateSendPointsFragmentModule::class])
@FragmentScope
interface DonateSendPointsFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: DonateSendPointsFragmentModule): Builder
        fun build(): DonateSendPointsFragmentComponent
    }

    fun inject(fragment: DonateSendPointsFragment)
}