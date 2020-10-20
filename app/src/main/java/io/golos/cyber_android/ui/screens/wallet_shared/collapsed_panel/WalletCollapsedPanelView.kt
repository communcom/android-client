package io.golos.cyber_android.ui.screens.wallet_shared.collapsed_panel

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet.data.enums.Currencies
import io.golos.cyber_android.ui.screens.wallet.model.CurrencyBalance
import io.golos.utils.format.CurrencyFormatter
import kotlinx.android.synthetic.main.view_wallet_collapsed_panel.view.*

class WalletCollapsedPanelView
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var onBackClickListener: (() -> Unit)? = null

    init {
        inflate(context, R.layout.view_wallet_collapsed_panel, this)

        backButton.setOnClickListener { onBackClickListener?.invoke() }
    }

    fun setTitle(value: String) {
        title.text = value
    }

    fun setValue(currencyBalance: CurrencyBalance) {
        textValue.text = when (currencyBalance.currency) {
            Currencies.USD -> {
                "$ " + CurrencyFormatter.format(currencyBalance.balance)
            }
            Currencies.COMMUN -> {
                CurrencyFormatter.format(currencyBalance.balance)
            }
        }
    }

    fun setOnBackButtonClickListener(listener: (() -> Unit)?) {
        onBackClickListener = listener
    }
}