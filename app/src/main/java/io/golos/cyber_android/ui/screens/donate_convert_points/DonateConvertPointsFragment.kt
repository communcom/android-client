package io.golos.cyber_android.ui.screens.donate_convert_points

import android.os.Bundle
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.donate_convert_points.di.DonateConvertPointsFragmentComponent
import io.golos.cyber_android.ui.screens.wallet_convert.view.WalletConvertFragment
import io.golos.cyber_android.ui.screens.wallet_dialogs.convert_completed.ConversionCompletedInfo
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain

class DonateConvertPointsFragment : WalletConvertFragment() {
    companion object {
        private const val COMMUNITY_ID = "COMMUNITY_ID"
        private const val BALANCE = "BALANCE"
        fun newInstance(communityId: CommunityIdDomain, balance: List<WalletCommunityBalanceRecordDomain>,callback:()->Unit) =
            DonateConvertPointsFragment().apply {
                this.callback = callback
                arguments = Bundle().apply {
                    putParcelable(COMMUNITY_ID, communityId)
                    putParcelableArray(BALANCE, balance.toTypedArray())
                }
            }
    }

    private var callback:(()->Unit)? = null

    override fun inject(key: String) =
        App.injections.get<DonateConvertPointsFragmentComponent>(
            key,
            arguments!!.getParcelable<CommunityIdDomain>(COMMUNITY_ID),
            arguments!!.getParcelableArray(BALANCE)!!.toList()
        ).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<DonateConvertPointsFragmentComponent>(key)

    override fun showWalletConversionCompletedDialog(data: ConversionCompletedInfo) {
        callback?.invoke()
        getDashboardFragment(this)?.navigateBack(null)
    }
}