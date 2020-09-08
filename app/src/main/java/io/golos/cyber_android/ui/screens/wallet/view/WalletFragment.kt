package io.golos.cyber_android.ui.screens.wallet.view

import android.os.Bundle
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentWalletBinding
import io.golos.cyber_android.ui.dialogs.WalletSettingsDialog
import io.golos.cyber_android.ui.screens.wallet.di.WalletFragmentComponent
import io.golos.cyber_android.ui.screens.wallet.dto.*
import io.golos.cyber_android.ui.screens.wallet.view_model.WalletViewModel
import io.golos.cyber_android.ui.screens.wallet_convert.view.WalletConvertFragment
import io.golos.cyber_android.ui.screens.wallet_dialogs.choose_friend_dialog.WalletChooseFriendDialog
import io.golos.cyber_android.ui.screens.wallet_dialogs.choose_points_dialog.WalletChoosePointsDialog
import io.golos.cyber_android.ui.screens.wallet_point.view.WalletPointFragment
import io.golos.cyber_android.ui.screens.wallet_send_points.view.WalletSendPointsFragment
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.GlobalConstants
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.UserBriefDomain
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import kotlinx.android.synthetic.main.fragment_wallet.*
import android.widget.Toast
import io.golos.cyber_android.ui.screens.wallet_dialogs.WalletHistoryFilterDialog

class WalletFragment : FragmentBaseMVVM<FragmentWalletBinding, WalletViewModel>() {
    companion object {
        const val  tag = "WALLET_FRAGMENT_TAG"

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
        primePanel.setOnUsdButtonClickListener { viewModel.onUsdClick() }
        primePanel.setOnCommunButtonClickListener { viewModel.onCommunClick() }
        primePanel.setOnMenuButtonClickListener { viewModel.onSettingsClick() }
        primePanel.setOnSendClickListener { viewModel.onSendPointsItemClick(null) }
        primePanel.setOnConvertClickListener { viewModel.onConvertClick() }

        toolbarContent.setOnBackButtonClickListener { viewModel.onBackClick() }

        myPointsArea.setOnSeeAllClickListener { viewModel.onSeeAllMyPointsClick() }

        sendPointsArea.setOnSeeAllClickListener { viewModel.onSeeAllSendPointsClick() }
    }

    override fun processViewCommand(command: ViewCommand) {
        when (command) {
            is NavigateBackwardCommand -> requireActivity().onBackPressed()
            is NavigateToWalletPoint -> moveToWalletPoint(command.selectedCommunityId, command.balance)

            is NavigateToWalletSendPoints ->
                moveToWalletSendPoints(command.selectedCommunityId, command.sendToUser, command.balance)

            is NavigateToWalletConvertCommand -> moveToWalletConvert(command.selectedCommunityId, command.balance)

            is ShowMyPointsDialog -> showMyPointsDialog(command.balance)
            is ShowSendPointsDialog -> showSendPointsDialog()
            is ShowSettingsDialog -> showSettingsDialog(command.emptyBalanceVisibility)
            is ShowFilterDialog -> showFilterDialog()
        }
    }

    private fun showFilterDialog() {
        WalletHistoryFilterDialog.show(this) {
            when (it) {
                is WalletHistoryFilterDialog.Result.ApplyFilter -> {
                    viewModel.applyFilters(it.filter)
                }
                is WalletHistoryFilterDialog.Result.RestoreToDefaults -> {
                    viewModel.applyFilters(null)
                }
                is WalletHistoryFilterDialog.Result.Cancel -> {
                    Toast.makeText(requireContext(), "Cancel", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun moveToWalletPoint(selectedCommunityId: CommunityIdDomain, balance: List<WalletCommunityBalanceRecordDomain>) =
        getDashboardFragment(this)?.navigateToFragment(WalletPointFragment.newInstance(selectedCommunityId, balance))

    private fun moveToWalletSendPoints(
        selectedCommunityId: CommunityIdDomain,
        sendToUser: UserBriefDomain?,
        balance: List<WalletCommunityBalanceRecordDomain>) =
        getDashboardFragment(this)?.navigateToFragment(WalletSendPointsFragment.newInstance(selectedCommunityId, sendToUser, balance))

    private fun moveToWalletConvert(selectedCommunityId: CommunityIdDomain, balance: List<WalletCommunityBalanceRecordDomain>) {
        getDashboardFragment(this)?.navigateToFragment(WalletConvertFragment.newInstance(selectedCommunityId, balance))
    }

    private fun showMyPointsDialog(balance: List<WalletCommunityBalanceRecordDomain>) =
        WalletChoosePointsDialog.show(this, balance) { communityId ->
            communityId?.let { viewModel.onMyPointItemClick(it) }
        }

    private fun showSendPointsDialog() =
        WalletChooseFriendDialog.show(this) { userId -> userId?.let { viewModel.onSendPointsItemClick(it) } }

    private fun showSettingsDialog(isVisible: Boolean) {
        WalletSettingsDialog.show(this@WalletFragment, isVisible) {
            when(it) {
                is WalletSettingsDialog.Result.StateSelected -> viewModel.onEmptyBalancesShowHide(it.emptyBalanceVisibility)
            }
        }
    }
}