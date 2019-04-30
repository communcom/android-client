package io.golos.cyber_android.ui.screens.login.signup.country

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.golos.cyber_android.R
import io.golos.domain.interactors.model.CountryModel
import kotlinx.android.synthetic.main.item_country.view.*

/**
 * [RecyclerView.Adapter] for [CountryModel]
 */
class CountriesAdapter(private val listener: Listener) :
    RecyclerView.Adapter<CountriesAdapter.ViewHolder>() {

    private var values: List<CountryModel> = emptyList()

    var selectedCountry: CountryModel? = null
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

    fun submit(list: List<CountryModel>) {
        val diff = DiffUtil.calculateDiff(CountryDiffCallback(values, list))
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
        fun bind(country: CountryModel) {
            with(itemView) {
                root.setOnClickListener { listener.onCountryClick(country) }

                Glide.with(context)
                    .load(country.thumbNailUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(flag)

                countryName.text = String.format(
                    context.getString(R.string.country_with_phone_code_format),
                    country.countryName,
                    country.countryPhoneCode
                )
                check.visibility = if (selectedCountry == country) View.VISIBLE else View.GONE
            }
        }
    }

    interface Listener {
        fun onCountryClick(country: CountryModel)
    }
}