package io.golos.cyber_android.ui.screens.wallet_dialogs

import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.golos.commun4j.services.model.TransferHistoryDirection
import io.golos.commun4j.services.model.TransferHistoryDonation
import io.golos.commun4j.services.model.TransferHistoryHoldType
import io.golos.commun4j.services.model.TransferHistoryTransferType
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dialogs.base.BottomSheetDialogFragmentBase
import io.golos.domain.dto.HistoryFilterDomain
import kotlinx.android.synthetic.main.dialog_wallet_history_filter.*

class WalletHistoryFilterDialog private constructor() : BottomSheetDialogFragmentBase<WalletHistoryFilterDialog.Result>() {

    sealed class Result {
        class ApplyFilter(val filter: HistoryFilterDomain) : Result()
        object RestoreToDefaults : Result()
    }

    private var mPreviousFilter: HistoryFilterDomain? = null

    companion object {
        fun show(fragmentManager: FragmentManager, previousFilter:HistoryFilterDomain,callback: (Result?) -> Unit) = WalletHistoryFilterDialog().apply {
            closeActionListener = callback
            mPreviousFilter = previousFilter
        }.show(fragmentManager,"Filter")
    }


    override val closeButton: View?
        get() = btnClose
    override val layout: Int
        get() = R.layout.dialog_wallet_history_filter

    override fun setupView() {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog?.setOnShowListener {
            Handler().post {
                val bottomSheet = (dialog as? BottomSheetDialog)?.findViewById<View>(R.id.design_bottom_sheet) as? FrameLayout
                bottomSheet?.let {
                    BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
        initView()
    }

    private fun initView() {
        mPreviousFilter?.let {
            tbClaim.isChecked = it.claim == TransferHistoryDonation.ALL
            tbDonations.isChecked = it.donation == TransferHistoryDonation.ALL
            tbRewards.isChecked = it.reward == "all"
            when(it.holdType){
                TransferHistoryHoldType.ALL ->{
                    tbLike.isChecked = true
                    tbDislike.isChecked = true
                }
                TransferHistoryHoldType.LIKE ->{
                    tbLike.isChecked = true
                    tbDislike.isChecked = false
                }
                TransferHistoryHoldType.DISLIKE ->{
                    tbLike.isChecked = false
                    tbDislike.isChecked = true
                }
                TransferHistoryHoldType.NONE ->{
                    tbLike.isChecked = false
                    tbDislike.isChecked = false
                }
            }
            when(it.direction){
                TransferHistoryDirection.ALL -> {
                    base_filters_layout.getTabAt(0)?.select()
                }
                TransferHistoryDirection.RECEIVE -> {
                    base_filters_layout.getTabAt(1)?.select()
                }
                else -> {
                    base_filters_layout.getTabAt(2)?.select()
                }
            }

            when(it.transferType){
                TransferHistoryTransferType.ALL -> {
                    tbTransfer.isChecked = true
                    tbConvert.isChecked = true
                }
                TransferHistoryTransferType.TRANSFER -> {
                    tbTransfer.isChecked = true
                    tbConvert.isChecked = false
                }
                TransferHistoryTransferType.CONVERT -> {
                    tbTransfer.isChecked = false
                    tbConvert.isChecked = true
                }
                TransferHistoryTransferType.NONE -> {
                    tbTransfer.isChecked = false
                    tbConvert.isChecked = false
                }
            }
        }
        save_button.setOnClickListener {
            val direction = when (base_filters_layout.selectedTabPosition) {
                0 -> TransferHistoryDirection.ALL
                1 -> TransferHistoryDirection.RECEIVE
                else -> TransferHistoryDirection.SEND
            }
            val transferType = when{
                tbTransfer.isChecked && tbConvert.isChecked ->{
                    TransferHistoryTransferType.ALL
                }
                tbTransfer.isChecked ->{
                    TransferHistoryTransferType.TRANSFER
                }
                tbConvert.isChecked -> {
                    TransferHistoryTransferType.CONVERT
                }
                else ->{
                    TransferHistoryTransferType.NONE
                }
            }
            val holdType = when{
                tbLike.isChecked && tbDislike.isChecked ->{
                    TransferHistoryHoldType.ALL
                }
                tbLike.isChecked ->{
                    TransferHistoryHoldType.LIKE
                }
                tbDislike.isChecked -> {
                    TransferHistoryHoldType.DISLIKE
                }
                else ->{
                    TransferHistoryHoldType.NONE
                }
            }
            val reward = if(tbRewards.isChecked) "all" else "none"
            val claim = if(tbClaim.isChecked) TransferHistoryDonation.ALL else TransferHistoryDonation.NONE
            val donation = if(tbDonations.isChecked) TransferHistoryDonation.ALL else TransferHistoryDonation.NONE
            closeOnItemSelected(
                Result.ApplyFilter(
                    HistoryFilterDomain(
                        direction = direction,
                        transferType = transferType,
                        holdType = holdType,
                        reward = reward,
                        claim = claim,
                        donation = donation
                    )
                )
            )
        }
        clean_all_button.setOnClickListener { closeOnItemSelected(Result.RestoreToDefaults) }
    }

}