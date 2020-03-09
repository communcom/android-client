package io.golos.cyber_android.ui.screens.wallet_dialogs.convert_completed

import android.annotation.SuppressLint
import android.view.View
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dialogs.base.DialogFragmentBase
import io.golos.utils.getColorRes
import io.golos.utils.getFormattedString
import io.golos.utils.format.CurrencyFormatter
import io.golos.cyber_android.ui.shared.glide.load
import io.golos.cyber_android.ui.shared.utils.toDD_MMMM_YYYY_Format
import kotlinx.android.synthetic.main.dialog_wallet_transfer_completed.*

class WalletConversionCompletedDialog : DialogFragmentBase() {
    enum class Action {
        BACK_TO_HOME,
        BACK_TO_WALLET
    }

    companion object {
        fun show(parent: Fragment, sourceData: ConversionCompletedInfo, closeAction: (Action?) -> Unit) {
            WalletConversionCompletedDialog().apply {
                this.closeActionListener = closeAction
                this.sourceData = sourceData
            }.show(parent.parentFragmentManager, "WALLET_CONVERT_COMPLETED_DIALOG")
        }
    }

    private lateinit var closeActionListener: (Action?) -> Unit

    private lateinit var sourceData: ConversionCompletedInfo

    private var isClosePositive = false

    override fun provideLayout(): Int = R.layout.dialog_wallet_transfer_completed

    @SuppressLint("SetTextI18n")
    override fun setupView() {
        transactionCompletedLabel.text = context!!.getString(R.string.conversion_completed)

        transactionDayLabel.text = sourceData.date.toDD_MMMM_YYYY_Format()

        pointsLabel.text = "+${CurrencyFormatter.format(sourceData.coins)}"
        pointsNameLabel.text = sourceData.buyerName
        pointsLabel.setTextColor(context!!.resources.getColorRes(R.color.green_bright))
        pointsNameLabel.setTextColor(context!!.resources.getColorRes(R.color.green_bright))

        pointsFeeLabel.visibility = View.GONE

        userLogo.load(sourceData.buyerAvatarUrl, R.drawable.ic_commun)
        userName.text = sourceData.buyerName
        userStatus.text = context!!.resources.getFormattedString(
            R.string.points_format2,
            CurrencyFormatter.format(sourceData.buyerPointsTotal))

        remainLogo.load(sourceData.sellerAvatarUrl, R.drawable.ic_commun)
        pointsNameBottomLabel.text = sourceData.sellerName
        remainLabel.text = CurrencyFormatter.format(sourceData.sellerPointsTotal)

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