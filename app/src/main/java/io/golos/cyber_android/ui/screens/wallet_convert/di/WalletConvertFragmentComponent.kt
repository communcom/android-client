package io.golos.cyber_android.ui.screens.wallet_convert.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.wallet_convert.view.WalletConvertFragment
import io.golos.domain.dependency_injection.scopes.SubFragmentScope

@Subcomponent(modules = [WalletConvertFragmentModuleBinds::class, WalletConvertFragmentModule::class])
@SubFragmentScope
interface WalletConvertFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: WalletConvertFragmentModule): Builder
        fun build(): WalletConvertFragmentComponent
    }

    fun inject(fragment: WalletConvertFragment)
}