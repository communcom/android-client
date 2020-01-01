package io.golos.cyber_android.ui.shared.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.view_no_connection.view.*

/**
 * Custom view for sms code input
 */
class NoConnectionWidget
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var onReconnectClickListener: (() -> Unit)? = null

    init {
        inflate(getContext(), R.layout.view_no_connection, this)

        reconnectButton.setOnClickListener {
            onReconnectClickListener?.invoke()
        }
    }

    fun setOnReconnectClickListener(listener: (() -> Unit)?) {
        onReconnectClickListener = listener
    }

    fun setReconnectingState(isReconnecting: Boolean) {
        reconnectButton.isEnabled = !isReconnecting
        reconnectingProgress.visibility = if(isReconnecting) View.VISIBLE else View.INVISIBLE
    }
}