package io.golos.cyber_android.ui.shared.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.glide.loadAvatar
import io.golos.cyber_android.ui.shared.glide.release
import io.golos.cyber_android.ui.shared.widgets.EditorWidget.Listener
import kotlinx.android.synthetic.main.view_editor_widget.view.*

/**
 * Custom view which represents Editor Widget. It may produce two types of events -
 * gallery button click and widget click itself (handled via [Listener]).
 * Also widget can display user avatar (via [loadUserAvatar] method).
 * This view doesn't support any custom xml attributes.
 */
class EditorWidget : LinearLayout {

    var listener: Listener? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        inflate(context, R.layout.view_editor_widget, this)

        galleryButton.setOnClickListener { listener?.onGalleryClick() }
        rootView.setOnClickListener { listener?.onWidgetClick() }
    }

    /**
     * Loads user avatar into avatar ImageView
     */
    fun loadUserAvatar(url: String?, username: String) {
        if (url?.isNotBlank() == true) {
            name.text = ""
        } else {
            name.text = username
        }
        avatar.loadAvatar(url)
    }

    /**
     * Clear user avatar
     */
    fun clearUserAvatar(){
        avatar.release()
    }

    interface Listener {

        /**
         * Click listener for gallery button
         */
        fun onGalleryClick()

        /**
         * Click listener for all of the view except gallery button
         */
        fun onWidgetClick()
    }

    /**
     * State of the editor widget
     */
    class EditorWidgetState(var avatarUrl: String?, var username: String)
}