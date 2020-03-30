package io.golos.cyber_android.ui.shared.popups.no_connection

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
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

    private val view: View = inflate(getContext(), R.layout.view_no_connection, this)

    init {
        reconnectButton.setOnClickListener {
            onReconnectClickListener?.invoke()
        }
    }

    fun setOnReconnectClickListener(listener: (() -> Unit)?) {
        onReconnectClickListener = listener
    }

}