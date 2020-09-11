package io.golos.cyber_android.ui.screens.wallet_dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import io.golos.commun4j.services.model.TransferHistoryDirection
import io.golos.commun4j.services.model.TransferHistoryDonation
import io.golos.commun4j.services.model.TransferHistoryHoldType
import io.golos.commun4j.services.model.TransferHistoryTransferType
import io.golos.cyber_android.R
import io.golos.cyber_android.databinding.DialogWalletHistoryFilterBinding
import io.golos.domain.dto.HistoryFilterDomain

class WalletHistoryFilterDialog private constructor() : DialogFragment() {

    sealed class Result {
        class ApplyFilter(val filter: HistoryFilterDomain) : Result()
        object RestoreToDefaults : Result()
        object Cancel : Result()
    }

    private var closeActionListener: ((Result?) -> Unit)? = null
    private lateinit var mBinding: DialogWalletHistoryFilterBinding

    companion object {
        fun show(parent: Fragment, callback: (Result?) -> Unit) = WalletHistoryFilterDialog().apply {
            closeActionListener = callback
        }.show(parent.parentFragmentManager, "Filter")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogFragment_RoundCorners)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DialogWalletHistoryFilterBinding.inflate(inflater, container, false)
        initView()
        return mBinding.root
    }

    private fun initView() {
        mBinding.saveButton.setOnClickListener {
            val direction = when (mBinding.baseFiltersLayout.selectedTabPosition) {
                0 -> TransferHistoryDirection.ALL
                1 -> TransferHistoryDirection.RECEIVE
                else -> TransferHistoryDirection.SEND
            }
            val transferType = when{
                mBinding.tbTransfer.isChecked && mBinding.tbConvert.isChecked ->{
                    TransferHistoryTransferType.ALL
                }
                mBinding.tbTransfer.isChecked ->{
                    TransferHistoryTransferType.TRANSFER
                }
                mBinding.tbConvert.isChecked -> {
                    TransferHistoryTransferType.CONVERT
                }
                else ->{
                    TransferHistoryTransferType.NONE
                }
            }
            val holdType = when{
                mBinding.tbLike.isChecked && mBinding.tbDislike.isChecked ->{
                    TransferHistoryHoldType.ALL
                }
                mBinding.tbLike.isChecked ->{
                    TransferHistoryHoldType.LIKE
                }
                mBinding.tbDislike.isChecked -> {
                    TransferHistoryHoldType.DISLIKE
                }
                else ->{
                    TransferHistoryHoldType.NONE
                }
            }
            val reward = if(mBinding.tbRewards.isChecked) "all" else "none"
            val claim = if(mBinding.tbClaim.isChecked) TransferHistoryDonation.ALL else TransferHistoryDonation.NONE
            val donation = if(mBinding.tbDonations.isChecked) TransferHistoryDonation.ALL else TransferHistoryDonation.NONE

            closeActionListener?.invoke(
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
            dismiss()
        }
        mBinding.cleanAllButton.setOnClickListener { closeActionListener?.invoke(Result.RestoreToDefaults);dismiss() }
        mBinding.btnClose.setOnClickListener { dismiss() }
    }
}