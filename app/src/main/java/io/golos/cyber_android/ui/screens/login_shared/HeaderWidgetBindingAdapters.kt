package io.golos.cyber_android.ui.screens.login_shared

import androidx.databinding.BindingAdapter

@BindingAdapter("header_widget_title")
fun setTitle(view: HeaderWidget, valueToBind: String) {
    view.setTitle(valueToBind)
}