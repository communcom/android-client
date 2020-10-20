package io.golos.cyber_android.ui.screens.app_start.shared

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.view_login_header.view.*

class HeaderWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var onBackButtonClickListener: (() -> Unit)? = null

    init {
        inflate(context, R.layout.view_login_header, this)
        attrs?.let { retrieveAttributes(it) }
        backButton.setOnClickListener { onBackButtonClickListener?.invoke() }
    }

    fun setOnBackButtonClickListener(listener: (() -> Unit)?) {
        onBackButtonClickListener = listener
    }

    fun setTitle(titleText: String) {
        title.text = titleText
    }

    fun setBackButtonVisibility(isVisible: Boolean) {
        backButton.visibility = if(isVisible) View.VISIBLE else View.INVISIBLE
    }

    private fun retrieveAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.HeaderWidget)

        title.text = typedArray.getString(R.styleable.HeaderWidget_title)

        typedArray.recycle()
    }
}