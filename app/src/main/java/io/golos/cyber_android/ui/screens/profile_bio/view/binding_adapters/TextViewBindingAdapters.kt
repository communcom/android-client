package io.golos.cyber_android.ui.screens.profile_bio.view.binding_adapters

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.screens.profile_bio.dto.TextLenInfo
import io.golos.cyber_android.ui.screens.profile_bio.view.widgets.TextLenView

@BindingAdapter("textLen")
fun setTextLenBinding(view: TextLenView, valueToBind: LiveData<TextLenInfo>?) =
    valueToBind?.value?.let { view.setTextLen(it.current, it.total) }