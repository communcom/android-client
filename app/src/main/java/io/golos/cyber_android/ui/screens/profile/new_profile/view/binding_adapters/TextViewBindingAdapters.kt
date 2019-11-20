package io.golos.cyber_android.ui.screens.profile.new_profile.view.binding_adapters

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.extensions.parentActivity
import io.golos.cyber_android.ui.common.formatters.counts.KiloCounterFormatter
import io.golos.cyber_android.utils.toMMMM_DD_YYYY_Format
import java.util.*

@SuppressLint("SetTextI18n")
@BindingAdapter("joined")
fun setJoinedDateBinding(view: TextView, valueToBind: LiveData<Date>?) {
    valueToBind?.let { value ->
        view.parentActivity?.let { activity ->
            value.observe(activity, Observer { date ->
                view.text = "${view.context.resources.getString(R.string.joined)} ${date.toMMMM_DD_YYYY_Format()}"
            })
        }
    }
}

@BindingAdapter("kilo")
fun setKiloBinding(view: TextView, valueToBind: LiveData<Long>?) {
    valueToBind?.let { value ->
        view.parentActivity?.let { activity ->
            value.observe(activity, Observer {
                view.text = KiloCounterFormatter.format(it)
            })
        }
    }
}