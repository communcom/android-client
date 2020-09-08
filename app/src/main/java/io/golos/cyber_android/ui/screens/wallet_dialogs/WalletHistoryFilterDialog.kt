package io.golos.cyber_android.ui.screens.wallet_dialogs

import android.view.View
import androidx.fragment.app.Fragment
import io.golos.commun4j.services.model.TransferHistoryDirection
import io.golos.commun4j.services.model.TransferHistoryHoldType
import io.golos.commun4j.services.model.TransferHistoryTransferType
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dialogs.base.BottomSheetDialogFragmentBase
import io.golos.domain.dto.HistoryFilterDomain
import kotlinx.android.synthetic.main.dialog_wallet_history_filter.*

class WalletHistoryFilterDialog private constructor() : BottomSheetDialogFragmentBase<WalletHistoryFilterDialog.Result>() {

    sealed class Result {
        class ApplyFilter(val filter : HistoryFilterDomain) : Result()
        object RestoreToDefaults : Result()
        object Cancel : Result()
    }

    companion object {
        fun show(parent: Fragment, callback: (Result?) -> Unit) = WalletHistoryFilterDialog().apply {
            closeActionListener = callback
        }.show(parent.parentFragmentManager, "Filter")
    }

    override val closeButton: View?
        get() = btnClose

    override val layout: Int
        get() = R.layout.dialog_wallet_history_filter

    override fun setupView() {
        save_button.setOnClickListener { closeOnItemSelected(Result.ApplyFilter(
            HistoryFilterDomain(
                transferType = if(type_tabs.selectedTabPosition == 0){
                    TransferHistoryTransferType.TRANSFER
                }else {
                    TransferHistoryTransferType.CONVERT
                },
                direction = when(base_filters_layout.selectedTabPosition){
                    0 -> TransferHistoryDirection.ALL
                    1-> TransferHistoryDirection.RECEIVE
                    else-> TransferHistoryDirection.SEND
                },
                holdType = TransferHistoryHoldType.NONE,
                reward = when(reward_tabs.selectedTabPosition){
                    0-> "post"
                    1-> "like"
                    else -> "comment"
                }
            )
        )) }
        clean_all_button.setOnClickListener { closeOnItemSelected(Result.RestoreToDefaults) }
    }

}