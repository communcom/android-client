package io.golos.cyber_android.ui.screens.wallet_send_points.view

import android.os.Bundle
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentWalletSendPointsBinding
import io.golos.cyber_android.ui.screens.wallet_send_points.di.WalletSendPointsFragmentComponent
import io.golos.cyber_android.ui.screens.wallet_send_points.view_model.WalletSendPointsViewModel
import io.golos.cyber_android.ui.shared.animation.AnimationUtils
import io.golos.cyber_android.ui.shared.keyboard.KeyboardVisibilityListener
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import kotlinx.android.synthetic.main.fragment_wallet_send_points.*
import javax.inject.Inject

class WalletSendPointsFragment : FragmentBaseMVVM<FragmentWalletSendPointsBinding, WalletSendPointsViewModel>() {
    companion object {
        private const val COMMUNITY_ID = "COMMUNITY_ID"
        private const val USER_ID = "USER_ID"
        private const val BALANCE = "BALANCE"
        fun newInstance(communityId: String, sendToUserId: UserIdDomain, balance: List<WalletCommunityBalanceRecordDomain>) =
            WalletSendPointsFragment().apply {
                arguments = Bundle().apply {
                    putString(COMMUNITY_ID, communityId)
                    putParcelable(USER_ID, sendToUserId)
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
            arguments!!.getParcelable<UserIdDomain>(USER_ID),
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

//        primePanel.setOnBackButtonClickListener { viewModel.onBackClick() }
//        primePanel.setOnItemSelectedListener { viewModel.onCommunitySelected(it) }
//        toolbarContent.setOnBackButtonClickListener { viewModel.onBackClick() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        keyboardVisibilityListener.stop(this)
    }

    override fun processViewCommand(command: ViewCommand) {
        when (command) {
            is NavigateBackwardCommand -> requireActivity().onBackPressed()
        }
    }

    private fun onKeyboardOpened(keyboardHeight: Int) {
        AnimationUtils.getFloatAnimator(
            duration = 200,
            startListener = {
                secondaryPanel.alpha = 0f
                secondaryPanel.visibility = View.VISIBLE
            },
            updateListener = {
                secondaryPanel.alpha = it
            },
            completeListener = {
                primaryPanel.visibility = View.INVISIBLE
            }
        ).start()

//        val bottomLayoutParams = bottomPanel.layoutParams
//        bottomLayoutParams.height = bottomLayoutParams.height - keyboardHeight
//        bottomPanel.layoutParams = bottomLayoutParams
    }

    private fun onKeyboardClosed(keyboardHeight: Int) {
        AnimationUtils.getFloatAnimator(
            forward = false,
            duration = 200,
            startListener = {
                primaryPanel.visibility = View.VISIBLE
                secondaryPanel.alpha = 1f
                secondaryPanel.visibility = View.VISIBLE
            },
            updateListener = {
                secondaryPanel.alpha = it
            },
            completeListener = {
                secondaryPanel.visibility = View.INVISIBLE
            }
        ).start()

//        val bottomLayoutParams = bottomPanel.layoutParams
//        bottomLayoutParams.height = bottomLayoutParams.height + keyboardHeight
//        bottomPanel.layoutParams = bottomLayoutParams
    }
}