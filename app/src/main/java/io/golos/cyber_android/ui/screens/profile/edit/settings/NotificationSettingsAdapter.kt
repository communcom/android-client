package io.golos.cyber_android.ui.screens.profile.edit.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.item_notification_setting.view.*

class NotificationSettingsAdapter(val listener: Listener) : RecyclerView.Adapter<NotificationSettingsAdapter.ViewHolder>() {

    private var values: List<NotificationSetting> = emptyList()

    fun submit(list: List<NotificationSetting>) {
        val diff = DiffUtil.calculateDiff(NotificationSettingsDiffCallback(values, list))
        values = list
        diff.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_notification_setting, parent, false))

    override fun getItemCount() = values.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int)  = holder.bind(values[position], listener)


    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(item: NotificationSetting, listener: Listener) {
            itemView.icon.setImageResource(item.icon)
            itemView.title.setText(item.title)
            itemView.settingSwitch.isChecked = item.isEnabled

            itemView.settingSwitch.setOnCheckedChangeListener { _, isChecked ->
                listener.onSettingChanged(item, isChecked)
            }
        }
    }

    interface Listener {
        fun onSettingChanged(item: NotificationSetting, isEnabled: Boolean)
    }
}