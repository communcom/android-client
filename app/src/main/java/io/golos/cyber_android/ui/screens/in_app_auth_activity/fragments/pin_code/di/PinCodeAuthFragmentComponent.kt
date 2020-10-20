package io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.pin_code.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.pin_code.PinCodeAuthFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [
    PinCodeAuthFragmentModule::class,
    PinCodeAuthFragmentModuleBinds::class
])
@FragmentScope
interface PinCodeAuthFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: PinCodeAuthFragmentModule): Builder
        fun build(): PinCodeAuthFragmentComponent
    }

    fun inject(fragment: PinCodeAuthFragment)
}