package io.golos.cyber_android.ui.shared.utils

import android.widget.TextView
import io.golos.cyber_android.ui.shared.spans.ClickableMovementMethod

/**
 * Move click event on top level views
 */
fun TextView.adjustSpannableClicks(){
    movementMethod = ClickableMovementMethod
    isClickable = false
    isLongClickable = false
}