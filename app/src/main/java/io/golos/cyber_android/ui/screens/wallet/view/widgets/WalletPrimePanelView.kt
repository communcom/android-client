package io.golos.cyber_android.ui.screens.wallet.view.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.formatters.currency.CurrencyFormatter
import kotlinx.android.synthetic.main.view_wallet_prime_panel.view.*

class WalletPrimePanelView
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var onSendClickListener: (() -> Unit)? = null
    private var onBuyClickListener: (() -> Unit)? = null
    private var onConvertClickListener: (() -> Unit)? = null

    init {
        inflate(context, R.layout.view_wallet_prime_panel, this)
    }

    fun setValue(value: Double) {
        textValue.text = CurrencyFormatter.format(value)
    }

    fun setOnSendClickListener(listener: (() -> Unit)?) {
        onSendClickListener = listener
    }

    fun setOnBuyClickListener(listener: (() -> Unit)?) {
        onBuyClickListener = listener
    }

    fun setOnConvertClickListener(listener: (() -> Unit)?) {
        onConvertClickListener = listener
    }
}