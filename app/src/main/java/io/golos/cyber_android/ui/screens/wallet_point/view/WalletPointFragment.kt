package io.golos.cyber_android.ui.screens.wallet_point.view

import android.os.Bundle
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentWalletPointBinding
import io.golos.cyber_android.ui.screens.wallet.dto.NavigateToWalletConvertCommand
import io.golos.cyber_android.ui.screens.wallet.dto.NavigateToWalletSendPoints
import io.golos.cyber_android.ui.screens.wallet.dto.ShowSendPointsDialog
import io.golos.cyber_android.ui.screens.wallet_convert.view.WalletConvertFragment
import io.golos.cyber_android.ui.screens.wallet_dialogs.choose_friend_dialog.WalletChooseFriendDialog
import io.golos.cyber_android.ui.screens.wallet_point.di.WalletPointFragmentComponent
import io.golos.cyber_android.ui.screens.wallet_point.view_model.WalletPointViewModel
import io.golos.cyber_android.ui.screens.wallet_send_points.view.WalletSendPointsFragment
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.UserBriefDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import kotlinx.android.synthetic.main.fragment_wallet_point.*
import android.widget.Toast
import io.golos.cyber_android.ui.screens.wallet.dto.ShowFilterDialog
import io.golos.cyber_android.ui.screens.wallet_dialogs.WalletHistoryFilterDialog

class WalletPointFragment : FragmentBaseMVVM<FragmentWalletPointBinding, WalletPointViewModel>() {
    companion object {
        private const val BALANCE = "BALANCE"
        private const val COMMUNITY_ID = "COMMUNITY_ID"
        fun newInstance(communityId: CommunityIdDomain, balance: List<WalletCommunityBalanceRecordDomain>) = WalletPointFragment().apply {
            arguments = Bundle().apply {
                putParcelable(COMMUNITY_ID, communityId)
                putParcelableArray(BALANCE, balance.toTypedArray())
            }
        }
    }

    override fun provideViewModelType(): Class<WalletPointViewModel> = WalletPointViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_wallet_point

    override fun inject(key: String) =
        App.injections.get<WalletPointFragmentComponent>(
            key,
            arguments!!.getParcelable<CommunityIdDomain>(COMMUNITY_ID),
            arguments!!.getParcelableArray(BALANCE)!!.toList())
            .inject(this)

    override fun releaseInjection(key: String) = App.injections.release<WalletPointFragmentComponent>(key)

    override fun linkViewModel(binding: FragmentWalletPointBinding, viewModel: WalletPointViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        primePanel.setOnBackButtonClickListener { viewModel.onBackClick() }
        primePanel.setOnItemSelectedListener { viewModel.onCommunitySelected(it) }
        primePanel.setOnSendClickListener { viewModel.onSendPointsItemClick(null) }
        primePanel.setOnConvertClickListener { viewModel.onConvertClick() }

        toolbarContent.setOnBackButtonClickListener { viewModel.onBackClick() }
        sendPointsArea.setOnSeeAllClickListener { viewModel.onSeeAllSendPointsClick() }
    }

    override fun processViewCommand(command: ViewCommand) {
        when (command) {
            is NavigateBackwardCommand -> requireActivity().onBackPressed()

            is NavigateToWalletSendPoints ->
                moveToWalletSendPoints(command.selectedCommunityId, command.sendToUser, command.balance)

            is NavigateToWalletConvertCommand -> moveToWalletConvert(command.selectedCommunityId, command.balance)

            is ShowSendPointsDialog -> showSendPointsDialog()

            is ShowFilterDialog -> showFilterDialog()
        }
    }

    private fun showFilterDialog() {
        WalletHistoryFilterDialog.show(this){
            when(it){
                is WalletHistoryFilterDialog.Result.ApplyFilter ->{
                    viewModel.applyFilters(it.filter)
                }
                is WalletHistoryFilterDialog.Result.RestoreToDefaults ->{
                    viewModel.applyFilters(null)
                }
                is WalletHistoryFilterDialog.Result.Cancel ->{
                    Toast.makeText(requireContext(),"Cancel",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun moveToWalletSendPoints(
        selectedCommunityId: CommunityIdDomain,
        sendToUser: UserBriefDomain?,
        balance: List<WalletCommunityBalanceRecordDomain>) =
        getDashboardFragment(this)?.navigateToFragment(WalletSendPointsFragment.newInstance(selectedCommunityId, sendToUser, balance))

    private fun moveToWalletConvert(selectedCommunityId: CommunityIdDomain, balance: List<WalletCommunityBalanceRecordDomain>) {
        getDashboardFragment(this)?.navigateToFragment(WalletConvertFragment.newInstance(selectedCommunityId, balance))
    }

    private fun showSendPointsDialog() =
        WalletChooseFriendDialog.show(this) { userId -> userId?.let { viewModel.onSendPointsItemClick(it) } }
}