package io.golos.cyber_android.ui.screens.wallet.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.wallet.view.WalletFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [WalletFragmentModuleBinds::class])
@FragmentScope
interface WalletFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): WalletFragmentComponent
    }

    fun inject(fragment: WalletFragment)
}