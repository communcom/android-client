package io.golos.cyber_android.ui.screens.wallet_send_points.view.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import io.golos.cyber_android.R

class WalletSendPointsExpandedTopPanel
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.view_wallet_send_points_expanded_top_panel, this)
    }
}