package io.golos.cyber_android.ui.screens.wallet_send_points.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.wallet_send_points.view.WalletSendPointsFragment
import io.golos.domain.dependency_injection.scopes.SubFragmentScope

@Subcomponent(modules = [WalletSendPointsFragmentModuleBinds::class, WalletSendPointsFragmentModule::class])
@SubFragmentScope
interface WalletSendPointsFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: WalletSendPointsFragmentModule): Builder
        fun build(): WalletSendPointsFragmentComponent
    }

    fun inject(fragment: WalletSendPointsFragment)
}