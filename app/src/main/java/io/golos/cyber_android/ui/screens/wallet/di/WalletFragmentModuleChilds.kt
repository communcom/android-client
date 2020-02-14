package io.golos.cyber_android.ui.screens.wallet.di

import dagger.Module
import io.golos.cyber_android.ui.screens.wallet_dialogs.choose_friend_dialog.di.WalletChooseFriendDialogComponent
import io.golos.cyber_android.ui.screens.wallet_point.di.WalletPointFragmentComponent

@Module(subcomponents = [
    WalletPointFragmentComponent::class,
    WalletChooseFriendDialogComponent::class
])
class WalletFragmentModuleChilds