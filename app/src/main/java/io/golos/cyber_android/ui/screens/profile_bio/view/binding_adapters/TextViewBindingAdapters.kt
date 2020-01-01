package io.golos.cyber_android.ui.screens.profile_bio.view.binding_adapters

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.ui.shared.extensions.parentActivity
import io.golos.cyber_android.ui.screens.profile_bio.dto.TextLenInfo
import io.golos.cyber_android.ui.screens.profile_bio.view.widgets.TextLenView

@BindingAdapter("textLen")
fun setTextLenBinding(view: TextLenView, valueToBind: LiveData<TextLenInfo>?) {
    valueToBind?.let { value ->
        view.parentActivity?.let { activity ->
            value.observe(activity, Observer {
                view.setTextLen(it.current, it.total)
            })
        }
    }
}