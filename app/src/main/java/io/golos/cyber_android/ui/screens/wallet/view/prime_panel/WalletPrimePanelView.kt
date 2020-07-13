package io.golos.cyber_android.ui.screens.wallet.view.prime_panel

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet.data.enums.Currencies
import io.golos.cyber_android.ui.screens.wallet.model.CurrencyBalance
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
    private var onUsdButtonClickListener: (() -> Unit)? = null
    private var onCommunButtonClickListener: (() -> Unit)? = null
    private var onMenuButtonClickListener: (() -> Unit)? = null

    init {
        inflate(context, R.layout.view_wallet_prime_panel, this)
        backButton.setOnClickListener { onBackButtonClickListener?.invoke() }
        usdIcon.setOnClickListener { onUsdButtonClickListener?.invoke() }
        communIcon.setOnClickListener { onCommunButtonClickListener?.invoke() }
        menuButton.setOnClickListener { onMenuButtonClickListener?.invoke() }
    }

    fun setValue(currencyBalance: CurrencyBalance) {
        when(currencyBalance.currency) {
            Currencies.USD -> {
                title.text = String.format(resources.getString(R.string.profile_wallet_title_var), resources.getString(R.string.usd_currency))
                textValue.text = StringBuilder("$").append(CurrencyFormatter.format(currencyBalance.balance))
                usdIcon.background = context.getDrawable(R.drawable.bcg_circle_white)
                communIcon.background = context.getDrawable(R.drawable.bcg_wallet_profile_icon)
                usdIcon.drawable.setColorFilter(Color.parseColor("#8899F7"), PorterDuff.Mode.MULTIPLY)
                communIcon.drawable.setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.MULTIPLY)
            }
            Currencies.COMMUN -> {
                title.text = String.format(resources.getString(R.string.profile_wallet_title_var), resources.getString(R.string.commun_currency))
                textValue.text = CurrencyFormatter.format(currencyBalance.balance)
                usdIcon.background = context.getDrawable(R.drawable.bcg_wallet_profile_icon)
                communIcon.background = context.getDrawable(R.drawable.bcg_circle_white)
                usdIcon.drawable.setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.MULTIPLY)
                communIcon.drawable.setColorFilter(Color.parseColor("#8899F7"), PorterDuff.Mode.MULTIPLY)
            }
        }
    }

    fun setOnBackButtonClickListener(listener: (() -> Unit)?) {
        onBackButtonClickListener = listener
    }

    fun setOnUsdButtonClickListener(listener: (() -> Unit)?) {
        onUsdButtonClickListener = listener
    }

    fun setOnCommunButtonClickListener(listener: (() -> Unit)?) {
        onCommunButtonClickListener = listener
    }

    fun setOnMenuButtonClickListener(listener: (() -> Unit)?) {
        onMenuButtonClickListener = listener
    }

    fun setOnSendClickListener(listener: (() -> Unit)?) = bottomArea.setOnSendClickListener(listener)

    fun setOnBuyClickListener(listener: (() -> Unit)?) = bottomArea.setOnBuyClickListener(listener)

    fun setOnConvertClickListener(listener: (() -> Unit)?) = bottomArea.setOnConvertClickListener(listener)

}