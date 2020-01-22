package io.golos.cyber_android.ui.screens.login_sign_up_countries.view.list

import androidx.recyclerview.widget.DiffUtil
import io.golos.domain.dto.CountryDomain

/**
 * [DiffUtil.Callback] impl for [CountryModel] lists
 */
class CountryDiffCallback(private val oldList: List<CountryDomain>, private val newList: List<CountryDomain>) :
    DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].countryCode.compareTo(newList[newItemPosition].countryCode) == 0
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
