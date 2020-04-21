package io.golos.cyber_android.ui.dialogs.notifications_settings

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.view_dialog_profile_settings_notifications_item.view.*

class NotificationSettingsItemWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var isChecked
        get() = notificationSwitch.isChecked
        set(value) { notificationSwitch.isChecked = value }

    init {
        inflate(context, R.layout.view_dialog_profile_settings_notifications_item, this)
        attrs?.let { retrieveAttributes(it) }
    }

    private fun retrieveAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.NotificationSettingsItemWidget)

        notificationName.text = typedArray.getString(R.styleable.NotificationSettingsItemWidget_notification_title)

        val icon = typedArray.getDrawable(R.styleable.NotificationSettingsItemWidget_notification_icon)!!
        notificationName.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)

        typedArray.recycle()
    }
}