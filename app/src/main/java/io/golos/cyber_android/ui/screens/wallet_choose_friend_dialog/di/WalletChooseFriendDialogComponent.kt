package io.golos.cyber_android.ui.screens.wallet_choose_friend_dialog.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.wallet_choose_friend_dialog.WalletChooseFriendDialog
import io.golos.domain.dependency_injection.scopes.SubFragmentScope

@Subcomponent
@SubFragmentScope
interface WalletChooseFriendDialogComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): WalletChooseFriendDialogComponent
    }

    fun inject(fragment: WalletChooseFriendDialog)
}