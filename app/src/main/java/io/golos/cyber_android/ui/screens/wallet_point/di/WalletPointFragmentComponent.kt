package io.golos.cyber_android.ui.screens.wallet_point.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.wallet_point.view.WalletPointFragment
import io.golos.domain.dependency_injection.scopes.SubFragmentScope

@Subcomponent(modules = [WalletPointFragmentModuleBinds::class, WalletPointFragmentModule::class])
@SubFragmentScope
interface WalletPointFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: WalletPointFragmentModule): Builder
        fun build(): WalletPointFragmentComponent
    }

    fun inject(fragment: WalletPointFragment)
}