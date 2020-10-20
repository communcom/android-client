package io.golos.cyber_android.ui.screens.wallet_dialogs.transfer_completed

import android.view.View
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dialogs.base.DialogFragmentBase
import io.golos.cyber_android.ui.shared.glide.load
import io.golos.cyber_android.ui.shared.utils.toDD_MMMM_YYYY_Format
import io.golos.utils.format.CurrencyFormatter
import kotlinx.android.synthetic.main.dialog_wallet_transfer_completed.*

class WalletTransferCompletedDialog : DialogFragmentBase() {
    enum class Action {
        BACK_TO_HOME,
        BACK_TO_WALLET
    }

    companion object {
        fun show(parent: Fragment, sourceData: TransferCompletedInfo, closeAction: (Action?) -> Unit) {
            WalletTransferCompletedDialog().apply {
                this.closeActionListener = closeAction
                this.sourceData = sourceData
            }.show(parent.parentFragmentManager, "WALLET_TRANSFER_COMPLETED_DIALOG")
        }
    }

    private lateinit var closeActionListener: (Action?) -> Unit

    private lateinit var sourceData: TransferCompletedInfo

    private var isClosePositive = false

    override fun provideLayout(): Int = R.layout.dialog_wallet_transfer_completed

    override fun setupView() {
        transactionDayLabel.text = sourceData.date.toDD_MMMM_YYYY_Format()

        pointsLabel.text = CurrencyFormatter.format(sourceData.amountTransfered)
        remainLabel.text = CurrencyFormatter.format(sourceData.amountRemain)

        pointsNameLabel.text = sourceData.pointsName
        pointsNameBottomLabel.text = sourceData.pointsName

        pointsFeeLabel.visibility = if(sourceData.showFee) View.VISIBLE else View.GONE

        userName.text = sourceData.userName

        userLogo.load(sourceData.userLogoUrl, R.drawable.ic_commun)
        remainLogo.load(sourceData.pointsLogoUrl, R.drawable.ic_commun)

        homeButton.setOnClickListener { closePositive(Action.BACK_TO_HOME) }

        backToWalletButton.setOnClickListener { closePositive(Action.BACK_TO_WALLET) }
    }

    override fun onDestroy() {
        super.onDestroy()

        if(!isClosePositive) {
            closeActionListener(null)
        }
    }

    private fun closePositive(action: Action) {
        isClosePositive = true
        dismiss()
        closeActionListener(action)
    }
}