package io.golos.cyber_android.ui.screens.wallet_convert.view

import android.os.Bundle
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentWalletConvertBinding
import io.golos.cyber_android.ui.screens.profile.dto.NavigateToHomeBackCommand
import io.golos.cyber_android.ui.screens.profile.dto.NavigateToWalletBackCommand
import io.golos.cyber_android.ui.screens.wallet.view.WalletFragment
import io.golos.cyber_android.ui.screens.wallet_convert.di.WalletConvertFragmentComponent
import io.golos.cyber_android.ui.screens.wallet_convert.dto.ShowWalletConversionCompletedDialogCommand
import io.golos.cyber_android.ui.screens.wallet_convert.view_model.WalletConvertViewModel
import io.golos.cyber_android.ui.screens.wallet_dialogs.choose_points_dialog.WalletChoosePointsDialog
import io.golos.cyber_android.ui.screens.wallet_dialogs.convert_completed.ConversionCompletedInfo
import io.golos.cyber_android.ui.screens.wallet_dialogs.convert_completed.WalletConversionCompletedDialog
import io.golos.cyber_android.ui.screens.wallet_send_points.dto.ShowSelectCommunityDialogCommand
import io.golos.cyber_android.ui.screens.wallet_send_points.dto.UpdateCarouselPositionCommand
import io.golos.cyber_android.ui.shared.animation.AnimationUtils
import io.golos.cyber_android.ui.shared.keyboard.KeyboardVisibilityListener
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.HideKeyboardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import kotlinx.android.synthetic.main.fragment_wallet_convert.*
import javax.inject.Inject

open class WalletConvertFragment : FragmentBaseMVVM<FragmentWalletConvertBinding, WalletConvertViewModel>() {
    companion object {
        private const val COMMUNITY_ID = "COMMUNITY_ID"
        private const val BALANCE = "BALANCE"
        fun newInstance(communityId: CommunityIdDomain, balance: List<WalletCommunityBalanceRecordDomain>) =
            WalletConvertFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(COMMUNITY_ID, communityId)
                    putParcelableArray(BALANCE, balance.toTypedArray())
                }
            }
    }

    @Inject
    internal lateinit var keyboardVisibilityListener: KeyboardVisibilityListener

    override fun provideViewModelType(): Class<WalletConvertViewModel> = WalletConvertViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_wallet_convert

    override fun inject(key: String) =
        App.injections.get<WalletConvertFragmentComponent>(
            key,
            arguments!!.getParcelable<CommunityIdDomain>(COMMUNITY_ID),
            arguments!!.getParcelableArray(BALANCE)!!.toList())
            .inject(this)

    override fun releaseInjection(key: String) = App.injections.release<WalletConvertFragmentComponent>(key)

    override fun linkViewModel(binding: FragmentWalletConvertBinding, viewModel: WalletConvertViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        keyboardVisibilityListener.start(this)
        keyboardVisibilityListener.setOnKeyboardOpenedListener { onKeyboardOpened(it) }
        keyboardVisibilityListener.setOnKeyboardClosedListener { onKeyboardClosed(it) }

        bottomPanel.setOnSellChangeListener { viewModel.onSellInputFieldUpdated(it) }
        bottomPanel.setOnBuyChangeListener { viewModel.onBuyInputFieldUpdated(it) }
        bottomPanel.setOnBuyClearListener { viewModel.onClearInputField() }
        bottomPanel.setOnSellClearListener { viewModel.onClearInputField() }
        bottomPanel.setOnSelectPointClickListener { viewModel.onSelectCommunityClick() }
        bottomPanel.setOnSendButtonClickListener { viewModel.onSendButtonClickListener() }

        expandedPanel.setOnItemSelectedListener { viewModel.onCarouselItemSelected(it) }
        expandedPanel.setOnBackButtonClickListener { viewModel.onBackClick() }
        expandedPanel.setOnSelectCommunityButtonClickListener { viewModel.onSelectCommunityClick() }

        collapsedPanel.setOnBackButtonClickListener { viewModel.onBackClick() }
        collapsedPanel.setOnSelectCommunityButtonClickListener { viewModel.onSelectCommunityClick() }

        swapButton.setOnClickListener { viewModel.onSwapClick() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        keyboardVisibilityListener.stop(this)
    }

    override fun processViewCommand(command: ViewCommand) {
        when (command) {
            is NavigateBackwardCommand -> requireActivity().onBackPressed()
            is ShowSelectCommunityDialogCommand -> showSelectCommunityDialog(command.balance)
            is UpdateCarouselPositionCommand -> expandedPanel.setCarouselPosition(command.position)
            is HideKeyboardCommand -> bottomPanel.hideKeyboard()
            is ShowWalletConversionCompletedDialogCommand -> showWalletConversionCompletedDialog(command.data)
            is NavigateToWalletBackCommand -> { getDashboardFragment(this)?.navigateBack(WalletFragment.tag) }
            is NavigateToHomeBackCommand -> { getDashboardFragment(this)?.navigateHome() }
        }
    }

    override fun onPause() {
        super.onPause()
        bottomPanel.hideKeyboard()
    }

    private fun onKeyboardOpened(keyboardHeight: Int) {
        expandedPanel.visibility = View.INVISIBLE
        collapsedPanel.visibility = View.VISIBLE

        setExchangeButtonVisibility(false)
    }

    private fun onKeyboardClosed(keyboardHeight: Int) {
        expandedPanel.visibility = View.VISIBLE
        collapsedPanel.visibility = View.INVISIBLE

        setExchangeButtonVisibility(true)

        bottomPanel.clearFocusOnSellField()
        bottomPanel.clearFocusOnBuyField()
    }

    private fun setExchangeButtonVisibility(isVisible: Boolean) {
        if(swapButton != null) {
            AnimationUtils.getFloatAnimator(
                duration = 400,
                forward = isVisible,
                startListener = {
                    swapButton?.let {
                        if (isVisible) {
                            it.visibility = View.VISIBLE
                        }
                    }
                },
                updateListener = { alpha ->
                    swapButton?.let {
                        it.alpha = alpha
                    }
                },
                completeListener = {
                    swapButton?.let {
                        if (!isVisible) {
                            it.visibility = View.INVISIBLE
                        }
                    }
                }
            ).start()
        }
    }

    private fun showSelectCommunityDialog(balance: List<WalletCommunityBalanceRecordDomain>) = showListDialog {
        WalletChoosePointsDialog.show(this, balance) { communityId ->
            communityId?.let { viewModel.onCommunitySelected(it) }
        }
    }

    open fun showWalletConversionCompletedDialog(data: ConversionCompletedInfo) {
        WalletConversionCompletedDialog.show(this, data) {
            when(it) {
                null,
                WalletConversionCompletedDialog.Action.BACK_TO_WALLET -> viewModel.onBackToWalletSelected()
                WalletConversionCompletedDialog.Action.BACK_TO_HOME -> viewModel.onBackToHomeSelected()
            }
        }
    }

    private fun showListDialog(dialogAction: () -> Unit) =
        bottomPanel.postDelayed( { dialogAction() } , if(bottomPanel.hideKeyboard()) 300L else 0L)
}