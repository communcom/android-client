package io.golos.cyber_android.ui.screens.app_start.sign_up.countries.view.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.utils.getStyledAttribute
import io.golos.domain.dto.CountryDomain
import kotlinx.android.synthetic.main.item_country.view.*

class CountriesAdapter(private val onCountrySelectedListener: (CountryDomain) -> Unit) :
    RecyclerView.Adapter<CountriesAdapter.ViewHolder>() {

    private var values: List<CountryDomain> = emptyList()

    var selectedCountry: CountryDomain? = null
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

    fun submit(list: List<CountryDomain>) {
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
        fun bind(country: CountryDomain) {
            with(itemView) {
                root.setOnClickListener { onCountrySelectedListener(country) }

                countryFlag.text = country.emoji
                countryName.text = "${country.name} (+${country.code})"
                check.visibility = if (selectedCountry == country) View.VISIBLE else View.GONE

                val textColor = if(country.available) getStyledAttribute(R.attr.black, context) else getStyledAttribute(R.attr.grey, context)
                countryName.setTextColor(textColor)
            }
        }
    }
}