package io.golos.cyber_android.ui.screens.profile.edit.settings.nsfw

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.domain.entities.NSFWSettingsEntity
import kotlinx.android.synthetic.main.item_settings_option.view.*

/**
 * [RecyclerView.Adapter] for [NSFWSettingsEntity]
 */
class NSFWSettingsAdapter(private val listener: Listener) :
    RecyclerView.Adapter<NSFWSettingsAdapter.ViewHolder>() {

    private var values: List<NSFWSettingsEntity> =
        listOf(NSFWSettingsEntity.ALWAYS_HIDE, NSFWSettingsEntity.ALERT_WARN, NSFWSettingsEntity.ALWAYS_SHOW)

    var selectedItem: NSFWSettingsEntity? = null
        set(value) {
            val prevValue = field
            field = value
            with(values.indexOf(value)) {
                if (this >= 0)
                    notifyItemChanged(this)
            }

            with(values.indexOf(prevValue)) {
                if (this >= 0)
                    notifyItemChanged(this)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_settings_option, parent, false))
    }

    override fun getItemCount() = values.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(values[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: NSFWSettingsEntity) {
            with(itemView) {
                root.setOnClickListener { listener.onOptionClick(item) }
                option.text = when (item) {
                    NSFWSettingsEntity.ALERT_WARN -> itemView.context.getString(R.string.always_alert)
                    NSFWSettingsEntity.ALWAYS_HIDE -> itemView.context.getString(R.string.always_hide)
                    NSFWSettingsEntity.ALWAYS_SHOW -> itemView.context.getString(R.string.always_show)
                    else -> itemView.context.getString(R.string.missing_setting_descr)
                }
                check.visibility = if (selectedItem == item) View.VISIBLE else View.GONE
            }
        }
    }

    interface Listener {
        fun onOptionClick(item: NSFWSettingsEntity)
    }
}