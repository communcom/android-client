package io.golos.cyber_android.ui.screens.community_get_points

import android.os.Bundle
import android.view.View
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.community_get_points.di.GetCommunityPointsFragmentComponent
import io.golos.cyber_android.ui.screens.wallet_convert.view.WalletConvertFragment
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain

class GetCommunityPointsFragment : WalletConvertFragment(){
    companion object {
        private const val COMMUNITY_ID = "COMMUNITY_ID"
        private const val BALANCE = "BALANCE"
        fun newInstance(communityId: CommunityIdDomain, balance: List<WalletCommunityBalanceRecordDomain>) =
            GetCommunityPointsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(COMMUNITY_ID, communityId)
                    putParcelableArray(BALANCE, balance.toTypedArray())
                }
            }
    }

    override fun inject(key: String) =
        App.injections.get<GetCommunityPointsFragmentComponent>(
            key,
            arguments!!.getParcelable<CommunityIdDomain>(COMMUNITY_ID),
            arguments!!.getParcelableArray(BALANCE)!!.toList())
            .inject(this)

    override fun releaseInjection(key: String) = App.injections.release<GetCommunityPointsFragmentComponent>(key)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.changeMode()
    }
}