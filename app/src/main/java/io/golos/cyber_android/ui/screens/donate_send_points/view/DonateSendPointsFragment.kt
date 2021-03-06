package io.golos.cyber_android.ui.screens.donate_send_points.view

import android.os.Bundle
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentWalletSendPointsBinding
import io.golos.cyber_android.ui.screens.donate_convert_points.DonateConvertPointsFragment
import io.golos.cyber_android.ui.screens.donate_send_points.di.DonateSendPointsFragmentComponent
import io.golos.cyber_android.ui.screens.wallet_dialogs.transfer_completed.TransferCompletedInfo
import io.golos.cyber_android.ui.screens.wallet_send_points.view.WalletSendPointsFragment
import io.golos.cyber_android.ui.screens.wallet_send_points.view_model.WalletSendPointsViewModel
import io.golos.domain.GlobalConstants
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.UserBriefDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain

class DonateSendPointsFragment : WalletSendPointsFragment() {
    companion object {
        private const val POST_ID = "POST_ID"
        private const val COMMUNITY_ID = "COMMUNITY_ID"
        private const val USER = "USER"
        private const val BALANCE = "BALANCE"
        private const val AMOUNT = "AMOUNT"

        fun newInstance(
            postId: ContentIdDomain,
            communityId: CommunityIdDomain,
            sendToUser: UserBriefDomain,
            balance: List<WalletCommunityBalanceRecordDomain>,
            amount: Double?) =
            DonateSendPointsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(POST_ID, postId)
                    putParcelable(COMMUNITY_ID, communityId)
                    putParcelable(USER, sendToUser)
                    putParcelableArray(BALANCE, balance.toTypedArray())
                    amount?.let { putDouble(AMOUNT, it) }
                }

            }
    }

    override fun inject(key: String) =
        App.injections.get<DonateSendPointsFragmentComponent>(
            key,
            arguments!!.getParcelable<ContentIdDomain>(POST_ID),
            arguments!!.getParcelable<CommunityIdDomain>(COMMUNITY_ID),
            arguments!!.getParcelable<UserBriefDomain>(USER),
            arguments!!.getParcelableArray(BALANCE)!!.toList(),
            arguments!!.getDouble(AMOUNT).takeIf { it > 0.0 })
            .inject(this)

    override fun releaseInjection(key: String) = App.injections.release<DonateSendPointsFragmentComponent>(key)

    override fun linkViewModel(binding: FragmentWalletSendPointsBinding, viewModel: WalletSendPointsViewModel) {
        super.linkViewModel(binding, viewModel)
    }

    override fun showWalletTransferCompletedDialog(data: TransferCompletedInfo) {
        uiHelper.showMessage(R.string.donate_successful, false)
        getDashboardFragment(this)?.navigateBack(null)
    }

    override fun openWalletConvertFragment(selectedCommunityId: CommunityIdDomain, balance: List<WalletCommunityBalanceRecordDomain>) {
        getDashboardFragment(this)?.navigateToFragment(
            DonateConvertPointsFragment.newInstance(
                balance.first { it.communityId.code != GlobalConstants.COMMUN_CODE }.communityId,
                balance
            ){
                viewModel.updateBalances()
            }
        )
    }

}