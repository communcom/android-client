package io.golos.cyber_android.ui.screens.app_start.shared

import androidx.databinding.BindingAdapter

@BindingAdapter("widget_title")
fun setHeaderWidgetTitle(view: HeaderWidget, valueToBind: String) {
    view.setTitle(valueToBind)
}

@BindingAdapter("back_visibility")
fun setHeaderWidgetBackVisibility(view: HeaderWidget, isVisible: Boolean) {
    view.setBackButtonVisibility(isVisible)
}