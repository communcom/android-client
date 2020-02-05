package io.golos.cyber_android.ui.screens.profile.view.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.formatters.currency.CurrencyFormatter
import kotlinx.android.synthetic.main.view_profile_wallet.view.*


class ProfileWalletView
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var onWalletClickListener: (() -> Unit)? = null

    init {
        inflate(context, R.layout.view_profile_wallet, this)

        walletButton.setOnClickListener { onWalletClickListener?.invoke() }
    }

    fun setValue(value: Double) {
        textValue.text = CurrencyFormatter.format(value)
    }

    fun setOnWalletClickListener(listener: (() -> Unit)?) {
        onWalletClickListener = listener
    }
}