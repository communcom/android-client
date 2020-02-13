package io.golos.cyber_android.ui.screens.wallet_shared

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.view_wallet_prime_panel_bottom_area.view.*

class WalletPrimePanelBottomArea
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var onSendClickListener: (() -> Unit)? = null
    private var onBuyClickListener: (() -> Unit)? = null
    private var onConvertClickListener: (() -> Unit)? = null

    init {
        inflate(context, R.layout.view_wallet_prime_panel_bottom_area, this)

        sendButton.setOnClickListener { onSendClickListener?.invoke() }
        buyButton.setOnClickListener { onBuyClickListener?.invoke() }
        convertButton.setOnClickListener { onConvertClickListener?.invoke() }
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