package io.golos.cyber_android.ui.screens.login_sign_up.fragments.country

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.extensions.getColorRes
import io.golos.domain.dto.CountryEntity
import kotlinx.android.synthetic.main.item_country.view.*

/**
 * [RecyclerView.Adapter] for [CountryModel]
 */
class CountriesAdapter(private val listener: Listener) :
    RecyclerView.Adapter<CountriesAdapter.ViewHolder>() {

    private var values: List<CountryEntity> = emptyList()

    var selectedCountry: CountryEntity? = null
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

    fun submit(list: List<CountryEntity>) {
        val diff = DiffUtil.calculateDiff(
            CountryDiffCallback(
                values,
                list
            )
        )
        values = list
        diff.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_country, parent, false))
    }

    override fun getItemCount() = values.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(values[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        @SuppressLint("SetTextI18n")
        fun bind(country: CountryEntity) {
            with(itemView) {
                root.setOnClickListener { listener.onCountryClick(country) }

                countryName.text = "${country.emoji} ${country.name} (+${country.code})"
                check.visibility = if (selectedCountry == country) View.VISIBLE else View.GONE

                val textColor = context.resources.getColorRes(if(country.available) R.color.black else R.color.grey)
                countryName.setTextColor(textColor)
            }
        }
    }

    interface Listener {
        fun onCountryClick(country: CountryEntity)
    }
}