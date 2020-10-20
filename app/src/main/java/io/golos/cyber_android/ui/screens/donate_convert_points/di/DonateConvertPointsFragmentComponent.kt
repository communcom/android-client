package io.golos.cyber_android.ui.screens.donate_convert_points.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.donate_convert_points.DonateConvertPointsFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [DonateConvertPointsFragmentModuleBinds::class, DonateConvertPointsFragmentModule::class])
@FragmentScope
interface DonateConvertPointsFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: DonateConvertPointsFragmentModule): Builder
        fun build(): DonateConvertPointsFragmentComponent
    }

    fun inject(fragment: DonateConvertPointsFragment)
}