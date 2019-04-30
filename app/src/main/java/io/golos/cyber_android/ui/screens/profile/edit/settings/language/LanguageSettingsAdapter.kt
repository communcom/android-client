package io.golos.cyber_android.ui.screens.profile.edit.settings.language

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.item_settings_option.view.*

/**
 * [RecyclerView.Adapter] for [LanguageOption]
 */
class LanguageSettingsAdapter(private val listener: Listener) :
    RecyclerView.Adapter<LanguageSettingsAdapter.ViewHolder>() {

    private var values = supportedLanguages

    var selectedCode: String? = null
        set(value) {
            val prevValue = field
            field = value
            with(values.indexOfFirst { it.code.compareTo(value ?: "") == 0 }) {
                if (this >= 0)
                    notifyItemChanged(this)
            }

            with(values.indexOfFirst { it.code.compareTo(prevValue ?: "") == 0 }) {
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
        fun bind(item: LanguageOption) {
            with(itemView) {
                root.setOnClickListener { listener.onOptionClick(item) }
                option.setText(item.displayedName)
                check.visibility = if (selectedCode?.compareTo(item.code) == 0) View.VISIBLE else View.GONE
            }
        }
    }

    interface Listener {
        fun onOptionClick(item: LanguageOption)
    }
}