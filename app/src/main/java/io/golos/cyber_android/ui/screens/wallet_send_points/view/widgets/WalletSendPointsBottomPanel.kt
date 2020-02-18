package io.golos.cyber_android.ui.screens.wallet_send_points.view.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet_send_points.dto.UserInfo
import io.golos.cyber_android.ui.shared.glide.loadAvatar
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
}
