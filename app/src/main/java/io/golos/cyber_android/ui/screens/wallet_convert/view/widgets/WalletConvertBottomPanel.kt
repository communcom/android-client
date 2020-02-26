package io.golos.cyber_android.ui.screens.wallet_convert.view.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet_convert.dto.ConvertButtonInfo
import io.golos.cyber_android.ui.screens.wallet_convert.dto.InputFieldsInfo
import io.golos.cyber_android.ui.screens.wallet_convert.dto.PointInfo
import io.golos.cyber_android.ui.shared.extensions.getFormattedString
import io.golos.cyber_android.ui.shared.extensions.setTextChangeListener
import io.golos.cyber_android.ui.shared.formatters.currency.CurrencyFormatter
import io.golos.cyber_android.ui.shared.glide.loadAvatar
import io.golos.cyber_android.ui.shared.keyboard.KeyboardUtils
import io.golos.cyber_android.ui.shared.text.CurrencyInputFilter
import kotlinx.android.synthetic.main.view_wallet_convert_bottom_panel.view.*

class WalletConvertBottomPanel
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var onSelectPointClickListener: (() -> Unit)? = null
    private var onSendButtonClickListener: (() -> Unit)? = null

    private var onSellChangeListener: ((String) -> Unit)? = null
    private var onSellClearListener: (() -> Unit)? = null

    private var onBuyChangeListener: ((String) -> Unit)? = null
    private var onBuyClearListener: (() -> Unit)? = null

    private var isSellFieldUpdating = false
    private var isBuyFieldUpdating = false

    var sellInputText: String
        get() = sellInputField.text.toString()
        set(value) {
            isSellFieldUpdating = true
            setAmount(value, sellInputField)
            isSellFieldUpdating = false
        }

    var buyInputText: String
        get() = buyInputField.text.toString()
        set(value) {
            isBuyFieldUpdating = true
            setAmount(value, buyInputField)
            isBuyFieldUpdating = false
        }

    init {
        inflate(context, R.layout.view_wallet_convert_bottom_panel, this)

        selectPointButton.setOnClickListener { onSelectPointClickListener?.invoke() }

        sellPanel.setOnClickListener {
            if(!sellInputField.isFocused) {
                sellInputField.requestFocus()
            }

            if(!KeyboardUtils.isKeyboardVisible(sellInputField)) {
                KeyboardUtils.showKeyboard(sellInputField)
            }
        }

        buyPanel.setOnClickListener {
            if(!buyInputField.isFocused) {
                buyInputField.requestFocus()
            }

            if(!KeyboardUtils.isKeyboardVisible(buyInputField)) {
                KeyboardUtils.showKeyboard(buyInputField)
            }
        }

        sellInputField.setTextChangeListener {
            if(!isSellFieldUpdating) {
                onSellChangeListener?.invoke(it)
            }
        }
        buyInputField.setTextChangeListener {
            if(!isBuyFieldUpdating) {
                onBuyChangeListener?.invoke(it)
            }
        }

        clearSellButton.setOnClickListener { onSellClearListener?.invoke() }
        clearBuyButton.setOnClickListener { onBuyClearListener?.invoke() }

        sendButton.setOnClickListener { onSendButtonClickListener?.invoke() }
    }

    fun setPointInfo(pointInfo: PointInfo) {
        sellLabel.text = context.resources.getFormattedString(R.string.sell_format, pointInfo.sellerName)
        buyLabel.text = context.resources.getFormattedString(R.string.buy_format, pointInfo.buyerName)

        pointName.text = pointInfo.buyerName
        pointLogo.loadAvatar(pointInfo.buyerLogoUrl, R.drawable.ic_commun)
        balanceValue.text = CurrencyFormatter.format(pointInfo.buyerBalance)

        selectPointButton.visibility = if(pointInfo.canSelectPoint) View.VISIBLE else View.GONE

        rateLabel.text = pointInfo.rateInfo
    }

    fun setInputFieldsInfo(fieldInfo: InputFieldsInfo) {
        sellInputField.setHint(fieldInfo.sellerHint)
        buyInputField.setHint(fieldInfo.buyerHint)

        sellInputField.filters = arrayOf(
            CurrencyInputFilter(7+fieldInfo.sellerDecimalPoints, fieldInfo.sellerDecimalPoints)
        )

        buyInputField.filters = arrayOf(
            CurrencyInputFilter(7+fieldInfo.buyerDecimalPoints, fieldInfo.buyerDecimalPoints)
        )
    }

    fun setConvertButtonInfo(convertButtonInfo: ConvertButtonInfo) {
        sendButton.text = convertButtonInfo.buttonText
        sendButton.isEnabled = convertButtonInfo.isButtonEnabled
    }

    fun setOnSelectPointClickListener(listener: (() -> Unit)?) {
        onSelectPointClickListener = listener
    }

    fun setOnSellChangeListener(listener: ((String) -> Unit)?) {
        onSellChangeListener = listener
    }

    fun setOnSellClearListener(listener: (() -> Unit)?) {
        onSellClearListener = listener
    }

    fun setOnBuyChangeListener(listener: ((String) -> Unit)?) {
        onBuyChangeListener = listener
    }

    fun setOnBuyClearListener(listener: (() -> Unit)?) {
        onBuyClearListener = listener
    }

    fun setOnSendButtonClickListener(listener: (() -> Unit)?) {
        onSendButtonClickListener = listener
    }

    fun clearFocusOnSellField() = sellInputField.clearFocus()
    fun clearFocusOnBuyField() = buyInputField.clearFocus()

    fun hideKeyboard(): Boolean =
        if(KeyboardUtils.isKeyboardVisible(sellInputField)) {
            KeyboardUtils.hideKeyboard(sellInputField)
            true
        } else {
            if(KeyboardUtils.isKeyboardVisible(buyInputField)) {
                KeyboardUtils.hideKeyboard(buyInputField)
                true
            } else {
                false
            }
        }

    private fun setAmount(value: String, field: EditText) {
        field.setText((field.filters[0] as CurrencyInputFilter).format(value))
    }
}
