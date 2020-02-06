package io.golos.cyber_android.ui.screens.wallet.view

import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.appbar.AppBarLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentWalletBinding
import io.golos.cyber_android.ui.screens.wallet.di.WalletFragmentComponent
import io.golos.cyber_android.ui.screens.wallet.view_model.WalletViewModel
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import kotlinx.android.synthetic.main.fragment_wallet.*

class WalletFragment : FragmentBaseMVVM<FragmentWalletBinding, WalletViewModel>() {

    companion object {
        fun newInstance() = WalletFragment()
    }

    override fun provideViewModelType(): Class<WalletViewModel> = WalletViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_wallet

    override fun inject(key: String) =
        App.injections.get<WalletFragmentComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<WalletFragmentComponent>(key)

    override fun linkViewModel(binding: FragmentWalletBinding, viewModel: WalletViewModel) {
        binding.viewModel = viewModel
    }

//    override fun processViewCommand(command: ViewCommand) {
//        when (command) {
//            is ShowSelectPhotoDialogCommand -> showPhotoDialog(command.place)
//            is ShowEditBioDialogCommand -> showEditBioDialog()
//            is MoveToSelectPhotoPageCommand -> moveToSelectPhotoPage(command.place, command.imageUrl)
//            is MoveToBioPageCommand -> moveToBioPage(command.text)
//            is MoveToFollowersPageCommand -> moveToFollowersPage(command.filter, command.mutualUsers)
//            is ShowSettingsDialogCommand -> showSettingsDialog()
//            is ShowExternalUserSettingsDialogCommand -> showExternalUserSettingsDialog(command.isBlocked)
//            is ShowConfirmationDialog -> showConfirmationDialog(command.textRes)
//            is MoveToLikedPageCommand -> moveToLikedPage()
//            is MoveToBlackListPageCommand -> moveToBlackListPage()
//            is NavigateBackwardCommand -> requireActivity().onBackPressed()
//            is RestartAppCommand -> restartApp()
//            is LoadPostsAndCommentsCommand -> initPages()
//        }
//    }
}