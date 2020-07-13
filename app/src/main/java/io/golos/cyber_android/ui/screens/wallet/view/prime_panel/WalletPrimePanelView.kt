package io.golos.cyber_android.ui.screens.wallet.view.prime_panel

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import io.golos.cyber_android.R
import io.golos.utils.format.CurrencyFormatter
import kotlinx.android.synthetic.main.view_wallet_prime_panel.view.*

class WalletPrimePanelView
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var onBackButtonClickListener: (() -> Unit)? = null
    private var onMenuButtonClickListener: (() -> Unit)? = null

    init {
        inflate(context, R.layout.view_wallet_prime_panel, this)
        backButton.setOnClickListener { onBackButtonClickListener?.invoke() }
        menuButton.setOnClickListener { onMenuButtonClickListener?.invoke() }
    }

    fun setValue(value: Double) {
        textValue.text = CurrencyFormatter.format(value)
    }

    fun setOnBackButtonClickListener(listener: (() -> Unit)?) {
        onBackButtonClickListener = listener
    }

    fun setOnMenuButtonClickListener(listener: (() -> Unit)?) {
        onMenuButtonClickListener = listener
    }

    fun setOnSendClickListener(listener: (() -> Unit)?) = bottomArea.setOnSendClickListener(listener)

    fun setOnBuyClickListener(listener: (() -> Unit)?) = bottomArea.setOnBuyClickListener(listener)

    fun setOnConvertClickListener(listener: (() -> Unit)?) = bottomArea.setOnConvertClickListener(listener)
}