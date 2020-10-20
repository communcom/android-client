package io.golos.posts_editor.components

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import io.golos.posts_editor.R

/**
 * Embedded image
 */
class EmbedWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(
    context,
    attrs,
    defStyleAttr) {

    init {
        inflate(getContext(), R.layout.widget_image_view, this)
    }
}