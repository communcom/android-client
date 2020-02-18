package io.golos.cyber_android.ui.screens.wallet_send_points.view

import android.os.Bundle
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentWalletSendPointsBinding
import io.golos.cyber_android.ui.screens.wallet_dialogs.choose_friend_dialog.WalletChooseFriendDialog
import io.golos.cyber_android.ui.screens.wallet_dialogs.choose_points_dialog.WalletChoosePointsDialog
import io.golos.cyber_android.ui.screens.wallet_send_points.di.WalletSendPointsFragmentComponent
import io.golos.cyber_android.ui.screens.wallet_send_points.dto.ShowSelectCommunityDialogCommand
import io.golos.cyber_android.ui.screens.wallet_send_points.dto.ShowSelectUserDialogCommand
import io.golos.cyber_android.ui.screens.wallet_send_points.dto.UpdateCarouselPositionCommand
import io.golos.cyber_android.ui.screens.wallet_send_points.view_model.WalletSendPointsViewModel
import io.golos.cyber_android.ui.shared.animation.AnimationUtils
import io.golos.cyber_android.ui.shared.keyboard.KeyboardVisibilityListener
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import kotlinx.android.synthetic.main.fragment_wallet_send_points.*
import javax.inject.Inject

class WalletSendPointsFragment : FragmentBaseMVVM<FragmentWalletSendPointsBinding, WalletSendPointsViewModel>() {
    companion object {
        private const val COMMUNITY_ID = "COMMUNITY_ID"
        private const val USER = "USER"
        private const val BALANCE = "BALANCE"
        fun newInstance(communityId: String, sendToUser: UserDomain?, balance: List<WalletCommunityBalanceRecordDomain>) =
            WalletSendPointsFragment().apply {
                arguments = Bundle().apply {
                    putString(COMMUNITY_ID, communityId)
                    putParcelable(USER, sendToUser)
                    putParcelableArray(BALANCE, balance.toTypedArray())
                }
            }
    }

    @Inject
    internal lateinit var keyboardVisibilityListener: KeyboardVisibilityListener

    override fun provideViewModelType(): Class<WalletSendPointsViewModel> = WalletSendPointsViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_wallet_send_points

    override fun inject(key: String) =
        App.injections.get<WalletSendPointsFragmentComponent>(
            key,
            arguments!!.getString(COMMUNITY_ID),
            arguments!!.getParcelable<UserDomain>(USER),
            arguments!!.getParcelableArray(BALANCE)!!.toList())
            .inject(this)

    override fun releaseInjection(key: String) = App.injections.release<WalletSendPointsFragmentComponent>(key)

    override fun linkViewModel(binding: FragmentWalletSendPointsBinding, viewModel: WalletSendPointsViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        keyboardVisibilityListener.start(this)
        keyboardVisibilityListener.setOnKeyboardOpenedListener { onKeyboardOpened(it) }
        keyboardVisibilityListener.setOnKeyboardClosedListener { onKeyboardClosed(it) }

        bottomPanel.setOnSelectUserClickListener { viewModel.onSelectUserClick() }

        expandedPanel.setOnItemSelectedListener { viewModel.onCarouselItemSelected(it) }
        expandedPanel.setOnBackButtonClickListener { viewModel.onBackClick() }
        expandedPanel.setOnSelectCommunityButtonClickListener { viewModel.onSelectCommunityClick() }

        collapsedPanel.setOnBackButtonClickListener { viewModel.onBackClick() }
        collapsedPanel.setOnSelectCommunityButtonClickListener { viewModel.onSelectCommunityClick() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        keyboardVisibilityListener.stop(this)
    }

    override fun processViewCommand(command: ViewCommand) {
        when (command) {
            is NavigateBackwardCommand -> requireActivity().onBackPressed()
            is ShowSelectUserDialogCommand -> showSelectUserDialog()
            is ShowSelectCommunityDialogCommand -> showSelectCommunityDialog(command.balance)
            is UpdateCarouselPositionCommand -> expandedPanel.setCarouselPosition(command.position)
        }
    }

    private fun onKeyboardOpened(keyboardHeight: Int) {
        AnimationUtils.getFloatAnimator(
            duration = 200,
            startListener = {
                collapsedPanel.alpha = 0f
                collapsedPanel.visibility = View.VISIBLE
            },
            updateListener = {
                collapsedPanel.alpha = it
            },
            completeListener = {
                expandedPanel.visibility = View.INVISIBLE
            }
        ).start()
    }

    private fun onKeyboardClosed(keyboardHeight: Int) {
        AnimationUtils.getFloatAnimator(
            forward = false,
            duration = 200,
            startListener = {
                expandedPanel.visibility = View.VISIBLE
                collapsedPanel.alpha = 1f
                collapsedPanel.visibility = View.VISIBLE
            },
            updateListener = {
                collapsedPanel.alpha = it
            },
            completeListener = {
                collapsedPanel.visibility = View.INVISIBLE
            }
        ).start()
    }

    private fun showSelectUserDialog() =
        WalletChooseFriendDialog.show(this) { userId -> userId?.let { viewModel.onUserSelected(it) } }

    private fun showSelectCommunityDialog(balance: List<WalletCommunityBalanceRecordDomain>) =
        WalletChoosePointsDialog.show(this, balance) { communityId ->
            communityId?.let { viewModel.onCommunitySelected(it) }
        }
}