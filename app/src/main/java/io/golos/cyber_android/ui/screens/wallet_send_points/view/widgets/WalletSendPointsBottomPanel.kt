package io.golos.cyber_android.ui.screens.wallet_send_points.view.widgets

import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import android.view.View
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet_send_points.dto.AmountFieldInfo
import io.golos.cyber_android.ui.screens.wallet_send_points.dto.UserInfo
import io.golos.cyber_android.ui.shared.glide.loadAvatar
import io.golos.cyber_android.ui.shared.keyboard.KeyboardUtils
import io.golos.cyber_android.ui.shared.text.CurrencyInputFilter
import kotlinx.android.synthetic.main.view_wallet_send_points_bottom_panel.view.*

class WalletSendPointsBottomPanel
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var onSelectUserClickListener: (() -> Unit)? = null

    init {
        inflate(context, R.layout.view_wallet_send_points_bottom_panel, this)

        selectedUserPanel.setOnClickListener { onSelectUserClickListener?.invoke() }

        amountPanel.setOnClickListener {
            if(!amountText.isFocused) {
                amountText.requestFocus()
            }

            if(!KeyboardUtils.isKeyboardVisible(amountText)) {
                KeyboardUtils.showKeyboard(amountText)
            }
        }

        amountText.filters = arrayOf(
            CurrencyInputFilter(22, 4)
        )
    }

    fun setUserInfo(userInfo: UserInfo) {
        userName.text = userInfo.name

        userLogo.loadAvatar(userInfo.avatar, R.drawable.ic_commun)

        findFriendButton.visibility = if(userInfo.isFound) View.INVISIBLE else View.VISIBLE
        friendFoundButton.visibility = if(userInfo.isFound) View.VISIBLE else View.INVISIBLE
    }

    fun setOnSelectUserClickListener(listener: (() -> Unit)?) {
        onSelectUserClickListener = listener
    }

    fun clearFocusOnAmountField() = amountText.clearFocus()

    fun hideKeyboard(): Boolean =
        if(KeyboardUtils.isKeyboardVisible(amountText)) {
            KeyboardUtils.hideKeyboard(amountText)
            true
        } else {
            false
        }

    fun setAmountFieldInfo(amountFieldInfo: AmountFieldInfo) {
        amountText.setHint(amountFieldInfo.hintResId)

        amountText.filters = arrayOf(
            CurrencyInputFilter(22, amountFieldInfo.decimalPointsQuantity)
        )
    }
}
