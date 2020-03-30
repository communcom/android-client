package io.golos.cyber_android.ui.screens.app_start.sign_up.create_password.view.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_UP
import android.widget.Button

class ClickableDisabledButton
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : Button(context, attrs, defStyle) {

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(!isEnabled && event?.action == ACTION_UP )  {
            performClick()
        }
        return super.onTouchEvent(event)
    }
}