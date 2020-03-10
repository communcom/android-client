package io.golos.cyber_android.ui.shared.popups.app_update

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.view_app_update.view.*

/**
 * Custom view for sms code input
 */
class AppUpdateWidget
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var onUpdateClickListener: (() -> Unit)? = null

    private val view: View = inflate(getContext(), R.layout.view_app_update, this)

    init {
        updateButton.setOnClickListener {
            onUpdateClickListener?.invoke()
        }
    }

    fun setOnUpdateClickListener(listener: (() -> Unit)?) {
        onUpdateClickListener = listener
    }
}