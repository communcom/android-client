package io.golos.cyber_android.ui.shared.extensions

import android.view.View

/** Set's onClickListener, performing no more than one click per [waitMillis] milliseconds (600 by default), ignoring the rest  */
fun View.setOneClickListener(waitMillis: Long = 600, listener: ((View) -> Unit)?) {
    if (listener == null) {
        setOnClickListener(null)
        return
    }

    var lastClickTime = 0L

    setOnClickListener {
        if (System.currentTimeMillis() > lastClickTime + waitMillis) {
            listener.invoke(it)
            lastClickTime = System.currentTimeMillis()
        }
    }
}