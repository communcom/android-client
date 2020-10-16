package io.golos.cyber_android.ui.screens.profile.view.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet.data.enums.Currencies
import io.golos.cyber_android.ui.screens.wallet.model.CurrencyBalance
import io.golos.utils.format.CurrencyFormatter
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

        root.setOnClickListener { walletButton.performClick() }
        walletButton.setOnClickListener { onWalletClickListener?.invoke() }
    }

    fun setValue(currencyBalance: CurrencyBalance) {
        when(currencyBalance.currency) {
            Currencies.USD -> {
                title.text = String.format(resources.getString(R.string.profile_wallet_title_var), resources.getString(R.string.usd_currency))
                textValue.text = StringBuilder("$").append(CurrencyFormatter.format(currencyBalance.balance))
            }
            Currencies.COMMUN -> {
                title.text = String.format(resources.getString(R.string.profile_wallet_title_var), resources.getString(R.string.commun_currency))
                textValue.text = CurrencyFormatter.format(currencyBalance.balance)
            }
        }
    }

    fun setOnWalletClickListener(listener: (() -> Unit)?) {
        onWalletClickListener = listener
    }
}