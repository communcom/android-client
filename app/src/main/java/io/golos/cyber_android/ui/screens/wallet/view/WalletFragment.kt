package io.golos.cyber_android.ui.screens.wallet.view

import android.os.Bundle
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentWalletBinding
import io.golos.cyber_android.ui.screens.wallet.di.WalletFragmentComponent
import io.golos.cyber_android.ui.screens.wallet.dto.NavigateToWalletPoint
import io.golos.cyber_android.ui.screens.wallet.dto.NavigateToWalletSendPoints
import io.golos.cyber_android.ui.screens.wallet.dto.ShowMyPointsDialog
import io.golos.cyber_android.ui.screens.wallet.dto.ShowSendPointsDialog
import io.golos.cyber_android.ui.screens.wallet.view_model.WalletViewModel
import io.golos.cyber_android.ui.screens.wallet_dialogs.choose_friend_dialog.WalletChooseFriendDialog
import io.golos.cyber_android.ui.screens.wallet_dialogs.choose_points_dialog.WalletChoosePointsDialog
import io.golos.cyber_android.ui.screens.wallet_point.view.WalletPointFragment
import io.golos.cyber_android.ui.screens.wallet_send_points.view.WalletSendPointsFragment
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.GlobalConstants
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import kotlinx.android.synthetic.main.fragment_wallet.*

class WalletFragment : FragmentBaseMVVM<FragmentWalletBinding, WalletViewModel>() {
    companion object {
        private const val BALANCE = "BALANCE"
        fun newInstance(sourceBalance: List<WalletCommunityBalanceRecordDomain>) = WalletFragment().apply {
            arguments = Bundle().apply {
                putParcelableArray(BALANCE, sourceBalance.toTypedArray())
            }
        }
    }

    override fun provideViewModelType(): Class<WalletViewModel> = WalletViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_wallet

    override fun inject(key: String) =
        App.injections.get<WalletFragmentComponent>(
            key,
            GlobalConstants.PAGE_SIZE,
            arguments!!.getParcelableArray(BALANCE)!!.toList())
            .inject(this)

    override fun releaseInjection(key: String) = App.injections.release<WalletFragmentComponent>(key)

    override fun linkViewModel(binding: FragmentWalletBinding, viewModel: WalletViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        primePanel.setOnBackButtonClickListener { viewModel.onBackClick() }
        toolbarContent.setOnBackButtonClickListener { viewModel.onBackClick() }
        myPointsArea.setOnSeeAllClickListener { viewModel.onSeeAllMyPointsClick() }
        sendPointsArea.setOnSeeAllClickListener { viewModel.onSeeAllSendPointsClick() }
    }

    override fun processViewCommand(command: ViewCommand) {
        when (command) {
            is NavigateBackwardCommand -> requireActivity().onBackPressed()

            is NavigateToWalletPoint -> moveToWalletPoint(command.selectedCommunityId, command.balance)

            is NavigateToWalletSendPoints ->
                moveToWalletSendPoints(command.selectedCommunityId, command.sendToUserId, command.balance)

            is ShowMyPointsDialog -> showMyPointsDialog(command.balance)

            is ShowSendPointsDialog -> showSendPointsDialog()
        }
    }

    private fun moveToWalletPoint(selectedCommunityId: String, balance: List<WalletCommunityBalanceRecordDomain>) =
        getDashboardFragment(this)?.showFragment(WalletPointFragment.newInstance(selectedCommunityId, balance))

    private fun moveToWalletSendPoints(
        selectedCommunityId: String,
        sendToUserId: UserIdDomain,
        balance: List<WalletCommunityBalanceRecordDomain>) =
        getDashboardFragment(this)?.showFragment(WalletSendPointsFragment.newInstance(selectedCommunityId, sendToUserId, balance))

    private fun showMyPointsDialog(balance: List<WalletCommunityBalanceRecordDomain>) =
        WalletChoosePointsDialog.show(this, balance) { communityId ->
            communityId?.let { viewModel.onMyPointItemClick(it) }
        }

    private fun showSendPointsDialog() =
        WalletChooseFriendDialog.show(this) { userId -> userId?.let { viewModel.onSendPointsItemClick(it) } }
}