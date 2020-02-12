package io.golos.cyber_android.ui.screens.wallet.view.panels_layout

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.formatters.currency.CurrencyFormatter
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

    fun setValue(value: Double) {
        textValue.text = CurrencyFormatter.format(value)
    }

    fun setOnBackClickListener(listener: (() -> Unit)?) {
        onBackClickListener = listener
    }
}